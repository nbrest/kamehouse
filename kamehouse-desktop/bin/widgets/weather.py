import os

from PyQt5.QtCore import QObject, pyqtSignal, QThread
from PyQt5.QtWidgets import QWidget
from loguru import logger

class WeatherWidget(QWidget):
    def __init__(self, window):
        super().__init__()
        logger.info("Initializing weather_widget")
        openWeatherMapApiKey = os.environ.get('OPENWEATHERMAP_API_KEY')
        logger.trace("openWeatherMapApiKey=" + openWeatherMapApiKey)
