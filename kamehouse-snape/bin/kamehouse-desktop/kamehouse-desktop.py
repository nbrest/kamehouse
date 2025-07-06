import sys
import subprocess
from PyQt5.QtWidgets import QApplication, QMainWindow, QLabel
from PyQt5.QtCore import Qt

class KameHouseDesktop(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("KameHouse - Desktop")

        self.setWindowFlags(Qt.WindowType.FramelessWindowHint | Qt.WindowType.WindowStaysOnTopHint)
        self.setAttribute(Qt.WidgetAttribute.WA_TranslucentBackground)
        self.setStyleSheet("background-color: transparent;")

        label = QLabel("kamehouse-desktop", self)
        label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        label.setStyleSheet("color: blue; font-size: 100px; background-color: transparent;")
        self.setCentralWidget(label)

        self.showFullScreen()

if __name__ == "__main__":
    process = subprocess.Popen("picom", shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    app = QApplication(sys.argv)
    window = KameHouseDesktop()
    sys.exit(app.exec_())