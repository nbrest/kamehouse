"""Authentication against the KameHouse MariaDB users."""

from __future__ import annotations

import logging

from kamehouse_groot.database import Database
from kamehouse_groot.services.password_verifier import verify_password
from kamehouse_groot.utils.input_validator import is_valid_input_for_db_access

LOGGER = logging.getLogger(__name__)

ADMIN_ROLE = "ROLE_KAMISAMA"


class AuthService:
    """Validate KameHouse credentials, roles and admin membership."""

    def __init__(self, database: Database) -> None:
        self._database = database

    async def verify_credentials(self, username: str, password: str) -> bool:
        """Return True if the username and password belong to a KameHouse user."""
        if not username or not password:
            return False
        if not is_valid_input_for_db_access(username):
            return False
        if not is_valid_input_for_db_access(password):
            return False

        try:
            user = await self._database.fetch_user_with_roles(username)
        except Exception:
            LOGGER.exception("Database error verifying credentials for user %s", username)
            return False

        if user is None:
            LOGGER.info("Authentication failed for unknown user")
            return False

        if not verify_password(password, user["password"]):
            LOGGER.info("Authentication failed for user %s", username)
            return False

        return True

    async def get_roles(self, username: str) -> list[str]:
        """Return the KameHouse roles for the specified user."""
        if not username or not is_valid_input_for_db_access(username):
            return []

        try:
            user = await self._database.fetch_user_with_roles(username)
        except Exception:
            LOGGER.exception("Database error getting roles for user %s", username)
            return []

        if user is None:
            return []
        return list(user["roles"])

    async def is_admin(self, username: str) -> bool:
        """Return True if the user has the ROLE_KAMISAMA role."""
        return ADMIN_ROLE in await self.get_roles(username)

    async def authenticate(self, username: str, password: str) -> bool:
        """Return True if the credentials belong to a KAMISAMA user."""
        if not await self.verify_credentials(username, password):
            return False

        if not await self.is_admin(username):
            LOGGER.info("User %s is not a %s", username, ADMIN_ROLE)
            return False

        LOGGER.info("Authenticated admin user %s", username)
        return True
