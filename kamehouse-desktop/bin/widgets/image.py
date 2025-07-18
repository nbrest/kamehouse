from PyQt5.QtWidgets import QLabel
from PyQt5.QtGui import QPixmap
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg

class ImageWidget(QLabel):
    def __init__(self, widgetName, window):
        super().__init__(window)
        logger.info("Initializing " + widgetName)
        if (kamehouseDesktopCfg.getBoolean(widgetName, 'hidden')):
            logger.debug(widgetName + " is set to hidden")
            self.setHidden(True)
            return
        self.setPixmap(QPixmap(kamehouseDesktopCfg.get(widgetName, 'img_src')))
        posX = kamehouseDesktopCfg.getInt(widgetName, 'pos_x')
        posY = kamehouseDesktopCfg.getInt(widgetName, 'pos_y')
        width = kamehouseDesktopCfg.getInt(widgetName, 'width')
        height = kamehouseDesktopCfg.getInt(widgetName, 'height')
        self.setGeometry(posX, posY, width, height)
        self.setScaledContents(kamehouseDesktopCfg.getBoolean(widgetName, 'scaled_contents')) 
        self.setHidden(False)
        