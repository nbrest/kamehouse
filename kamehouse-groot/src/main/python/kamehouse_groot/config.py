"""Runtime configuration for the KameHouse GRoot web service."""

from __future__ import annotations

from functools import lru_cache
from pathlib import Path

from pydantic_settings import BaseSettings, SettingsConfigDict

DEFAULT_SESSION_MAX_AGE_SECONDS = 2629746


class Settings(BaseSettings):
    """Settings loaded from GROOT_* environment variables."""

    model_config = SettingsConfigDict(env_prefix="GROOT_", extra="ignore")

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
