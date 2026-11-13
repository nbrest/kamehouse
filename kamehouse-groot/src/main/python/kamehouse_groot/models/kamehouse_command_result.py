"""Models returned by the KameHouse shell command endpoints."""

from __future__ import annotations

from pydantic import BaseModel, Field


class KamehouseCommandResult(BaseModel):
    """Result of executing a kamehouse-shell command.

    The JSON contract matches the Java KameHouseCommandResult class and the
    existing groot execute endpoint.
    """

    command: str = ""
    exitCode: int = -1
    pid: int = -1
    status: str | None = None
    standardOutput: list[str] = Field(default_factory=list)
    standardOutputHtml: list[str] = Field(default_factory=list)
    standardError: list[str] = Field(default_factory=list)
    standardErrorHtml: list[str] = Field(default_factory=list)
