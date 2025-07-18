import websocket
import stomper
import json
import time
import socket
import datetime

from PyQt5.QtCore import QObject, pyqtSignal, QThread, QTimer, QTime, Qt, QPropertyAnimation, QSequentialAnimationGroup, QPoint, QRect
from PyQt5.QtWidgets import QWidget, QGraphicsOpacityEffect
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
from widgets.image import ImageWidget
from widgets.movie import MovieWidget
from widgets.text import TextWidget

class ZtvPlayerWidget(QWidget):
    isHidden = False
    vlcRcStatus = {}
    window = None
    
    def __init__(self, window):
        super().__init__(window)
        logger.info("Initializing ztv_player_widget")
        if (kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'hidden')):
            logger.debug("ztv_player_widget is set to hidden")
            self.setHidden(True)
            return
        self.window = window
        self.logo = ImageWidget("ztv_player_logo_widget", window)
        self.hiddenLogo = ImageWidget("ztv_player_hidden_logo_widget", window)
        self.hiddenLogo.setHidden(True)
        self.title = TextWidget("ztv_player_title_widget", "N/A", window)
        self.artist = TextWidget("ztv_player_artist_widget", "N/A", window)
        self.currentTime = TextWidget("ztv_player_current_time_widget", "--:--:--", window)
        self.totalTime = TextWidget("ztv_player_total_time_widget", "--:--:--", window)
        if (kamehouseDesktopCfg.getBoolean('ztv_player_sound_wave_widget', 'use_movie_src')):
            self.soundWave = MovieWidget("ztv_player_sound_wave_widget", window)
            self.startSoundWaveMovie()
        else:
            self.soundWave = ImageWidget("ztv_player_sound_wave_widget", window)
            self.setSoundWaveAnimation()
            self.startSoundWaveAnimationExpand()
            self.startSoundWaveAnimationOpacity()
        self.initWebsocket(window)
        timer = QTimer(window)
        timer.timeout.connect(window.updateZtvPlayerView)
        timer.start(1000)

    def initWebsocket(self, window):
        self.websocketThread = QThread()
        self.websocket = ZtvPlayerWebsocket(window)
        self.websocket.moveToThread(self.websocketThread)
        self.websocketThread.started.connect(self.websocket.run)
        self.websocket.finished.connect(self.websocketThread.quit)
        self.websocket.finished.connect(self.reconnectWebSocket)
        self.websocketThread.finished.connect(self.websocketThread.deleteLater)
        self.websocketThread.start()

    def reconnectWebSocket(self):
        logger.info("Reconnecting websocket")
        self.initWebsocket(self.window)

    def formatTime(self, secondsToFormat):
        timeDelta = datetime.timedelta(seconds=secondsToFormat)
        hours, remainder = divmod(timeDelta.total_seconds(), 3600)
        minutes, seconds = divmod(remainder, 60)
        return f"{int(hours):02}:{int(minutes):02}:{int(seconds):02}"
    
    def updateView(self):
        logTrace = kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'trace_log_enabled')
        logVlcRcStatus = kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'log_vlc_rc_status')
        vlcRcStatus = self.vlcRcStatus
        if (logTrace):
            logger.trace("Updating ztv_player_widget view")
            if (logVlcRcStatus):
                logger.trace("vlcRcStatus: " + str(vlcRcStatus))
        isZtvPlayerHidden = kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'hidden')
        if (isZtvPlayerHidden or
            vlcRcStatus == {} or 
            vlcRcStatus['time'] is None or
            vlcRcStatus['length'] is None or
            vlcRcStatus['information'] is None or
            vlcRcStatus['information']['meta'] is None or
            vlcRcStatus['information']['meta']['filename'] is None):
            self.hideZtvPlayer()
            return
        time = self.formatTime(vlcRcStatus['time'])
        length = self.formatTime(vlcRcStatus['length'])
        filename = vlcRcStatus['information']['meta']['filename']
        artist = vlcRcStatus['information']['meta']['artist']
        self.title.setText(filename)
        self.artist.setText(artist)
        self.currentTime.setText(time)
        self.totalTime.setText(length)
        self.showZtvPlayer()

    def showZtvPlayer(self):
        if (self.isHidden):
            self.isHidden = False
            self.hiddenLogo.setHidden(True)
            self.logo.setHidden(False)
            self.title.setHidden(False)
            self.artist.setHidden(False)
            self.currentTime.setHidden(False)
            self.totalTime.setHidden(False)
            self.soundWave.setHidden(False) 

    def hideZtvPlayer(self):
        if (not self.isHidden):
            self.isHidden = True
            self.hiddenLogo.setHidden(False)
            self.logo.setHidden(True)
            self.title.setHidden(True)
            self.artist.setHidden(True)
            self.currentTime.setHidden(True)
            self.totalTime.setHidden(True) 
            self.soundWave.setHidden(True) 

    def startSoundWaveMovie(self):
        if (kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'hidden') or
            kamehouseDesktopCfg.getBoolean('ztv_player_sound_wave_widget', 'hidden')):
            return
        timer = QTimer(self.window)
        timer.singleShot(100, self.soundWave.start)

    def setSoundWaveAnimation(self):
        posX = kamehouseDesktopCfg.getInt('ztv_player_sound_wave_widget', 'pos_x')
        posY = kamehouseDesktopCfg.getInt('ztv_player_sound_wave_widget', 'pos_y')
        width = kamehouseDesktopCfg.getInt('ztv_player_sound_wave_widget', 'width')
        height = kamehouseDesktopCfg.getInt('ztv_player_sound_wave_widget', 'height')
        expandPx = kamehouseDesktopCfg.getInt('ztv_player_sound_wave_widget', 'expand_px')
        expandedPosX = posX - expandPx
        expandedPosY = posY - expandPx
        expandedWidth = width + expandPx * 2 
        expandedHeight = height + expandPx * 2
        animationMs = kamehouseDesktopCfg.getInt('ztv_player_sound_wave_widget', 'animation_ms')
        self.expand = QPropertyAnimation(self.soundWave, b"geometry")
        self.expand.setStartValue(QRect(posX, posY, width, height))
        self.expand.setEndValue(QRect(expandedPosX, expandedPosY, expandedWidth, expandedHeight))
        self.expand.setDuration(animationMs)
        self.contract = QPropertyAnimation(self.soundWave, b"geometry")
        self.contract.setStartValue(QRect(expandedPosX, expandedPosY, expandedWidth, expandedHeight))
        self.contract.setEndValue(QRect(posX, posY, width, height))
        self.contract.setDuration(animationMs)
        self.soundWaveAnimExpand = QSequentialAnimationGroup()
        self.soundWaveAnimExpand.addAnimation(self.expand)
        self.soundWaveAnimExpand.addAnimation(self.contract)
        self.soundWaveAnimExpand.finished.connect(self.startSoundWaveAnimationExpand)

        minOpacity = kamehouseDesktopCfg.getFloat('ztv_player_sound_wave_widget', 'min_opacity')
        maxOpacity = kamehouseDesktopCfg.getFloat('ztv_player_sound_wave_widget', 'max_opacity')
        effect = QGraphicsOpacityEffect(self.soundWave)
        self.soundWave.setGraphicsEffect(effect)
        self.brighten = QPropertyAnimation(effect, b"opacity")
        self.brighten.setStartValue(minOpacity)
        self.brighten.setEndValue(maxOpacity)
        self.brighten.setDuration(animationMs)
        self.darken = QPropertyAnimation(effect, b"opacity")
        self.darken.setStartValue(maxOpacity)
        self.darken.setEndValue(minOpacity)
        self.darken.setDuration(animationMs)
        self.soundWaveAnimOpacity = QSequentialAnimationGroup()
        self.soundWaveAnimOpacity.addAnimation(self.brighten)
        self.soundWaveAnimOpacity.addAnimation(self.darken)
        self.soundWaveAnimOpacity.finished.connect(self.startSoundWaveAnimationOpacity)

    def startSoundWaveAnimationExpand(self):
        self.soundWaveAnimExpand.start()

    def startSoundWaveAnimationOpacity(self):
        self.soundWaveAnimOpacity.start()

class ZtvPlayerWebsocket(QObject):
    topic = "/topic/vlc-player/status-out"
    finished = pyqtSignal()
    progress = pyqtSignal(int)
    result = pyqtSignal(str)

    def __init__(self, window):
        super().__init__()
        self.window = window
        logger.info("Initializing ztv_player_websocket")

    def run(self):
        hostname = kamehouseDesktopCfg.get('ztv_player_widget', 'websocket_hostname')
        port = kamehouseDesktopCfg.get('ztv_player_widget', 'websocket_port')
        url = "ws://" + hostname + ":" + port + "/kame-house-vlcrc/api/ws/vlcrc/default"
        logger.debug("Connecting to: " + url)
        self.websocket = websocket.WebSocketApp(
          url,
          on_open=self.onOpen,
          on_message=self.onMessage,
          on_error=self.onError,
          on_close=self.onClose
        )
        self.websocket.run_forever()
        self.result.emit("Disconnected from ztv_player_websocket")
        self.finished.emit()

    def onMessage(self, ws, message):
        frame = stomper.unpack_frame(message)
        if (self.isEmptyBody(frame)):
            self.window.ztvPlayer.vlcRcStatus = {}
        else:
            self.window.ztvPlayer.vlcRcStatus = json.loads(frame["body"])
        
    def onError(self, ws, error):
        logger.error("Error receiving data from the ztv_player_websocket")

    def onClose(self, ws, close_status_code, close_msg):
        logger.info("Closed: status code: " + close_status_code + ", message: " + close_msg)

    def onOpen(self, ws):
        logger.debug("Connection opened")
        connect_frame = stomper.connect('', '', '') 
        ws.send(connect_frame)
        subscribe_frame = stomper.subscribe(self.topic, socket.gethostname(), ack='auto')
        ws.send(subscribe_frame)
        logger.debug("Subscribed to: " + self.topic)

    def isEmptyBody(self, frame):
        if (frame["cmd"] != "MESSAGE"):
            return True
        if (frame["body"] is None):
            return True
        return False
