from PyQt5.QtWidgets import QLabel
from PyQt5.QtCore import Qt
from loguru import logger

from effects.drop_shadow_effect import DropShadowEffect

class KameHouseKatakanaWidget(QLabel):
    def __init__(self, window):
        super().__init__("カメハウス", window)
        logger.info("Initializing kamehouse katakana widget")
        self.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.setStyleSheet("color: #c0c0c0; font-size: 30px; background-color: transparent;")
        self.setGeometry(150, 950, 150, 100)
        DropShadowEffect(self)
        