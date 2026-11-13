"""Tests for the legacy API error responses."""

from __future__ import annotations

import asyncio
import json

from kamehouse_groot.api.errors import (
    ApiException,
    BadRequestException,
    ForbiddenException,
    UnauthorizedException,
    api_exception_handler,
    internal_exception_handler,
)


def _body(response) -> dict[str, object]:
    return json.loads(response.body)


def test_api_exception_handler() -> None:
    response = asyncio.run(api_exception_handler(None, UnauthorizedException()))
    assert response.status_code == 401
    assert _body(response) == {"code": 401, "message": "Invalid username and password"}
    assert response.headers["www-authenticate"].startswith("Basic")


def test_bad_request_exception() -> None:
    response = asyncio.run(api_exception_handler(None, BadRequestException()))
    assert response.status_code == 400
    assert _body(response) == {"code": 400, "message": "Invalid input"}


def test_forbidden_exception() -> None:
    response = asyncio.run(api_exception_handler(None, ForbiddenException("nope")))
    assert response.status_code == 403
    assert _body(response) == {"code": 403, "message": "nope"}


def test_internal_exception_handler() -> None:
    response = asyncio.run(internal_exception_handler(None, RuntimeError("boom")))
    assert response.status_code == 500
    assert _body(response) == {"code": 500, "message": "Internal server error"}


def test_custom_api_exception() -> None:
    error = ApiException(418, "teapot")
    assert error.status_code == 418
    assert error.message == "teapot"
    assert error.headers == {}
