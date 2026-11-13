"""Tests for the kamehouse secret service."""

from __future__ import annotations

from pathlib import Path

import pytest

from kamehouse_groot.services.secret_service import SecretRetrievalError, SecretService


def _script(tmp_path: Path, body: str) -> Path:
    script = tmp_path / "secret.sh"
    script.write_text(f"#!/bin/bash\n{body}\n", encoding="utf-8")
    script.chmod(0o755)
    return script


def test_returns_secret(tmp_path: Path) -> None:
    service = SecretService(_script(tmp_path, "echo secret-value"))
    assert service.get_secret("MARIADB_PASS_KAMEHOUSE") == "secret-value"


def test_strips_whitespace(tmp_path: Path) -> None:
    service = SecretService(_script(tmp_path, "echo '  secret  '"))
    assert service.get_secret("KEY") == "secret"


def test_caches_secret(tmp_path: Path) -> None:
    script = _script(tmp_path, "echo first")
    service = SecretService(script)
    assert service.get_secret("KEY") == "first"
    script.write_text("#!/bin/bash\necho second\n", encoding="utf-8")
    assert service.get_secret("KEY") == "first"


def test_failure_raises(tmp_path: Path) -> None:
    service = SecretService(_script(tmp_path, "echo boom >&2; exit 2"))
    with pytest.raises(SecretRetrievalError, match="boom"):
        service.get_secret("KEY")


def test_missing_script_raises(tmp_path: Path) -> None:
    service = SecretService(tmp_path / "missing.sh")
    with pytest.raises(SecretRetrievalError):
        service.get_secret("KEY")


def test_timeout_raises(tmp_path: Path) -> None:
    service = SecretService(_script(tmp_path, "sleep 5"), timeout_seconds=1)
    with pytest.raises(SecretRetrievalError):
        service.get_secret("KEY")
