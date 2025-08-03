import websocket
import stomper
import json
import time
import socket
import datetime
import requests
import urllib3

from PyQt5.QtCore import QObject, pyqtSignal, QThread, QTimer, QTime, Qt, QPropertyAnimation, QParallelAnimationGroup, QSequentialAnimationGroup, QPoint, QRect
from PyQt5.QtWidgets import QWidget, QGraphicsOpacityEffect
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
from widgets.image import ImageWidget
from widgets.movie import MovieWidget
from widgets.text import OutlinedTextWidget

class ZtvPlayerWidget(QWidget):
    isHidden = False
    logTrace = False
    vlcRcStatus = {}
    websocketUpdateTime = int(time.time())
    window = None
    defaultTitle = "DBGT - Dan Dan.mp3"
    defaultArtist = "Son Goku"
    
    def __init__(self, window):
        super().__init__(window)
        logger.info("Initializing ztv_player_widget")
        if (kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'hidden')):
            logger.debug("ztv_player_widget is set to hidden")
            self.setHidden(True)
            return
        self.window = window
        self.logTrace = kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'trace_log_enabled')
        self.setPlayerHiddenWidgets()
        self.logo = ImageWidget("ztv_player_logo_widget", window)
        if (kamehouseDesktopCfg.getBoolean('ztv_player_logo_widget', 'use_animation')):
            self.setLogoAnimation()
            self.startLogoAnimation()
        self.title = OutlinedTextWidget("ztv_player_title_widget", self.defaultTitle, window)
        self.artist = OutlinedTextWidget("ztv_player_artist_widget", self.defaultArtist, window)
        self.currentTime = OutlinedTextWidget("ztv_player_current_time_widget", "--:--:--", window)
        self.totalTime = OutlinedTextWidget("ztv_player_total_time_widget", "--:--:--", window)
        if (kamehouseDesktopCfg.getBoolean('ztv_player_sound_wave_widget', 'use_movie_src')):
            self.soundWave = MovieWidget("ztv_player_sound_wave_widget", window)
            self.startSoundWaveMovie()
        else:
            self.soundWave = ImageWidget("ztv_player_sound_wave_widget", window)
            self.setSoundWaveAnimation()
            self.startSoundWaveAnimation()
        self.initUpdateViewSync()

    def setPlayerHiddenWidgets(self):
        self.hiddenGoku = ImageWidget("ztv_player_hidden_goku_widget", self.window)
        self.hiddenGoku.setHidden(True)
        self.hiddenMessageBubble = ImageWidget("ztv_player_hidden_message_bubble_widget", self.window)
        self.hiddenMessageBubble.setHidden(True)
        self.hiddenMessageText = OutlinedTextWidget("ztv_player_hidden_message_text_widget", "音楽をかけて", self.window)
        self.hiddenMessageText.setHidden(True)

    def initSyncThreads(self):
        if (kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'hidden')):
            return
        self.initWebsocket()
        self.initHttpVlcRcStatusSync()

    def initWebsocket(self):
        self.websocketThread = QThread()
        self.websocket = ZtvPlayerWebsocket(self.window)
        self.websocket.moveToThread(self.websocketThread)
        self.websocketThread.started.connect(self.websocket.run)
        self.websocket.finished.connect(self.websocketThread.quit)
        self.websocketThread.finished.connect(self.websocketThread.deleteLater)
        self.websocketThread.start()

    def initHttpVlcRcStatusSync(self):
        self.httpSyncThread = QThread()
        self.httpSync = ZtvPlayerHttpSync(self.window)
        self.httpSync.moveToThread(self.httpSyncThread)
        self.httpSyncThread.started.connect(self.httpSync.run)
        self.httpSync.finished.connect(self.httpSyncThread.quit)
        self.httpSyncThread.finished.connect(self.httpSyncThread.deleteLater)
        self.httpSyncThread.start()

    def resetVlcPlayerFullScreen(self):
        self.vlcPlayerFullScreenThread = QThread()
        self.vlcPlayerFullScreen = VlcPlayerFullScreenSetter(self.window)
        self.vlcPlayerFullScreen.moveToThread(self.vlcPlayerFullScreenThread)
        self.vlcPlayerFullScreenThread.started.connect(self.vlcPlayerFullScreen.run)
        self.vlcPlayerFullScreen.finished.connect(self.vlcPlayerFullScreenThread.quit)
        self.vlcPlayerFullScreenThread.finished.connect(self.vlcPlayerFullScreenThread.deleteLater)
        self.vlcPlayerFullScreenThread.start()        

    def initUpdateViewSync(self):
        timer = QTimer(self.window)
        timer.timeout.connect(self.window.updateZtvPlayerView)
        timer.start(1000)

    def formatTime(self, secondsToFormat):
        timeDelta = datetime.timedelta(seconds=secondsToFormat)
        hours, remainder = divmod(timeDelta.total_seconds(), 3600)
        minutes, seconds = divmod(remainder, 60)
        return f"{int(hours):02}:{int(minutes):02}:{int(seconds):02}"
    
    def updateView(self):
        logVlcRcStatus = kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'log_vlc_rc_status')
        vlcRcStatus = self.vlcRcStatus
        if (self.logTrace):
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
        artist = self.formatArtist(vlcRcStatus['information']['meta']['artist'])
        self.title.setText(self.formatTitle(filename, artist))
        self.artist.setText(artist)
        self.currentTime.setText(time)
        self.totalTime.setText(length)
        self.showZtvPlayer()

    def formatTitle(self, filename, artist):
        if (filename is None):
            return self.defaultTitle
        return filename.replace("-", " ").replace("_", " ").replace(".mp3", "").replace(".MP3", "").replace(".wav", "").replace(".WAV", "").replace(".", " ").title().replace(artist + " ", "").replace(artist, "")
        
    def formatArtist(self, artist):
        if (artist is None):
            return self.defaultArtist
        return artist.replace("-", " ").replace("_", " ").replace(".", " ").title()

    def showZtvPlayer(self):
        if (self.isHidden):
            self.isHidden = False
            self.hiddenGoku.setHidden(True)
            self.hiddenMessageBubble.setHidden(True)
            self.hiddenMessageText.setHidden(True)
            self.logo.setHidden(False)
            self.title.setHidden(False)
            self.artist.setHidden(False)
            self.currentTime.setHidden(False)
            self.totalTime.setHidden(False)
            self.soundWave.setHidden(False) 

    def hideZtvPlayer(self):
        if (not self.isHidden):
            self.isHidden = True
            self.hiddenGoku.setHidden(False)
            self.hiddenMessageBubble.setHidden(False)
            self.hiddenMessageText.setHidden(False)
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
        minOpacity = kamehouseDesktopCfg.getFloat('ztv_player_sound_wave_widget', 'min_opacity')
        maxOpacity = kamehouseDesktopCfg.getFloat('ztv_player_sound_wave_widget', 'max_opacity')
        # expand animation
        self.soundWave.expand = QPropertyAnimation(self.soundWave, b"geometry")
        self.soundWave.expand.setStartValue(QRect(posX, posY, width, height))
        self.soundWave.expand.setEndValue(QRect(expandedPosX, expandedPosY, expandedWidth, expandedHeight))
        self.soundWave.expand.setDuration(animationMs)
        # contract animation
        self.soundWave.contract = QPropertyAnimation(self.soundWave, b"geometry")
        self.soundWave.contract.setStartValue(QRect(expandedPosX, expandedPosY, expandedWidth, expandedHeight))
        self.soundWave.contract.setEndValue(QRect(posX, posY, width, height))
        self.soundWave.contract.setDuration(animationMs)
        # brighten
        effect = QGraphicsOpacityEffect(self.soundWave)
        self.soundWave.setGraphicsEffect(effect)
        self.soundWave.brighten = QPropertyAnimation(effect, b"opacity")
        self.soundWave.brighten.setStartValue(minOpacity)
        self.soundWave.brighten.setEndValue(maxOpacity)
        self.soundWave.brighten.setDuration(animationMs)
        # darken
        self.soundWave.darken = QPropertyAnimation(effect, b"opacity")
        self.soundWave.darken.setStartValue(maxOpacity)
        self.soundWave.darken.setEndValue(minOpacity)
        self.soundWave.darken.setDuration(animationMs)
        # animation groups
        self.soundWave.contractDarken = QParallelAnimationGroup()
        self.soundWave.contractDarken.addAnimation(self.soundWave.contract)
        self.soundWave.contractDarken.addAnimation(self.soundWave.darken)
        self.soundWave.expandBrighten = QParallelAnimationGroup()
        self.soundWave.expandBrighten.addAnimation(self.soundWave.expand)
        self.soundWave.expandBrighten.addAnimation(self.soundWave.brighten)
        self.soundWave.animGroup = QSequentialAnimationGroup()
        self.soundWave.animGroup.addAnimation(self.soundWave.contractDarken)
        self.soundWave.animGroup.addAnimation(self.soundWave.expandBrighten)
        self.soundWave.animGroup.finished.connect(self.startSoundWaveAnimation)

    def startSoundWaveAnimation(self):
        self.soundWave.animGroup.start()

    def setLogoAnimation(self):
        animationMs = kamehouseDesktopCfg.getInt('ztv_player_logo_widget', 'animation_ms')
        minOpacity = kamehouseDesktopCfg.getFloat('ztv_player_logo_widget', 'min_opacity')
        maxOpacity = kamehouseDesktopCfg.getFloat('ztv_player_logo_widget', 'max_opacity')
        # brighten
        effect = QGraphicsOpacityEffect(self.logo)
        self.logo.setGraphicsEffect(effect)
        self.logo.brighten = QPropertyAnimation(effect, b"opacity")
        self.logo.brighten.setStartValue(minOpacity)
        self.logo.brighten.setEndValue(maxOpacity)
        self.logo.brighten.setDuration(animationMs)
        # darken
        self.logo.darken = QPropertyAnimation(effect, b"opacity")
        self.logo.darken.setStartValue(maxOpacity)
        self.logo.darken.setEndValue(minOpacity)
        self.logo.darken.setDuration(animationMs)
        # animation groups
        self.logo.animGroup = QSequentialAnimationGroup()
        self.logo.animGroup.addAnimation(self.logo.brighten)
        self.logo.animGroup.addAnimation(self.logo.darken)
        self.logo.animGroup.finished.connect(self.startLogoAnimation)

    def startLogoAnimation(self):
        self.logo.animGroup.start()

class ZtvPlayerHttpSync(QObject):
    finished = pyqtSignal()
    progress = pyqtSignal(int)
    result = pyqtSignal(str)
    logTrace = False

    def __init__(self, window):
        super().__init__()
        self.window = window
        logger.info("Initializing ztv_player_http_sync")
        self.logTrace = kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'trace_log_enabled')

    def run(self):
        self.runHttpSyncLoop()
        logger.error("Something went wrong. VlcRcStatus http sync loop ended")
        self.result.emit("Exiting http sync loop thread")
        self.finished.emit()

    def runHttpSyncLoop(self):
        while True:
            websocketMaxSyncDelayMs = kamehouseDesktopCfg.getInt('ztv_player_widget', 'websocket_max_sync_delay_ms')
            currentTime = int(time.time())
            timeSinceLastWebsocketUpdate = (currentTime - self.window.ztvPlayer.websocketUpdateTime) * 1000
            if (timeSinceLastWebsocketUpdate < websocketMaxSyncDelayMs):
                if (self.logTrace):
                    logger.trace("Websocket is connected. Skipping http vlcRcStatus sync")
            else:
                self.executeHttpRequest()
            httpSyncWaitSec = kamehouseDesktopCfg.getInt('ztv_player_widget', 'http_sync_wait_sec')
            time.sleep(httpSyncWaitSec)

    def executeHttpRequest(self):
        if (self.logTrace):
            logger.trace("Executing http vlcRcStatus sync")
        protocol = kamehouseDesktopCfg.get('ztv_player_widget', 'http_protocol')
        hostname = kamehouseDesktopCfg.get('ztv_player_widget', 'hostname')
        port = kamehouseDesktopCfg.get('ztv_player_widget', 'port')
        url = protocol + "://" + hostname + ":" + port + "/kame-house-vlcrc/api/v1/vlc-rc/players/localhost/status"
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
        verifySsl = kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'verify_ssl')
        try:
            response = requests.get(url, verify=verifySsl)
            response.raise_for_status() 
            vlcRcStatus = response.json()
            self.window.ztvPlayer.vlcRcStatus = vlcRcStatus
        except requests.exceptions.RequestException as error:
            if (self.logTrace):
                logger.error("Error getting vlcRcStatus via http")

class ZtvPlayerWebsocket(QObject):
    topic = "/topic/vlc-player/status-out"
    finished = pyqtSignal()
    progress = pyqtSignal(int)
    result = pyqtSignal(str)
    logTrace = False

    def __init__(self, window):
        super().__init__()
        self.window = window
        logger.info("Initializing ztv_player_websocket")
        self.logTrace = kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'trace_log_enabled')

    def run(self):
        self.runWebsocketLoop()
        logger.error("Something went wrong. VlcRcStatus websocket sync loop ended")
        self.result.emit("Exiting websocket loop thread")
        self.finished.emit()

    def runWebsocketLoop(self):
        while True:
            protocol = kamehouseDesktopCfg.get('ztv_player_widget', 'ws_protocol')
            hostname = kamehouseDesktopCfg.get('ztv_player_widget', 'hostname')
            port = kamehouseDesktopCfg.get('ztv_player_widget', 'port')
            url = protocol + "://" + hostname + ":" + port + "/kame-house-vlcrc/api/ws/vlcrc/default"
            if (self.logTrace):
                logger.debug("Connecting websocket to: " + url)
            try:
                self.websocket = websocket.WebSocketApp(
                    url,
                    on_open=self.onOpen,
                    on_message=self.onMessage,
                    on_error=self.onError,
                    on_close=self.onClose
                )
                self.websocket.run_forever()
            except Exception as error:
                logger.error("Error running websocket client") 
            websocketReconnectWaitSec = kamehouseDesktopCfg.getInt('ztv_player_widget', 'websocket_reconnect_wait_sec')
            if (self.logTrace):
                logger.warning("Disconnected from ztv_player_websocket. Reconnecting in " + str(websocketReconnectWaitSec) + " seconds")
            time.sleep(websocketReconnectWaitSec)

    def onMessage(self, ws, message):
        frame = stomper.unpack_frame(message)
        if (self.isEmptyBody(frame)):
            self.window.ztvPlayer.vlcRcStatus = {}
        else:
            self.window.ztvPlayer.vlcRcStatus = json.loads(frame["body"])
        self.window.ztvPlayer.websocketUpdateTime = int(time.time())
        
    def onError(self, ws, error):
        if (self.logTrace):
            logger.error("Error receiving data from the ztv_player_websocket")

    def onClose(self, ws, close_status_code, close_msg):
        if (self.logTrace):
            logger.warning("Closed: status code: " + close_status_code + ", message: " + close_msg)

    def onOpen(self, ws):
        if (self.logTrace):
            logger.debug("Connection opened")
        connect_frame = stomper.connect('', '', '') 
        ws.send(connect_frame)
        subscribe_frame = stomper.subscribe(self.topic, socket.gethostname(), ack='auto')
        ws.send(subscribe_frame)
        if (self.logTrace):
            logger.debug("Subscribed to: " + self.topic)

    def isEmptyBody(self, frame):
        if (frame["cmd"] != "MESSAGE"):
            return True
        if (frame["body"] is None):
            return True
        return False

class VlcPlayerFullScreenSetter(QObject):
    finished = pyqtSignal()
    progress = pyqtSignal(int)
    result = pyqtSignal(str)
    logTrace = False

    def __init__(self, window):
        super().__init__()
        self.window = window
        logger.info("Initializing vlc_player_fullscreen_setter")
        self.logTrace = kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'trace_log_enabled')

    def run(self):
        self.executeHttpRequest()
        time.sleep(2)
        self.executeHttpRequest()
        self.result.emit("Exiting vlc player fullscreen setter thread")
        self.finished.emit()

    def executeHttpRequest(self):
        logger.debug("Executing vlc player fullscreen toggle request")
        protocol = kamehouseDesktopCfg.get('ztv_player_widget', 'http_protocol')
        hostname = kamehouseDesktopCfg.get('ztv_player_widget', 'hostname')
        port = kamehouseDesktopCfg.get('ztv_player_widget', 'port')
        url = protocol + "://" + hostname + ":" + port + "/kame-house-vlcrc/api/v1/vlc-rc/players/localhost/commands"
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
        verifySsl = kamehouseDesktopCfg.getBoolean('ztv_player_widget', 'verify_ssl')
        requestBody = {
          "name": "fullscreen",
          "val": None
        }
        try:
            requests.post(url, json=requestBody, verify=verifySsl)
        except requests.exceptions.RequestException as error:
            logger.error("Error sending request to toggle vlc player fullscreen")
