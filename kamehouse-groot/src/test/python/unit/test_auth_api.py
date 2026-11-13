"""Tests for the login, logout and page authorization endpoints."""

from __future__ import annotations

from fastapi import FastAPI
from fastapi.testclient import TestClient

from kamehouse_groot.api.dependencies import get_auth_service, get_session_service
from kamehouse_groot.api.errors import ApiException, api_exception_handler
from kamehouse_groot.api.v1 import auth

LOGIN_PATH = "/kame-house-groot/api/v1/auth/login"
LOGOUT_PATH = "/kame-house-groot/api/v1/auth/logout"
AUTHORIZE_PATH = "/kame-house-groot/api/v1/auth/authorize-page"
LOGIN_PAGE = "/kame-house/groot/login.html"
COOKIE_NAME = "kamehouse-groot-session"


class FakeAuthService:
    def __init__(self, valid: bool = True, admin: bool = True) -> None:
        self.valid = valid
        self.admin = admin

    async def verify_credentials(self, _: str, __: str) -> bool:
        return self.valid

    async def is_admin(self, _: str) -> bool:
        return self.admin


class FakeSessionService:
    cookie_name = COOKIE_NAME

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


def _client(
    auth_service: FakeAuthService | None = None,
    session_service: FakeSessionService | None = None,
) -> tuple[TestClient, FakeAuthService, FakeSessionService]:
    auth_service = auth_service or FakeAuthService()
    session_service = session_service or FakeSessionService()
    app = FastAPI()
    app.add_exception_handler(ApiException, api_exception_handler)
    app.include_router(auth.router)
    app.dependency_overrides[get_auth_service] = lambda: auth_service
    app.dependency_overrides[get_session_service] = lambda: session_service
    return TestClient(app), auth_service, session_service


def test_login_creates_session_and_redirects_home() -> None:
    client, _, session = _client()
    response = client.post(
        LOGIN_PATH,
        data={"username": "seiya", "password": "ikki"},
        follow_redirects=False,
    )
    assert response.status_code == 302
    assert response.headers["location"] == "/kame-house/groot/"
    assert session.created == ["seiya"]
    assert f"{COOKIE_NAME}=new-token" in response.headers["set-cookie"]


def test_login_redirects_to_groot_referrer() -> None:
    client, _, _ = _client()
    response = client.post(
        LOGIN_PATH,
        data={
            "username": "seiya",
            "password": "ikki",
            "referrer": "/kame-house/groot/admin/kamehouse-shell/",
        },
        follow_redirects=False,
    )
    assert response.headers["location"] == "/kame-house/groot/admin/kamehouse-shell/"


def test_login_ignores_external_referrer() -> None:
    client, _, _ = _client()
    response = client.post(
        LOGIN_PATH,
        data={
            "username": "seiya",
            "password": "ikki",
            "referrer": "https://example.com/",
        },
        follow_redirects=False,
    )
    assert response.headers["location"] == "/kame-house/groot/"


def test_login_rejects_missing_credentials() -> None:
    client, _, session = _client()
    response = client.post(LOGIN_PATH, data={"username": "seiya"}, follow_redirects=False)
    assert response.status_code == 302
    assert response.headers["location"] == f"{LOGIN_PAGE}?error=true"
    assert session.created == []


def test_login_rejects_invalid_credentials() -> None:
    client, _, session = _client(FakeAuthService(valid=False))
    response = client.post(
        LOGIN_PATH,
        data={"username": "seiya", "password": "wrong"},
        follow_redirects=False,
    )
    assert response.headers["location"] == f"{LOGIN_PAGE}?error=true"
    assert session.created == []


def test_get_login_redirects_to_error_page() -> None:
    client, _, _ = _client()
    response = client.get(LOGIN_PATH, follow_redirects=False)
    assert response.headers["location"] == f"{LOGIN_PAGE}?error=true"


def test_logout_deletes_session() -> None:
    client, _, session = _client(session_service=FakeSessionService("seiya"))
    client.cookies.set(COOKIE_NAME, "token")
    response = client.get(LOGOUT_PATH, follow_redirects=False)
    assert response.status_code == 302
    assert response.headers["location"] == f"{LOGIN_PAGE}?logout=true"
    assert session.deleted == ["token"]
    assert f'{COOKIE_NAME}=""' in response.headers["set-cookie"]


def test_authorize_page_allows_admin_session() -> None:
    client, _, _ = _client(session_service=FakeSessionService("seiya"))
    client.cookies.set(COOKIE_NAME, "token")
    response = client.get(AUTHORIZE_PATH)
    assert response.status_code == 200
    assert response.text == ""


def test_authorize_page_redirects_without_session() -> None:
    client, _, _ = _client()
    response = client.get(AUTHORIZE_PATH, follow_redirects=False)
    assert response.status_code == 302
    assert response.headers["location"] == (
        f"{LOGIN_PAGE}?unauthorizedPageAccess=true&referrer={AUTHORIZE_PATH}"
    )


def test_authorize_page_redirects_non_admin() -> None:
    client, _, _ = _client(
        auth_service=FakeAuthService(admin=False),
        session_service=FakeSessionService("shiryu"),
    )
    client.cookies.set(COOKIE_NAME, "token")
    response = client.get(AUTHORIZE_PATH, follow_redirects=False)
    assert response.status_code == 302
    assert "unauthorizedPageAccess=true" in response.headers["location"]


def test_authorize_page_includes_query_string_in_referrer() -> None:
    client, _, _ = _client()
    response = client.get(
        AUTHORIZE_PATH,
        params={"page": "server"},
        follow_redirects=False,
    )
    assert response.headers["location"] == (
        f"{LOGIN_PAGE}?unauthorizedPageAccess=true"
        f"&referrer={AUTHORIZE_PATH}?page=server"
    )
