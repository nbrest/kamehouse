from PyQt5 import QtCore
from PyQt5.QtCore import QTimer
from PyQt5.QtWidgets import QLabel
from PyQt5.QtGui import QMovie
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouse_desktop_cfg
from effects.drop_shadow_effect import DropShadowEffect
from effects.opacity_effect import OpacityEffect

class MovieWidget(QLabel):
    def __init__(self, widget_name, window):
        super().__init__(window)
        logger.info("Initializing " + widget_name)
        if (kamehouse_desktop_cfg.getBoolean(widget_name, 'hidden')):
            logger.debug(widget_name + " is set to hidden")
            self.setHidden(True)
            return
        pos_x = kamehouse_desktop_cfg.getInt(widget_name, 'pos_x')
        pos_y = kamehouse_desktop_cfg.getInt(widget_name, 'pos_y')
        width = kamehouse_desktop_cfg.getInt(widget_name, 'width')
        height = kamehouse_desktop_cfg.getInt(widget_name, 'height')
        self.widget_name = widget_name
        self.setGeometry(pos_x, pos_y, width, height)
        self.setScaledContents(kamehouse_desktop_cfg.getBoolean(widget_name, 'scaled_contents')) 
        self.setMinimumSize(QtCore.QSize(width, height))
        self.setMaximumSize(QtCore.QSize(width, height))
        if (kamehouse_desktop_cfg.getBoolean(widget_name, 'use_drop_shadow')):
            DropShadowEffect(self, widget_name)
        if (kamehouse_desktop_cfg.getBoolean(widget_name, 'use_opacity')):
            OpacityEffect(self, widget_name)
        self.setHidden(False)

    def start(self):
        self.movie = QMovie(kamehouse_desktop_cfg.get(self.widget_name, 'movie_src'))
        self.movie.setCacheMode(QMovie.CacheAll)
        self.setMovie(self.movie)
        self.movie.start()
