"""Tests for KameHouse authentication."""

from __future__ import annotations

from typing import Any

import pytest

from kamehouse_groot.services import auth_service
from kamehouse_groot.services.auth_service import AuthService


class FakeDatabase:
    """Return a fixed user and record lookups."""

    def __init__(self, user: dict[str, Any] | None = None, error: Exception | None = None) -> None:
        self.user = user
        self.error = error
        self.usernames: list[str] = []

    async def fetch_user_with_roles(self, username: str) -> dict[str, Any] | None:
        self.usernames.append(username)
        if self.error is not None:
            raise self.error
        return self.user


def _user(roles: list[str] | None = None) -> dict[str, Any]:
    return {
        "id": 1,
        "username": "seiya",
        "password": "$2b$12$hash",
        "roles": roles if roles is not None else ["ROLE_KAMISAMA"],
    }


def _allow_password(monkeypatch: pytest.MonkeyPatch, allowed: bool = True) -> None:
    monkeypatch.setattr(auth_service, "verify_password", lambda *_: allowed)


@pytest.mark.asyncio
async def test_authenticates_admin(monkeypatch: pytest.MonkeyPatch) -> None:
    _allow_password(monkeypatch)
    database = FakeDatabase(_user())
    assert await AuthService(database).authenticate("seiya", "ikki") is True
    assert database.usernames == ["seiya", "seiya"]


@pytest.mark.asyncio
async def test_verify_credentials_accepts_non_admin(monkeypatch: pytest.MonkeyPatch) -> None:
    _allow_password(monkeypatch)
    database = FakeDatabase(_user(roles=["ROLE_SAIYAJIN"]))
    assert await AuthService(database).verify_credentials("seiya", "ikki") is True
    assert database.usernames == ["seiya"]


@pytest.mark.asyncio
async def test_rejects_unknown_user(monkeypatch: pytest.MonkeyPatch) -> None:
    _allow_password(monkeypatch)
    assert await AuthService(FakeDatabase(None)).authenticate("seiya", "ikki") is False


@pytest.mark.asyncio
async def test_rejects_wrong_password(monkeypatch: pytest.MonkeyPatch) -> None:
    _allow_password(monkeypatch, allowed=False)
    assert await AuthService(FakeDatabase(_user())).authenticate("seiya", "ikki") is False


@pytest.mark.asyncio
async def test_rejects_non_admin(monkeypatch: pytest.MonkeyPatch) -> None:
    _allow_password(monkeypatch)
    database = FakeDatabase(_user(roles=["ROLE_SAIYAJIN"]))
    assert await AuthService(database).authenticate("seiya", "ikki") is False


@pytest.mark.asyncio
async def test_rejects_empty_credentials() -> None:
    database = FakeDatabase(_user())
    service = AuthService(database)
    assert await service.authenticate("", "ikki") is False
    assert await service.authenticate("seiya", "") is False
    assert await service.verify_credentials("", "ikki") is False
    assert await service.verify_credentials("seiya", "") is False
    assert database.usernames == []


@pytest.mark.asyncio
async def test_rejects_invalid_db_input() -> None:
    database = FakeDatabase(_user())
    service = AuthService(database)
    assert await service.authenticate("sei;ya", "ikki") is False
    assert await service.authenticate("seiya", "ik'ki") is False
    assert await service.verify_credentials("sei;ya", "ikki") is False
    assert await service.verify_credentials("seiya", "ik'ki") is False
    assert database.usernames == []


@pytest.mark.asyncio
async def test_database_errors_return_false(monkeypatch: pytest.MonkeyPatch) -> None:
    _allow_password(monkeypatch)
    database = FakeDatabase(error=RuntimeError("db down"))
    service = AuthService(database)
    assert await service.authenticate("seiya", "ikki") is False
    assert await service.verify_credentials("seiya", "ikki") is False
    assert await service.get_roles("seiya") == []
    assert await service.is_admin("seiya") is False


@pytest.mark.asyncio
async def test_get_roles(monkeypatch: pytest.MonkeyPatch) -> None:
    _allow_password(monkeypatch)
    database = FakeDatabase(_user(roles=["ROLE_SAIYAJIN", "ROLE_KAMISAMA"]))
    service = AuthService(database)
    assert await service.get_roles("seiya") == ["ROLE_SAIYAJIN", "ROLE_KAMISAMA"]
    assert await service.get_roles("") == []
    assert await service.get_roles("sei;ya") == []
    assert await service.is_admin("seiya") is True


@pytest.mark.asyncio
async def test_unknown_user_has_no_roles() -> None:
    service = AuthService(FakeDatabase(None))
    assert await service.get_roles("seiya") == []
    assert await service.is_admin("seiya") is False
