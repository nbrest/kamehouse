"""Tests for service wiring and authentication dependencies."""

from __future__ import annotations

import asyncio
from pathlib import Path
from types import SimpleNamespace

import pytest
from fastapi import Request
from fastapi.security import HTTPBasicCredentials
from starlette.applications import Starlette

from kamehouse_groot.api import dependencies
from kamehouse_groot.api.errors import UnauthorizedException
from kamehouse_groot.config import Settings
from kamehouse_groot.services.command_executor import CommandExecutor
from kamehouse_groot.services.session_service import SessionService
from kamehouse_groot.services.shell_execution_service import ShellExecutionService


class FakeAuthService:
    def __init__(self, allowed: bool, admin: bool = True) -> None:
        self.allowed = allowed
        self.admin = admin

    async def authenticate(self, _: str, __: str) -> bool:
        return self.allowed

    async def is_admin(self, _: str) -> bool:
        return self.admin


class FakeSessionService:
    cookie_name = "kamehouse-groot-session"

    def __init__(self, username: str | None = None) -> None:
        self.username = username

    def get_username(self, _: str | None) -> str | None:
        return self.username


def _request() -> SimpleNamespace:
    return SimpleNamespace(cookies={})


def test_build_container(monkeypatch: pytest.MonkeyPatch, tmp_path: Path) -> None:
    monkeypatch.setattr(dependencies, "get_settings", lambda: Settings(deploy_path=tmp_path))
    container = dependencies.build_container()
    assert container.settings.deploy_path == tmp_path
    assert container.auth_service is not None
    assert isinstance(container.shell_execution_service, ShellExecutionService)
    assert isinstance(container.session_service, SessionService)
    assert container.docker_environment is not None


def test_build_container_uses_custom_services(tmp_path: Path) -> None:
    settings = Settings(deploy_path=tmp_path)
    shell_service = ShellExecutionService(settings, CommandExecutor())
    session_service = SessionService(settings, cache_sessions=False)
    container = dependencies.build_container(
        settings,
        shell_execution_service=shell_service,
        session_service=session_service,
    )
    assert container.settings is settings
    assert container.shell_execution_service is shell_service
    assert container.session_service is session_service


def test_get_container_reads_app_state(tmp_path: Path) -> None:
    container = dependencies.build_container(Settings(deploy_path=tmp_path))
    app = Starlette()
    app.state.container = container
    request = Request(scope={"type": "http", "app": app})
    assert dependencies.get_container(request) is container


def test_service_accessors(tmp_path: Path) -> None:
    container = dependencies.build_container(Settings(deploy_path=tmp_path))
    assert dependencies.get_auth_service(container) is container.auth_service
    assert (
        dependencies.get_shell_execution_service(container)
        is container.shell_execution_service
    )
    assert dependencies.get_session_service(container) is container.session_service
    assert dependencies.get_docker_environment(container) is container.docker_environment


def test_require_admin_without_credentials() -> None:
    with pytest.raises(UnauthorizedException) as error:
        asyncio.run(
            dependencies.require_admin(
                _request(),
                None,
                FakeAuthService(True),
                FakeSessionService(),
            )
        )
    assert error.value.status_code == 401


def test_require_admin_with_valid_session() -> None:
    result = asyncio.run(
        dependencies.require_admin(
            _request(),
            None,
            FakeAuthService(False),
            FakeSessionService("seiya"),
        )
    )
    assert result == "seiya"


def test_require_admin_with_non_admin_session_and_no_credentials() -> None:
    with pytest.raises(UnauthorizedException):
        asyncio.run(
            dependencies.require_admin(
                _request(),
                None,
                FakeAuthService(True, admin=False),
                FakeSessionService("seiya"),
            )
        )


def test_require_admin_with_valid_credentials() -> None:
    credentials = HTTPBasicCredentials(username="seiya", password="ikki")
    result = asyncio.run(
        dependencies.require_admin(
            _request(),
            credentials,
            FakeAuthService(True),
            FakeSessionService(),
        )
    )
    assert result == "seiya"


def test_require_admin_with_non_admin_session_and_valid_credentials() -> None:
    credentials = HTTPBasicCredentials(username="seiya", password="ikki")
    result = asyncio.run(
        dependencies.require_admin(
            _request(),
            credentials,
            FakeAuthService(True, admin=False),
            FakeSessionService("shiryu"),
        )
    )
    assert result == "seiya"


def test_require_admin_with_invalid_credentials() -> None:
    credentials = HTTPBasicCredentials(username="seiya", password="wrong")
    with pytest.raises(UnauthorizedException):
        asyncio.run(
            dependencies.require_admin(
                _request(),
                credentials,
                FakeAuthService(False),
                FakeSessionService(),
            )
        )
