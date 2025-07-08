from PyQt5.QtWidgets import QLabel
from PyQt5.QtGui import QPixmap
from loguru import logger

class WorldCupLogoWidget(QLabel):
    def __init__(self, window):
        super().__init__(window)
        logger.info("Initializing world cup logo widget")
        self.setPixmap(QPixmap('lib/ui/img/sports/world-cup.png'))
        self.setGeometry(90, 980, 45, 45)
        self.setScaledContents(True) 
        