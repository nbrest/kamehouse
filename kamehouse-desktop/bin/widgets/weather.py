import os
import requests
import urllib3
import json
import time

from PyQt5.QtCore import QObject, pyqtSignal, QThread
from PyQt5.QtWidgets import QWidget
from PyQt5.QtGui import QPixmap
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouse_desktop_cfg
from widgets.image import ImageWidget
from widgets.text import OutlinedTextWidget

class WeatherWidget(QWidget):
    log_trace = False
    openweathermap_api_key = None
    weather_status = None

    def __init__(self, window):
        super().__init__()
        logger.info("Initializing weather_widget")
        self.window = window
        self.log_trace = kamehouse_desktop_cfg.getBoolean('weather_widget', 'trace_log_enabled')
        self.openweathermap_api_key = os.environ.get('OPENWEATHERMAP_API_KEY')
        self.text = OutlinedTextWidget('weather_text_widget', "", window)
        self.logo = ImageWidget('weather_logo_widget', window)
        self.logo.setHidden(True)
        if (self.log_trace):
            logger.trace("openweathermap_api_key=" + self.openweathermap_api_key)

    def postInit(self):
        self.initHttpStatusSync()

    def initHttpStatusSync(self):
        self.http_sync_thread = QThread()
        self.http_sync = WeatherHttpSync(self.window, self.openweathermap_api_key)
        self.http_sync.moveToThread(self.http_sync_thread)
        self.http_sync_thread.started.connect(self.http_sync.run)
        self.http_sync.finished.connect(self.http_sync_thread.quit)
        self.http_sync_thread.finished.connect(self.http_sync_thread.deleteLater)
        self.http_sync_thread.start()
    
    def updateStatus(self):
        if (self.isEmptyStatus()):
            self.text.setText("")
            self.logo.setHidden(True)
            return
        temp = str(int(self.weather_status["main"]["temp"]))
        if (self.log_trace):
            logger.trace("Updating weather to " + temp + " degrees")
        formatted_temp = temp + "°"
        self.text.setText(formatted_temp)
        self.logo.setHidden(False)

    def isEmptyStatus(self):
        if (self.weather_status is None):
            return True
        if (self.weather_status["weather"] is None):
            return True
        if (self.weather_status["main"] is None):
            return True
        return False

class WeatherHttpSync(QObject):
    finished = pyqtSignal()
    progress = pyqtSignal(int)
    result = pyqtSignal(str)
    log_trace = False
    openweathermap_api_key = None

    def __init__(self, window, openweathermap_api_key):
        super().__init__()
        self.window = window
        self.openweathermap_api_key = openweathermap_api_key
        logger.info("Initializing weather widget http sync")
        self.log_trace = kamehouse_desktop_cfg.getBoolean('weather_widget', 'trace_log_enabled')

    def run(self):
        self.runHttpSyncLoop()
        logger.error("Something went wrong. weather status http sync loop ended")
        self.result.emit("Exiting http sync loop thread")
        self.finished.emit()

    def runHttpSyncLoop(self):
        while True:
            self.executeHttpRequest()
            http_sync_wait_sec = kamehouse_desktop_cfg.getInt('weather_widget', 'http_sync_wait_sec')
            time.sleep(http_sync_wait_sec)

    def executeHttpRequest(self):
        if (self.log_trace):
            logger.trace("Executing http weather sync")
        city = kamehouse_desktop_cfg.get('weather_widget', 'location')
        url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=" + self.openweathermap_api_key + "&units=metric"
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
        verify_ssl = kamehouse_desktop_cfg.getBoolean('weather_widget', 'verify_ssl')
        try:
            response = requests.get(url, verify=verify_ssl)
            response.raise_for_status() 
            weather_status = response.json()
            if (self.isEmptyStatus(weather_status)):
                if (self.log_trace):
                    logger.warning("Unable to get weather status")
                    self.window.weather.weather_status = None
                return
            if (self.log_trace):
                logger.trace(weather_status)
            self.window.weather.weather_status = weather_status
            icon_url = 'https://openweathermap.org/img/wn/' + weather_status["weather"][0]["icon"] + ".png"
            if (self.log_trace):
                logger.trace("Loading icon from: " + icon_url)
            response = requests.get(icon_url, verify=verify_ssl)
            pixmap = QPixmap()
            pixmap.loadFromData(response.content)
            self.window.weather.logo.setPixmap(pixmap)
            self.window.weather.updateStatus()
        except requests.exceptions.RequestException as error:
            if (self.log_trace):
                logger.error("Error getting weather status via http")

    def isEmptyStatus(self, weather_status):
        if (weather_status is None):
            return True
        if (weather_status["weather"] is None):
            return True
        if (weather_status["main"] is None):
            return True
        return False