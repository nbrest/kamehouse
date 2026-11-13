"""Session status returned by the legacy groot commons session endpoint."""

from __future__ import annotations

from pydantic import BaseModel, Field


class GrootSessionStatus(BaseModel):
    """KameHouse GRoot session status JSON contract."""

    server: str
    username: str
    isLinuxHost: bool
    isLinuxDockerHost: bool
    isDockerContainer: bool
    dockerControlHost: bool
    roles: list[str] = Field(default_factory=list)
