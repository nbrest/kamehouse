"""Tests for the shell execution service."""

from __future__ import annotations

from pathlib import Path

import pytest

from kamehouse_groot.config import Settings
from kamehouse_groot.services.command_executor import ExecutedCommand
from kamehouse_groot.services.exceptions import ForbiddenScriptError
from kamehouse_groot.services.shell_execution_service import ShellExecutionService


class FakeExecutor:
    """Record executed commands and return a configured result."""

    def __init__(self, result: ExecutedCommand | None = None) -> None:
        self.result = result
        self.calls: list[dict[str, object]] = []

    def execute(
        self,
        command: list[str],
        daemon: bool = False,
        log_file: Path | None = None,
    ) -> ExecutedCommand:
        self.calls.append({"command": command, "daemon": daemon, "log_file": log_file})
        if self.result is not None:
            return self.result
        return ExecutedCommand(command=command, exit_code=0)


def _settings(tmp_path: Path, use_script_log: bool = True) -> Settings:
    return Settings(
        deploy_path=tmp_path / "deploy",
        user_home=tmp_path / "home",
        shell_bin=tmp_path / "bin",
        use_script_log=use_script_log,
    )


def _service(tmp_path: Path, result: ExecutedCommand | None = None, use_script_log: bool = True) -> tuple[
    ShellExecutionService, FakeExecutor
]:
    executor = FakeExecutor(result)
    return ShellExecutionService(_settings(tmp_path, use_script_log), executor), executor


def test_builds_full_command(tmp_path: Path) -> None:
    service, executor = _service(tmp_path)
    service.execute(
        "tomcat/tomcat-status.sh",
        script_args="-v",
        execute_on_docker_host=True,
        daemon=True,
    )
    command = executor.calls[0]["command"]
    assert command == [
        str(tmp_path / "bin" / "groot" / "exec-script.sh"),
        "-s",
        "tomcat/tomcat-status.sh",
        "-a",
        "-v",
        "--execute-on-docker-host",
        "--daemon",
    ]


def test_daemon_uses_script_log(tmp_path: Path) -> None:
    service, executor = _service(tmp_path)
    service.execute("tomcat/tomcat-status.sh", daemon=True)
    assert executor.calls[0]["log_file"] == tmp_path / "home" / "logs" / "tomcat-status.log"


def test_daemon_log_can_be_disabled(tmp_path: Path) -> None:
    service, executor = _service(tmp_path, use_script_log=False)
    service.execute("tomcat/tomcat-status.sh", daemon=True)
    assert executor.calls[0]["log_file"] is None


def test_foreground_does_not_use_log(tmp_path: Path) -> None:
    service, executor = _service(tmp_path)
    service.execute("tomcat/tomcat-status.sh")
    assert executor.calls[0]["log_file"] is None


def test_completed_status(tmp_path: Path) -> None:
    result = ExecutedCommand(command=["cmd"], exit_code=0, standard_output=["[0;32mOK[0m"])
    service, _ = _service(tmp_path, result)
    response = service.execute("tomcat/tomcat-status.sh")
    assert response.status == "completed"
    assert response.exitCode == 0
    assert response.standardOutput == ["[0;32mOK[0m"]
    assert response.standardOutputHtml == ['<span style="color:green">OK</span>']


def test_failed_status(tmp_path: Path) -> None:
    result = ExecutedCommand(command=["cmd"], exit_code=2)
    service, _ = _service(tmp_path, result)
    response = service.execute("tomcat/tomcat-status.sh")
    assert response.status == "failed"
    assert response.exitCode == 2


def test_timed_out_status(tmp_path: Path) -> None:
    result = ExecutedCommand(command=["cmd"], exit_code=-1, timed_out=True)
    service, _ = _service(tmp_path, result)
    response = service.execute("tomcat/tomcat-status.sh")
    assert response.status == "running"


def test_daemon_status(tmp_path: Path) -> None:
    result = ExecutedCommand(command=["cmd"], exit_code=-1, pid=1234)
    service, _ = _service(tmp_path, result)
    response = service.execute("tomcat/tomcat-status.sh", daemon=True)
    assert response.status == "running"
    assert response.pid == 1234


def test_forbidden_script_raises(tmp_path: Path) -> None:
    service, executor = _service(tmp_path)
    with pytest.raises(ForbiddenScriptError):
        service.execute("secrets/get-kamehouse-secret.sh")
    assert executor.calls == []


def test_get_scripts_parses_csv_output(tmp_path: Path) -> None:
    result = ExecutedCommand(
        command=["cmd"],
        exit_code=0,
        standard_output=[",is-linux-host.sh,tomcat/tomcat-status.sh,"],
    )
    service, executor = _service(tmp_path, result)
    assert service.get_scripts() == ["", "is-linux-host.sh", "tomcat/tomcat-status.sh", ""]
    assert executor.calls[0]["command"][-2:] == ["-s", "csv/csv-kamehouse-shell.sh"]


def test_get_scripts_falls_back_when_output_is_empty(tmp_path: Path) -> None:
    service, _ = _service(tmp_path, ExecutedCommand(command=["cmd"], exit_code=0))
    assert service.get_scripts() == ["couldn-find-scripts.sh"]
