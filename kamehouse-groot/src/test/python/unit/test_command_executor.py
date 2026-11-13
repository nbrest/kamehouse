"""Tests for the command executor."""

from __future__ import annotations

import os
import signal
import sys
import time
from pathlib import Path

import pytest

from kamehouse_groot.services import command_executor
from kamehouse_groot.services.command_executor import CommandExecutor, _to_lines


def test_to_lines_handles_bytes_and_empty() -> None:
    assert _to_lines(None) == []
    assert _to_lines("") == []
    assert _to_lines(b"one\ntwo") == ["one", "two"]


def test_foreground_success() -> None:
    executor = CommandExecutor(timeout_seconds=10)
    result = executor.execute([sys.executable, "-c", "print('hello')"])
    assert result.exit_code == 0
    assert result.standard_output == ["hello"]
    assert result.standard_error == []
    assert result.timed_out is False


def test_foreground_failure_and_stderr() -> None:
    executor = CommandExecutor(timeout_seconds=10)
    result = executor.execute(
        [sys.executable, "-c", "import sys; print('oops', file=sys.stderr); sys.exit(3)"]
    )
    assert result.exit_code == 3
    assert result.standard_error == ["oops"]


def test_foreground_timeout() -> None:
    executor = CommandExecutor(timeout_seconds=1)
    result = executor.execute([sys.executable, "-c", "import time; time.sleep(5)"])
    assert result.exit_code == -1
    assert result.timed_out is True
    assert result.standard_output == []


def test_command_not_found() -> None:
    executor = CommandExecutor()
    result = executor.execute(["/path/does/not/exist"])
    assert result.exit_code == 127
    assert result.standard_error


def test_os_error(monkeypatch: pytest.MonkeyPatch) -> None:
    def raise_os_error(*_: object, **__: object) -> None:
        raise OSError("boom")

    monkeypatch.setattr(command_executor.subprocess, "run", raise_os_error)
    result = CommandExecutor().execute(["anything"])
    assert result.exit_code == 1
    assert result.standard_error == ["boom"]


def test_daemon_starts_detached() -> None:
    executor = CommandExecutor(daemon_wait_seconds=1)
    result = executor.execute(
        [sys.executable, "-c", "import time; time.sleep(30)"],
        daemon=True,
    )
    try:
        assert result.pid > 0
        assert result.exit_code == -1
    finally:
        if result.pid > 0:
            os.kill(result.pid, signal.SIGTERM)


def test_daemon_writes_log_file(tmp_path: Path) -> None:
    log_file = tmp_path / "daemon.log"
    executor = CommandExecutor(daemon_wait_seconds=1)
    result = executor.execute(
        [
            sys.executable,
            "-c",
            "import time; print('started', flush=True); time.sleep(30)",
        ],
        daemon=True,
        log_file=log_file,
    )
    try:
        time.sleep(0.1)
        assert "started" in log_file.read_text(encoding="utf-8")
    finally:
        if result.pid > 0:
            os.kill(result.pid, signal.SIGTERM)


def test_daemon_invalid_command(tmp_path: Path) -> None:
    result = CommandExecutor(daemon_wait_seconds=1).execute(
        ["/path/does/not/exist"],
        daemon=True,
        log_file=tmp_path / "daemon.log",
    )
    assert result.exit_code == 1
    assert result.standard_error
