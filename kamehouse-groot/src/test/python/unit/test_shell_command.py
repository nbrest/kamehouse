"""Tests for building shell script commands."""

from __future__ import annotations

from pathlib import Path

import pytest

from kamehouse_groot.utils import shell_command


def test_build_shell_command_leaves_non_windows_commands_unchanged(
    monkeypatch: pytest.MonkeyPatch,
) -> None:
    monkeypatch.setattr(shell_command, "is_windows", lambda: False)
    command = ["/path/script.sh", "-s", "KEY"]
    assert shell_command.build_shell_command(command) == command


def test_build_shell_command_leaves_non_scripts_unchanged_on_windows(
    monkeypatch: pytest.MonkeyPatch,
) -> None:
    monkeypatch.setattr(shell_command, "is_windows", lambda: True)
    command = ["python", "-c", "print('hello')"]
    assert shell_command.build_shell_command(command) == command


def test_build_shell_command_wraps_shell_scripts_on_windows(
    monkeypatch: pytest.MonkeyPatch,
    tmp_path: Path,
) -> None:
    bash_silent_bat = tmp_path / "bash-silent.bat"
    bash_silent_bat.write_text("", encoding="utf-8")
    monkeypatch.setattr(shell_command, "is_windows", lambda: True)
    monkeypatch.setattr(shell_command, "find_bash_silent_bat", lambda _: bash_silent_bat)
    command = [r"C:\script.sh", "-s", "KEY"]
    assert shell_command.build_shell_command(command) == [
        str(bash_silent_bat),
        "-c",
        r'. /etc/profile ; "C:\script.sh" -s KEY',
    ]


def test_build_shell_command_wraps_shell_scripts_without_args(
    monkeypatch: pytest.MonkeyPatch,
    tmp_path: Path,
) -> None:
    bash_silent_bat = tmp_path / "bash-silent.bat"
    monkeypatch.setattr(shell_command, "is_windows", lambda: True)
    monkeypatch.setattr(shell_command, "find_bash_silent_bat", lambda _: bash_silent_bat)
    assert shell_command.build_shell_command([r"C:\script.sh"]) == [
        str(bash_silent_bat),
        "-c",
        r'. /etc/profile ; "C:\script.sh"',
    ]


def test_build_shell_command_without_bash_silent_bat_raises(
    monkeypatch: pytest.MonkeyPatch,
) -> None:
    monkeypatch.setattr(shell_command, "is_windows", lambda: True)
    monkeypatch.setattr(shell_command, "find_bash_silent_bat", lambda _: None)
    with pytest.raises(FileNotFoundError, match="bash-silent.bat is required"):
        shell_command.build_shell_command(["script.sh"])


def test_is_shell_script_handles_empty_command() -> None:
    assert shell_command.is_shell_script([]) is False
    assert shell_command.is_shell_script(["SCRIPT.SH"]) is True


def test_find_bash_silent_bat_uses_script_shell(
    monkeypatch: pytest.MonkeyPatch,
    tmp_path: Path,
) -> None:
    shell_bin = tmp_path / "shell" / "bin"
    handlers = shell_bin / "win" / "bat"
    script = shell_bin / "secrets" / "get-kamehouse-secret.sh"
    bash_silent_bat = handlers / "bash-silent.bat"
    handlers.mkdir(parents=True)
    bash_silent_bat.write_text("", encoding="utf-8")
    monkeypatch.setattr(shell_command, "DEFAULT_WINDOWS_BASH_SILENT_BAT", bash_silent_bat)
    assert shell_command.find_bash_silent_bat(script) == bash_silent_bat


def test_find_bash_silent_bat_uses_default(
    monkeypatch: pytest.MonkeyPatch,
    tmp_path: Path,
) -> None:
    script = tmp_path / "missing-shell" / "secrets" / "get-kamehouse-secret.sh"
    default_bash_silent_bat = tmp_path / "bash-silent.bat"
    default_bash_silent_bat.write_text("", encoding="utf-8")
    monkeypatch.setattr(
        shell_command,
        "DEFAULT_WINDOWS_BASH_SILENT_BAT",
        default_bash_silent_bat,
    )
    assert shell_command.find_bash_silent_bat(script) == default_bash_silent_bat


def test_find_bash_silent_bat_returns_none_when_missing(
    monkeypatch: pytest.MonkeyPatch,
    tmp_path: Path,
) -> None:
    monkeypatch.setattr(
        shell_command,
        "DEFAULT_WINDOWS_BASH_SILENT_BAT",
        tmp_path / "missing-bash-silent.bat",
    )
    assert shell_command.find_bash_silent_bat(tmp_path / "script.sh") is None
