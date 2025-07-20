from PyQt5.QtWidgets import QLabel
from PyQt5.QtCore import Qt
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
from effects.drop_shadow_effect import DropShadowEffect

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
            self.setAlignment(Qt.AlignmentFlag.AlignLeft)
        elif (align == "right"):
            self.setAlignment(Qt.AlignmentFlag.AlignRight)
        else:
            self.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.setStyleSheet(kamehouseDesktopCfg.get(widgetName, 'stylesheet'))
        posX = kamehouseDesktopCfg.getInt(widgetName, 'pos_x')
        posY = kamehouseDesktopCfg.getInt(widgetName, 'pos_y')
        width = kamehouseDesktopCfg.getInt(widgetName, 'width')
        height = kamehouseDesktopCfg.getInt(widgetName, 'height')
        self.setGeometry(posX, posY, width, height)
        if (kamehouseDesktopCfg.getBoolean(widgetName, 'use_drop_shadow')):
            DropShadowEffect(self, widgetName)
        self.setHidden(False)
        