import socket

from PyQt5.QtWidgets import QLabel
from PyQt5.QtCore import Qt
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg

from effects.drop_shadow_effect import DropShadowEffect

class HostnameWidget(QLabel):
    def __init__(self, window):
        super().__init__(socket.gethostname(), window)
        logger.info("Initializing hostname widget")
        if (kamehouseDesktopCfg.getBoolean('hostname', 'hidden')):
            logger.debug("hostname widget is set to hidden")
            self.setHidden(True)
            return

        self.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.setStyleSheet(kamehouseDesktopCfg.get('hostname', 'stylesheet'))
        posX = kamehouseDesktopCfg.getInt('hostname', 'pos_x')
        posY = kamehouseDesktopCfg.getInt('hostname', 'pos_y')
        width = kamehouseDesktopCfg.getInt('hostname', 'width')
        height = kamehouseDesktopCfg.getInt('hostname', 'height')
        self.setGeometry(posX, posY, width, height)
        DropShadowEffect(self)
        