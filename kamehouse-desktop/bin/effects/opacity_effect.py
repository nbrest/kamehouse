from PyQt5 import QtGui
from PyQt5.QtWidgets import QGraphicsOpacityEffect

from config.kamehouse_desktop_cfg import kamehouse_desktop_cfg

class OpacityEffect(QGraphicsOpacityEffect):
    def __init__(self, item, widget_name):
        super().__init__()
        self.setOpacity(kamehouse_desktop_cfg.getFloat(widget_name, 'opacity'))
        item.setGraphicsEffect(self)
