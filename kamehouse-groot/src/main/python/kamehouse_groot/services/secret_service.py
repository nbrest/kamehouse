"""Retrieve KameHouse secrets through the kamehouse-shell scripts."""

from __future__ import annotations

import logging
import subprocess
from pathlib import Path

LOGGER = logging.getLogger(__name__)


class SecretRetrievalError(RuntimeError):
    """Raised when a kamehouse secret cannot be retrieved."""


class SecretService:
    """Read secrets from the encrypted KameHouse secrets store."""

    def __init__(self, script_path: Path, timeout_seconds: int = 30) -> None:
        self._script_path = script_path
        self._timeout_seconds = timeout_seconds
        self._cache: dict[str, str] = {}

    def get_secret(self, secret_key: str) -> str:
        """Return the value of the requested secret, caching the result."""
        if secret_key in self._cache:
            return self._cache[secret_key]

        command = [str(self._script_path), "-s", secret_key]
        LOGGER.debug("Retrieving kamehouse secret %s", secret_key)
        try:
            result = subprocess.run(
                command,
                capture_output=True,
                check=False,
                text=True,
                timeout=self._timeout_seconds,
            )
        except (OSError, subprocess.TimeoutExpired) as exc:
            raise SecretRetrievalError(
                f"Error retrieving kamehouse secret {secret_key}: {exc}"
            ) from exc

        if result.returncode != 0:
            message = result.stderr.strip() or result.stdout.strip()
            raise SecretRetrievalError(
                f"Error retrieving kamehouse secret {secret_key}: {message}"
            )

        secret = result.stdout.strip()
        self._cache[secret_key] = secret
        return secret
