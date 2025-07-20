from PyQt5 import QtGui
from PyQt5.QtWidgets import QGraphicsDropShadowEffect

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg

class DropShadowEffect(QGraphicsDropShadowEffect):
    def __init__(self, item, widgetName):
        super().__init__()
        self.setBlurRadius(kamehouseDesktopCfg.getInt(widgetName, 'drop_shadow_blur_radius'))
        self.setColor(QtGui.QColor(kamehouseDesktopCfg.get(widgetName, 'drop_shadow_color')))
        offsetX = kamehouseDesktopCfg.getInt(widgetName, 'drop_shadow_offset_x')
        offsetY = kamehouseDesktopCfg.getInt(widgetName, 'drop_shadow_offset_y')
        self.setOffset(offsetX, offsetY)
        item.setGraphicsEffect(self)
