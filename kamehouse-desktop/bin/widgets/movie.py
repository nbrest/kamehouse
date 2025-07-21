from PyQt5 import QtCore
from PyQt5.QtCore import QTimer
from PyQt5.QtWidgets import QLabel
from PyQt5.QtGui import QMovie
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
from effects.drop_shadow_effect import DropShadowEffect
from effects.opacity_effect import OpacityEffect

class MovieWidget(QLabel):
    def __init__(self, widgetName, window):
        super().__init__(window)
        logger.info("Initializing " + widgetName)
        if (kamehouseDesktopCfg.getBoolean(widgetName, 'hidden')):
            logger.debug(widgetName + " is set to hidden")
            self.setHidden(True)
            return
        posX = kamehouseDesktopCfg.getInt(widgetName, 'pos_x')
        posY = kamehouseDesktopCfg.getInt(widgetName, 'pos_y')
        width = kamehouseDesktopCfg.getInt(widgetName, 'width')
        height = kamehouseDesktopCfg.getInt(widgetName, 'height')
        self.widgetName = widgetName
        self.setGeometry(posX, posY, width, height)
        self.setScaledContents(kamehouseDesktopCfg.getBoolean(widgetName, 'scaled_contents')) 
        self.setMinimumSize(QtCore.QSize(width, height))
        self.setMaximumSize(QtCore.QSize(width, height))
        if (kamehouseDesktopCfg.getBoolean(widgetName, 'use_drop_shadow')):
            DropShadowEffect(self, widgetName)
        if (kamehouseDesktopCfg.getBoolean(widgetName, 'use_opacity')):
            OpacityEffect(self, widgetName)
        self.setHidden(False)

    def start(self):
        self.movie = QMovie(kamehouseDesktopCfg.get(self.widgetName, 'movie_src'))
        self.movie.setCacheMode(QMovie.CacheAll)
        self.setMovie(self.movie)
        self.movie.start()
