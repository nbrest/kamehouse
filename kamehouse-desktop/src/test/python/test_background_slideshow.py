from unittest.mock import MagicMock

import pytest
from PyQt5.QtGui import QImage
from PyQt5.QtWidgets import QWidget

from widgets import background_slideshow as background_module
from widgets.background_slideshow import BackgroundImage, BackgroundSlideshowWidget


def create_image(path, width, height):
    image = QImage(width, height, QImage.Format_RGB32)
    image.fill(0)
    assert image.save(str(path))
    return path


@pytest.fixture
def desktop_home(tmp_path, monkeypatch, desktop_config, app):
    home = tmp_path / "home"
    backgrounds = home / ".kamehouse" / "data" / "desktop" / "backgrounds"
    backgrounds.mkdir(parents=True)
    create_image(backgrounds / "landscape.png", 100, 50)
    create_image(backgrounds / "portrait-one.png", 50, 100)
    create_image(backgrounds / "portrait-two.png", 60, 100)
    (home / ".kamehouse" / "data" / "desktop" / "backgrounds-success.list").write_text("")
    (home / ".kamehouse" / "data" / "desktop" / "backgrounds-error.list").write_text("")
    monkeypatch.setattr(background_module.os.path, "expanduser", lambda _: str(home))
    desktop_config["background_slideshow_widget"]["images_src_path"] = "/.kamehouse/data/desktop/backgrounds"
    return home


@pytest.fixture
def window(app):
    parent = QWidget()
    parent.app = app
    return parent


def create_widget(window):
    return BackgroundSlideshowWidget(window)


def test_init_loads_background_images(window, desktop_home):
    widget = create_widget(window)
    assert len(widget.background_images) == 3
    assert len(widget.portrait_background_images) == 2
    assert widget.background.img_src is not None
    assert widget.background.anim_group is not None


def test_hidden_background_widget(app, desktop_home, desktop_config):
    desktop_config["background_slideshow_widget"]["hidden"] = "true"
    parent = QWidget()
    widget = BackgroundSlideshowWidget(parent)
    assert widget.isHidden() is True


def test_is_valid_image_file(window, desktop_home):
    widget = create_widget(window)
    assert widget.isValidImageFile(None) is False
    for extension in [".jpeg", ".jpg", ".heic", ".bmp", ".png", ".webp", ".JPEG"]:
        assert widget.isValidImageFile("image" + extension) is True
    assert widget.isValidImageFile("image.gif") is False


def test_add_background_to_lists(window, desktop_home):
    widget = create_widget(window)
    initial_count = len(widget.background_images)
    widget.addBackgroundToLists("invalid.gif")
    assert len(widget.background_images) == initial_count
    widget.addBackgroundToLists(str(desktop_home / ".kamehouse/data/desktop/backgrounds/landscape.png"))
    assert len(widget.background_images) == initial_count + 1


def test_get_background_image_landscape_and_portrait(window, desktop_home):
    widget = create_widget(window)
    landscape = widget.getBackgroundImage(
        str(desktop_home / ".kamehouse/data/desktop/backgrounds/landscape.png"))
    portrait = widget.getBackgroundImage(
        str(desktop_home / ".kamehouse/data/desktop/backgrounds/portrait-one.png"))
    assert landscape.getWidth() == 100
    assert landscape.getHeight() == 50
    assert landscape.getPortrait() is False
    assert portrait.getPortrait() is True


def test_get_background_image_unreadable(window, desktop_home):
    widget = create_widget(window)
    assert widget.getBackgroundImage("does-not-exist.png") is None


def test_get_background_image_invalid_size(window, desktop_home, monkeypatch):
    widget = create_widget(window)

    class InvalidSize:
        def isValid(self):
            return False

    class Reader:
        def __init__(self, path):
            pass

        def canRead(self):
            return True

        def size(self):
            return InvalidSize()

    monkeypatch.setattr(background_module, "QImageReader", Reader)
    assert widget.getBackgroundImage("image.png") is None


def test_get_background_image_zero_dimensions(window, desktop_home, monkeypatch):
    widget = create_widget(window)

    class Size:
        def __init__(self, width, height):
            self.width_value = width
            self.height_value = height

        def isValid(self):
            return True

        def width(self):
            return self.width_value

        def height(self):
            return self.height_value

    class Reader:
        def __init__(self, path):
            pass

        def canRead(self):
            return True

        def size(self):
            return Size(0, 10)

    monkeypatch.setattr(background_module, "QImageReader", Reader)
    assert widget.getBackgroundImage("image.png") is None


def test_configure_expand_contract_landscape(window, desktop_home):
    widget = create_widget(window)
    widget.screen_width = 1000
    widget.screen_height = 500
    widget.expand_px = 10
    widget.background.img_src = background_module.QPixmap(200, 100)
    widget.configureExpandContractParameters()
    assert widget.background.contract.endValue().width() == 1000
    assert widget.background.contract.endValue().height() == 500


def test_configure_expand_contract_portrait(window, desktop_home):
    widget = create_widget(window)
    widget.screen_width = 1000
    widget.screen_height = 500
    widget.expand_px = 10
    widget.background.img_src = background_module.QPixmap(50, 100)
    widget.configureExpandContractParameters()
    assert widget.background.contract.endValue().height() == 500
    assert widget.background.contract.endValue().width() == 250


def test_configure_expand_contract_invalid_images(window, desktop_home):
    widget = create_widget(window)
    widget.background.img_src = background_module.QPixmap(0, 0)
    widget.random_image = BackgroundImage()
    widget.random_image.setPortrait(True)
    widget.configureExpandContractParameters()

    widget.random_image.setPortrait(False)
    widget.random_image.setFilename("broken.png")
    widget.configureExpandContractParameters()


def test_restart_background_animation(window, desktop_home, monkeypatch):
    widget = create_widget(window)
    widget.log_trace = True
    monkeypatch.setattr(widget, "selectRandomBackgroundImage", MagicMock())
    monkeypatch.setattr(widget, "configureExpandContractParameters", MagicMock())
    widget.background.anim_group = MagicMock()
    widget.restartBackgroundAnimation()
    widget.background.anim_group.start.assert_called_once()
    widget.selectRandomBackgroundImage.assert_called_once()
    widget.configureExpandContractParameters.assert_called_once()

    widget.log_trace = False
    widget.restartBackgroundAnimation()


def test_select_random_background_image_branches(window, desktop_home, monkeypatch):
    widget = create_widget(window)
    portrait = BackgroundImage()
    portrait.setPortrait(True)
    landscape = BackgroundImage()
    landscape.setPortrait(False)

    monkeypatch.setattr(widget, "getRandomImage", lambda: portrait)
    monkeypatch.setattr(widget, "setPortraitBackground", MagicMock())
    monkeypatch.setattr(widget, "setLandscapeBackground", MagicMock())
    widget.selectRandomBackgroundImage()
    widget.setPortraitBackground.assert_called_once()

    monkeypatch.setattr(widget, "getRandomImage", lambda: landscape)
    widget.selectRandomBackgroundImage()
    widget.setLandscapeBackground.assert_called_once()


def test_set_portrait_and_landscape_backgrounds(window, desktop_home):
    widget = create_widget(window)
    paths = [
        str(desktop_home / ".kamehouse/data/desktop/backgrounds/portrait-one.png"),
        str(desktop_home / ".kamehouse/data/desktop/backgrounds/portrait-two.png"),
    ]
    first = BackgroundImage()
    first.setFilename(paths[0])
    first.setPortrait(True)
    second = BackgroundImage()
    second.setFilename(paths[1])
    second.setPortrait(True)
    widget.random_image = first
    widget.portrait_background_images = [first, second]
    widget.setPortraitBackground()
    assert widget.background.img_src is not None

    landscape = BackgroundImage()
    landscape.setFilename(str(desktop_home / ".kamehouse/data/desktop/backgrounds/landscape.png"))
    widget.random_image = landscape
    widget.setLandscapeBackground()
    assert widget.background.img_src is not None


def test_log_selected_image_branches(window, desktop_home):
    widget = create_widget(window)
    image = BackgroundImage()
    widget.log_trace = False
    widget.logSelectedImage(image)
    widget.log_trace = True
    widget.log_background_images = False
    widget.logSelectedImage(image)
    widget.log_background_images = True
    widget.logSelectedImage(image)


def test_set_background_images_list_default_fallback(window, desktop_home, monkeypatch):
    widget = create_widget(window)
    widget.background_images = []
    calls = []
    monkeypatch.setattr(widget, "addBackgroundsToLists", lambda path: calls.append(path))
    widget.setBackgroundImagesList()
    assert "lib/ui/img/banners" in calls


def test_update_background_image_list_file(window, desktop_home, desktop_config):
    widget = create_widget(window)
    list_file = "/.kamehouse/data/desktop/backgrounds-success.list"
    (desktop_home / list_file.lstrip("/")).write_text("")
    widget.updateBackgroundImageListFile(list_file, "first.png")
    widget.updateBackgroundImageListFile(list_file, "first.png")
    assert (desktop_home / list_file.lstrip("/")).read_text() == "first.png\n"

    desktop_config["background_slideshow_widget"]["skip_update_backgrounds_list_files"] = "true"
    widget.updateBackgroundImageListFile(list_file, "second.png")
    assert "second.png" not in (desktop_home / list_file.lstrip("/")).read_text()

    desktop_config["background_slideshow_widget"]["skip_update_backgrounds_list_files"] = "false"
    widget.updateBackgroundImageListFile("/missing/directory/file.list", "third.png")


def test_background_image_getters_setters_and_str(app):
    image = BackgroundImage()
    image.setFilename("image.png")
    image.setPortrait(True)
    image.setWidth(10)
    image.setHeight(20)
    assert image.getFilename() == "image.png"
    assert image.getPortrait() is True
    assert image.getWidth() == 10
    assert image.getHeight() == 20
    assert '"filename": "image.png"' in str(image)
