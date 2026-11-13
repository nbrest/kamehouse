"""Logging configuration for the KameHouse GRoot web service."""

from __future__ import annotations

import logging
import time
from logging.handlers import RotatingFileHandler
from pathlib import Path

from kamehouse_groot.config import Settings

LOG_FORMAT = "%(asctime)s.%(msecs)03d [%(threadName)s] %(levelname)s %(name)s - %(message)s"
LOG_DATE_FORMAT = "%Y-%m-%d %H:%M:%S"
MAX_LOG_BYTES = 10 * 1024 * 1024
LOG_BACKUP_COUNT = 5

APACHE_LOG_MONTHS = (
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec",
)


class ApacheAccessFormatter(logging.Formatter):
    """Formats uvicorn access log entries in the apache httpd access log format."""

    def format(self, record: logging.LogRecord) -> str:
        try:
            client_addr, method, full_path, http_version, status_code = record.args
        except (TypeError, ValueError):
            return super().format(record)
        client_ip = str(client_addr).rsplit(":", 1)[0]
        return (
            f"{client_ip} - - [{self._apache_timestamp(record)}] "
            f'"{method} {full_path} HTTP/{http_version}" {status_code}'
        )

    def _apache_timestamp(self, record: logging.LogRecord) -> str:
        created = time.localtime(record.created)
        month = APACHE_LOG_MONTHS[created.tm_mon - 1]
        timestamp = time.strftime(f"%d/{month}/%Y:%H:%M:%S", created)
        return f"{timestamp} {self._timezone_offset(created)}"

    def _timezone_offset(self, created: time.struct_time) -> str:
        if created.tm_isdst and time.daylight:
            offset_seconds = -time.altzone
        else:
            offset_seconds = -time.timezone
        sign = "+" if offset_seconds >= 0 else "-"
        minutes = abs(offset_seconds) // 60
        return f"{sign}{minutes // 60:02d}{minutes % 60:02d}"


_LOG_RECORD_FACTORY = logging.getLogRecordFactory()


def _record_factory(*args, **kwargs) -> logging.LogRecord:
    record = _LOG_RECORD_FACTORY(*args, **kwargs)
    record.threadName = record.threadName.replace(" ", "-")
    return record


def _file_handler(path: Path, level: int) -> RotatingFileHandler:
    handler = RotatingFileHandler(
        path,
        maxBytes=MAX_LOG_BYTES,
        backupCount=LOG_BACKUP_COUNT,
        encoding="utf-8",
    )
    handler.setLevel(level)
    handler.setFormatter(logging.Formatter(LOG_FORMAT, LOG_DATE_FORMAT))
    return handler


def _access_file_handler(path: Path, level: int) -> RotatingFileHandler:
    handler = RotatingFileHandler(
        path,
        maxBytes=MAX_LOG_BYTES,
        backupCount=LOG_BACKUP_COUNT,
        encoding="utf-8",
    )
    handler.setLevel(level)
    handler.setFormatter(ApacheAccessFormatter())
    return handler


def configure_logging(settings: Settings) -> None:
    """Configure application and uvicorn logging into the deployment log folder."""
    logging.setLogRecordFactory(_record_factory)
    log_dir = settings.log_dir
    log_dir.mkdir(parents=True, exist_ok=True)

    level = getattr(logging, settings.log_level.upper(), logging.INFO)

    root = logging.getLogger()
    root.setLevel(level)
    for handler in list(root.handlers):
        root.removeHandler(handler)
    root.addHandler(_file_handler(log_dir / f"{settings.service_name}.log", level))
    console_handler = logging.StreamHandler()
    console_handler.setLevel(level)
    console_handler.setFormatter(logging.Formatter(LOG_FORMAT, LOG_DATE_FORMAT))
    root.addHandler(console_handler)

    for logger_name in ("uvicorn", "uvicorn.error"):
        uvicorn_logger = logging.getLogger(logger_name)
        uvicorn_logger.handlers.clear()
        uvicorn_logger.setLevel(level)
        uvicorn_logger.propagate = True

    access_logger = logging.getLogger("uvicorn.access")
    access_logger.handlers.clear()
    access_logger.setLevel(level)
    access_logger.propagate = False
    access_logger.addHandler(_access_file_handler(log_dir / f"{settings.service_name}-access.log", level))
