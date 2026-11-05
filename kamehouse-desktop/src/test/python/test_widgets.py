from PyQt5 import QtCore
from PyQt5.QtWidgets import QFrame, QWidget

from widgets import image as image_module
from widgets import movie as movie_module
from widgets.clock import ClockWidget
from widgets.image import ImageWidget
from widgets.movie import MovieWidget
from widgets.text import OutlinedTextWidget, TextWidget


def test_image_widget_loads_default_image(app):
    parent = QWidget()
    widget = ImageWidget("kamehouse_logo_widget", parent)
    assert widget.img_src.isNull() is False
    assert widget.isHidden() is False


def test_image_widget_uses_christmas_image(app, monkeypatch):
    monkeypatch.setattr(image_module, "strftime", lambda fmt: "Dec")
    parent = QWidget()
    widget = ImageWidget("kamehouse_logo_widget", parent)
    assert widget.isHidden() is False


def test_image_widget_hidden(app, desktop_config):
    desktop_config["kamehouse_logo_widget"]["hidden"] = "true"
    parent = QWidget()
    widget = ImageWidget("kamehouse_logo_widget", parent)
    assert widget.isHidden() is True


def test_movie_widget_start(app, monkeypatch):
    real_movie = movie_module.QMovie

    class FakeMovie(real_movie):
        def __init__(self, filename):
            super().__init__(filename)
            self.started = False
            self.cache_mode = None

        def setCacheMode(self, mode):
            self.cache_mode = mode
            super().setCacheMode(mode)

        def start(self):
            self.started = True

    monkeypatch.setattr(movie_module, "QMovie", FakeMovie)
    parent = QWidget()
    widget = MovieWidget("ztv_player_sound_wave_widget", parent)
    widget.start()
    assert widget.movie.started is True
    assert widget.movie.cache_mode == FakeMovie.CacheAll


def test_movie_widget_hidden(app, desktop_config):
    desktop_config["ztv_player_sound_wave_widget"]["hidden"] = "true"
    parent = QWidget()
    widget = MovieWidget("ztv_player_sound_wave_widget", parent)
    assert widget.isHidden() is True


def test_text_widget_alignment_and_effects(app, desktop_config):
    parent = QWidget()
    left = TextWidget("ztv_player_title_widget", "left", parent)
    right = TextWidget("hostname_widget", "right", parent)
    center = TextWidget("clock_text_widget", "center", parent)
    assert left.alignment() & QtCore.Qt.AlignLeft
    assert right.alignment() & QtCore.Qt.AlignRight
    assert center.alignment() & QtCore.Qt.AlignHCenter


def test_text_widget_hidden_and_opacity(app, desktop_config):
    desktop_config["hostname_widget"]["hidden"] = "true"
    parent = QWidget()
    widget = TextWidget("hostname_widget", "hidden", parent)
    assert widget.isHidden() is True

    desktop_config["hostname_widget"]["hidden"] = "false"
    desktop_config["hostname_widget"]["use_opacity"] = "true"
    widget = TextWidget("hostname_widget", "opacity", parent)
    assert widget.graphicsEffect() is not None


def test_outlined_text_widget_paints_all_alignments(app):
    parent = QWidget()
    widgets = [
        OutlinedTextWidget("ztv_player_title_widget", "left", parent),
        OutlinedTextWidget("weather_text_widget", "right", parent),
        OutlinedTextWidget("clock_text_widget", "center", parent),
    ]
    for widget in widgets:
        widget.resize(400, 200)
        pixmap = widget.grab()
        assert pixmap.isNull() is False


def test_outlined_text_widget_frame_and_empty_text(app, monkeypatch):
    parent = QWidget()
    widget = OutlinedTextWidget("ztv_player_title_widget", "framed", parent)
    widget.setFrameShape(QFrame.Box)
    widget.resize(300, 100)
    assert widget.grab().isNull() is False

    empty = OutlinedTextWidget("ztv_player_title_widget", "", parent)
    empty.resize(300, 100)
    assert empty.grab().isNull() is False

    icons = [
        QtCore.Qt.AlignLeft | QtCore.Qt.AlignTop,
        QtCore.Qt.AlignRight | QtCore.Qt.AlignBottom,
        QtCore.Qt.AlignCenter,
    ]
    for alignment in icons:
        widget.setAlignment(alignment)
        assert widget.grab().isNull() is False


def test_clock_widget_updates_time(app):
    parent = QWidget()
    widget = ClockWidget(parent)
    parent.clock = widget
    widget.postInit()
    widget.updateTime()
    assert len(widget.text.text()) == 5
    assert ":" in widget.text.text()
