"""Tests for the admin kamehouse-shell API endpoint."""

from __future__ import annotations

import inspect
from typing import Any

from fastapi import FastAPI
from fastapi.testclient import TestClient

from kamehouse_groot.api.dependencies import (
    get_auth_service,
    get_session_service,
    get_shell_execution_service,
)
from kamehouse_groot.api.errors import (
    INVALID_CREDENTIALS_MESSAGE,
    LOGIN_AS_ADMIN_MESSAGE,
    ApiException,
    api_exception_handler,
    internal_exception_handler,
)
from kamehouse_groot.api.v1.admin import kamehouse_shell
from kamehouse_groot.models.kamehouse_command_result import KamehouseCommandResult
from kamehouse_groot.services.exceptions import ForbiddenScriptError

EXECUTE_PATH = "/kame-house-groot/api/v1/admin/kamehouse-shell/execute"
SCRIPTS_PATH = "/kame-house-groot/api/v1/admin/kamehouse-shell/scripts"
VALID_AUTH = ("seiya", "ikki")


class FakeAuthService:
    """Authenticate the default integration test user."""

    async def authenticate(self, username: str, password: str) -> bool:
        return (username, password) == VALID_AUTH

    async def is_admin(self, username: str) -> bool:
        return username == VALID_AUTH[0]


class FakeShellService:
    """Record endpoint arguments and return a completed result."""

    def __init__(self) -> None:
        self.calls: list[dict[str, Any]] = []
        self.scripts = ["is-linux-host.sh", "tomcat/tomcat-status.sh"]

    def execute(
        self,
        script: str,
        script_args: str | None = None,
        execute_on_docker_host: bool = False,
        daemon: bool = False,
    ) -> KamehouseCommandResult:
        self.calls.append(
            {
                "script": script,
                "script_args": script_args,
                "execute_on_docker_host": execute_on_docker_host,
                "daemon": daemon,
            }
        )
        if script == "forbidden.sh":
            raise ForbiddenScriptError("Script not allowed for execution")
        return KamehouseCommandResult(
            command=f"exec-script.sh -s {script}",
            exitCode=0,
            status="completed",
            standardOutput=["ok"],
            standardOutputHtml=["ok"],
        )

    def get_scripts(self) -> list[str]:
        return self.scripts


class FakeSessionService:
    """Return no login sessions."""

    cookie_name = "kamehouse-groot-session"

    def get_username(self, _: str | None) -> str | None:
        return None


def _client(shell: FakeShellService | None = None) -> tuple[TestClient, FakeShellService]:
    shell_service = shell or FakeShellService()
    app = FastAPI()
    app.add_exception_handler(ApiException, api_exception_handler)
    app.add_exception_handler(Exception, internal_exception_handler)
    app.include_router(kamehouse_shell.router)
    app.dependency_overrides[get_auth_service] = lambda: FakeAuthService()
    app.dependency_overrides[get_session_service] = lambda: FakeSessionService()
    app.dependency_overrides[get_shell_execution_service] = lambda: shell_service
    return TestClient(app), shell_service


def test_requires_authentication() -> None:
    client, _ = _client()
    response = client.get(EXECUTE_PATH, params={"script": "is-linux-host.sh"})
    assert response.status_code == 401
    assert response.json() == {"code": 401, "message": LOGIN_AS_ADMIN_MESSAGE}
    assert "www-authenticate" in response.headers


def test_rejects_invalid_credentials() -> None:
    client, _ = _client()
    response = client.get(
        EXECUTE_PATH,
        params={"script": "is-linux-host.sh"},
        auth=("seiya", "wrong"),
    )
    assert response.status_code == 401
    assert response.json() == {"code": 401, "message": INVALID_CREDENTIALS_MESSAGE}


def test_requires_script_parameter() -> None:
    client, _ = _client()
    response = client.get(EXECUTE_PATH, auth=VALID_AUTH)
    assert response.status_code == 400
    assert response.json() == {"code": 400, "message": "Invalid input"}


def test_rejects_invalid_script_input() -> None:
    client, _ = _client()
    response = client.get(EXECUTE_PATH, params={"script": "is-linux-host.sh;rm"}, auth=VALID_AUTH)
    assert response.status_code == 400


def test_rejects_invalid_args_input() -> None:
    client, _ = _client()
    response = client.get(
        EXECUTE_PATH,
        params={"script": "is-linux-host.sh", "args": "| rm"},
        auth=VALID_AUTH,
    )
    assert response.status_code == 400


def test_forbidden_script() -> None:
    client, _ = _client()
    response = client.get(EXECUTE_PATH, params={"script": "forbidden.sh"}, auth=VALID_AUTH)
    assert response.status_code == 403
    assert response.json() == {"code": 403, "message": "Script not allowed for execution"}


def test_executes_script() -> None:
    client, shell = _client()
    response = client.get(
        EXECUTE_PATH,
        params={"script": "is-linux-host.sh", "args": "-v"},
        auth=VALID_AUTH,
    )
    assert response.status_code == 200
    assert response.json()["status"] == "completed"
    assert response.json()["exitCode"] == 0
    assert shell.calls == [
        {
            "script": "is-linux-host.sh",
            "script_args": "-v",
            "execute_on_docker_host": False,
            "daemon": False,
        }
    ]


def test_parses_camel_case_boolean_flags() -> None:
    client, shell = _client()
    response = client.get(
        EXECUTE_PATH,
        params={
            "script": "is-linux-host.sh",
            "executeOnDockerHost": "true",
            "daemon": "TRUE",
        },
        auth=VALID_AUTH,
    )
    assert response.status_code == 200
    assert shell.calls[0]["execute_on_docker_host"] is True
    assert shell.calls[0]["daemon"] is True


def test_parses_is_daemon_flag() -> None:
    client, shell = _client()
    response = client.get(
        EXECUTE_PATH,
        params={"script": "is-linux-host.sh", "isDaemon": "true"},
        auth=VALID_AUTH,
    )
    assert response.status_code == 200
    assert shell.calls[0]["daemon"] is True


def _router_endpoints() -> dict[str, Any]:
    return {route.path: route.endpoint for route in kamehouse_shell.router.routes}


def test_shell_endpoints_run_blocking_commands_off_the_event_loop() -> None:
    endpoints = _router_endpoints()
    execute = next(fn for path, fn in endpoints.items() if path.endswith("/execute"))
    scripts = next(fn for path, fn in endpoints.items() if path.endswith("/scripts"))
    assert not inspect.iscoroutinefunction(execute)
    assert not inspect.iscoroutinefunction(scripts)


def test_scripts_requires_authentication() -> None:
    client, _ = _client()
    response = client.get(SCRIPTS_PATH)
    assert response.status_code == 401
    assert response.json() == {"code": 401, "message": LOGIN_AS_ADMIN_MESSAGE}


def test_scripts_returns_list() -> None:
    client, shell = _client()
    response = client.get(SCRIPTS_PATH, auth=VALID_AUTH)
    assert response.status_code == 200
    assert response.json() == shell.scripts
