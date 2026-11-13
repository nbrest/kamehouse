"""Integration tests for a running KameHouse GRoot service."""

from __future__ import annotations

import os

import httpx
import pytest

pytestmark = [
    pytest.mark.integration,
    pytest.mark.skipif(
        os.getenv("KAMEHOUSE_GROOT_INTEGRATION") != "true",
        reason="Set KAMEHOUSE_GROOT_INTEGRATION=true to run groot integration tests",
    ),
]

BASE_URL = os.getenv("KAMEHOUSE_GROOT_TEST_URL", "http://localhost:9091")
USERNAME = os.getenv("KAMEHOUSE_TEST_USERNAME", "seiya")
PASSWORD = os.getenv("KAMEHOUSE_TEST_PASSWORD", "ikki")
EXECUTE_PATH = "/kame-house-groot/api/v1/admin/kamehouse-shell/execute"


def _client() -> httpx.Client:
    return httpx.Client(base_url=BASE_URL, timeout=30)


def test_execute_requires_authentication() -> None:
    with _client() as client:
        response = client.get(EXECUTE_PATH, params={"script": "is-linux-host.sh"})
    assert response.status_code == 401
    assert response.json()["code"] == 401


def test_execute_rejects_invalid_credentials() -> None:
    with _client() as client:
        response = client.get(
            EXECUTE_PATH,
            params={"script": "is-linux-host.sh"},
            auth=(USERNAME, "invalid"),
        )
    assert response.status_code == 401


def test_execute_is_linux_host() -> None:
    with _client() as client:
        response = client.get(
            EXECUTE_PATH,
            params={"script": "is-linux-host.sh"},
            auth=(USERNAME, PASSWORD),
        )
    assert response.status_code == 200
    body = response.json()
    assert body["status"] == "completed"
    assert body["exitCode"] == 0
    assert body["command"]
    assert body["standardOutputHtml"]


SCRIPTS_PATH = "/kame-house-groot/api/v1/admin/kamehouse-shell/scripts"
SESSION_STATUS_PATH = "/kame-house-groot/api/v1/commons/session/status"
LOGIN_PATH = "/kame-house-groot/api/v1/auth/login"
LOGOUT_PATH = "/kame-house-groot/api/v1/auth/logout"
AUTHORIZE_PAGE_PATH = "/kame-house-groot/api/v1/auth/authorize-page"
TEST_PATH = "/kame-house-groot/api/v1/test/test"


def test_scripts_requires_authentication() -> None:
    with _client() as client:
        response = client.get(SCRIPTS_PATH)
    assert response.status_code == 401
    assert response.json()["code"] == 401


def test_scripts_returns_list() -> None:
    with _client() as client:
        response = client.get(SCRIPTS_PATH, auth=(USERNAME, PASSWORD))
    assert response.status_code == 200
    body = response.json()
    assert isinstance(body, list)
    assert any("is-linux-host.sh" in script for script in body)


def test_session_status_is_anonymous_without_auth() -> None:
    with _client() as client:
        response = client.get(SESSION_STATUS_PATH)
    assert response.status_code == 200
    body = response.json()
    assert body["username"] == "anonymousUser"
    assert body["roles"] == []
    assert "server" in body
    assert "isLinuxHost" in body


def test_basic_auth_session_can_call_scripts() -> None:
    with _client() as client:
        status_response = client.get(SESSION_STATUS_PATH, auth=(USERNAME, PASSWORD))
        assert status_response.status_code == 200
        assert status_response.json()["username"] == USERNAME
        assert any(
            cookie.name == "kamehouse-groot-session" for cookie in client.cookies.jar
        )
        scripts_response = client.get(SCRIPTS_PATH)
    assert scripts_response.status_code == 200


def test_login_logout_flow() -> None:
    with _client() as client:
        login_response = client.post(
            LOGIN_PATH,
            data={"username": USERNAME, "password": PASSWORD},
        )
        assert login_response.status_code == 302
        assert login_response.headers["location"] == "/kame-house/groot/"
        scripts_response = client.get(SCRIPTS_PATH)
        assert scripts_response.status_code == 200
        logout_response = client.get(LOGOUT_PATH)
        assert logout_response.status_code == 302
        assert logout_response.headers["location"].endswith("login.html?logout=true")
        scripts_response = client.get(SCRIPTS_PATH)
    assert scripts_response.status_code == 401


def test_login_rejects_invalid_credentials() -> None:
    with _client() as client:
        response = client.post(
            LOGIN_PATH,
            data={"username": USERNAME, "password": "invalid"},
        )
    assert response.status_code == 302
    assert response.headers["location"].endswith("login.html?error=true")


def test_authorize_page_redirects_without_session() -> None:
    with _client() as client:
        response = client.get(AUTHORIZE_PAGE_PATH)
    assert response.status_code == 302
    assert "unauthorizedPageAccess=true" in response.headers["location"]


def test_authorize_page_allows_session() -> None:
    with _client() as client:
        login_response = client.post(
            LOGIN_PATH,
            data={"username": USERNAME, "password": PASSWORD},
        )
        assert login_response.status_code == 302
        response = client.get(AUTHORIZE_PAGE_PATH)
    assert response.status_code == 200


def test_test_endpoint() -> None:
    with _client() as client:
        response = client.get(TEST_PATH)
    assert response.status_code == 200
    assert "testFunction: mada mada dane" in response.text
    assert "isLinuxHostExample()" in response.text


def test_execute_missing_script_returns_bad_request() -> None:
    with _client() as client:
        response = client.get(EXECUTE_PATH, auth=(USERNAME, PASSWORD))
    assert response.status_code == 400
    assert response.json() == {"code": 400, "message": "Invalid input"}


def test_execute_invalid_input_returns_bad_request() -> None:
    with _client() as client:
        response = client.get(
            EXECUTE_PATH,
            params={"script": "is-linux-host.sh;id"},
            auth=(USERNAME, PASSWORD),
        )
    assert response.status_code == 400
    assert response.json()["code"] == 400


def test_execute_forbidden_script_returns_forbidden() -> None:
    with _client() as client:
        response = client.get(
            EXECUTE_PATH,
            params={"script": "get-kamehouse-secret.sh"},
            auth=(USERNAME, PASSWORD),
        )
    assert response.status_code == 403
    assert response.json()["code"] == 403
    assert response.json()["message"].startswith("Script not allowed")


def test_session_status_returns_roles_with_basic_auth() -> None:
    with _client() as client:
        response = client.get(SESSION_STATUS_PATH, auth=(USERNAME, PASSWORD))
    assert response.status_code == 200
    assert "ROLE_KAMISAMA" in response.json()["roles"]


def test_session_status_invalid_basic_auth_returns_anonymous() -> None:
    with _client() as client:
        login_response = client.post(
            LOGIN_PATH,
            data={"username": USERNAME, "password": PASSWORD},
        )
        assert login_response.status_code == 302
        response = client.get(SESSION_STATUS_PATH, auth=(USERNAME, "invalid"))
        assert response.status_code == 200
        assert response.json()["username"] == "anonymousUser"
        assert response.json()["roles"] == []
        assert not any(
            cookie.name == "kamehouse-groot-session" for cookie in client.cookies.jar
        )


def test_login_with_groot_referrer_redirects_to_referrer() -> None:
    referrer = "/kame-house/groot/admin/shell.html"
    with _client() as client:
        response = client.post(
            LOGIN_PATH,
            data={"username": USERNAME, "password": PASSWORD, "referrer": referrer},
        )
    assert response.status_code == 302
    assert response.headers["location"] == referrer


def test_login_ignores_non_groot_referrer() -> None:
    with _client() as client:
        response = client.post(
            LOGIN_PATH,
            data={
                "username": USERNAME,
                "password": PASSWORD,
                "referrer": "https://example.com/",
            },
        )
    assert response.status_code == 302
    assert response.headers["location"] == "/kame-house/groot/"


def test_authorize_page_redirects_without_session_with_query_string() -> None:
    with _client() as client:
        response = client.get(AUTHORIZE_PAGE_PATH, params={"page": "server"})
    assert response.status_code == 302
    assert "unauthorizedPageAccess=true" in response.headers["location"]
    assert "page=server" in response.headers["location"]
