"""KameHouse GRoot test endpoint."""

from __future__ import annotations

import html
import sys

from fastapi import APIRouter, Request
from fastapi.responses import HTMLResponse

router = APIRouter(prefix="/kame-house-groot/api/v1/test", tags=["test"])


@router.get("/test", response_class=HTMLResponse)
async def test_endpoint(request: Request) -> HTMLResponse:
    """Render the legacy test page content."""
    body: list[str] = ["<h1>print server info</h1>", "<h1>print all headers</h1>"]
    for name, value in request.headers.items():
        body.append(f"{html.escape(name)}: {html.escape(value)}<br>")
    body.append("<br><br>testFunction: mada mada dane:<br><br>")
    body.append("isLinuxHostExample():<br>")
    if sys.platform.startswith("linux"):
        body.append("its a linux host. do linux host specific stuff")
    else:
        body.append("its NOT linux host. do windows host specific stuff")
    return HTMLResponse("".join(body))
