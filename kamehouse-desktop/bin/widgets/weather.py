import os

from PyQt5.QtWidgets import QWidget
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg

class WeatherWidget(QWidget):
    logTrace = False

    def __init__(self, window):
        super().__init__()
        logger.info("Initializing weather_widget")
        self.window = window
        self.logTrace = kamehouseDesktopCfg.getBoolean('weather_widget', 'trace_log_enabled')
        openWeatherMapApiKey = os.environ.get('OPENWEATHERMAP_API_KEY')
        if (self.logTrace):
            logger.trace("openWeatherMapApiKey=" + openWeatherMapApiKey)
