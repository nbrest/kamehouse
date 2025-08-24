from PyQt5.QtWidgets import QWidget
from PyQt5.QtCore import QTimer, QTime, Qt
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
from widgets.text import OutlinedTextWidget

class ClockWidget(QWidget):
    logTrace = False

    def __init__(self, window):
        super().__init__(window)
        logger.info("Initializing clock widget")
        self.logTrace = kamehouseDesktopCfg.getBoolean('clock_widget', 'trace_log_enabled')
        self.text = OutlinedTextWidget('clock_text_widget', "00:00", window)
        self.updateTime()
        timer = QTimer(window)
        timer.timeout.connect(window.updateClockTime)
        timer.start(kamehouseDesktopCfg.getInt('clock_widget', 'timer_wait_ms'))

    def updateTime(self):
        currentTime = QTime.currentTime()
        formattedCurrentTime = currentTime.toString('hh:mm')
        if (self.logTrace):
            logger.trace("Updating clock time to " + formattedCurrentTime)
        self.text.setText(formattedCurrentTime)
