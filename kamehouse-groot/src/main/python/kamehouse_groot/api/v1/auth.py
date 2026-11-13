"""KameHouse GRoot authentication endpoints."""

from __future__ import annotations

from fastapi import APIRouter, Depends, Form, Request, Response
from fastapi.responses import RedirectResponse

from kamehouse_groot.api.dependencies import get_auth_service, get_session_service
from kamehouse_groot.services.auth_service import AuthService
from kamehouse_groot.services.session_service import SessionService

router = APIRouter(prefix="/kame-house-groot/api/v1/auth", tags=["auth"])

LOGIN_PAGE = "/kame-house/groot/login.html"
GROOT_HOME = "/kame-house/groot/"


@router.post("/login")
async def login(
    username: str | None = Form(default=None),
    password: str | None = Form(default=None),
    referrer: str | None = Form(default=None),
    auth_service: AuthService = Depends(get_auth_service),
    session_service: SessionService = Depends(get_session_service),
) -> RedirectResponse:
    """Authenticate a KameHouse user and create a login session."""
    if not username or not password:
        return RedirectResponse(f"{LOGIN_PAGE}?error=true", status_code=302)

    if not await auth_service.verify_credentials(username, password):
        return RedirectResponse(f"{LOGIN_PAGE}?error=true", status_code=302)

    token = session_service.create(username)
    redirect_url = GROOT_HOME
    if referrer and referrer.startswith(GROOT_HOME):
        redirect_url = referrer

    response = RedirectResponse(redirect_url, status_code=302)
    _set_session_cookie(response, session_service, token)
    return response


@router.get("/login")
async def login_page() -> RedirectResponse:
    """Redirect direct login endpoint access back to the login page."""
    return RedirectResponse(f"{LOGIN_PAGE}?error=true", status_code=302)


@router.get("/logout")
async def logout(
    request: Request,
    session_service: SessionService = Depends(get_session_service),
) -> RedirectResponse:
    """Destroy the current login session."""
    session_service.delete(request.cookies.get(session_service.cookie_name))
    response = RedirectResponse(f"{LOGIN_PAGE}?logout=true", status_code=302)
    response.delete_cookie(session_service.cookie_name, path="/")
    return response


@router.get("/authorize-page")
async def authorize_page(
    request: Request,
    auth_service: AuthService = Depends(get_auth_service),
    session_service: SessionService = Depends(get_session_service),
) -> Response:
    """Allow admin sessions and redirect everyone else to the login page."""
    username = session_service.get_username(
        request.cookies.get(session_service.cookie_name)
    )
    if username and await auth_service.is_admin(username):
        return Response(status_code=200)

    referrer = request.url.path
    if request.url.query:
        referrer = f"{referrer}?{request.url.query}"
    redirect_url = f"{LOGIN_PAGE}?unauthorizedPageAccess=true"
    if referrer:
        redirect_url = f"{redirect_url}&referrer={referrer}"
    return RedirectResponse(redirect_url, status_code=302)


def _set_session_cookie(
    response: Response,
    session_service: SessionService,
    token: str,
) -> None:
    response.set_cookie(
        session_service.cookie_name,
        token,
        path="/",
        httponly=True,
        samesite="lax",
    )
