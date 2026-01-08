from PyQt5 import QtGui
from PyQt5.QtWidgets import QGraphicsDropShadowEffect

from config.kamehouse_desktop_cfg import kamehouse_desktop_cfg

class DropShadowEffect(QGraphicsDropShadowEffect):
    def __init__(self, item, widget_name):
        super().__init__()
        self.setBlurRadius(kamehouse_desktop_cfg.getInt(widget_name, 'drop_shadow_blur_radius'))
        self.setColor(QtGui.QColor(kamehouse_desktop_cfg.get(widget_name, 'drop_shadow_color')))
        offset_x = kamehouse_desktop_cfg.getInt(widget_name, 'drop_shadow_offset_x')
        offset_y = kamehouse_desktop_cfg.getInt(widget_name, 'drop_shadow_offset_y')
        self.setOffset(offset_x, offset_y)
        item.setGraphicsEffect(self)
