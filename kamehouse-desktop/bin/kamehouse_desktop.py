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
        self.app = app
        logger.info("Starting kamehouse-desktop")
        self.startCompositor()
        self.setWindowProperties()
        self.initWidgets()
        self.showFullScreen()

    def initWidgets(self):
        self.hostname = TextWidget('hostname_widget', self.getHostname(), self)
        self.logo = ImageWidget('kamehouse_logo_widget', self)
        self.katakana = TextWidget('kamehouse_katakana_widget', "カメハウス", self)
        self.world_cup_logo = ImageWidget('world_cup_logo_widget', self)
        self.clock = ClockWidget(self)
        self.weather = WeatherWidget(self)
        self.background_slideshow = BackgroundSlideshowWidget(self)

    def initDesktop(self):
        self.ztv_player = ZtvPlayerWidget(self)
        self.ztv_player.initSyncThreads()
        self.ztv_player.resetVlcPlayerFullScreen()

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
 
    def keyPressEvent(self, event):
        if event.key() == Qt.Key_Q:
            self.pressedQKeyAction()

    def pressedQKeyAction(self):
        logger.info("Captured key 'q' press. Extiting kamehouse-desktop")
        QApplication.quit()

    def getHostname(self):
        hostname = socket.gethostname()
        if (kamehouse_desktop_cfg.getBoolean('hostname_widget', 'format_hostname')):
            hostname = hostname.replace("-", " ").replace("_", " ").replace(".", " ")
        return hostname

    # this is needed on raspberrypi to render transparent backgrounds
    def startCompositor(self):
        if (kamehouse_desktop_cfg.getBoolean('kamehouse_desktop', 'execute_compositor')):
            compositor_command = kamehouse_desktop_cfg.get('kamehouse_desktop', 'compositor_command')
            logger.debug("Starting compositor " + compositor_command)
            process = subprocess.Popen(compositor_command, shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        else:
            logger.debug("Skipping compositor")
        
    def initLogger(self):
        logger.remove(0)
        log_level = kamehouse_desktop_cfg.get('kamehouse_desktop', 'log_level')
        logger.add(sys.stdout, level=log_level)
        logger.trace("trace logging is enabled")

    def updateClockTime(self):
        self.clock.updateTime()

    def updateWeatherStatus(self):
        self.weather.updateStatus()

    def updateZtvPlayerView(self):
        self.ztv_player.updateView()

    def setZtvPlayerRandomLogo(self):
        self.ztv_player.setRandomLogo()
        
if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = KameHouseDesktop(app)
    window.initDesktop()
    sys.exit(app.exec_())
