"""Tests for the async database access."""

from __future__ import annotations

from typing import Any

import pytest

from kamehouse_groot import database
from kamehouse_groot.config import Settings
from kamehouse_groot.database import Database


class FakeSecretService:
    def get_secret(self, _: str) -> str:
        return "p@ss word"


class FakeMappings:
    def __init__(self, rows: list[dict[str, Any]]) -> None:
        self._rows = rows

    def first(self) -> dict[str, Any] | None:
        return self._rows[0] if self._rows else None

    def all(self) -> list[dict[str, Any]]:
        return self._rows


class FakeResult:
    def __init__(self, rows: list[dict[str, Any]]) -> None:
        self._rows = rows

    def mappings(self) -> FakeMappings:
        return FakeMappings(self._rows)


class FakeSession:
    def __init__(self, results: list[FakeResult]) -> None:
        self._results = iter(results)

    async def execute(self, _: object, __: dict[str, Any]) -> FakeResult:
        return next(self._results)

    async def __aenter__(self) -> FakeSession:
        return self

    async def __aexit__(self, *_: object) -> bool:
        return False


class FakeSessionFactory:
    def __init__(self, results: list[FakeResult]) -> None:
        self._results = results

    def __call__(self) -> FakeSession:
        return FakeSession(self._results)


class FakeEngine:
    def __init__(self) -> None:
        self.disposed = False

    async def dispose(self) -> None:
        self.disposed = True


def _database() -> Database:
    settings = Settings(db_user="kamehouse", db_password_secret="SECRET")
    return Database(settings, FakeSecretService())


def test_database_url_encodes_password() -> None:
    url = _database()._database_url()
    assert url == "mysql+asyncmy://kamehouse:p%40ss+word@127.0.0.1:3306/kamehouse"


@pytest.mark.asyncio
async def test_fetch_user_with_roles() -> None:
    db = _database()
    db._session_factory = FakeSessionFactory(
        [
            FakeResult([{"id": 7, "username": "seiya", "password": "$2a$hash"}]),
            FakeResult([{"name": "ROLE_KAMISAMA"}, {"name": "ROLE_SAIYAJIN"}]),
        ]
    )
    user = await db.fetch_user_with_roles("seiya")
    assert user == {
        "id": 7,
        "username": "seiya",
        "password": "$2a$hash",
        "roles": ["ROLE_KAMISAMA", "ROLE_SAIYAJIN"],
    }


@pytest.mark.asyncio
async def test_fetch_unknown_user() -> None:
    db = _database()
    db._session_factory = FakeSessionFactory([FakeResult([])])
    assert await db.fetch_user_with_roles("unknown") is None


@pytest.mark.asyncio
async def test_close_disposes_engine() -> None:
    db = _database()
    engine = FakeEngine()
    db._engine = engine  # type: ignore[assignment]
    await db.close()
    assert engine.disposed is True
    assert db._engine is None
    assert db._session_factory is None


@pytest.mark.asyncio
async def test_close_without_engine() -> None:
    await _database().close()


def test_get_session_factory_creates_engine(monkeypatch: pytest.MonkeyPatch) -> None:
    engine = FakeEngine()
    monkeypatch.setattr(database, "create_async_engine", lambda *_a, **_k: engine)
    db = _database()
    factory = db._get_session_factory()
    assert db._engine is engine
    assert factory is db._session_factory
    assert db._get_session_factory() is factory
