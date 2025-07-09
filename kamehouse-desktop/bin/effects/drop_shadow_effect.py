from PyQt5 import QtGui
from PyQt5.QtWidgets import QGraphicsDropShadowEffect

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg

class DropShadowEffect(QGraphicsDropShadowEffect):
    def __init__(self, item):
        super().__init__()
        self.setBlurRadius(kamehouseDesktopCfg.getInt('drop_shadow_effect', 'blur_radius'))
        self.setColor(QtGui.QColor(kamehouseDesktopCfg.get('drop_shadow_effect', 'color')))
        offsetX = kamehouseDesktopCfg.getInt('drop_shadow_effect', 'offset_x')
        offsetY = kamehouseDesktopCfg.getInt('drop_shadow_effect', 'offset_y')
        self.setOffset(offsetX, offsetY)
        item.setGraphicsEffect(self)
