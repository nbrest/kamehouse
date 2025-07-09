from PyQt5.QtWidgets import QWidget
from PyQt5.QtCore import QTimer, QTime, Qt
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
from widgets.text import TextWidget

class ClockWidget(QWidget):
    def __init__(self, window):
        super().__init__()
        logger.info("Initializing clock widget")
        self.text = TextWidget('clock_text_widget', "00:00", window)
        self.updateTime()
        timer = QTimer(window)
        timer.timeout.connect(window.updateClockTime)
        timer.start(15000)
    
    def updateTime(self):
        currentTime = QTime.currentTime()
        formattedCurrentTime = currentTime.toString('hh:mm')
        if (kamehouseDesktopCfg.getBoolean('clock_widget', 'trace_log_enabled')):
            logger.trace("Updating clock time to " + formattedCurrentTime)
        self.text.setText(formattedCurrentTime)
