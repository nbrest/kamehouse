import pytest

from kamehouse_groot.config import Settings, get_settings


@pytest.fixture(autouse=True)
def isolate_groot_config(monkeypatch, tmp_path):
    monkeypatch.setattr(Settings, "config_dir", tmp_path / "config")
    monkeypatch.setattr(Settings, "default_deploy_path", tmp_path / "deploy")
    get_settings.cache_clear()
    yield
    get_settings.cache_clear()
