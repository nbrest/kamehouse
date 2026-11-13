"""API v1 router."""

from __future__ import annotations

from fastapi import APIRouter

from kamehouse_groot.api.v1 import auth, test_endpoint
from kamehouse_groot.api.v1.admin import kamehouse_shell
from kamehouse_groot.api.v1.commons import session

api_router = APIRouter()
api_router.include_router(auth.router)
api_router.include_router(session.router)
api_router.include_router(kamehouse_shell.router)
api_router.include_router(test_endpoint.router)
