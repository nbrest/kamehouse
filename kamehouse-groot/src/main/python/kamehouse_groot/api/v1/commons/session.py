"""KameHouse GRoot session status endpoint."""

from __future__ import annotations

import socket
import sys

from fastapi import APIRouter, Depends, Request
from fastapi.responses import JSONResponse
from fastapi.security import HTTPBasicCredentials

from kamehouse_groot.api.dependencies import (
    basic_auth,
    get_auth_service,
    get_docker_environment,
    get_session_service,
)
from kamehouse_groot.models.groot_session_status import GrootSessionStatus
from kamehouse_groot.services.auth_service import AuthService
from kamehouse_groot.services.docker_service import DockerEnvironment
from kamehouse_groot.services.session_service import SessionService

router = APIRouter(prefix="/kame-house-groot/api/v1/commons/session", tags=["commons-session"])

ANONYMOUS_USER = "anonymousUser"


@router.get("/status", response_model=GrootSessionStatus)
async def session_status(
    request: Request,
    credentials: HTTPBasicCredentials | None = Depends(basic_auth),
    auth_service: AuthService = Depends(get_auth_service),
    session_service: SessionService = Depends(get_session_service),
    docker_environment: DockerEnvironment = Depends(get_docker_environment),
) -> JSONResponse:
    """Return the current session status and apply Basic auth to the session."""
    token = request.cookies.get(session_service.cookie_name)
    username = session_service.get_username(token)
    new_token: str | None = None
    clear_cookie = False

    if credentials is not None:
        if await auth_service.verify_credentials(credentials.username, credentials.password):
            if username != credentials.username:
                new_token = session_service.create(credentials.username)
            username = credentials.username
        else:
            session_service.delete(token)
            username = None
            clear_cookie = True

    if username is None:
        username = ANONYMOUS_USER

    roles = [] if username == ANONYMOUS_USER else await auth_service.get_roles(username)
    docker_properties = docker_environment.get_properties()
    status = GrootSessionStatus(
        server=socket.gethostname(),
        username=username,
        isLinuxHost=sys.platform.startswith("linux"),
        isLinuxDockerHost=DockerEnvironment.get_boolean_property(
            docker_properties, "IS_LINUX_DOCKER_HOST"
        ),
        isDockerContainer=DockerEnvironment.get_boolean_property(
            docker_properties, "IS_DOCKER_CONTAINER"
        ),
        dockerControlHost=DockerEnvironment.get_boolean_property(
            docker_properties, "DOCKER_CONTROL_HOST"
        ),
        roles=roles,
    )

    response = JSONResponse(content=status.model_dump())
    if new_token is not None:
        _set_session_cookie(response, session_service, new_token)
    elif clear_cookie:
        response.delete_cookie(session_service.cookie_name, path="/")
    return response


def _set_session_cookie(
    response: JSONResponse,
    session_service: SessionService,
    token: str,
) -> None:
    response.set_cookie(
        session_service.cookie_name,
        token,
        max_age=session_service.cookie_max_age,
        path="/",
        httponly=True,
        samesite="lax",
    )
