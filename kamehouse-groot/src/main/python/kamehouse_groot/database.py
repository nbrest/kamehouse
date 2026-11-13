"""Async MariaDB access for KameHouse GRoot."""

from __future__ import annotations

import logging
from typing import Any
from urllib.parse import quote_plus

from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncEngine, AsyncSession, async_sessionmaker, create_async_engine

from kamehouse_groot.config import Settings
from kamehouse_groot.services.secret_service import SecretService

LOGGER = logging.getLogger(__name__)

USER_QUERY = text(
    """
    SELECT u.id, u.username, u.password
    FROM kamehouse_user u
    WHERE u.username = :username
    """
)

ROLES_QUERY = text(
    """
    SELECT r.name
    FROM kamehouse_role r
    WHERE r.kamehouse_user_id = :user_id
    """
)


class Database:
    """Owns the async SQLAlchemy engine and user/role queries."""

    def __init__(self, settings: Settings, secret_service: SecretService) -> None:
        self._settings = settings
        self._secret_service = secret_service
        self._engine: AsyncEngine | None = None
        self._session_factory: async_sessionmaker[AsyncSession] | None = None

    def _database_url(self) -> str:
        password = self._secret_service.get_secret(self._settings.db_password_secret)
        user = quote_plus(self._settings.db_user)
        encoded_password = quote_plus(password)
        return (
            f"mysql+asyncmy://{user}:{encoded_password}"
            f"@{self._settings.db_host}:{self._settings.db_port}/{self._settings.db_name}"
        )

    def _get_session_factory(self) -> async_sessionmaker[AsyncSession]:
        if self._session_factory is None:
            self._engine = create_async_engine(
                self._database_url(),
                pool_pre_ping=True,
                pool_recycle=3600,
            )
            self._session_factory = async_sessionmaker(self._engine, expire_on_commit=False)
        return self._session_factory

    async def fetch_user_with_roles(self, username: str) -> dict[str, Any] | None:
        """Return the user row with its roles or None if the user doesn't exist."""
        session_factory = self._get_session_factory()
        async with session_factory() as session:
            user_result = await session.execute(USER_QUERY, {"username": username})
            user = user_result.mappings().first()
            if user is None:
                return None
            roles_result = await session.execute(ROLES_QUERY, {"user_id": user["id"]})
            roles = [row["name"] for row in roles_result.mappings().all()]
            return {
                "id": user["id"],
                "username": user["username"],
                "password": user["password"],
                "roles": roles,
            }

    async def close(self) -> None:
        """Dispose the database engine."""
        if self._engine is not None:
            await self._engine.dispose()
            self._engine = None
            self._session_factory = None
