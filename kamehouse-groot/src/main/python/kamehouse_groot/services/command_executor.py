"""Execute kamehouse-shell commands and capture their output."""

from __future__ import annotations

import logging
import subprocess
import time
from dataclasses import dataclass, field
from pathlib import Path

LOGGER = logging.getLogger(__name__)

COMMAND_NOT_FOUND_EXIT_CODE = 127
GENERIC_ERROR_EXIT_CODE = 1


@dataclass
class ExecutedCommand:
    """Raw result of executing a command."""

    command: list[str]
    exit_code: int = -1
    pid: int = -1
    standard_output: list[str] = field(default_factory=list)
    standard_error: list[str] = field(default_factory=list)
    timed_out: bool = False


def _to_lines(output: str | bytes | None) -> list[str]:
    if not output:
        return []
    if isinstance(output, bytes):
        output = output.decode("utf-8", errors="replace")
    return output.splitlines()


class CommandExecutor:
    """Run commands in the foreground or detached as a daemon."""

    def __init__(self, timeout_seconds: int = 300, daemon_wait_seconds: int = 5) -> None:
        self._timeout_seconds = timeout_seconds
        self._daemon_wait_seconds = daemon_wait_seconds

    def execute(
        self,
        command: list[str],
        daemon: bool = False,
        log_file: Path | None = None,
    ) -> ExecutedCommand:
        """Execute the command and return its raw result."""
        if daemon:
            return self._execute_daemon(command, log_file)
        return self._execute_foreground(command)

    def _execute_foreground(self, command: list[str]) -> ExecutedCommand:
        LOGGER.info("Executing command: %s", " ".join(command))
        try:
            result = subprocess.run(
                command,
                capture_output=True,
                check=False,
                text=True,
                timeout=self._timeout_seconds,
            )
        except subprocess.TimeoutExpired as exc:
            LOGGER.warning("Command timed out after %s seconds: %s", self._timeout_seconds, command)
            return ExecutedCommand(
                command=command,
                exit_code=-1,
                pid=-1,
                standard_output=_to_lines(exc.stdout),
                standard_error=_to_lines(exc.stderr),
                timed_out=True,
            )
        except FileNotFoundError:
            return ExecutedCommand(
                command=command,
                exit_code=COMMAND_NOT_FOUND_EXIT_CODE,
                standard_error=[f"Command not found: {command[0]}"],
            )
        except OSError as exc:
            return ExecutedCommand(
                command=command,
                exit_code=GENERIC_ERROR_EXIT_CODE,
                standard_error=[str(exc)],
            )

        return ExecutedCommand(
            command=command,
            exit_code=result.returncode,
            pid=-1,
            standard_output=_to_lines(result.stdout),
            standard_error=_to_lines(result.stderr),
        )

    def _execute_daemon(self, command: list[str], log_file: Path | None) -> ExecutedCommand:
        LOGGER.info("Starting daemon command: %s", " ".join(command))
        try:
            if log_file is not None:
                log_file.parent.mkdir(parents=True, exist_ok=True)
                output = open(log_file, "a", encoding="utf-8")  # noqa: SIM115
            else:
                output = subprocess.DEVNULL

            try:
                process = subprocess.Popen(
                    command,
                    stdout=output,
                    stderr=subprocess.STDOUT,
                    start_new_session=True,
                )
            finally:
                if log_file is not None:
                    output.close()
        except (OSError, ValueError) as exc:
            return ExecutedCommand(
                command=command,
                exit_code=GENERIC_ERROR_EXIT_CODE,
                standard_error=[str(exc)],
            )

        time.sleep(self._daemon_wait_seconds)
        return ExecutedCommand(
            command=command,
            exit_code=-1,
            pid=process.pid,
        )
