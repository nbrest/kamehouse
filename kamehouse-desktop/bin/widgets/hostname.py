import socket

from PyQt5.QtWidgets import QLabel
from PyQt5.QtCore import Qt
from loguru import logger

from effects.drop_shadow_effect import DropShadowEffect

class HostnameWidget(QLabel):
    def __init__(self, window):
        super().__init__(socket.gethostname(), window)
        logger.info("Initializing hostname widget")
        self.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.setStyleSheet("color: white; font-size: 40px; background-color: transparent;")
        self.setGeometry(1200, 80, 450, 100)
        DropShadowEffect(self)
        