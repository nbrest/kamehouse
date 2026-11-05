from types import SimpleNamespace

import pytest
import requests
from PyQt5.QtWidgets import QWidget

from widgets import weather as weather_module
from widgets.weather import WeatherHttpSync, WeatherWidget


class FakeSignal:
    def __init__(self):
        self.slots = []

    def connect(self, slot):
        self.slots.append(slot)


class FakeThread:
    def __init__(self):
        self.started = FakeSignal()
        self.finished = FakeSignal()
        self.started_thread = False
        self.quit_called = False
        self.deleted = False

    def start(self):
        self.started_thread = True

    def quit(self):
        self.quit_called = True

    def deleteLater(self):
        self.deleted = True


class FakeWorker:
    def __init__(self, *args):
        self.finished = FakeSignal()
        self.run_called = False

    def moveToThread(self, thread):
        self.thread = thread

    def run(self):
        self.run_called = True


class FakeLogo:
    def __init__(self):
        self.pixmap = None

    def setPixmap(self, pixmap):
        self.pixmap = pixmap


class FakeWindow:
    def __init__(self):
        self.weather = SimpleNamespace(
            weather_status=None,
            updates=0,
            logo=FakeLogo(),
            text=SimpleNamespace(setText=lambda value: setattr(self.weather, "text_value", value)),
        )
        self.weather.updateStatus = self.updateStatus

    def updateStatus(self):
        self.weather.updates += 1


class FakeResponse:
    def __init__(self, payload=None, content=b"image"):
        self.payload = payload
        self.content = content

    def raise_for_status(self):
        return None

    def json(self):
        return self.payload


def test_weather_widget_update_status(app):
    parent = QWidget()
    widget = WeatherWidget(parent)
    widget.weather_status = None
    widget.updateStatus()
    assert widget.text.text() == ""
    assert widget.logo.isHidden() is True

    widget.weather_status = {"main": {"temp": 20.6}, "weather": [{"icon": "01d"}]}
    widget.updateStatus()
    assert widget.text.text() == "20°"
    assert widget.logo.isHidden() is False


def test_weather_widget_is_empty_status(app):
    widget = WeatherWidget(QWidget())
    assert widget.isEmptyStatus() is True
    widget.weather_status = {"weather": None, "main": {"temp": 1}}
    assert widget.isEmptyStatus() is True
    widget.weather_status = {"weather": [{"icon": "01d"}], "main": None}
    assert widget.isEmptyStatus() is True
    widget.weather_status = {"weather": [{"icon": "01d"}], "main": {"temp": 1}}
    assert widget.isEmptyStatus() is False


def test_weather_widget_init_http_status_sync(app, monkeypatch):
    monkeypatch.setattr(weather_module, "QThread", FakeThread)
    monkeypatch.setattr(weather_module, "WeatherHttpSync", FakeWorker)
    widget = WeatherWidget(QWidget())
    widget.initHttpStatusSync()
    assert widget.http_sync_thread.started_thread is True
    assert widget.http_sync.thread is widget.http_sync_thread
    assert widget.http_sync.finished.slots


def test_weather_http_sync_run(app, monkeypatch):
    sync = WeatherHttpSync(FakeWindow(), "key")
    monkeypatch.setattr(sync, "runHttpSyncLoop", lambda: setattr(sync, "loop_called", True))
    sync.run()
    assert sync.loop_called is True


def test_weather_http_sync_loop(app, monkeypatch):
    sync = WeatherHttpSync(FakeWindow(), "key")
    calls = []
    monkeypatch.setattr(sync, "executeHttpRequest", lambda: calls.append(True))

    def stop(_):
        raise StopIteration

    monkeypatch.setattr(weather_module.time, "sleep", stop)
    with pytest.raises(StopIteration):
        sync.runHttpSyncLoop()
    assert calls == [True]


def test_weather_http_sync_execute_request_success(app, monkeypatch):
    window = FakeWindow()
    sync = WeatherHttpSync(window, "key")
    payload = {"weather": [{"icon": "01d"}], "main": {"temp": 15}}
    monkeypatch.setattr(weather_module.requests, "get", lambda *args, **kwargs: FakeResponse(payload))
    sync.executeHttpRequest()
    assert window.weather.weather_status == payload


def test_weather_http_sync_execute_request_empty_status(app, monkeypatch):
    window = FakeWindow()
    sync = WeatherHttpSync(window, "key")
    payload = {"weather": None, "main": None}
    monkeypatch.setattr(weather_module.requests, "get", lambda *args, **kwargs: FakeResponse(payload))
    sync.executeHttpRequest()
    assert window.weather.weather_status is None


def test_weather_http_sync_execute_request_error(app, monkeypatch):
    window = FakeWindow()
    sync = WeatherHttpSync(window, "key")

    def fail(*args, **kwargs):
        raise requests.exceptions.RequestException("error")

    monkeypatch.setattr(weather_module.requests, "get", fail)
    sync.executeHttpRequest()
    assert window.weather.weather_status is None


def test_weather_http_sync_update_logo(app, monkeypatch):
    window = FakeWindow()
    window.weather.weather_status = {"weather": [{"icon": "10d"}]}
    sync = WeatherHttpSync(window, "key")
    monkeypatch.setattr(weather_module.requests, "get", lambda *args, **kwargs: FakeResponse(content=b"png"))
    sync.updateWeatherLogo()
    assert window.weather.logo.pixmap is not None


def test_weather_http_sync_update_logo_error(app, monkeypatch):
    window = FakeWindow()
    window.weather.weather_status = {"weather": [{"icon": "10d"}]}
    sync = WeatherHttpSync(window, "key")

    def fail(*args, **kwargs):
        raise requests.exceptions.RequestException("error")

    monkeypatch.setattr(weather_module.requests, "get", fail)
    sync.updateWeatherLogo()
    assert window.weather.logo.pixmap is None


def test_weather_http_sync_is_empty_status(app):
    sync = WeatherHttpSync(FakeWindow(), "key")
    assert sync.isEmptyStatus(None) is True
    assert sync.isEmptyStatus({"weather": None, "main": {}}) is True
    assert sync.isEmptyStatus({"weather": [], "main": None}) is True
    assert sync.isEmptyStatus({"weather": [], "main": {}}) is False
