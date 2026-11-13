"""bcrypt password verification."""

from __future__ import annotations

import logging

import bcrypt

LOGGER = logging.getLogger(__name__)

BCRYPT_2Y_PREFIX = "$2y$"
PYTHON_2B_PREFIX = "$2b$"


def normalize_bcrypt_hash(password_hash: str) -> str:
    """Convert bcrypt prefixes unsupported by Python bcrypt to $2b$."""
    if password_hash.startswith(BCRYPT_2Y_PREFIX):
        return PYTHON_2B_PREFIX + password_hash[len(BCRYPT_2Y_PREFIX) :]
    return password_hash


def verify_password(password: str, password_hash: str | None) -> bool:
    """Return True if the password matches the stored bcrypt hash."""
    if not password_hash:
        return False

    normalized_hash = normalize_bcrypt_hash(password_hash)
    try:
        return bcrypt.checkpw(password.encode("utf-8"), normalized_hash.encode("utf-8"))
    except (ValueError, TypeError) as exc:
        LOGGER.warning("Invalid password hash format: %s", exc)
        return False
