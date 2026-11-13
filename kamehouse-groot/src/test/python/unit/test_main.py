"""Tests for the FastAPI application factory and lifespan."""

from __future__ import annotations

import asyncio

from fastapi import FastAPI

from kamehouse_groot import main
from kamehouse_groot.api.dependencies import ServiceContainer
from kamehouse_groot.config import Settings


class FakeDatabase:
    def __init__(self) -> None:
        self.closed = False

    async def close(self) -> None:
        self.closed = True


class FakeContainer:
    def __init__(self) -> None:
        self.settings = Settings()
        self.database = FakeDatabase()


def test_create_app() -> None:
    app = main.create_app()
    assert isinstance(app, FastAPI)
    assert app.title == "KameHouse GRoot"
    schema_paths = app.openapi()["paths"]
    assert "/kame-house-groot/api/v1/admin/kamehouse-shell/execute" in schema_paths
    route_paths = {getattr(route, "path", None) for route in app.routes}
    assert "/kame-house-groot/docs" in route_paths
    assert isinstance(app.state.container, ServiceContainer)


def test_create_base_app_with_custom_container(tmp_path) -> None:
    container = main.build_container(Settings(deploy_path=tmp_path))
    app = main.create_base_app(
        container=container,
        title="KameHouse Batcave GRoot",
        root_path="/kame-house-batcave-groot",
    )
    assert app.title == "KameHouse Batcave GRoot"
    assert app.state.container is container
    route_paths = {getattr(route, "path", None) for route in app.routes}
    assert "/kame-house-batcave-groot/docs" in route_paths


def test_lifespan_closes_database(monkeypatch) -> None:
    container = FakeContainer()
    monkeypatch.setattr(main, "configure_logging", lambda _: None)

    app = FastAPI()
    app.state.container = container

    async def run() -> None:
        async with main.lifespan(app):
            pass

    asyncio.run(run())
    assert container.database.closed is True
