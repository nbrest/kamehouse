from PyQt5.QtWidgets import QLabel
from PyQt5.QtGui import QPixmap
from loguru import logger
from time import strftime

from config.kamehouse_desktop_cfg import kamehouse_desktop_cfg
from effects.drop_shadow_effect import DropShadowEffect
from effects.opacity_effect import OpacityEffect

class ImageWidget(QLabel):
    def __init__(self, widget_name, window):
        super().__init__(window)
        logger.info("Initializing " + widget_name)
        if (kamehouse_desktop_cfg.getBoolean(widget_name, 'hidden')):
            logger.debug(widget_name + " is set to hidden")
            self.setHidden(True)
            return
        if (kamehouse_desktop_cfg.getBoolean(widget_name, 'use_christmas') and self.isChristmasTime()):
            self.img_src = QPixmap(kamehouse_desktop_cfg.get(widget_name, 'img_src_christmas'))
        else:
            self.img_src = QPixmap(kamehouse_desktop_cfg.get(widget_name, 'img_src'))
        self.setPixmap(self.img_src)
        pos_x = kamehouse_desktop_cfg.getInt(widget_name, 'pos_x')
        pos_y = kamehouse_desktop_cfg.getInt(widget_name, 'pos_y')
        width = kamehouse_desktop_cfg.getInt(widget_name, 'width')
        height = kamehouse_desktop_cfg.getInt(widget_name, 'height')
        self.setGeometry(pos_x, pos_y, width, height)
        self.setScaledContents(kamehouse_desktop_cfg.getBoolean(widget_name, 'scaled_contents')) 
        if (kamehouse_desktop_cfg.getBoolean(widget_name, 'use_drop_shadow')):
            DropShadowEffect(self, widget_name)
        if (kamehouse_desktop_cfg.getBoolean(widget_name, 'use_opacity')):
            OpacityEffect(self, widget_name)
        self.setHidden(False)
    
    def isChristmasTime(self):
        return strftime("%b") == "Dec"
