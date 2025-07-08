from PyQt5.QtWidgets import QLabel
from PyQt5.QtGui import QPixmap
from loguru import logger

class KameHouseLogoWidget(QLabel):
    def __init__(self, window):
        super().__init__(window)
        logger.info("Initializing kamehouse logo widget")
        self.setPixmap(QPixmap('lib/ico/kamehouse.png'))
        self.setGeometry(1660, 100, 60, 60)
        self.setScaledContents(True) 
        