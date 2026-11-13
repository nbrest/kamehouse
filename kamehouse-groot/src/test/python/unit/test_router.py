"""Tests for the API v1 router."""

from __future__ import annotations

from fastapi import FastAPI

from kamehouse_groot.api.v1.router import api_router


def test_router_includes_all_clean_routes() -> None:
    app = FastAPI()
    app.include_router(api_router)
    paths = app.openapi()["paths"]
    assert "/kame-house-groot/api/v1/admin/kamehouse-shell/execute" in paths
    assert "/kame-house-groot/api/v1/admin/kamehouse-shell/scripts" in paths
    assert "/kame-house-groot/api/v1/auth/login" in paths
    assert "/kame-house-groot/api/v1/auth/logout" in paths
    assert "/kame-house-groot/api/v1/auth/authorize-page" in paths
    assert "/kame-house-groot/api/v1/commons/session/status" in paths
    assert "/kame-house-groot/api/v1/test/test" in paths


def test_router_routes_have_no_file_extension() -> None:
    app = FastAPI()
    app.include_router(api_router)
    paths = app.openapi()["paths"]
    for path in paths:
        assert "." not in path.rsplit("/", 1)[-1]
