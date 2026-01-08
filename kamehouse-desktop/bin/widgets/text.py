import sys, math

from PyQt5.QtWidgets import QLabel
from PyQt5.QtCore import Qt
from PyQt5.QtGui import QBrush, QColor, QPen, QPainter, QPainterPath, QFontMetrics

from loguru import logger

from config.kamehouse_desktop_cfg import kamehouse_desktop_cfg
from effects.drop_shadow_effect import DropShadowEffect
from effects.opacity_effect import OpacityEffect

class TextWidget(QLabel):
    def __init__(self, widget_name, text, window):
        super().__init__(text, window)
        logger.info("Initializing " + widget_name)
        if (kamehouse_desktop_cfg.getBoolean(widget_name, 'hidden')):
            logger.debug(widget_name + " is set to hidden")
            self.setHidden(True)
            return
        align = kamehouse_desktop_cfg.get(widget_name, 'align')
        if (align == "left"):
            self.setAlignment(Qt.AlignmentFlag.AlignVCenter | Qt.AlignmentFlag.AlignLeft)
        elif (align == "right"):
            self.setAlignment(Qt.AlignmentFlag.AlignVCenter | Qt.AlignmentFlag.AlignRight)
        else:
            self.setAlignment(Qt.AlignmentFlag.AlignVCenter | Qt.AlignmentFlag.AlignCenter)
        self.setStyleSheet(kamehouse_desktop_cfg.get(widget_name, 'stylesheet'))
        pos_x = kamehouse_desktop_cfg.getInt(widget_name, 'pos_x')
        pos_y = kamehouse_desktop_cfg.getInt(widget_name, 'pos_y')
        width = kamehouse_desktop_cfg.getInt(widget_name, 'width')
        height = kamehouse_desktop_cfg.getInt(widget_name, 'height')
        self.setGeometry(pos_x, pos_y, width, height)
        if (kamehouse_desktop_cfg.getBoolean(widget_name, 'use_drop_shadow')):
            DropShadowEffect(self, widget_name)
        if (kamehouse_desktop_cfg.getBoolean(widget_name, 'use_opacity')):
            OpacityEffect(self, widget_name)
        self.setHidden(False)
                
class OutlinedTextWidget(TextWidget):
    def __init__(self, widget_name, text, window):
        super().__init__(widget_name, text, window)
        self.widget_name = widget_name
        self.outline_px = kamehouse_desktop_cfg.getInt(self.widget_name, 'outline_size_px')
        self.setBrush()
        self.setPen()

    def setBrush(self):
        self.brush = QBrush(QColor(kamehouse_desktop_cfg.get(self.widget_name, 'text_color')), Qt.SolidPattern)

    def setPen(self):
        self.pen = QPen(QColor(kamehouse_desktop_cfg.get(self.widget_name, 'outline_color')))
        self.pen.setJoinStyle(Qt.RoundJoin)
    
    def paintEvent(self, event):
        rect = self.rect()
        metrics = QFontMetrics(self.font())
        tr = metrics.boundingRect(self.text()).adjusted(0, 0, self.outline_px, self.outline_px)
        if self.indent() == -1:
            if self.frameWidth():
                indent = (metrics.boundingRect('x').width() + self.outline_px * 2) / 2
            else:
                indent = self.outline_px
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

        self.pen.setWidthF(self.outline_px * 2)
        qp.strokePath(path, self.pen)
        if 1 < self.brush.style() < 15:
            qp.fillPath(path, self.palette().window())
        qp.fillPath(path, self.brush)
