from types import SimpleNamespace
from unittest.mock import MagicMock

import pytest
import requests
from PyQt5.QtWidgets import QWidget

from widgets import ztv_player as ztv_module
from widgets.ztv_player import (
    VlcPlayerFullScreenSetter,
    ZtvPlayerHttpSync,
    ZtvPlayerWebsocket,
    ZtvPlayerWidget,
)


class FakeSignal:
    def __init__(self):
        self.slots = []

    def connect(self, slot):
        self.slots.append(slot)


class FakeThread:
    def __init__(self):
        self.started = FakeSignal()
        self.finished = FakeSignal()
        self.start_called = False
        self.quit_called = False
        self.deleted = False

    def start(self):
        self.start_called = True

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


class FakeTimer:
    instances = []
    single_shots = []

    def __init__(self, *args):
        self.timeout = FakeSignal()
        self.started = None
        FakeTimer.instances.append(self)

    def start(self, milliseconds):
        self.started = milliseconds

    def singleShot(self, milliseconds, slot):
        FakeTimer.single_shots.append((milliseconds, slot))


class FakeResponse:
    def __init__(self, payload=None):
        self.payload = payload

    def raise_for_status(self):
        return None

    def json(self):
        return self.payload


@pytest.fixture
def window(app):
    parent = QWidget()
    parent.app = app
    return parent


@pytest.fixture
def widget(window):
    player = ZtvPlayerWidget(window)
    window.ztv_player = player
    return player


def create_image():
    return {"img_src": "lib/ico/kamehouse.png", "pos_x": 10, "pos_y": 20, "width": 30, "height": 40}


def valid_status():
    return {
        "time": 5,
        "length": 65,
        "information": {"meta": {"filename": "song-artist.mp3", "artist": "Artist"}},
    }


def test_ztv_player_widget_hidden(app, desktop_config):
    desktop_config["ztv_player_widget"]["hidden"] = "true"
    parent = QWidget()
    widget = ZtvPlayerWidget(parent)
    assert widget.isHidden() is True


def test_ztv_player_widget_post_init(widget, monkeypatch):
    calls = []
    for method in ["initUpdateViewSync", "initSyncThreads", "setLogoRandomSrc", "resetVlcPlayerFullScreen"]:
        monkeypatch.setattr(widget, method, lambda name=method: calls.append(name))
    widget.postInit()
    assert calls == ["initUpdateViewSync", "initSyncThreads", "setLogoRandomSrc", "resetVlcPlayerFullScreen"]


def test_init_threads(widget, monkeypatch):
    monkeypatch.setattr(ztv_module, "QThread", FakeThread)
    monkeypatch.setattr(ztv_module, "ZtvPlayerWebsocket", FakeWorker)
    monkeypatch.setattr(ztv_module, "ZtvPlayerHttpSync", FakeWorker)
    monkeypatch.setattr(ztv_module, "VlcPlayerFullScreenSetter", FakeWorker)

    widget.initWebsocket()
    widget.initHttpVlcRcStatusSync()
    widget.resetVlcPlayerFullScreen()

    assert widget.websocket_thread.start_called is True
    assert widget.websocket.thread is widget.websocket_thread
    assert widget.http_sync_thread.start_called is True
    assert widget.http_sync.thread is widget.http_sync_thread
    assert widget.vlc_player_full_screen_thread.start_called is True
    assert widget.vlc_player_full_screen.thread is widget.vlc_player_full_screen_thread


def test_init_update_view_sync_and_timers(widget, window, monkeypatch):
    FakeTimer.instances = []
    FakeTimer.single_shots = []
    monkeypatch.setattr(ztv_module, "QTimer", FakeTimer)
    widget.initUpdateViewSync()
    assert FakeTimer.instances[-1].started == 1000
    assert FakeTimer.instances[-1].timeout.slots

    widget.startSoundWaveMovie()
    assert FakeTimer.single_shots[-1][0] == 100


def test_start_sound_wave_movie_skips_hidden(widget, desktop_config, monkeypatch):
    FakeTimer.single_shots = []
    monkeypatch.setattr(ztv_module, "QTimer", FakeTimer)
    desktop_config["ztv_player_widget"]["hidden"] = "true"
    widget.startSoundWaveMovie()
    assert FakeTimer.single_shots == []

    desktop_config["ztv_player_widget"]["hidden"] = "false"
    desktop_config["ztv_player_sound_wave_widget"]["hidden"] = "true"
    widget.startSoundWaveMovie()
    assert FakeTimer.single_shots == []


def test_set_logo_random_src(widget, desktop_config, monkeypatch):
    FakeTimer.instances = []
    monkeypatch.setattr(ztv_module, "QTimer", FakeTimer)
    desktop_config["ztv_player_logo_random_src"]["random_src_entries_count"] = "2"
    widget.setLogoRandomSrc()
    assert len(widget.ztv_player_logo_random_src) == 2
    assert FakeTimer.instances[-1].started == 60000

    desktop_config["ztv_player_logo_widget"]["use_random_src"] = "false"
    desktop_config["ztv_player_off_logo_widget"]["use_random_src"] = "false"
    timer_count = len(FakeTimer.instances)
    widget.setLogoRandomSrc()
    assert len(FakeTimer.instances) == timer_count


def test_set_random_logo(widget, desktop_config):
    desktop_config["ztv_player_logo_random_src"]["random_src_entries_count"] = "2"
    desktop_config["ztv_player_logo_widget"]["use_random_src"] = "true"
    desktop_config["ztv_player_off_logo_widget"]["use_random_src"] = "true"
    widget.ztv_player_logo_random_src = [
        dict(create_image(), img_src="lib/ico/kamehouse.png"),
        dict(create_image(), img_src="lib/ico/kamehouse-christmas.png"),
    ]
    widget.ztv_player_logo_src = None
    widget.setRandomLogo()
    assert widget.ztv_player_logo_src is not None
    first_src = widget.ztv_player_logo_src["img_src"]
    widget.setRandomLogo()
    assert widget.ztv_player_logo_src["img_src"] != first_src

    desktop_config["ztv_player_logo_widget"]["use_random_src"] = "false"
    desktop_config["ztv_player_off_logo_widget"]["use_random_src"] = "false"
    widget.setRandomLogo()


def test_format_time_title_and_artist(widget):
    assert widget.formatTime(0) == "00:00:00"
    assert widget.formatTime(3661) == "01:01:01"
    assert widget.formatTitle(None, None) == widget.default_title
    assert widget.formatTitle("my-song_name.mp3", "my song name") == "My Song Name"
    assert widget.formatArtist(None) == widget.default_artist
    assert widget.formatArtist("my-song_name") == "My Song Name"


def test_update_view_invalid_statuses(widget, monkeypatch):
    hide = MagicMock()
    show = MagicMock()
    monkeypatch.setattr(widget, "hideZtvPlayer", hide)
    monkeypatch.setattr(widget, "showZtvPlayer", show)

    invalid_statuses = [
        {},
        {"time": None, "length": 1, "information": {"meta": {"filename": "file"}}},
        {"time": 1, "length": None, "information": {"meta": {"filename": "file"}}},
        {"time": 1, "length": 1, "information": None},
        {"time": 1, "length": 1, "information": {"meta": None}},
        {"time": 1, "length": 1, "information": {"meta": {"filename": None}}},
    ]
    for status in invalid_statuses:
        widget.vlc_rc_status = status
        widget.updateView()
    assert hide.call_count == len(invalid_statuses)

    widget.log_trace = True
    widget.vlc_rc_status = {}
    widget.updateView()


def test_update_view_valid_status(widget):
    widget.vlc_rc_status = valid_status()
    widget.updateView()
    assert widget.title.text() == "Song "
    assert widget.artist.text() == "Artist"
    assert widget.current_time.text() == "00:00:05"
    assert widget.total_time.text() == "00:01:05"
    assert widget.is_playing_media is True


def test_show_and_hide_ztv_player(widget):
    widget.showZtvPlayer()
    widget.showZtvPlayer()
    assert widget.is_playing_media is True
    assert widget.logo.isHidden() is False
    assert widget.ztv_player_off_logo.isHidden() is True

    widget.hideZtvPlayer()
    widget.hideZtvPlayer()
    assert widget.is_playing_media is False
    assert widget.logo.isHidden() is True
    assert widget.ztv_player_off_logo.isHidden() is False


def test_sound_wave_and_logo_animations(widget):
    widget.setSoundWaveAnimation()
    widget.startSoundWaveAnimation()
    widget.sound_wave.anim_group.stop()
    widget.setLogoAnimation()
    widget.startLogoAnimation()
    widget.logo.anim_group.stop()


class FakeZtvWindow:
    def __init__(self, update_time):
        self.ztv_player = SimpleNamespace(
            websocket_update_time=update_time,
            vlc_rc_status={},
        )


def test_http_sync_run(monkeypatch):
    sync = ZtvPlayerHttpSync(FakeZtvWindow(0))
    monkeypatch.setattr(sync, "runHttpSyncLoop", lambda: setattr(sync, "loop_called", True))
    sync.run()
    assert sync.loop_called is True


def test_http_sync_loop_skips_when_websocket_is_fresh(monkeypatch):
    sync = ZtvPlayerHttpSync(FakeZtvWindow(100))
    called = []
    monkeypatch.setattr(ztv_module.time, "time", lambda: 100)
    monkeypatch.setattr(sync, "executeHttpRequest", lambda: called.append(True))

    def stop(_):
        raise StopIteration

    monkeypatch.setattr(ztv_module.time, "sleep", stop)
    with pytest.raises(StopIteration):
        sync.runHttpSyncLoop()
    assert called == []


def test_http_sync_loop_executes_when_websocket_is_stale(monkeypatch):
    sync = ZtvPlayerHttpSync(FakeZtvWindow(0))
    called = []
    monkeypatch.setattr(ztv_module.time, "time", lambda: 100)
    monkeypatch.setattr(sync, "executeHttpRequest", lambda: called.append(True))

    def stop(_):
        raise StopIteration

    monkeypatch.setattr(ztv_module.time, "sleep", stop)
    with pytest.raises(StopIteration):
        sync.runHttpSyncLoop()
    assert called == [True]


def test_http_sync_execute_request(monkeypatch):
    window = FakeZtvWindow(0)
    sync = ZtvPlayerHttpSync(window)
    payload = {"time": 1}
    monkeypatch.setattr(ztv_module.requests, "get", lambda *args, **kwargs: FakeResponse(payload))
    sync.executeHttpRequest()
    assert window.ztv_player.vlc_rc_status == payload


def test_http_sync_execute_request_error(monkeypatch):
    window = FakeZtvWindow(0)
    sync = ZtvPlayerHttpSync(window)

    def fail(*args, **kwargs):
        raise requests.exceptions.RequestException("error")

    monkeypatch.setattr(ztv_module.requests, "get", fail)
    sync.executeHttpRequest()
    assert window.ztv_player.vlc_rc_status == {}


def test_websocket_run(monkeypatch):
    websocket = ZtvPlayerWebsocket(FakeZtvWindow(0))
    monkeypatch.setattr(websocket, "runWebsocketLoop", lambda: setattr(websocket, "loop_called", True))
    websocket.run()
    assert websocket.loop_called is True


class FakeWebSocketApp:
    urls = []
    should_fail = False

    def __init__(self, url, on_open, on_message, on_error, on_close):
        self.url = url
        self.on_open = on_open
        self.on_message = on_message
        self.on_error = on_error
        self.on_close = on_close
        FakeWebSocketApp.urls.append(url)

    def run_forever(self):
        if FakeWebSocketApp.should_fail:
            raise RuntimeError("websocket error")


def test_websocket_loop_reconnects(monkeypatch):
    FakeWebSocketApp.urls = []
    FakeWebSocketApp.should_fail = False
    websocket = ZtvPlayerWebsocket(FakeZtvWindow(0))
    monkeypatch.setattr(ztv_module.websocket, "WebSocketApp", FakeWebSocketApp)

    def stop(_):
        raise StopIteration

    monkeypatch.setattr(ztv_module.time, "sleep", stop)
    with pytest.raises(StopIteration):
        websocket.runWebsocketLoop()
    assert FakeWebSocketApp.urls
    assert FakeWebSocketApp.urls[0].startswith("ws://localhost:")

    FakeWebSocketApp.should_fail = True
    with pytest.raises(StopIteration):
        websocket.runWebsocketLoop()


def test_websocket_callbacks(monkeypatch):
    window = FakeZtvWindow(0)
    websocket = ZtvPlayerWebsocket(window)
    websocket.log_trace = True
    monkeypatch.setattr(ztv_module.time, "time", lambda: 123)
    monkeypatch.setattr(ztv_module.stomper, "unpack_frame", lambda message: {"cmd": "MESSAGE", "body": '{"time": 1}'})
    websocket.onMessage(None, "message")
    assert window.ztv_player.vlc_rc_status == {"time": 1}
    assert window.ztv_player.websocket_update_time == 123

    monkeypatch.setattr(ztv_module.stomper, "unpack_frame", lambda message: {"cmd": "MESSAGE", "body": None})
    websocket.onMessage(None, "message")
    assert window.ztv_player.vlc_rc_status == {}

    websocket.onError(None, "error")
    websocket.onClose(None, "1000", "closed")

    sent = []

    class FakeSocket:
        def send(self, message):
            sent.append(message)

    monkeypatch.setattr(ztv_module.stomper, "connect", lambda *args: "connect")
    monkeypatch.setattr(ztv_module.stomper, "subscribe", lambda *args, **kwargs: "subscribe")
    websocket.onOpen(FakeSocket())
    assert sent == ["connect", "subscribe"]


def test_websocket_is_empty_body():
    websocket = ZtvPlayerWebsocket(FakeZtvWindow(0))
    assert websocket.isEmptyBody({"cmd": "CONNECTED", "body": "value"}) is True
    assert websocket.isEmptyBody({"cmd": "MESSAGE", "body": None}) is True
    assert websocket.isEmptyBody({"cmd": "MESSAGE", "body": "value"}) is False


def test_vlc_player_fullscreen_setter_run(monkeypatch):
    setter = VlcPlayerFullScreenSetter(FakeZtvWindow(0))
    monkeypatch.setattr(setter, "executeHttpRequest", MagicMock())
    monkeypatch.setattr(ztv_module.time, "sleep", lambda _: None)
    setter.run()
    assert setter.executeHttpRequest.call_count == 2


def test_vlc_player_fullscreen_setter_execute_request(monkeypatch):
    setter = VlcPlayerFullScreenSetter(FakeZtvWindow(0))
    posts = []
    monkeypatch.setattr(ztv_module.requests, "post", lambda *args, **kwargs: posts.append((args, kwargs)))
    setter.executeHttpRequest()
    assert posts
    assert posts[0][1]["json"]["name"] == "fullscreen"


def test_vlc_player_fullscreen_setter_execute_request_error(monkeypatch):
    setter = VlcPlayerFullScreenSetter(FakeZtvWindow(0))

    def fail(*args, **kwargs):
        raise requests.exceptions.RequestException("error")

    monkeypatch.setattr(ztv_module.requests, "post", fail)
    setter.executeHttpRequest()
