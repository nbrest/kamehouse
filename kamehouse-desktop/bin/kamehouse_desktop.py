import sys
import subprocess

from PyQt5 import QtGui
from PyQt5.QtWidgets import QApplication, QMainWindow
from PyQt5.QtCore import Qt
from loguru import logger

from widgets.hostname import HostnameWidget
from widgets.kamehouse_logo import KameHouseLogoWidget
from widgets.kamehouse_katakana import KameHouseKatakanaWidget
from widgets.world_cup_logo import WorldCupLogoWidget

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

    def setWindowProperties(self):
        logger.debug("Setting main window properties")
        self.setWindowTitle("KameHouse - Desktop")
        self.setWindowIcon(QtGui.QIcon('lib/ico/kamehouse.png'))
        # Qt.WindowType.WindowStaysOnBottomHint 
        # Qt.WindowType.WindowStaysOnTopHint
        self.setWindowFlags(Qt.WindowType.FramelessWindowHint | Qt.WindowType.WindowStaysOnBottomHint)
        self.setAttribute(Qt.WidgetAttribute.WA_TranslucentBackground)
        self.setStyleSheet("background-color: transparent;")
 
    # this is needed on raspberrypi to render transparent backgrounds
    def startCompositor(self):
        logger.debug("Starting compositor")
        process = subprocess.Popen("picom", shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        # process = subprocess.Popen("xcompmgr", shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

    def initLogger(self):
        logger.remove(0)
        logger.add(sys.stdout, level="DEBUG")

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = KameHouseDesktop()
    sys.exit(app.exec_())
