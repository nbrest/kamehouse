from PyQt5.QtWidgets import QWidget

from effects.drop_shadow_effect import DropShadowEffect
from effects.opacity_effect import OpacityEffect


def test_drop_shadow_effect_uses_configuration(app):
    widget = QWidget()
    effect = DropShadowEffect(widget, "hostname_widget")
    assert widget.graphicsEffect() is effect
    assert effect.blurRadius() == 10
    assert effect.offset().x() == 4
    assert effect.offset().y() == 4


def test_opacity_effect_uses_configuration(app):
    widget = QWidget()
    effect = OpacityEffect(widget, "ztv_player_off_kintoun_widget")
    assert widget.graphicsEffect() is effect
    assert effect.opacity() == 0.9
