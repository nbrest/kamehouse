"""Tests for the input validation."""

from __future__ import annotations

import pytest

from kamehouse_groot.utils.input_validator import (
    is_forbidden_script,
    is_valid_input_for_db_access,
    is_valid_input_for_shell,
    parse_boolean,
)


@pytest.mark.parametrize("value", [None, "", "tomcat-status.sh", "script.sh -a value"])
def test_valid_shell_input(value: str | None) -> None:
    assert is_valid_input_for_shell(value) is True


@pytest.mark.parametrize("value", [">", "<", ";", "|", "&", "*", "(", ")", "..", "$", "?", "`"])
def test_shell_input_rejects_forbidden_characters(value: str) -> None:
    assert is_valid_input_for_shell(value) is False


def test_shell_input_rejects_too_long_value() -> None:
    assert is_valid_input_for_shell("a" * 401) is False


@pytest.mark.parametrize("value", [None, "", "seiya", "my-password_1"])
def test_valid_db_input(value: str | None) -> None:
    assert is_valid_input_for_db_access(value) is True


@pytest.mark.parametrize("value", [";", "|", '"', "'", "#", "\\", "/", ",", "`", "!"])
def test_db_input_rejects_forbidden_characters(value: str) -> None:
    assert is_valid_input_for_db_access(value) is False


def test_db_input_rejects_too_long_value() -> None:
    assert is_valid_input_for_db_access("a" * 101) is False


@pytest.mark.parametrize(
    "script",
    [
        "secrets/get-kamehouse-secret.sh",
        "secrets/encrypt-kamehouse-secrets.sh",
        "secrets/decrypt-kamehouse-secrets.sh",
        "secrets/edit-kamehouse-secrets.sh",
        "docker/docker-ssh-kamehouse.sh",
        "ssh/ssh.sh",
    ],
)
def test_forbidden_scripts(script: str) -> None:
    assert is_forbidden_script(script, None) is True


def test_kamehouse_cmd_decrypt_is_forbidden() -> None:
    assert is_forbidden_script("cmd/kamehouse-cmd.sh", "decrypt-secrets") is True


def test_kamehouse_cmd_is_allowed_without_decrypt() -> None:
    assert is_forbidden_script("cmd/kamehouse-cmd.sh", "version") is False


def test_allowed_script() -> None:
    assert is_forbidden_script("tomcat/tomcat-status.sh", None) is False


@pytest.mark.parametrize(
    ("value", "expected"),
    [("true", True), ("TRUE", True), ("false", False), ("1", False), (None, False)],
)
def test_parse_boolean(value: str | None, expected: bool) -> None:
    assert parse_boolean(value) is expected
