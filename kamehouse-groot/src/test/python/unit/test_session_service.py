"""Tests for the cookie based session service."""

from __future__ import annotations

import os
import time
from pathlib import Path

import pytest

from kamehouse_groot.config import Settings
from kamehouse_groot.services.session_service import SessionService, is_valid_session_token


def _settings(tmp_path: Path) -> Settings:
    return Settings(deploy_path=tmp_path / "deploy")


def test_create_get_and_delete_session(tmp_path: Path) -> None:
    service = SessionService(_settings(tmp_path))
    token = service.create("seiya")
    assert service.get_username(token) == "seiya"
    assert (tmp_path / "deploy" / "run" / "sessions" / token).is_file()
    service.delete(token)
    assert service.get_username(token) is None
    assert not (tmp_path / "deploy" / "run" / "sessions" / token).exists()


def test_sessions_are_loaded_from_disk(tmp_path: Path) -> None:
    service = SessionService(_settings(tmp_path))
    token = service.create("seiya")
    reloaded = SessionService(_settings(tmp_path))
    assert reloaded.get_username(token) == "seiya"


def test_get_username_loads_lazily_from_disk(tmp_path: Path) -> None:
    service = SessionService(_settings(tmp_path))
    session_dir = tmp_path / "deploy" / "run" / "sessions"
    session_dir.mkdir(parents=True)
    token = "abcDEF123-_"
    (session_dir / token).write_text("shiryu\n", encoding="utf-8")
    assert service.get_username(token) == "shiryu"


def test_expired_session_is_removed(tmp_path: Path) -> None:
    settings = Settings(deploy_path=tmp_path / "deploy", session_max_age_seconds=10)
    SessionService(settings).create("seiya")
    session_dir = tmp_path / "deploy" / "run" / "sessions"
    token = next(session_dir.iterdir()).name
    path = session_dir / token
    expired = time.time() - 11
    os.utime(path, (expired, expired))

    service = SessionService(settings, cache_sessions=False)
    assert service.get_username(token) is None
    assert not path.exists()


def test_expired_sessions_are_not_loaded(tmp_path: Path) -> None:
    settings = Settings(deploy_path=tmp_path / "deploy", session_max_age_seconds=10)
    session_dir = tmp_path / "deploy" / "run" / "sessions"
    session_dir.mkdir(parents=True)
    token = "expiredToken"
    path = session_dir / token
    path.write_text("seiya\n", encoding="utf-8")
    expired = time.time() - 11
    os.utime(path, (expired, expired))

    service = SessionService(settings)
    assert service.get_username(token) is None
    assert not path.exists()


def test_zero_max_age_disables_expiry(tmp_path: Path) -> None:
    settings = Settings(deploy_path=tmp_path / "deploy", session_max_age_seconds=0)
    service = SessionService(settings)
    token = service.create("seiya")
    path = tmp_path / "deploy" / "run" / "sessions" / token
    expired = time.time() - 999999
    os.utime(path, (expired, expired))

    reloaded = SessionService(settings, cache_sessions=False)
    assert reloaded.get_username(token) == "seiya"
    assert reloaded.cookie_max_age is None


def test_cookie_max_age(tmp_path: Path) -> None:
    service = SessionService(_settings(tmp_path))
    assert service.max_age_seconds == 2629746
    assert service.cookie_max_age == 2629746


def test_invalid_tokens_are_rejected(tmp_path: Path) -> None:
    service = SessionService(_settings(tmp_path))
    assert service.get_username(None) is None
    assert service.get_username("") is None
    assert service.get_username("../secrets") is None
    assert service.get_username("token with spaces") is None
    service.delete(None)
    service.delete("../secrets")


def test_missing_session_file_returns_none(tmp_path: Path) -> None:
    service = SessionService(_settings(tmp_path))
    assert service.get_username("missingToken") is None


def test_empty_session_file_returns_none(tmp_path: Path) -> None:
    service = SessionService(_settings(tmp_path))
    session_dir = tmp_path / "deploy" / "run" / "sessions"
    session_dir.mkdir(parents=True)
    token = "emptyToken"
    (session_dir / token).write_text("\n", encoding="utf-8")
    assert service.get_username(token) is None


def test_session_read_errors_are_ignored(
    monkeypatch: pytest.MonkeyPatch, tmp_path: Path
) -> None:
    service = SessionService(_settings(tmp_path))
    session_dir = tmp_path / "deploy" / "run" / "sessions"
    session_dir.mkdir(parents=True)
    token = "readErrorToken"
    path = session_dir / token
    path.write_text("seiya\n", encoding="utf-8")

    original_read_text = Path.read_text

    def fail_read(self: Path, *args: object, **kwargs: object) -> str:
        if self == path:
            raise OSError("read failed")
        return original_read_text(self, *args, **kwargs)

    monkeypatch.setattr(Path, "read_text", fail_read)
    assert service.get_username(token) is None


def test_session_write_errors_are_ignored(
    monkeypatch: pytest.MonkeyPatch, tmp_path: Path
) -> None:
    service = SessionService(_settings(tmp_path))

    def fail_write(self: Path, *args: object, **kwargs: object) -> int:
        raise OSError("write failed")

    monkeypatch.setattr(Path, "write_text", fail_write)
    token = service.create("seiya")
    assert service.get_username(token) == "seiya"


def test_session_delete_errors_are_ignored(
    monkeypatch: pytest.MonkeyPatch, tmp_path: Path
) -> None:
    service = SessionService(_settings(tmp_path))
    token = service.create("seiya")

    def fail_unlink(self: Path, *args: object, **kwargs: object) -> None:
        raise OSError("delete failed")

    monkeypatch.setattr(Path, "unlink", fail_unlink)
    service.delete(token)
    assert (tmp_path / "deploy" / "run" / "sessions" / token).is_file()


def test_load_ignores_invalid_files(
    monkeypatch: pytest.MonkeyPatch, tmp_path: Path
) -> None:
    session_dir = tmp_path / "deploy" / "run" / "sessions"
    session_dir.mkdir(parents=True)
    (session_dir / "validToken").write_text("seiya\n", encoding="utf-8")
    (session_dir / "invalid token").write_text("shiryu\n", encoding="utf-8")
    (session_dir / "emptyToken").write_text("\n", encoding="utf-8")
    (session_dir / "folder").mkdir()

    original_read_text = Path.read_text

    def fail_read(self: Path, *args: object, **kwargs: object) -> str:
        if self.name == "validToken":
            raise OSError("read failed")
        return original_read_text(self, *args, **kwargs)

    monkeypatch.setattr(Path, "read_text", fail_read)
    service = SessionService(_settings(tmp_path))
    assert service.get_username("validToken") is None
    assert service.get_username("invalid token") is None


def test_token_validation() -> None:
    assert is_valid_session_token("abcDEF0123-_")
    assert not is_valid_session_token(None)
    assert not is_valid_session_token("")
    assert not is_valid_session_token("abc/def")
