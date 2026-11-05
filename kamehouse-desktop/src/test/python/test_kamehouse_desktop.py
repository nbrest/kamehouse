from unittest.mock import MagicMock

from PyQt5.QtCore import QEvent, QRect, Qt
from PyQt5.QtGui import QKeyEvent
from PyQt5.QtWidgets import QMainWindow

import kamehouse_desktop as desktop_module
from kamehouse_desktop import KameHouseDesktop


def create_bare_window(app, monkeypatch, keep=()):
    for method in [
        "initLogger",
        "startCompositor",
        "setWindowProperties",
        "moveToTargetScreen",
        "initWidgets",
        "showFullScreen",
    ]:
        if method not in keep:
            monkeypatch.setattr(KameHouseDesktop, method, MagicMock())
    return KameHouseDesktop(app)


def test_init_calls_setup_methods(app, monkeypatch):
    methods = {}
    for method in [
        "initLogger",
        "startCompositor",
        "setWindowProperties",
        "moveToTargetScreen",
        "initWidgets",
        "showFullScreen",
    ]:
        mock = MagicMock()
        methods[method] = mock
        monkeypatch.setattr(KameHouseDesktop, method, mock)
    window = KameHouseDesktop(app)
    for mock in methods.values():
        mock.assert_called_once()
    assert window.app is app


def test_post_init(app, monkeypatch):
    window = create_bare_window(app, monkeypatch)
    window.clock = MagicMock()
    window.weather = MagicMock()
    window.ztv_player = MagicMock()
    window.postInit()
    window.clock.postInit.assert_called_once()
    window.weather.postInit.assert_called_once()
    window.ztv_player.postInit.assert_called_once()


def test_init_logger(monkeypatch):
    fake_logger = MagicMock()
    monkeypatch.setattr(desktop_module, "logger", fake_logger)
    window = KameHouseDesktop.__new__(KameHouseDesktop)
    window.initLogger()
    fake_logger.remove.assert_called_once_with(0)
    fake_logger.add.assert_called_once()


def test_start_compositor(app, monkeypatch):
    window = create_bare_window(app, monkeypatch, keep=("startCompositor",))
    popen = MagicMock()
    monkeypatch.setattr(desktop_module.subprocess, "Popen", popen)
    desktop_module.kamehouse_desktop_cfg.config["kamehouse_desktop"]["execute_compositor"] = "true"
    window.startCompositor()
    popen.assert_called_once()

    popen.reset_mock()
    desktop_module.kamehouse_desktop_cfg.config["kamehouse_desktop"]["execute_compositor"] = "false"
    window.startCompositor()
    popen.assert_not_called()


def test_set_window_properties(app, monkeypatch):
    window = create_bare_window(app, monkeypatch, keep=("setWindowProperties",))
    window.setWindowProperties()
    assert window.windowTitle() == "KameHouse - Desktop"


def test_move_to_target_screen_without_environment(app, monkeypatch):
    window = create_bare_window(app, monkeypatch, keep=("moveToTargetScreen",))
    monkeypatch.delenv("KAMEHOUSE_DESKTOP_SCREEN", raising=False)
    window.moveToTargetScreen()


def test_move_to_target_screen_invalid_value(app, monkeypatch):
    window = create_bare_window(app, monkeypatch, keep=("moveToTargetScreen",))
    monkeypatch.setenv("KAMEHOUSE_DESKTOP_SCREEN", "not-a-number")
    window.moveToTargetScreen()


def test_move_to_target_screen_valid_and_out_of_range(app, monkeypatch):
    window = create_bare_window(app, monkeypatch, keep=("moveToTargetScreen",))

    class FakeScreen:
        def __init__(self, name, geometry):
            self._name = name
            self._geometry = geometry

        def name(self):
            return self._name

        def geometry(self):
            return self._geometry

    class FakeApp:
        def screens(self):
            return [FakeScreen("screen-0", QRect(10, 20, 300, 200))]

    window.app = FakeApp()
    monkeypatch.setenv("KAMEHOUSE_DESKTOP_SCREEN", "0")
    window.moveToTargetScreen()
    assert window.geometry() == QRect(10, 20, 300, 200)

    monkeypatch.setenv("KAMEHOUSE_DESKTOP_SCREEN", "2")
    window.moveToTargetScreen()


def test_get_hostname(app, monkeypatch):
    monkeypatch.setattr(desktop_module.socket, "gethostname", lambda: "my-host_name.example")
    window = KameHouseDesktop.__new__(KameHouseDesktop)

    desktop_module.kamehouse_desktop_cfg.config["hostname_widget"]["format_hostname"] = "true"
    assert window.getHostname() == "my host name example"

    desktop_module.kamehouse_desktop_cfg.config["hostname_widget"]["format_hostname"] = "false"
    assert window.getHostname() == "my-host_name.example"


def test_init_widgets(app, monkeypatch):
    window = create_bare_window(app, monkeypatch, keep=("initWidgets",))
    monkeypatch.setattr(window, "getHostname", lambda: "hostname")

    created = []

    class FakeTextWidget:
        def __init__(self, name, text, parent):
            self.name = name
            self.text = text
            self.parent = parent
            created.append(("text", name))

    class FakeImageWidget:
        def __init__(self, name, parent):
            self.name = name
            self.parent = parent
            created.append(("image", name))

    class FakeWidget:
        def __init__(self, parent):
            self.parent = parent
            created.append(("widget", type(self).__name__))

    monkeypatch.setattr(desktop_module, "TextWidget", FakeTextWidget)
    monkeypatch.setattr(desktop_module, "ImageWidget", FakeImageWidget)
    monkeypatch.setattr(desktop_module, "BackgroundSlideshowWidget", FakeWidget)
    monkeypatch.setattr(desktop_module, "ClockWidget", FakeWidget)
    monkeypatch.setattr(desktop_module, "WeatherWidget", FakeWidget)
    monkeypatch.setattr(desktop_module, "ZtvPlayerWidget", FakeWidget)

    window.initWidgets()
    assert window.hostname.text == "hostname"
    assert window.katakana.text == "カメハウス"
    assert len(created) == 8


def test_key_press_event(app, monkeypatch):
    window = create_bare_window(app, monkeypatch)
    quit_mock = MagicMock()
    monkeypatch.setattr(desktop_module.QApplication, "quit", quit_mock)
    event = QKeyEvent(QEvent.KeyPress, Qt.Key_Q, Qt.NoModifier)
    window.keyPressEvent(event)
    quit_mock.assert_called_once()

    other_event = QKeyEvent(QEvent.KeyPress, Qt.Key_A, Qt.NoModifier)
    QMainWindow.keyPressEvent(window, other_event)
