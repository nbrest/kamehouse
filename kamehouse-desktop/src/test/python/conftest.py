import os
import sys
from pathlib import Path

# Use built-in tomllib for Python 3.11+, fallback to tomli if necessary
if sys.version_info >= (3, 11):
    import tomllib
else:
    import tomli as tomllib

os.environ.setdefault("QT_QPA_PLATFORM", "offscreen")
os.environ.setdefault("OPENWEATHERMAP_API_KEY", "test-openweathermap-api-key")

DESKTOP_DIR = Path(__file__).resolve().parents[3]
BIN_DIR = DESKTOP_DIR / "bin"
if str(BIN_DIR) not in sys.path:
    sys.path.insert(0, str(BIN_DIR))

import pytest
from PyQt5.QtWidgets import QApplication

from config.kamehouse_desktop_cfg import kamehouse_desktop_cfg


@pytest.fixture(scope="session")
def app():
    application = QApplication.instance()
    if application is None:
        application = QApplication([])
    return application


@pytest.fixture(autouse=True)
def desktop_config(app):
    config_file = DESKTOP_DIR / "conf" / "default-kamehouse-desktop.toml"
    with open(config_file, "rb") as f:
        config = tomllib.load(f)
        
    original_config = kamehouse_desktop_cfg.config
    kamehouse_desktop_cfg.config = config
    yield config
    kamehouse_desktop_cfg.config = original_config