import sys
import subprocess
import socket
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import QApplication, QMainWindow, QLabel, QGraphicsDropShadowEffect
from PyQt5.QtCore import Qt
from PyQt5.QtGui import QPixmap

class KameHouseDesktop(QMainWindow):
    def __init__(self):
        super().__init__()
        self.startCompositor()
        self.setWindowProperties()
        self.addHostnameWidget()
        self.addKameHouseLogoWidget()
        self.addKameHouseKatakanaWidget()
        self.addWorldCupLogoWidget()
        self.showFullScreen()

    def setWindowProperties(self):
        self.setWindowTitle("KameHouse - Desktop")
        self.setWindowIcon(QtGui.QIcon('lib/ico/kamehouse.png'))
        # Qt.WindowType.WindowStaysOnBottomHint 
        # Qt.WindowType.WindowStaysOnTopHint
        self.setWindowFlags(Qt.WindowType.FramelessWindowHint | Qt.WindowType.WindowStaysOnBottomHint)
        self.setAttribute(Qt.WidgetAttribute.WA_TranslucentBackground)
        self.setStyleSheet("background-color: transparent;")

    def addHostnameWidget(self):
        self.hostname = QLabel(socket.gethostname(), self)
        self.hostname.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.hostname.setStyleSheet("color: white; font-size: 40px; background-color: transparent;")
        self.hostname.setGeometry(1400, 25, 450, 100)
        self.addShadowEffect(self.hostname)

    def addKameHouseKatakanaWidget(self):
        self.kameHouseKatakana = QLabel("カメハウス", self)
        self.kameHouseKatakana.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.kameHouseKatakana.setStyleSheet("color: #c0c0c0; font-size: 30px; background-color: transparent;")
        self.kameHouseKatakana.setGeometry(50, 1000, 150, 100)
        self.addShadowEffect(self.kameHouseKatakana)
 
    def addWorldCupLogoWidget(self):
        self.kameHouseLogo = QLabel(self)
        self.kameHouseLogoPixmap = QPixmap('lib/ui/img/sports/world-cup.png') 
        self.kameHouseLogo.setPixmap(self.kameHouseLogoPixmap)
        self.kameHouseLogo.setGeometry(14, 1039, 25, 25)
        self.kameHouseLogo.setScaledContents(True) 

    def addKameHouseLogoWidget(self):
        self.kameHouseLogo = QLabel(self)
        self.kameHouseLogoPixmap = QPixmap('lib/ico/kamehouse.png') 
        self.kameHouseLogo.setPixmap(self.kameHouseLogoPixmap)
        self.kameHouseLogo.setGeometry(1850, 45, 60, 60)
        self.kameHouseLogo.setScaledContents(True) 

    def addShadowEffect(self, item):
        effect = QtWidgets.QGraphicsDropShadowEffect()
        effect.setBlurRadius(10)
        effect.setColor(QtGui.QColor("black"))
        effect.setOffset(4,4)
        item.setGraphicsEffect(effect)

    def startCompositor(self):
        process = subprocess.Popen("picom", shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        # process = subprocess.Popen("xcompmgr", shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = KameHouseDesktop()
    sys.exit(app.exec_())
