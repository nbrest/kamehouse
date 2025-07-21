from PyQt5 import QtGui
from PyQt5.QtWidgets import QGraphicsOpacityEffect

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg

class OpacityEffect(QGraphicsOpacityEffect):
    def __init__(self, item, widgetName):
        super().__init__()
        self.setOpacity(kamehouseDesktopCfg.getFloat(widgetName, 'opacity'))
        item.setGraphicsEffect(self)
