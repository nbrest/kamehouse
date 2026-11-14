"""FastAPI application for the KameHouse GRoot web service."""

from __future__ import annotations

from collections.abc import AsyncIterator
from contextlib import asynccontextmanager

from fastapi import FastAPI

from kamehouse_groot.api.dependencies import ServiceContainer, build_container
from kamehouse_groot.api.errors import ApiException, api_exception_handler, internal_exception_handler
from kamehouse_groot.api.v1.router import api_router
from kamehouse_groot.config import Settings, get_settings
from kamehouse_groot.logging_config import configure_logging


@asynccontextmanager
async def lifespan(app: FastAPI) -> AsyncIterator[None]:
    """Configure logging and release the database engine on shutdown."""
    configure_logging(app.state.container.settings)
    try:
        yield
    finally:
        await app.state.container.database.close()


def create_base_app(
    settings: Settings | None = None,
    container: ServiceContainer | None = None,
    title: str = "KameHouse GRoot",
    root_path: str = "/kame-house-groot",
    version: str = "0.0.0",
) -> FastAPI:
    """Create a FastAPI application with the shared service wiring.

    Other web services built on kamehouse-groot, 
    reuse this factory with their own settings and container to share the
    exception handlers, lifespan and app state wiring.
    """
    if container is None:
        container = build_container(settings or get_settings())
    app = FastAPI(
        title=title,
        version=version,
        lifespan=lifespan,
        docs_url=f"{root_path}/docs",
        redoc_url=f"{root_path}/redoc",
        openapi_url=f"{root_path}/openapi.json",
    )
    app.state.container = container
    app.add_exception_handler(ApiException, api_exception_handler)
    app.add_exception_handler(Exception, internal_exception_handler)
    return app


def create_app() -> FastAPI:
    """Create the KameHouse GRoot FastAPI application."""
    app = create_base_app()
    app.include_router(api_router)
    return app


app = create_app()
