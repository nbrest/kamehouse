"""Tests for logging configuration."""

from __future__ import annotations

import logging
import re
import threading
from pathlib import Path

from kamehouse_groot.config import Settings
from kamehouse_groot.logging_config import configure_logging

JAVA_LOG_LINE_RX = re.compile(
    r"^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{3} \[[A-Za-z0-9 _-]+\] "
    r"(TRACE|DEBUG|INFO|WARN|ERROR) kamehouse_groot\.test - hello from test$"
)

APACHE_ACCESS_LINE_RX = re.compile(
    r"^127\.0\.0\.1 - - \[\d{2}/[A-Z][a-z]{2}/\d{4}:\d{2}:\d{2}:\d{2} [+-]\d{4}\] "
    r'"GET /kame-house-groot/api/v1/test HTTP/1\.1" 200$'
)


def _flush() -> None:
    for handler in logging.getLogger().handlers:
        handler.flush()
    for handler in logging.getLogger("uvicorn.access").handlers:
        handler.flush()


def _restore_logging(previous_handlers: list, previous_level: int) -> None:
    for handler in logging.getLogger().handlers:
        handler.close()
    logging.getLogger().handlers = previous_handlers
    logging.getLogger().setLevel(previous_level)


def test_configure_logging_creates_log_files(tmp_path: Path) -> None:
    root = logging.getLogger()
    previous_handlers = list(root.handlers)
    previous_level = root.level
    try:
        settings = Settings(deploy_path=tmp_path, log_level="DEBUG")
        configure_logging(settings)
        log_dir = tmp_path / "logs"
        assert (log_dir / "kamehouse-groot.log").exists()
        assert (log_dir / "kamehouse-groot-access.log").exists()

        logging.getLogger("kamehouse_groot.test").info("hello from test")
        _flush()
        assert "hello from test" in (log_dir / "kamehouse-groot.log").read_text(encoding="utf-8")
    finally:
        _restore_logging(previous_handlers, previous_level)


def test_log_format_matches_kamehouse_java_log(tmp_path: Path) -> None:
    root = logging.getLogger()
    previous_handlers = list(root.handlers)
    previous_level = root.level
    try:
        configure_logging(Settings(deploy_path=tmp_path, log_level="DEBUG"))
        logging.getLogger("kamehouse_groot.test").info("hello from test")
        _flush()
        log_line = (tmp_path / "logs" / "kamehouse-groot.log").read_text(encoding="utf-8").strip()
        assert JAVA_LOG_LINE_RX.match(log_line)
    finally:
        _restore_logging(previous_handlers, previous_level)


def test_thread_names_with_spaces_are_sanitized(tmp_path: Path) -> None:
    root = logging.getLogger()
    previous_handlers = list(root.handlers)
    previous_level = root.level
    try:
        configure_logging(Settings(deploy_path=tmp_path, log_level="DEBUG"))
        thread = threading.Thread(
            target=lambda: logging.getLogger("kamehouse_groot.test").info("hello from test"),
            name="AnyIO worker thread",
        )
        thread.start()
        thread.join()
        _flush()
        log_line = (tmp_path / "logs" / "kamehouse-groot.log").read_text(encoding="utf-8").strip()
        assert "[AnyIO-worker-thread] INFO kamehouse_groot.test - hello from test" in log_line
    finally:
        _restore_logging(previous_handlers, previous_level)


def test_access_log_format_matches_apache_access_log(tmp_path: Path) -> None:
    access_logger = logging.getLogger("uvicorn.access")
    previous_handlers = list(access_logger.handlers)
    previous_level = access_logger.level
    previous_propagate = access_logger.propagate
    root = logging.getLogger()
    previous_root_handlers = list(root.handlers)
    previous_root_level = root.level
    try:
        configure_logging(Settings(deploy_path=tmp_path, log_level="DEBUG"))
        access_logger.info(
            '%s - "%s %s HTTP/%s" %d',
            "127.0.0.1:12345",
            "GET",
            "/kame-house-groot/api/v1/test",
            "1.1",
            200,
        )
        _flush()
        log_line = (tmp_path / "logs" / "kamehouse-groot-access.log").read_text(encoding="utf-8").strip()
        assert APACHE_ACCESS_LINE_RX.match(log_line)
    finally:
        for handler in access_logger.handlers:
            handler.close()
        access_logger.handlers = previous_handlers
        access_logger.setLevel(previous_level)
        access_logger.propagate = previous_propagate
        _restore_logging(previous_root_handlers, previous_root_level)


def test_invalid_log_level_falls_back_to_info(tmp_path: Path) -> None:
    root = logging.getLogger()
    previous_handlers = list(root.handlers)
    previous_level = root.level
    try:
        configure_logging(Settings(deploy_path=tmp_path, log_level="NOT_A_LEVEL"))
        assert root.level == logging.INFO
    finally:
        _restore_logging(previous_handlers, previous_level)
