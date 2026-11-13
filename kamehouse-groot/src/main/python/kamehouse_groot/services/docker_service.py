"""Read the KameHouse docker container environment file."""

from __future__ import annotations

import logging
from pathlib import Path

LOGGER = logging.getLogger(__name__)

DOCKER_ENV_FILE = Path(".kamehouse") / "config" / ".kamehouse-docker-container-env"
TRUE_VALUES = ("true", "TRUE")


class DockerEnvironment:
    """Parse the docker container environment written by kamehouse-shell."""

    def __init__(self, user_home: Path) -> None:
        self._user_home = user_home

    @property
    def env_file(self) -> Path:
        """Path to the docker container environment file."""
        return self._user_home / DOCKER_ENV_FILE

    def get_properties(self) -> dict[str, str]:
        """Return the docker environment properties, or an empty dict."""
        env_file = self.env_file
        if not env_file.is_file():
            return {}

        try:
            content = env_file.read_text(encoding="utf-8")
        except OSError as exc:
            LOGGER.warning("Could not read docker environment file %s: %s", env_file, exc)
            return {}

        properties: dict[str, str] = {}
        for line in content.splitlines():
            line = line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            key, value = line.split("=", 1)
            properties[key] = value
        return properties

    @staticmethod
    def get_boolean_property(properties: dict[str, str], property_name: str) -> bool:
        """Return the boolean value of a docker property."""
        return properties.get(property_name) in TRUE_VALUES
