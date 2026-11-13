"""Tests for bcrypt password verification."""

from __future__ import annotations

import bcrypt
import pytest

from kamehouse_groot.services import password_verifier
from kamehouse_groot.services.password_verifier import normalize_bcrypt_hash, verify_password


def _hash(password: str, prefix: bytes = b"$2b$") -> str:
    hashed = bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt(rounds=4))
    return prefix.decode("utf-8") + hashed[4:].decode("utf-8")


def test_normalize_2y_hash() -> None:
    assert normalize_bcrypt_hash("$2y$12$abcdef") == "$2b$12$abcdef"


def test_normalize_keeps_2a_and_2b() -> None:
    assert normalize_bcrypt_hash("$2a$12$abcdef") == "$2a$12$abcdef"
    assert normalize_bcrypt_hash("$2b$12$abcdef") == "$2b$12$abcdef"


def test_verify_password_2b() -> None:
    hashed = _hash("ikki")
    assert verify_password("ikki", hashed) is True


def test_verify_password_2a() -> None:
    hashed = _hash("ikki", prefix=b"$2a$")
    assert verify_password("ikki", hashed) is True


def test_verify_password_2y() -> None:
    hashed = _hash("ikki", prefix=b"$2y$")
    assert verify_password("ikki", hashed) is True


def test_verify_password_wrong_password() -> None:
    assert verify_password("wrong", _hash("ikki")) is False


def test_verify_password_empty_hash() -> None:
    assert verify_password("ikki", None) is False
    assert verify_password("ikki", "") is False


def test_verify_password_invalid_hash(monkeypatch: pytest.MonkeyPatch) -> None:
    def raise_value_error(*_: object) -> bool:
        raise ValueError("invalid salt")

    monkeypatch.setattr(password_verifier.bcrypt, "checkpw", raise_value_error)
    assert verify_password("ikki", "$2b$12$invalid") is False
