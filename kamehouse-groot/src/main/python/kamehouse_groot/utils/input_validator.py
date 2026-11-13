"""Input validation compatible with the existing groot implementation."""

from __future__ import annotations

MAX_SHELL_INPUT_LENGTH = 400
MAX_DB_INPUT_LENGTH = 100

SHELL_FORBIDDEN_CHARS = (
    ">",
    "<",
    ";",
    "|",
    "&",
    "*",
    "(",
    ")",
    "{",
    "}",
    "[",
    "]",
    "^",
    "#",
    "`",
    "\u00b4",
    "..",
    "%",
    "!",
    "$",
    "?",
)

DB_FORBIDDEN_CHARS = (
    ";",
    "|",
    '"',
    "'",
    "#",
    "\\",
    "/",
    ",",
    "`",
    "!",
)

FORBIDDEN_SCRIPT_PATTERNS = (
    "get-kamehouse-secret.sh",
    "encrypt-kamehouse-secrets.sh",
    "decrypt-kamehouse-secrets.sh",
    "edit-kamehouse-secrets.sh",
    "docker-ssh-",
    "ssh.sh",
)


def _is_empty(value: str | None) -> bool:
    return value is None or value == ""


def _contains_forbidden_char(value: str, forbidden_chars: tuple[str, ...]) -> bool:
    return any(forbidden in value for forbidden in forbidden_chars)


def is_valid_input_for_shell(value: str | None) -> bool:
    """Return True if the value can be safely passed to a kamehouse-shell script."""
    if _is_empty(value):
        return True
    if len(value) > MAX_SHELL_INPUT_LENGTH:
        return False
    return not _contains_forbidden_char(value, SHELL_FORBIDDEN_CHARS)


def is_valid_input_for_db_access(value: str | None) -> bool:
    """Return True if the value is valid for a kamehouse database access."""
    if _is_empty(value):
        return True
    if len(value) > MAX_DB_INPUT_LENGTH:
        return False
    return not _contains_forbidden_char(value, DB_FORBIDDEN_CHARS)


def is_forbidden_script(script: str | None, script_args: str | None) -> bool:
    """Return True if the script is forbidden for shell execution through groot."""
    script = script or ""
    script_args = script_args or ""

    if "kamehouse-cmd.sh" in script and "decrypt" in script_args:
        return True

    return any(pattern in script for pattern in FORBIDDEN_SCRIPT_PATTERNS)


def parse_boolean(value: str | None) -> bool:
    """Parse the boolean values 'true' and 'TRUE'."""
    return value in ("true", "TRUE")
