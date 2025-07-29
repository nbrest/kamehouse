import os
import requests
import urllib3
import json
import time

from PyQt5.QtCore import QObject, pyqtSignal, QThread
from PyQt5.QtWidgets import QWidget
from PyQt5.QtGui import QPixmap
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
from widgets.image import ImageWidget
from widgets.text import TextWidget

class WeatherWidget(QWidget):
    logTrace = False
    openWeatherMapApiKey = None
    weatherStatus = None

    def __init__(self, window):
        super().__init__()
        logger.info("Initializing weather_widget")
        self.window = window
        self.logTrace = kamehouseDesktopCfg.getBoolean('weather_widget', 'trace_log_enabled')
        self.openWeatherMapApiKey = os.environ.get('OPENWEATHERMAP_API_KEY')
        self.text = TextWidget('weather_text_widget', "", window)
        self.logo = ImageWidget('weather_logo_widget', window)
        self.logo.setHidden(True)
        if (self.logTrace):
            logger.trace("openWeatherMapApiKey=" + self.openWeatherMapApiKey)
        self.initHttpStatusSync()

    def initHttpStatusSync(self):
        self.httpSyncThread = QThread()
        self.httpSync = WeatherHttpSync(self.window, self.openWeatherMapApiKey)
        self.httpSync.moveToThread(self.httpSyncThread)
        self.httpSyncThread.started.connect(self.httpSync.run)
        self.httpSync.finished.connect(self.httpSyncThread.quit)
        self.httpSyncThread.finished.connect(self.httpSyncThread.deleteLater)
        self.httpSyncThread.start()
    
    def updateStatus(self):
        if (self.isEmptyStatus()):
            self.text.setText("")
            self.logo.setHidden(True)
            return
        temp = str(int(self.weatherStatus["main"]["temp"]))
        if (self.logTrace):
            logger.trace("Updating weather to " + temp + " degrees")
        formattedTemp = temp + "°"
        self.text.setText(formattedTemp)
        self.logo.setHidden(False)

    def isEmptyStatus(self):
        if (self.weatherStatus is None):
            return True
        if (self.weatherStatus["weather"] is None):
            return True
        if (self.weatherStatus["main"] is None):
            return True
        return False

class WeatherHttpSync(QObject):
    finished = pyqtSignal()
    progress = pyqtSignal(int)
    result = pyqtSignal(str)
    logTrace = False
    openWeatherMapApiKey = None

    def __init__(self, window, openWeatherMapApiKey):
        super().__init__()
        self.window = window
        self.openWeatherMapApiKey = openWeatherMapApiKey
        logger.info("Initializing weather widget http sync")
        self.logTrace = kamehouseDesktopCfg.getBoolean('weather_widget', 'trace_log_enabled')

    def run(self):
        self.runHttpSyncLoop()
        logger.error("Something went wrong. weather status http sync loop ended")
        self.result.emit("Exiting http sync loop thread")
        self.finished.emit()

    def runHttpSyncLoop(self):
        while True:
            self.executeHttpRequest()
            httpSyncWaitSec = kamehouseDesktopCfg.getInt('weather_widget', 'http_sync_wait_sec')
            time.sleep(httpSyncWaitSec)

    def executeHttpRequest(self):
        if (self.logTrace):
            logger.trace("Executing http weather sync")
        city = kamehouseDesktopCfg.get('weather_widget', 'location')
        url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=" + self.openWeatherMapApiKey + "&units=metric"
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
        verifySsl = kamehouseDesktopCfg.getBoolean('weather_widget', 'verify_ssl')
        try:
            response = requests.get(url, verify=verifySsl)
            response.raise_for_status() 
            weatherStatus = response.json()
            if (self.isEmptyStatus(weatherStatus)):
                if (self.logTrace):
                    logger.warning("Unable to get weather status")
                    self.window.weather.weatherStatus = None
                return
            if (self.logTrace):
                logger.trace(weatherStatus)
            self.window.weather.weatherStatus = weatherStatus
            iconUrl = 'https://openweathermap.org/img/wn/' + weatherStatus["weather"][0]["icon"] + ".png"
            if (self.logTrace):
                logger.trace("Loading icon from: " + iconUrl)
            response = requests.get(iconUrl, verify=verifySsl)
            pixmap = QPixmap()
            pixmap.loadFromData(response.content)
            self.window.weather.logo.setPixmap(pixmap)
            self.window.updateWeatherStatus()
        except requests.exceptions.RequestException as error:
            if (self.logTrace):
                logger.error("Error getting weather status via http")

    def isEmptyStatus(self, weatherStatus):
        if (weatherStatus is None):
            return True
        if (weatherStatus["weather"] is None):
            return True
        if (weatherStatus["main"] is None):
            return True
        return False