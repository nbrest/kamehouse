from pathlib import Path

import pytest

from config.kamehouse_desktop_cfg import KameHouseDesktopCfg


@pytest.fixture
def home_with_config(monkeypatch, tmp_path):
    home = tmp_path / "home"
    config_dir = home / ".kamehouse" / "config"
    config_dir.mkdir(parents=True)
    (home / "programs" / "kamehouse-desktop" / "conf").mkdir(parents=True)
    
    # Updated to valid TOML: quoted strings and lowercase booleans
    (config_dir / "kamehouse-desktop.toml").write_text(
        "[section]\n"
        "text = \"value\"\n"
        "integer = 42\n"
        "float = 0.75\n"
        "boolean_true = true\n"
        "boolean_false = false\n"
    )
    monkeypatch.setattr(Path, "home", lambda: home)
    return home


def test_reads_values_from_config(home_with_config):
    config = KameHouseDesktopCfg()
    assert config.get("section", "text") == "value"
    assert config.getInt("section", "integer") == 42
    assert config.getFloat("section", "float") == 0.75
    assert config.getBoolean("section", "boolean_true") is True
    assert config.getBoolean("section", "boolean_false") is False


class FakeConfig:
    def __init__(self, value):
        self.value = value

    def __getitem__(self, key):
        return {"value": self.value}


def test_get_float_and_boolean_with_none_values():
    config = KameHouseDesktopCfg.__new__(KameHouseDesktopCfg)
    config.config = FakeConfig(None)
    assert config.getFloat("section", "value") == 0.0
    assert config.getBoolean("section", "value") is False