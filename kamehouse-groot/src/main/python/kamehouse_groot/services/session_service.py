"""Cookie based login sessions for the KameHouse GRoot service."""

from __future__ import annotations

import logging
import secrets
import threading
import time
from pathlib import Path

from kamehouse_groot.config import Settings

LOGGER = logging.getLogger(__name__)

SESSION_TOKEN_BYTES = 32
SAFE_TOKEN_CHARS = frozenset("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_")


def is_valid_session_token(token: str | None) -> bool:
    """Return True if the token can safely be used as a session file name."""
    if not token:
        return False
    return all(character in SAFE_TOKEN_CHARS for character in token)


class SessionService:
    """Store login sessions in memory and under the deployment run folder.

    When cache_sessions is false, sessions are always read from and written to
    the session files instead of an in-memory cache. Services sharing the
    session store with another web service should disable the cache so
    sessions created or deleted by the other service are picked up on every
    request.
    """

    def __init__(self, settings: Settings, cache_sessions: bool = True) -> None:
        self._settings = settings
        self._cache_sessions = cache_sessions
        self._sessions: dict[str, tuple[str, float]] = {}
        self._lock = threading.Lock()
        self._load_sessions()

    @property
    def cookie_name(self) -> str:
        """Name of the session cookie."""
        return self._settings.session_cookie_name

    @property
    def max_age_seconds(self) -> int:
        """Lifetime of a login session in seconds."""
        return self._settings.session_max_age_seconds

    @property
    def cookie_max_age(self) -> int | None:
        """Max-Age for the session cookie, or None for a browser session cookie."""
        if self.max_age_seconds <= 0:
            return None
        return self.max_age_seconds

    def create(self, username: str) -> str:
        """Create a session for the username and return its token."""
        token = secrets.token_urlsafe(SESSION_TOKEN_BYTES)
        created_at = time.time()
        with self._lock:
            if self._cache_sessions:
                self._sessions[token] = (username, created_at)
            self._write_session(token, username)
        return token

    def get_username(self, token: str | None) -> str | None:
        """Return the username associated with the token, or None."""
        if not is_valid_session_token(token):
            return None

        now = time.time()
        if self._cache_sessions:
            with self._lock:
                session = self._sessions.get(token)
                if session is not None:
                    username, created_at = session
                    if self._is_expired(created_at, now):
                        self._sessions.pop(token, None)
                        self._unlink_session(token)
                        return None
                    return username

        path = self._session_path(token)
        if not path.is_file():
            return None

        try:
            created_at = path.stat().st_mtime
        except OSError as exc:
            LOGGER.warning("Could not stat session file %s: %s", path, exc)
            return None

        if self._is_expired(created_at, now):
            self._unlink_session(token)
            return None

        try:
            username = path.read_text(encoding="utf-8").strip()
        except OSError as exc:
            LOGGER.warning("Could not read session file %s: %s", path, exc)
            return None

        if not username:
            return None

        if self._cache_sessions:
            with self._lock:
                self._sessions[token] = (username, created_at)
        return username

    def delete(self, token: str | None) -> None:
        """Delete the session associated with the token."""
        if not is_valid_session_token(token):
            return

        with self._lock:
            self._sessions.pop(token, None)
            self._unlink_session(token)

    def _is_expired(self, created_at: float, now: float) -> bool:
        return self.max_age_seconds > 0 and now - created_at >= self.max_age_seconds

    def _session_path(self, token: str) -> Path:
        return self._settings.resolved_session_dir / token

    def _unlink_session(self, token: str) -> None:
        try:
            self._session_path(token).unlink(missing_ok=True)
        except OSError as exc:
            LOGGER.warning("Could not delete session file for %s: %s", token, exc)

    def _load_sessions(self) -> None:
        if not self._cache_sessions:
            return

        session_dir = self._settings.resolved_session_dir
        if not session_dir.is_dir():
            return

        now = time.time()
        for path in session_dir.iterdir():
            if not path.is_file() or not is_valid_session_token(path.name):
                continue
            try:
                created_at = path.stat().st_mtime
                if self._is_expired(created_at, now):
                    self._unlink_session(path.name)
                    continue
                username = path.read_text(encoding="utf-8").strip()
            except OSError as exc:
                LOGGER.warning("Could not read session file %s: %s", path, exc)
                continue
            if username:
                self._sessions[path.name] = (username, created_at)

    def _write_session(self, token: str, username: str) -> None:
        try:
            self._settings.resolved_session_dir.mkdir(parents=True, exist_ok=True)
            path = self._session_path(token)
            path.write_text(f"{username}\n", encoding="utf-8")
            path.chmod(0o600)
        except OSError as exc:
            LOGGER.warning("Could not persist session for %s: %s", username, exc)
