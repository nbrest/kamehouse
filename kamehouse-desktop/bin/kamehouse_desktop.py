import sys
import subprocess
import socket

from PyQt5 import QtGui
from PyQt5.QtWidgets import QApplication, QMainWindow
from PyQt5.QtCore import Qt
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
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
        self.app = app
        logger.info("Starting kamehouse-desktop")
        self.startCompositor()
        self.setWindowProperties()
        self.initWidgets()
        self.showFullScreen()

    def initWidgets(self):
        self.hostname = TextWidget('hostname_widget', socket.gethostname(), self)
        self.logo = ImageWidget('kamehouse_logo_widget', self)
        self.katakana = TextWidget('kamehouse_katakana_widget', "カメハウス", self)
        self.worldCupLogo = ImageWidget('world_cup_logo_widget', self)
        self.clock = ClockWidget(self)
        self.weather = WeatherWidget(self)
        self.backgroundSlideshow = BackgroundSlideshowWidget(self)

    def initDesktop(self):
        self.ztvPlayer = ZtvPlayerWidget(self)
        self.ztvPlayer.initSyncThreads()
        self.ztvPlayer.resetVlcPlayerFullScreen()

    def setWindowProperties(self):
        logger.debug("Setting main window properties")
        self.setWindowTitle("KameHouse - Desktop")
        self.setWindowIcon(QtGui.QIcon(kamehouseDesktopCfg.get('kamehouse_desktop', 'icon_src')))
        if (kamehouseDesktopCfg.getBoolean('kamehouse_desktop', 'stays_on_bottom')):
            self.setWindowFlags(Qt.WindowType.FramelessWindowHint | Qt.WindowType.WindowStaysOnBottomHint)
        else:
            self.setWindowFlags(Qt.WindowType.FramelessWindowHint | Qt.WindowType.WindowStaysOnTopHint)        
        self.setAttribute(Qt.WidgetAttribute.WA_TranslucentBackground)
        self.setStyleSheet(kamehouseDesktopCfg.get('kamehouse_desktop', 'stylesheet'))
 
    # this is needed on raspberrypi to render transparent backgrounds
    def startCompositor(self):
        if (kamehouseDesktopCfg.getBoolean('kamehouse_desktop', 'execute_compositor')):
            compositorCommand = kamehouseDesktopCfg.get('kamehouse_desktop', 'compositor_command')
            logger.debug("Starting compositor " + compositorCommand)
            process = subprocess.Popen(compositorCommand, shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        else:
            logger.debug("Skipping compositor")
        
    def initLogger(self):
        logger.remove(0)
        logLevel = kamehouseDesktopCfg.get('kamehouse_desktop', 'log_level')
        logger.add(sys.stdout, level=logLevel)
        logger.trace("trace logging is enabled")

    def updateClockTime(self):
        self.clock.updateTime()

    def updateWeatherStatus(self):
        self.weather.updateStatus()

    def updateZtvPlayerView(self):
        self.ztvPlayer.updateView()

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = KameHouseDesktop(app)
    window.initDesktop()
    sys.exit(app.exec_())
