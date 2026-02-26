from PyQt5.QtWidgets import QWidget
from PyQt5.QtCore import QTimer, QTime, Qt
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouse_desktop_cfg
from widgets.text import OutlinedTextWidget

class ClockWidget(QWidget):
    log_trace = False

    def __init__(self, window):
        super().__init__(window)
        logger.info("Initializing clock widget")
        self.window = window
        self.log_trace = kamehouse_desktop_cfg.getBoolean('clock_widget', 'trace_log_enabled')
        self.text = OutlinedTextWidget('clock_text_widget', "00:00", window)
        self.updateTime()

    def postInit(self):
        timer = QTimer(self.window)
        timer.timeout.connect(self.window.clock.updateTime)
        timer.start(kamehouse_desktop_cfg.getInt('clock_widget', 'timer_wait_ms'))

    def updateTime(self):
        current_time = QTime.currentTime()
        formatted_current_time = current_time.toString('hh:mm')
        if (self.log_trace):
            logger.trace("Updating clock time to " + formatted_current_time)
        self.text.setText(formatted_current_time)
