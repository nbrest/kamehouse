import sys
import subprocess
import socket

from PyQt5 import QtGui
from PyQt5.QtWidgets import QApplication, QMainWindow
from PyQt5.QtCore import Qt
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouse_desktop_cfg
from widgets.background_slideshow import BackgroundSlideshowWidget
from widgets.clock import ClockWidget
from widgets.image import ImageWidget
from widgets.text import TextWidget
from widgets.weather import WeatherWidget
from widgets.ztv_player import ZtvPlayerWidget

class KameHouseDesktop(QMainWindow):
    def __init__(self, app):
        super().__init__()
        self.initLogger()
        logger.info("Starting kamehouse-desktop")
        self.app = app
        self.startCompositor()
        self.setWindowProperties()
        self.initWidgets()
        self.showFullScreen()

    def postInit(self):
        self.clock.postInit()
        self.weather.postInit()
        self.ztv_player.postInit()

    def initLogger(self):
        logger.remove(0)
        log_level = kamehouse_desktop_cfg.get('kamehouse_desktop', 'log_level')
        logger.add(sys.stdout, level=log_level)
        logger.trace("trace logging is enabled")

    # this is needed on raspberrypi to render transparent backgrounds
    def startCompositor(self):
        if (kamehouse_desktop_cfg.getBoolean('kamehouse_desktop', 'execute_compositor')):
            compositor_command = kamehouse_desktop_cfg.get('kamehouse_desktop', 'compositor_command')
            logger.debug("Starting compositor " + compositor_command)
            process = subprocess.Popen(compositor_command, shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        else:
            logger.debug("Skipping compositor")

    def setWindowProperties(self):
        logger.debug("Setting main window properties")
        self.setWindowTitle("KameHouse - Desktop")
        self.setWindowIcon(QtGui.QIcon(kamehouse_desktop_cfg.get('kamehouse_desktop', 'icon_src')))
        if (kamehouse_desktop_cfg.getBoolean('kamehouse_desktop', 'stays_on_bottom')):
            self.setWindowFlags(Qt.WindowType.FramelessWindowHint | Qt.WindowType.WindowStaysOnBottomHint)
        else:
            self.setWindowFlags(Qt.WindowType.FramelessWindowHint | Qt.WindowType.WindowStaysOnTopHint)        
        self.setAttribute(Qt.WidgetAttribute.WA_TranslucentBackground)
        self.setStyleSheet(kamehouse_desktop_cfg.get('kamehouse_desktop', 'stylesheet'))
 
    def initWidgets(self):
        self.hostname = TextWidget('hostname_widget', self.getHostname(), self)
        self.logo = ImageWidget('kamehouse_logo_widget', self)
        self.katakana = TextWidget('kamehouse_katakana_widget', "カメハウス", self)
        self.world_cup_logo = ImageWidget('world_cup_logo_widget', self)
        self.background_slideshow = BackgroundSlideshowWidget(self)
        self.clock = ClockWidget(self)
        self.weather = WeatherWidget(self)
        self.ztv_player = ZtvPlayerWidget(self)

    def getHostname(self):
        hostname = socket.gethostname()
        if (kamehouse_desktop_cfg.getBoolean('hostname_widget', 'format_hostname')):
            hostname = hostname.replace("-", " ").replace("_", " ").replace(".", " ")
        return hostname

    def keyPressEvent(self, event):
        if event.key() == Qt.Key_Q:
            logger.info("Captured key 'q' press. Extiting kamehouse-desktop")
            QApplication.quit()
            return
        super().keyPressEvent(event)

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = KameHouseDesktop(app)
    window.postInit()
    sys.exit(app.exec_())
