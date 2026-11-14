"""Tests for application settings."""

from __future__ import annotations

from pathlib import Path

import pytest

from kamehouse_groot.config import Settings, deep_merge, get_settings


def _write_config(path: Path, content: str) -> Path:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")
    return path


def _write_user_config(content: str) -> Path:
    return _write_config(Settings.config_dir / Settings.config_file_name, content)


def _write_default_config(content: str) -> Path:
    return _write_config(
        Settings.default_deploy_path / "conf" / Settings.default_config_file_name,
        content,
    )


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


def test_environment_variables_are_ignored(monkeypatch) -> None:
    monkeypatch.setenv("GROOT_PORT", "3333")
    monkeypatch.setenv("GROOT_CONFIG_FILE", "/tmp/does-not-exist.toml")

    assert Settings().port == 9091


def test_user_config_file_overrides_defaults() -> None:
    _write_user_config("[groot]\nport = 1234\nsession_max_age_seconds = 42\n")

    settings = Settings()

    assert settings.port == 1234
    assert settings.session_max_age_seconds == 42


def test_user_config_overrides_default_config_file() -> None:
    _write_default_config('[groot]\nhost = "127.0.0.1"\nport = 1111\n')
    _write_user_config("[groot]\nport = 2222\n")

    settings = Settings()

    assert settings.host == "127.0.0.1"
    assert settings.port == 2222


def test_config_file_expands_home_paths() -> None:
    _write_user_config('[groot]\nshell_bin = "~/custom/bin"\n')

    settings = Settings()

    assert settings.shell_bin == Path.home() / "custom" / "bin"


def test_path_initializer_expands_home() -> None:
    settings = Settings(shell_bin=Path("~/custom/bin"))
    assert settings.shell_bin == Path.home() / "custom" / "bin"


def test_empty_config_file_is_ignored() -> None:
    _write_user_config("")

    assert Settings().port == 9091


def test_missing_config_table_raises() -> None:
    _write_user_config("[other]\nport = 1\n")

    with pytest.raises(ValueError, match=r"Missing \[groot\] table"):
        Settings()


def test_empty_required_path_raises() -> None:
    _write_user_config('[groot]\nshell_bin = ""\n')

    with pytest.raises(ValueError, match="shell_bin can't be empty"):
        Settings()


def test_missing_config_files_use_defaults() -> None:
    assert Settings().port == 9091


def test_config_files_are_loaded_from_config_dir_and_deploy_path() -> None:
    _write_default_config('[groot]\nhost = "127.0.0.1"\nport = 1111\n')
    _write_user_config("[groot]\nport = 2222\n")

    settings = Settings()

    assert settings.host == "127.0.0.1"
    assert settings.port == 2222


def test_repository_default_config_file(monkeypatch, tmp_path) -> None:
    monkeypatch.setattr(
        Settings,
        "default_deploy_path",
        Path(__file__).resolve().parents[4],
    )
    monkeypatch.setattr(Settings, "config_dir", tmp_path / "missing")

    settings = Settings()

    assert settings.host == "0.0.0.0"
    assert settings.port == 9091
    assert settings.session_max_age_seconds == 2629746
    assert settings.service_name == "kamehouse-groot"


def test_deep_merge_merges_nested_tables() -> None:
    assert deep_merge({"a": {"b": 1, "c": 2}, "d": 3}, {"a": {"b": 4}, "d": 5}) == {
        "a": {"b": 4, "c": 2},
        "d": 5,
    }


def test_get_settings_is_cached() -> None:
    get_settings.cache_clear()
    _write_user_config("[groot]\nport = 1234\n")
    try:
        first = get_settings()
        second = get_settings()
        assert first is second
        assert first.port == 1234
    finally:
        get_settings.cache_clear()
