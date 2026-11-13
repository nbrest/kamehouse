"""API errors returned with the legacy groot JSON contract."""

from __future__ import annotations

from fastapi import Request
from fastapi.responses import JSONResponse

LOGIN_AS_ADMIN_MESSAGE = "Login as admin to /kame-house/groot/ to access this endpoint"
INVALID_CREDENTIALS_MESSAGE = "Invalid username and password"
INVALID_INPUT_MESSAGE = "Invalid input"
INTERNAL_ERROR_MESSAGE = "Internal server error"


class ApiException(Exception):
    """Exception rendered as {"code": <int>, "message": "<text>"}."""

    def __init__(self, status_code: int, message: str, headers: dict[str, str] | None = None) -> None:
        super().__init__(message)
        self.status_code = status_code
        self.message = message
        self.headers = headers or {}


class UnauthorizedException(ApiException):
    """401 error with an optional Basic authentication challenge."""

    def __init__(self, message: str = INVALID_CREDENTIALS_MESSAGE) -> None:
        headers = {"WWW-Authenticate": 'Basic realm="kamehouse-groot"'}
        super().__init__(401, message, headers)


class BadRequestException(ApiException):
    """400 error for invalid endpoint input."""

    def __init__(self, message: str = INVALID_INPUT_MESSAGE) -> None:
        super().__init__(400, message)


class ForbiddenException(ApiException):
    """403 error for forbidden operations."""

    def __init__(self, message: str) -> None:
        super().__init__(403, message)


async def api_exception_handler(_: Request, exc: ApiException) -> JSONResponse:
    """Render ApiException with the legacy JSON shape."""
    return JSONResponse(
        status_code=exc.status_code,
        content={"code": exc.status_code, "message": exc.message},
        headers=exc.headers,
    )


async def internal_exception_handler(_: Request, exc: Exception) -> JSONResponse:
    """Render unexpected exceptions without leaking internals."""
    import logging

    logging.getLogger(__name__).exception("Unhandled exception", exc_info=exc)
    return JSONResponse(
        status_code=500,
        content={"code": 500, "message": INTERNAL_ERROR_MESSAGE},
    )
