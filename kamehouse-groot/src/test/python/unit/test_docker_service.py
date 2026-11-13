"""Tests for the docker container environment reader."""

from __future__ import annotations

from pathlib import Path

import pytest

from kamehouse_groot.services.docker_service import DockerEnvironment


def _write_env(tmp_path: Path, content: str) -> DockerEnvironment:
    config_dir = tmp_path / ".kamehouse" / "config"
    config_dir.mkdir(parents=True)
    (config_dir / ".kamehouse-docker-container-env").write_text(content, encoding="utf-8")
    return DockerEnvironment(tmp_path)


def test_missing_env_file_returns_empty(tmp_path: Path) -> None:
    assert DockerEnvironment(tmp_path).get_properties() == {}


def test_parses_properties(tmp_path: Path) -> None:
    environment = _write_env(
        tmp_path,
        "# comment\n\nIS_LINUX_DOCKER_HOST=true\nIS_DOCKER_CONTAINER=TRUE\n"
        "DOCKER_CONTROL_HOST=false\nIGNORED_LINE\nVALUE=a=b\n",
    )
    properties = environment.get_properties()
    assert properties["IS_LINUX_DOCKER_HOST"] == "true"
    assert properties["IS_DOCKER_CONTAINER"] == "TRUE"
    assert properties["DOCKER_CONTROL_HOST"] == "false"
    assert properties["VALUE"] == "a=b"
    assert "IGNORED_LINE" not in properties


def test_boolean_property_accepts_true_and_uppercase_true() -> None:
    properties = {
        "TRUE_LOWER": "true",
        "TRUE_UPPER": "TRUE",
        "FALSE_LOWER": "false",
        "OTHER": "1",
    }
    assert DockerEnvironment.get_boolean_property(properties, "TRUE_LOWER") is True
    assert DockerEnvironment.get_boolean_property(properties, "TRUE_UPPER") is True
    assert DockerEnvironment.get_boolean_property(properties, "FALSE_LOWER") is False
    assert DockerEnvironment.get_boolean_property(properties, "OTHER") is False
    assert DockerEnvironment.get_boolean_property(properties, "MISSING") is False


def test_read_errors_return_empty(
    monkeypatch: pytest.MonkeyPatch, tmp_path: Path
) -> None:
    environment = _write_env(tmp_path, "IS_DOCKER_CONTAINER=true\n")
    env_file = environment.env_file

    original_read_text = Path.read_text

    def fail_read(self: Path, *args: object, **kwargs: object) -> str:
        if self == env_file:
            raise OSError("read failed")
        return original_read_text(self, *args, **kwargs)

    monkeypatch.setattr(Path, "read_text", fail_read)
    assert environment.get_properties() == {}
