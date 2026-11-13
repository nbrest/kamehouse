"""Execute kamehouse-shell scripts for the groot admin endpoint."""

from __future__ import annotations

import logging
import shlex
from pathlib import Path

from kamehouse_groot.config import Settings
from kamehouse_groot.models.kamehouse_command_result import KamehouseCommandResult
from kamehouse_groot.services.command_executor import CommandExecutor, ExecutedCommand
from kamehouse_groot.services.exceptions import ForbiddenScriptError
from kamehouse_groot.utils.bash_colors import convert_lines_to_html
from kamehouse_groot.utils.input_validator import is_forbidden_script

LOGGER = logging.getLogger(__name__)

STATUS_COMPLETED = "completed"
STATUS_FAILED = "failed"
STATUS_RUNNING = "running"


class ShellExecutionService:
    """Build and execute the kamehouse-shell exec-script.sh command."""

    def __init__(self, settings: Settings, executor: CommandExecutor) -> None:
        self._settings = settings
        self._executor = executor
        self._exec_script = settings.shell_bin / "sudoers" / "www-data" / "exec-script.sh"

    def execute(
        self,
        script: str,
        script_args: str | None = None,
        execute_on_docker_host: bool = False,
        daemon: bool = False,
    ) -> KamehouseCommandResult:
        """Execute the script and return a KameHouse compatible result."""
        if self.is_forbidden_script(script, script_args):
            raise ForbiddenScriptError(f"Script not allowed for execution: {script}")

        command = self._build_command(script, script_args, execute_on_docker_host, daemon)
        log_file = self._resolve_log_file(script) if daemon and self._settings.use_script_log else None
        executed = self._executor.execute(command, daemon=daemon, log_file=log_file)
        return self._to_result(executed, daemon)

    def get_scripts(self) -> list[str]:
        """Return the kamehouse-shell scripts as the legacy JSON array."""
        result = self.execute(self._settings.scripts_csv)
        csv_output = "\n".join(result.standardOutput).strip()
        if not csv_output:
            return ["couldn-find-scripts.sh"]
        return csv_output.split(",")

    def is_forbidden_script(self, script: str | None, script_args: str | None) -> bool:
        """Return True if the script is forbidden for shell execution through groot."""
        return is_forbidden_script(script, script_args)

    def _build_command(
        self,
        script: str,
        script_args: str | None,
        execute_on_docker_host: bool,
        daemon: bool,
    ) -> list[str]:
        command = [str(self._exec_script), "-s", script]
        if script_args:
            command.extend(["-a", script_args])
        if execute_on_docker_host:
            command.append("--execute-on-docker-host")
        if daemon:
            command.append("--daemon")
        return command

    def _resolve_log_file(self, script: str) -> Path:
        script_name = Path(script).name
        if script_name.endswith(".sh"):
            script_name = script_name[: -len(".sh")]
        return self._settings.user_home / "logs" / f"{script_name}.log"

    def _to_result(self, executed: ExecutedCommand, daemon: bool) -> KamehouseCommandResult:
        status = self._resolve_status(executed, daemon)
        LOGGER.info(
            "Command %s finished with status %s and exit code %s",
            shlex.join(executed.command),
            status,
            executed.exit_code,
        )
        return KamehouseCommandResult(
            command=shlex.join(executed.command),
            exitCode=executed.exit_code,
            pid=executed.pid,
            status=status,
            standardOutput=executed.standard_output,
            standardOutputHtml=convert_lines_to_html(executed.standard_output),
            standardError=executed.standard_error,
            standardErrorHtml=convert_lines_to_html(executed.standard_error),
        )

    @staticmethod
    def _resolve_status(executed: ExecutedCommand, daemon: bool) -> str:
        if daemon or executed.timed_out:
            return STATUS_RUNNING
        if executed.exit_code == 0:
            return STATUS_COMPLETED
        return STATUS_FAILED
