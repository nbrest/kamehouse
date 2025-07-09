import sys
import subprocess

from PyQt5 import QtGui
from PyQt5.QtWidgets import QApplication, QMainWindow
from PyQt5.QtCore import Qt
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
from widgets.hostname import HostnameWidget
from widgets.kamehouse_logo import KameHouseLogoWidget
from widgets.kamehouse_katakana import KameHouseKatakanaWidget
from widgets.world_cup_logo import WorldCupLogoWidget
from widgets.clock import ClockWidget

class KameHouseDesktop(QMainWindow):
    def __init__(self):
        super().__init__()
        self.initLogger()
        logger.info("Starting kamehouse-desktop")
        self.startCompositor()
        self.setWindowProperties()
        self.initWidgets()
        self.showFullScreen()

    def initWidgets(self):
        HostnameWidget(self)
        KameHouseLogoWidget(self)
        KameHouseKatakanaWidget(self)
        WorldCupLogoWidget(self)
        self.clock = ClockWidget(self)

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

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = KameHouseDesktop()
    sys.exit(app.exec_())
