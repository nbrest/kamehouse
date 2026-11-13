"""Run the KameHouse GRoot web service with uvicorn."""

from __future__ import annotations

import uvicorn

from kamehouse_groot.config import get_settings
from kamehouse_groot.logging_config import configure_logging


def main() -> None:
    """Start the KameHouse GRoot uvicorn server."""
    settings = get_settings()
    configure_logging(settings)
    uvicorn.run(
        "kamehouse_groot.main:create_app",
        factory=True,
        host=settings.host,
        port=settings.port,
        log_config=None,
        access_log=True,
    )


if __name__ == "__main__":
    main()
