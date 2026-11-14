"""Tests for application settings."""

from __future__ import annotations

from pathlib import Path

from kamehouse_groot.config import Settings, get_settings


def test_defaults() -> None:
    settings = Settings()
    assert settings.host == "0.0.0.0"
    assert settings.port == 9091
    assert settings.db_name == "kamehouse"
    assert settings.command_timeout_seconds == 300
    assert settings.session_max_age_seconds == 2629746


def test_log_dir() -> None:
    settings = Settings(deploy_path=Path("/tmp/deploy"))
    assert settings.log_dir == Path("/tmp/deploy/logs")


def test_resolved_secret_script_default() -> None:
    settings = Settings(shell_bin=Path("/tmp/bin"))
    assert settings.resolved_secret_script == Path("/tmp/bin/secrets/get-kamehouse-secret.sh")


def test_resolved_secret_script_override() -> None:
    settings = Settings(secret_script=Path("/tmp/custom.sh"))
    assert settings.resolved_secret_script == Path("/tmp/custom.sh")


def test_resolved_session_dir_default() -> None:
    settings = Settings(deploy_path=Path("/tmp/deploy"))
    assert settings.resolved_session_dir == Path("/tmp/deploy/run/sessions")


def test_resolved_session_dir_override() -> None:
    settings = Settings(session_dir=Path("/tmp/other-sessions"))
    assert settings.resolved_session_dir == Path("/tmp/other-sessions")


def test_get_settings_is_cached(monkeypatch) -> None:
    get_settings.cache_clear()
    monkeypatch.setenv("GROOT_PORT", "1234")
    try:
        first = get_settings()
        second = get_settings()
        assert first is second
        assert first.port == 1234
    finally:
        get_settings.cache_clear()
