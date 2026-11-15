"""Build commands that can execute kamehouse-shell scripts on every host.

On Windows, shell scripts go through kamehouse-shell's bash-silent.bat so the
banner and interactive-shell warnings from bash.bat don't corrupt output.
"""

from __future__ import annotations

import sys
from pathlib import Path

DEFAULT_WINDOWS_BASH_SILENT_BAT = (
    Path.home()
    / ".kamehouse"
    / "opt"
    / "kamehouse-shell"
    / "bin"
    / "win"
    / "bat"
    / "bash-silent.bat"
)
WINDOWS_BASH_SILENT_PREFIX = ". /etc/profile ; "


def is_windows() -> bool:
    """Return True when the service is running on Windows."""
    return sys.platform == "win32"


def is_shell_script(command: list[str]) -> bool:
    """Return True when the command starts with a shell script."""
    return bool(command) and command[0].lower().endswith(".sh")


def find_bash_silent_bat(script_path: Path | None = None) -> Path | None:
    """Return the kamehouse-shell bash-silent.bat used to run shell scripts."""
    candidates: list[Path] = []
    if script_path is not None:
        candidates.append(script_path.parent.parent / "win" / "bat" / "bash-silent.bat")
    candidates.append(DEFAULT_WINDOWS_BASH_SILENT_BAT)
    for candidate in candidates:
        if candidate.is_file():
            return candidate
    return None


def build_shell_command(command: list[str]) -> list[str]:
    """Return the command wrapped with bash-silent.bat when running on Windows."""
    if not is_windows() or not is_shell_script(command):
        return command
    bash_silent_bat = find_bash_silent_bat(Path(command[0]))
    if bash_silent_bat is None:
        raise FileNotFoundError(
            "bash-silent.bat is required to execute shell scripts on Windows but it was not found"
        )
    script_command = f'{WINDOWS_BASH_SILENT_PREFIX}"{command[0]}"'
    if len(command) > 1:
        script_command = f"{script_command} {' '.join(command[1:])}"
    return [str(bash_silent_bat), "-c", script_command]
