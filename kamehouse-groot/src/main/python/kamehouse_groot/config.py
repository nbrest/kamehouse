"""Runtime configuration for the KameHouse GRoot web service."""

from __future__ import annotations

import tomllib
from functools import lru_cache
from pathlib import Path
from typing import Any, ClassVar

from pydantic import ValidationInfo, field_validator
from pydantic.fields import FieldInfo
from pydantic_settings import BaseSettings, PydanticBaseSettingsSource, SettingsConfigDict

DEFAULT_SESSION_MAX_AGE_SECONDS = 2629746

_PATH_FIELDS = ("deploy_path", "user_home", "shell_bin", "secret_script", "session_dir")


def deep_merge(base: dict[str, Any], override: dict[str, Any]) -> dict[str, Any]:
    """Recursively merge override into base and return base."""
    for key, value in override.items():
        if isinstance(value, dict) and isinstance(base.get(key), dict):
            deep_merge(base[key], value)
        else:
            base[key] = value
    return base


def _read_config_table(path: Path, table: str) -> dict[str, Any]:
    """Read the settings table from a TOML configuration file."""
    with open(path, "rb") as config_file:
        data = tomllib.load(config_file)
    if not data:
        return {}
    if table not in data:
        raise ValueError(f"Missing [{table}] table in {path}")
    return data[table]


def load_config_file(settings_cls: type[BaseSettings]) -> dict[str, Any]:
    """Load the user configuration on top of the default configuration."""
    default_file = (
        getattr(settings_cls, "default_deploy_path")
        / "conf"
        / getattr(settings_cls, "default_config_file_name")
    )
    user_file = getattr(settings_cls, "config_dir") / getattr(settings_cls, "config_file_name")

    config: dict[str, Any] = {}
    config_table = getattr(settings_cls, "config_table")
    if default_file.is_file():
        config = deep_merge(config, _read_config_table(default_file, config_table))
    if user_file.is_file():
        config = deep_merge(config, _read_config_table(user_file, config_table))
    return config


class TomlConfigSettingsSource(PydanticBaseSettingsSource):
    """Settings source that reads the groot TOML configuration files."""

    def __init__(self, settings_cls: type[BaseSettings]) -> None:
        super().__init__(settings_cls)
        self._data = load_config_file(settings_cls)

    def get_field_value(self, field: FieldInfo, field_name: str) -> tuple[Any, str, bool]:
        return self._data.get(field_name), field_name, False

    def __call__(self) -> dict[str, Any]:
        return self._data


class Settings(BaseSettings):
    """Settings loaded from the groot TOML configuration files."""

    model_config = SettingsConfigDict(extra="ignore")

    config_dir: ClassVar[Path] = Path.home() / ".kamehouse" / "config"
    config_file_name: ClassVar[str] = "kamehouse-groot.toml"
    default_config_file_name: ClassVar[str] = "default-kamehouse-groot.toml"
    config_table: ClassVar[str] = "groot"
    default_deploy_path: ClassVar[Path] = Path.home() / "programs" / "kamehouse-groot"

    host: str = "0.0.0.0"
    port: int = 9091
    log_level: str = "INFO"
    deploy_path: Path = Path.home() / "programs" / "kamehouse-groot"
    user_home: Path = Path.home()

    db_host: str = "127.0.0.1"
    db_port: int = 3306
    db_name: str = "kamehouse"
    db_user: str = "kamehouse"
    db_password_secret: str = "MARIADB_PASS_KAMEHOUSE"

    shell_bin: Path = Path.home() / "programs" / "kamehouse-shell" / "bin"
    secret_script: Path | None = None

    scripts_csv: str = "csv/csv-kamehouse-shell.sh"

    session_cookie_name: str = "kamehouse-groot-session"
    session_dir: Path | None = None
    session_max_age_seconds: int = DEFAULT_SESSION_MAX_AGE_SECONDS

    command_timeout_seconds: int = 300
    daemon_wait_seconds: int = 5
    use_script_log: bool = True

    service_name: str = "kamehouse-groot"

    @field_validator(*_PATH_FIELDS, mode="before")
    @classmethod
    def _normalize_path(cls, value: Any, info: ValidationInfo) -> Any:
        if value is None:
            return None
        if isinstance(value, str):
            value = value.strip()
            if not value:
                raise ValueError(f"{info.field_name} can't be empty")
            return Path(value).expanduser()
        if isinstance(value, Path):
            return value.expanduser()
        return value

    @classmethod
    def settings_customise_sources(
        cls,
        settings_cls: type[BaseSettings],
        init_settings: PydanticBaseSettingsSource,
        env_settings: PydanticBaseSettingsSource,
        dotenv_settings: PydanticBaseSettingsSource,
        file_secret_settings: PydanticBaseSettingsSource,
    ) -> tuple[PydanticBaseSettingsSource, ...]:
        return (
            init_settings,
            TomlConfigSettingsSource(settings_cls),
        )

    @property
    def log_dir(self) -> Path:
        """Directory where the service writes its log files."""
        return self.deploy_path / "logs"

    @property
    def resolved_session_dir(self) -> Path:
        """Directory where the service persists its login sessions."""
        if self.session_dir is not None:
            return self.session_dir
        return self.deploy_path / "run" / "sessions"

    @property
    def resolved_secret_script(self) -> Path:
        """Path to the kamehouse-shell get-kamehouse-secret.sh script."""
        if self.secret_script is not None:
            return self.secret_script
        return self.shell_bin / "secrets" / "get-kamehouse-secret.sh"


@lru_cache
def get_settings() -> Settings:
    """Return the cached application settings."""
    return Settings()
