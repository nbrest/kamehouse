"""Tests for the session status endpoint."""

from __future__ import annotations

import socket

from fastapi import FastAPI
from fastapi.testclient import TestClient

from kamehouse_groot.api.dependencies import (
    get_auth_service,
    get_docker_environment,
    get_session_service,
)
from kamehouse_groot.api.errors import ApiException, api_exception_handler
from kamehouse_groot.api.v1.commons import session

STATUS_PATH = "/kame-house-groot/api/v1/commons/session/status"
COOKIE_NAME = "kamehouse-groot-session"


class FakeAuthService:
    def __init__(self, valid: bool = True, roles: list[str] | None = None) -> None:
        self.valid = valid
        self.roles = roles if roles is not None else ["ROLE_KAMISAMA"]

    async def verify_credentials(self, _: str, __: str) -> bool:
        return self.valid

    async def get_roles(self, _: str) -> list[str]:
        return self.roles


class FakeSessionService:
    cookie_name = COOKIE_NAME
    cookie_max_age = 2629746

    def __init__(self, username: str | None = None) -> None:
        self.username = username
        self.created: list[str] = []
        self.deleted: list[str | None] = []

    def create(self, username: str) -> str:
        self.created.append(username)
        self.username = username
        return "new-token"

    def get_username(self, token: str | None) -> str | None:
        if not token:
            return None
        return self.username

    def delete(self, token: str | None) -> None:
        self.deleted.append(token)
        self.username = None


class FakeDockerEnvironment:
    def __init__(self, properties: dict[str, str] | None = None) -> None:
        self.properties = properties or {}

    def get_properties(self) -> dict[str, str]:
        return self.properties


def _client(
    auth_service: FakeAuthService | None = None,
    session_service: FakeSessionService | None = None,
    docker_environment: FakeDockerEnvironment | None = None,
) -> tuple[TestClient, FakeAuthService, FakeSessionService, FakeDockerEnvironment]:
    auth_service = auth_service or FakeAuthService()
    session_service = session_service or FakeSessionService()
    docker_environment = docker_environment or FakeDockerEnvironment()
    app = FastAPI()
    app.add_exception_handler(ApiException, api_exception_handler)
    app.include_router(session.router)
    app.dependency_overrides[get_auth_service] = lambda: auth_service
    app.dependency_overrides[get_session_service] = lambda: session_service
    app.dependency_overrides[get_docker_environment] = lambda: docker_environment
    return TestClient(app), auth_service, session_service, docker_environment


def test_anonymous_status(monkeypatch) -> None:
    monkeypatch.setattr(socket, "gethostname", lambda: "kamehouse-host")
    client, _, _, _ = _client()
    response = client.get(STATUS_PATH)
    assert response.status_code == 200
    assert response.json() == {
        "server": "kamehouse-host",
        "username": "anonymousUser",
        "isLinuxHost": True,
        "isLinuxDockerHost": False,
        "isDockerContainer": False,
        "dockerControlHost": False,
        "roles": [],
    }


def test_status_with_session_cookie() -> None:
    client, _, _, _ = _client(session_service=FakeSessionService("seiya"))
    client.cookies.set(COOKIE_NAME, "token")
    response = client.get(STATUS_PATH)
    assert response.json()["username"] == "seiya"
    assert response.json()["roles"] == ["ROLE_KAMISAMA"]


def test_basic_auth_creates_session() -> None:
    client, _, session_service, _ = _client()
    response = client.get(STATUS_PATH, auth=("seiya", "ikki"))
    assert response.status_code == 200
    assert response.json()["username"] == "seiya"
    assert session_service.created == ["seiya"]
    assert f"{COOKIE_NAME}=new-token" in response.headers["set-cookie"]
    assert "Max-Age=2629746" in response.headers["set-cookie"]


def test_basic_auth_reuses_matching_session() -> None:
    client, _, session_service, _ = _client(session_service=FakeSessionService("seiya"))
    client.cookies.set(COOKIE_NAME, "token")
    response = client.get(STATUS_PATH, auth=("seiya", "ikki"))
    assert response.json()["username"] == "seiya"
    assert session_service.created == []
    assert "set-cookie" not in response.headers


def test_basic_auth_replaces_different_session() -> None:
    client, _, session_service, _ = _client(session_service=FakeSessionService("shiryu"))
    client.cookies.set(COOKIE_NAME, "token")
    response = client.get(STATUS_PATH, auth=("seiya", "ikki"))
    assert response.json()["username"] == "seiya"
    assert session_service.created == ["seiya"]
    assert f"{COOKIE_NAME}=new-token" in response.headers["set-cookie"]


def test_invalid_basic_auth_clears_session() -> None:
    client, _, session_service, _ = _client(
        auth_service=FakeAuthService(valid=False),
        session_service=FakeSessionService("seiya"),
    )
    client.cookies.set(COOKIE_NAME, "token")
    response = client.get(STATUS_PATH, auth=("seiya", "wrong"))
    assert response.json()["username"] == "anonymousUser"
    assert response.json()["roles"] == []
    assert session_service.deleted == ["token"]
    assert f'{COOKIE_NAME}=""' in response.headers["set-cookie"]


def test_docker_properties() -> None:
    docker_environment = FakeDockerEnvironment(
        {
            "IS_LINUX_DOCKER_HOST": "true",
            "IS_DOCKER_CONTAINER": "TRUE",
            "DOCKER_CONTROL_HOST": "true",
        }
    )
    client, _, _, _ = _client(docker_environment=docker_environment)
    response = client.get(STATUS_PATH)
    body = response.json()
    assert body["isLinuxDockerHost"] is True
    assert body["isDockerContainer"] is True
    assert body["dockerControlHost"] is True
