"""FastAPI dependencies and service wiring."""

from __future__ import annotations

from dataclasses import dataclass

from fastapi import Depends, Request
from fastapi.security import HTTPBasic, HTTPBasicCredentials

from kamehouse_groot.api.errors import LOGIN_AS_ADMIN_MESSAGE, UnauthorizedException
from kamehouse_groot.config import Settings, get_settings
from kamehouse_groot.database import Database
from kamehouse_groot.services.auth_service import AuthService
from kamehouse_groot.services.command_executor import CommandExecutor
from kamehouse_groot.services.docker_service import DockerEnvironment
from kamehouse_groot.services.secret_service import SecretService
from kamehouse_groot.services.session_service import SessionService
from kamehouse_groot.services.shell_execution_service import ShellExecutionService

basic_auth = HTTPBasic(auto_error=False)


@dataclass
class ServiceContainer:
    """Application services wired to the running FastAPI app."""

    settings: Settings
    secret_service: SecretService
    database: Database
    auth_service: AuthService
    shell_execution_service: ShellExecutionService
    session_service: SessionService
    docker_environment: DockerEnvironment


def build_container(
    settings: Settings | None = None,
    shell_execution_service: ShellExecutionService | None = None,
    session_service: SessionService | None = None,
) -> ServiceContainer:
    """Build the application service container from the given settings.

    The optional services allow another web service built on kamehouse-groot,
    like kamehouse-batcave-groot, to reuse this wiring with its own subclasses.
    """
    settings = settings or get_settings()
    secret_service = SecretService(settings.resolved_secret_script)
    database = Database(settings, secret_service)
    auth_service = AuthService(database)
    executor = CommandExecutor(
        timeout_seconds=settings.command_timeout_seconds,
        daemon_wait_seconds=settings.daemon_wait_seconds,
    )
    if shell_execution_service is None:
        shell_execution_service = ShellExecutionService(settings, executor)
    if session_service is None:
        session_service = SessionService(settings)
    docker_environment = DockerEnvironment(settings.user_home)
    return ServiceContainer(
        settings=settings,
        secret_service=secret_service,
        database=database,
        auth_service=auth_service,
        shell_execution_service=shell_execution_service,
        session_service=session_service,
        docker_environment=docker_environment,
    )


def get_container(request: Request) -> ServiceContainer:
    """Return the service container attached to the running app."""
    return request.app.state.container


def get_auth_service(container: ServiceContainer = Depends(get_container)) -> AuthService:
    """Return the authentication service."""
    return container.auth_service


def get_shell_execution_service(
    container: ServiceContainer = Depends(get_container),
) -> ShellExecutionService:
    """Return the shell execution service."""
    return container.shell_execution_service


def get_session_service(
    container: ServiceContainer = Depends(get_container),
) -> SessionService:
    """Return the login session service."""
    return container.session_service


def get_docker_environment(
    container: ServiceContainer = Depends(get_container),
) -> DockerEnvironment:
    """Return the docker environment service."""
    return container.docker_environment


async def require_admin(
    request: Request,
    credentials: HTTPBasicCredentials | None = Depends(basic_auth),
    auth_service: AuthService = Depends(get_auth_service),
    session_service: SessionService = Depends(get_session_service),
) -> str:
    """Require an admin login session or HTTP Basic credentials for ROLE_KAMISAMA."""
    session_username = session_service.get_username(
        request.cookies.get(session_service.cookie_name)
    )
    if session_username and await auth_service.is_admin(session_username):
        return session_username

    if credentials is None:
        raise UnauthorizedException(LOGIN_AS_ADMIN_MESSAGE)
    if not await auth_service.authenticate(credentials.username, credentials.password):
        raise UnauthorizedException()
    return credentials.username
