import sys, math

from PyQt5.QtWidgets import QLabel
from PyQt5.QtCore import Qt
from PyQt5.QtGui import QBrush, QColor, QPen, QPainter, QPainterPath, QFontMetrics

from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
from effects.drop_shadow_effect import DropShadowEffect
from effects.opacity_effect import OpacityEffect

class TextWidget(QLabel):
    def __init__(self, widgetName, text, window):
        super().__init__(text, window)
        logger.info("Initializing " + widgetName)
        if (kamehouseDesktopCfg.getBoolean(widgetName, 'hidden')):
            logger.debug(widgetName + " is set to hidden")
            self.setHidden(True)
            return
        align = kamehouseDesktopCfg.get(widgetName, 'align')
        if (align == "left"):
            self.setAlignment(Qt.AlignmentFlag.AlignVCenter | Qt.AlignmentFlag.AlignLeft)
        elif (align == "right"):
            self.setAlignment(Qt.AlignmentFlag.AlignVCenter | Qt.AlignmentFlag.AlignRight)
        else:
            self.setAlignment(Qt.AlignmentFlag.AlignVCenter | Qt.AlignmentFlag.AlignCenter)
        self.setStyleSheet(kamehouseDesktopCfg.get(widgetName, 'stylesheet'))
        posX = kamehouseDesktopCfg.getInt(widgetName, 'pos_x')
        posY = kamehouseDesktopCfg.getInt(widgetName, 'pos_y')
        width = kamehouseDesktopCfg.getInt(widgetName, 'width')
        height = kamehouseDesktopCfg.getInt(widgetName, 'height')
        self.setGeometry(posX, posY, width, height)
        if (kamehouseDesktopCfg.getBoolean(widgetName, 'use_drop_shadow')):
            DropShadowEffect(self, widgetName)
        if (kamehouseDesktopCfg.getBoolean(widgetName, 'use_opacity')):
            OpacityEffect(self, widgetName)
        self.setHidden(False)
                
class OutlinedTextWidget(TextWidget):
    def __init__(self, widgetName, text, window):
        super().__init__(widgetName, text, window)
        self.widgetName = widgetName
        self.outlinePx = kamehouseDesktopCfg.getInt(self.widgetName, 'outline_size_px')
        self.setBrush()
        self.setPen()

    def setBrush(self):
        self.brush = QBrush(QColor(kamehouseDesktopCfg.get(self.widgetName, 'text_color')), Qt.SolidPattern)

    def setPen(self):
        self.pen = QPen(QColor(kamehouseDesktopCfg.get(self.widgetName, 'outline_color')))
        self.pen.setJoinStyle(Qt.RoundJoin)
    
    def paintEvent(self, event):
        rect = self.rect()
        metrics = QFontMetrics(self.font())
        tr = metrics.boundingRect(self.text()).adjusted(0, 0, self.outlinePx, self.outlinePx)
        if self.indent() == -1:
            if self.frameWidth():
                indent = (metrics.boundingRect('x').width() + self.outlinePx * 2) / 2
            else:
                indent = self.outlinePx
        else:
            indent = self.indent()

        if self.alignment() & Qt.AlignLeft:
            try:
                x = rect.left() + indent - min(metrics.leftBearing(self.text()[0]), 0)
            except Exception as error:
                x = rect.left() + indent
        elif self.alignment() & Qt.AlignRight:
            x = rect.x() + rect.width() - indent - tr.width()
        else:
            x = (rect.width() - tr.width()) / 2
            
        if self.alignment() & Qt.AlignTop:
            y = rect.top() + indent + metrics.ascent()
        elif self.alignment() & Qt.AlignBottom:
            y = rect.y() + rect.height() - indent - metrics.descent()
        else:
            y = (rect.height() + metrics.ascent() - metrics.descent()) / 2

        path = QPainterPath()
        path.addText(x, y, self.font(), self.text())
        qp = QPainter(self)
        qp.setRenderHint(QPainter.Antialiasing)

        self.pen.setWidthF(self.outlinePx * 2)
        qp.strokePath(path, self.pen)
        if 1 < self.brush.style() < 15:
            qp.fillPath(path, self.palette().window())
        qp.fillPath(path, self.brush)
