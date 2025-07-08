from PyQt5 import QtGui
from PyQt5.QtWidgets import QGraphicsDropShadowEffect
from PyQt5.QtCore import Qt

class DropShadowEffect(QGraphicsDropShadowEffect):
    def __init__(self, item):
        super().__init__()
        self.setBlurRadius(10)
        self.setColor(QtGui.QColor("black"))
        self.setOffset(4,4)
        item.setGraphicsEffect(self)
