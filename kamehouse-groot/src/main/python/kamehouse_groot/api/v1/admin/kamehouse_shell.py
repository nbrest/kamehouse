"""KameHouse shell admin endpoints."""

from __future__ import annotations

from fastapi import APIRouter, Depends, Query

from kamehouse_groot.api.dependencies import get_shell_execution_service, require_admin
from kamehouse_groot.api.errors import BadRequestException, ForbiddenException
from kamehouse_groot.models.kamehouse_command_result import KamehouseCommandResult
from kamehouse_groot.services.exceptions import ForbiddenScriptError
from kamehouse_groot.services.shell_execution_service import ShellExecutionService
from kamehouse_groot.utils.input_validator import is_valid_input_for_shell, parse_boolean


def build_admin_shell_router(prefix: str, tags: list[str]) -> APIRouter:
    """Build the admin shell endpoints router for the given base path.

    Other web services built on kamehouse-groot, 
    reuse this factory with their own prefix to expose the same endpoints
    backed by their own shell execution service.
    """
    router = APIRouter(prefix=prefix, tags=tags)

    @router.get(
        "/execute",
        response_model=KamehouseCommandResult,
        summary="Execute a shell script",
    )
    def execute_script(
        script: str | None = Query(default=None, description="Shell script path"),
        args: str | None = Query(default=None, description="Script arguments"),
        execute_on_docker_host: str | None = Query(
            default=None,
            alias="executeOnDockerHost",
            description="Execute the script on the docker host",
        ),
        daemon: str | None = Query(default=None, description="Start the script as a daemon"),
        is_daemon: str | None = Query(
            default=None,
            alias="isDaemon",
            description="Legacy alias for the daemon flag",
        ),
        _: str = Depends(require_admin),
        shell_service: ShellExecutionService = Depends(get_shell_execution_service),
    ) -> KamehouseCommandResult:
        """Execute a shell script as an admin user."""
        if not script or not is_valid_input_for_shell(script) or not is_valid_input_for_shell(args):
            raise BadRequestException()

        try:
            return shell_service.execute(
                script=script,
                script_args=args,
                execute_on_docker_host=parse_boolean(execute_on_docker_host),
                daemon=parse_boolean(daemon) or parse_boolean(is_daemon),
            )
        except ForbiddenScriptError as exc:
            raise ForbiddenException(str(exc)) from exc

    @router.get(
        "/scripts",
        response_model=list[str],
        summary="List the shell scripts",
    )
    def get_scripts(
        _: str = Depends(require_admin),
        shell_service: ShellExecutionService = Depends(get_shell_execution_service),
    ) -> list[str]:
        """Return the shell scripts as a JSON array."""
        return shell_service.get_scripts()

    return router


router = build_admin_shell_router(
    "/kame-house-groot/api/v1/admin/kamehouse-shell",
    ["admin-kamehouse-shell"],
)
