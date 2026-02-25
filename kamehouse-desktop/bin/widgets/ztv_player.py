import websocket
import stomper
import json
import random
import time
import socket
import datetime
import requests
import urllib3

from PyQt5.QtCore import QObject, pyqtSignal, QThread, QTimer, QTime, Qt, QPropertyAnimation, QParallelAnimationGroup, QSequentialAnimationGroup, QPoint, QRect
from PyQt5.QtWidgets import QWidget, QGraphicsOpacityEffect
from PyQt5.QtGui import QPixmap
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouse_desktop_cfg
from widgets.image import ImageWidget
from widgets.movie import MovieWidget
from widgets.text import OutlinedTextWidget

class ZtvPlayerWidget(QWidget):
    is_playing_media = True
    log_trace = False
    vlc_rc_status = {}
    websocket_update_time = int(time.time())
    window = None
    default_title = "DBGT - Dan Dan.mp3"
    default_artist = "Son Goku"
    
    def __init__(self, window):
        super().__init__(window)
        logger.info("Initializing ztv_player_widget")
        if (kamehouse_desktop_cfg.getBoolean('ztv_player_widget', 'hidden')):
            logger.debug("ztv_player_widget is set to hidden")
            self.setHidden(True)
            return
        self.window = window
        self.log_trace = kamehouse_desktop_cfg.getBoolean('ztv_player_widget', 'trace_log_enabled')
        self.setPlayerOffWidgets()
        self.configureLogo()
        self.title = OutlinedTextWidget("ztv_player_title_widget", self.default_title, window)
        self.artist = OutlinedTextWidget("ztv_player_artist_widget", self.default_artist, window)
        self.current_time = OutlinedTextWidget("ztv_player_current_time_widget", "--:--:--", window)
        self.total_time = OutlinedTextWidget("ztv_player_total_time_widget", "--:--:--", window)
        self.configureSoundWave()
        self.initUpdateViewSync()

    def setPlayerOffWidgets(self):
        self.ztv_player_off_kintoun = ImageWidget("ztv_player_off_kintoun_widget", self.window)
        self.ztv_player_off_kintoun.setHidden(True)
        self.ztv_player_off_message = OutlinedTextWidget("ztv_player_off_message_widget", "音楽をかけて", self.window)
        self.ztv_player_off_message.setHidden(True)
        self.ztv_player_off_logo = ImageWidget("ztv_player_off_logo_widget", self.window)
        self.ztv_player_off_logo.setHidden(True)
        if (kamehouse_desktop_cfg.getBoolean('ztv_player_off_logo_widget', 'use_random_src')):
            random_src_count = kamehouse_desktop_cfg.getInt('ztv_player_off_logo_widget', 'random_src_entries_count')
            random_src = []
            for i in range(1, random_src_count + 1):
                random_src_entry_name = "random_src_" + str(i).zfill(2)
                random_src_entry = kamehouse_desktop_cfg.get('ztv_player_off_logo_widget', random_src_entry_name)
                random_src.append(json.loads(random_src_entry))
                if (self.log_trace):
                    logger.trace("Adding ztv_player_off_logo_widget random_src: " + random_src_entry_name)                
            self.ztv_player_off_logo.random_src = random_src
            timer = QTimer(self.window)
            timer.timeout.connect(self.window.setZtvPlayerRandomLogo)
            timer.start(kamehouse_desktop_cfg.getInt('ztv_player_logo_widget', 'random_src_wait_ms'))

    def configureLogo(self):
        self.logo = ImageWidget("ztv_player_logo_widget", self.window)
        if (kamehouse_desktop_cfg.getBoolean('ztv_player_logo_widget', 'use_animation')):
            self.setLogoAnimation()
            self.startLogoAnimation()
        if (kamehouse_desktop_cfg.getBoolean('ztv_player_logo_widget', 'use_random_src')):
            random_src_count = kamehouse_desktop_cfg.getInt('ztv_player_logo_widget', 'random_src_entries_count')
            random_src = []
            for i in range(1, random_src_count + 1):
                random_src_entry_name = "random_src_" + str(i).zfill(2)
                random_src_entry = kamehouse_desktop_cfg.get('ztv_player_logo_widget', random_src_entry_name)
                random_src.append(json.loads(random_src_entry))
                if (self.log_trace):
                    logger.trace("Adding ztv_player_logo_widget random_src: " + random_src_entry_name)                
            self.logo.random_src = random_src
            timer = QTimer(self.window)
            timer.timeout.connect(self.window.setZtvPlayerRandomLogo)
            timer.start(kamehouse_desktop_cfg.getInt('ztv_player_logo_widget', 'random_src_wait_ms'))

    def configureSoundWave(self):
        if (kamehouse_desktop_cfg.getBoolean('ztv_player_sound_wave_widget', 'use_movie_src')):
            self.sound_wave = MovieWidget("ztv_player_sound_wave_widget", self.window)
            self.startSoundWaveMovie()
        else:
            self.sound_wave = ImageWidget("ztv_player_sound_wave_widget", self.window)
            self.setSoundWaveAnimation()
            self.startSoundWaveAnimation()

    def initSyncThreads(self):
        if (kamehouse_desktop_cfg.getBoolean('ztv_player_widget', 'hidden')):
            return
        self.initWebsocket()
        self.initHttpVlcRcStatusSync()

    def initWebsocket(self):
        self.websocket_thread = QThread()
        self.websocket = ZtvPlayerWebsocket(self.window)
        self.websocket.moveToThread(self.websocket_thread)
        self.websocket_thread.started.connect(self.websocket.run)
        self.websocket.finished.connect(self.websocket_thread.quit)
        self.websocket_thread.finished.connect(self.websocket_thread.deleteLater)
        self.websocket_thread.start()

    def initHttpVlcRcStatusSync(self):
        self.http_sync_thread = QThread()
        self.http_sync = ZtvPlayerHttpSync(self.window)
        self.http_sync.moveToThread(self.http_sync_thread)
        self.http_sync_thread.started.connect(self.http_sync.run)
        self.http_sync.finished.connect(self.http_sync_thread.quit)
        self.http_sync_thread.finished.connect(self.http_sync_thread.deleteLater)
        self.http_sync_thread.start()

    def resetVlcPlayerFullScreen(self):
        self.vlc_player_full_screen_thread = QThread()
        self.vlc_player_full_screen = VlcPlayerFullScreenSetter(self.window)
        self.vlc_player_full_screen.moveToThread(self.vlc_player_full_screen_thread)
        self.vlc_player_full_screen_thread.started.connect(self.vlc_player_full_screen.run)
        self.vlc_player_full_screen.finished.connect(self.vlc_player_full_screen_thread.quit)
        self.vlc_player_full_screen_thread.finished.connect(self.vlc_player_full_screen_thread.deleteLater)
        self.vlc_player_full_screen_thread.start()        

    def initUpdateViewSync(self):
        timer = QTimer(self.window)
        timer.timeout.connect(self.window.updateZtvPlayerView)
        timer.start(1000)

    def formatTime(self, secondsToFormat):
        time_delta = datetime.timedelta(seconds=secondsToFormat)
        hours, remainder = divmod(time_delta.total_seconds(), 3600)
        minutes, seconds = divmod(remainder, 60)
        return f"{int(hours):02}:{int(minutes):02}:{int(seconds):02}"
    
    def updateView(self):
        log_vlcrc_status = kamehouse_desktop_cfg.getBoolean('ztv_player_widget', 'log_vlc_rc_status')
        vlc_rc_status = self.vlc_rc_status
        if (self.log_trace):
            logger.trace("Updating ztv_player_widget view")
            if (log_vlcrc_status):
                logger.trace("vlc_rc_status: " + str(vlc_rc_status))
        is_ztv_player_hidden = kamehouse_desktop_cfg.getBoolean('ztv_player_widget', 'hidden')
        if (is_ztv_player_hidden or
            vlc_rc_status == {} or 
            vlc_rc_status['time'] is None or
            vlc_rc_status['length'] is None or
            vlc_rc_status['information'] is None or
            vlc_rc_status['information']['meta'] is None or
            vlc_rc_status['information']['meta']['filename'] is None):
            self.hideZtvPlayer()
            return
        time = self.formatTime(vlc_rc_status['time'])
        length = self.formatTime(vlc_rc_status['length'])
        filename = vlc_rc_status['information']['meta']['filename']
        artist = self.formatArtist(vlc_rc_status['information']['meta']['artist'])
        self.title.setText(self.formatTitle(filename, artist))
        self.artist.setText(artist)
        self.current_time.setText(time)
        self.total_time.setText(length)
        self.showZtvPlayer()

    def formatTitle(self, filename, artist):
        if (filename is None):
            return self.default_title
        return filename.replace("-", " ").replace("_", " ").replace(".mp3", "").replace(".MP3", "").replace(".wav", "").replace(".WAV", "").replace(".", " ").title().replace(artist + " ", "").replace(artist, "")
        
    def formatArtist(self, artist):
        if (artist is None):
            return self.default_artist
        return artist.replace("-", " ").replace("_", " ").replace(".", " ").title()

    def showZtvPlayer(self):
        if (not self.is_playing_media):
            self.is_playing_media = True
            self.ztv_player_off_logo.setHidden(True)
            self.ztv_player_off_kintoun.setHidden(True)
            self.ztv_player_off_message.setHidden(True)
            self.logo.setHidden(False)
            self.title.setHidden(False)
            self.artist.setHidden(False)
            self.current_time.setHidden(False)
            self.total_time.setHidden(False)
            self.sound_wave.setHidden(False) 

    def hideZtvPlayer(self):
        if (self.is_playing_media):
            self.is_playing_media = False
            self.ztv_player_off_logo.setHidden(False)
            self.ztv_player_off_kintoun.setHidden(False)
            self.ztv_player_off_message.setHidden(False)
            self.logo.setHidden(True)
            self.title.setHidden(True)
            self.artist.setHidden(True)
            self.current_time.setHidden(True)
            self.total_time.setHidden(True) 
            self.sound_wave.setHidden(True) 

    def startSoundWaveMovie(self):
        if (kamehouse_desktop_cfg.getBoolean('ztv_player_widget', 'hidden') or
            kamehouse_desktop_cfg.getBoolean('ztv_player_sound_wave_widget', 'hidden')):
            return
        timer = QTimer(self.window)
        timer.singleShot(100, self.sound_wave.start)

    def setSoundWaveAnimation(self):
        pos_x = kamehouse_desktop_cfg.getInt('ztv_player_sound_wave_widget', 'pos_x')
        pos_y = kamehouse_desktop_cfg.getInt('ztv_player_sound_wave_widget', 'pos_y')
        width = kamehouse_desktop_cfg.getInt('ztv_player_sound_wave_widget', 'width')
        height = kamehouse_desktop_cfg.getInt('ztv_player_sound_wave_widget', 'height')
        expand_px = kamehouse_desktop_cfg.getInt('ztv_player_sound_wave_widget', 'expand_px')
        expanded_pos_x = pos_x - expand_px
        expanded_pos_y = pos_y - expand_px
        expanded_width = width + expand_px * 2 
        expanded_height = height + expand_px * 2
        animation_ms = kamehouse_desktop_cfg.getInt('ztv_player_sound_wave_widget', 'animation_ms')
        min_opacity = kamehouse_desktop_cfg.getFloat('ztv_player_sound_wave_widget', 'min_opacity')
        max_opacity = kamehouse_desktop_cfg.getFloat('ztv_player_sound_wave_widget', 'max_opacity')
        # expand animation
        self.sound_wave.expand = QPropertyAnimation(self.sound_wave, b"geometry")
        self.sound_wave.expand.setStartValue(QRect(pos_x, pos_y, width, height))
        self.sound_wave.expand.setEndValue(QRect(expanded_pos_x, expanded_pos_y, expanded_width, expanded_height))
        self.sound_wave.expand.setDuration(animation_ms)
        # contract animation
        self.sound_wave.contract = QPropertyAnimation(self.sound_wave, b"geometry")
        self.sound_wave.contract.setStartValue(QRect(expanded_pos_x, expanded_pos_y, expanded_width, expanded_height))
        self.sound_wave.contract.setEndValue(QRect(pos_x, pos_y, width, height))
        self.sound_wave.contract.setDuration(animation_ms)
        # brighten
        effect = QGraphicsOpacityEffect(self.sound_wave)
        self.sound_wave.setGraphicsEffect(effect)
        self.sound_wave.brighten = QPropertyAnimation(effect, b"opacity")
        self.sound_wave.brighten.setStartValue(min_opacity)
        self.sound_wave.brighten.setEndValue(max_opacity)
        self.sound_wave.brighten.setDuration(animation_ms)
        # darken
        self.sound_wave.darken = QPropertyAnimation(effect, b"opacity")
        self.sound_wave.darken.setStartValue(max_opacity)
        self.sound_wave.darken.setEndValue(min_opacity)
        self.sound_wave.darken.setDuration(animation_ms)
        # animation groups
        self.sound_wave.contract_darken = QParallelAnimationGroup()
        self.sound_wave.contract_darken.addAnimation(self.sound_wave.contract)
        self.sound_wave.contract_darken.addAnimation(self.sound_wave.darken)
        self.sound_wave.expand_brighten = QParallelAnimationGroup()
        self.sound_wave.expand_brighten.addAnimation(self.sound_wave.expand)
        self.sound_wave.expand_brighten.addAnimation(self.sound_wave.brighten)
        self.sound_wave.anim_group = QSequentialAnimationGroup()
        self.sound_wave.anim_group.addAnimation(self.sound_wave.contract_darken)
        self.sound_wave.anim_group.addAnimation(self.sound_wave.expand_brighten)
        self.sound_wave.anim_group.finished.connect(self.startSoundWaveAnimation)

    def startSoundWaveAnimation(self):
        self.sound_wave.anim_group.start()

    def setLogoAnimation(self):
        animation_ms = kamehouse_desktop_cfg.getInt('ztv_player_logo_widget', 'animation_ms')
        min_opacity = kamehouse_desktop_cfg.getFloat('ztv_player_logo_widget', 'min_opacity')
        max_opacity = kamehouse_desktop_cfg.getFloat('ztv_player_logo_widget', 'max_opacity')
        # brighten
        effect = QGraphicsOpacityEffect(self.logo)
        self.logo.setGraphicsEffect(effect)
        self.logo.brighten = QPropertyAnimation(effect, b"opacity")
        self.logo.brighten.setStartValue(min_opacity)
        self.logo.brighten.setEndValue(max_opacity)
        self.logo.brighten.setDuration(animation_ms)
        # darken
        self.logo.darken = QPropertyAnimation(effect, b"opacity")
        self.logo.darken.setStartValue(max_opacity)
        self.logo.darken.setEndValue(min_opacity)
        self.logo.darken.setDuration(animation_ms)
        # animation groups
        self.logo.anim_group = QSequentialAnimationGroup()
        self.logo.anim_group.addAnimation(self.logo.brighten)
        self.logo.anim_group.addAnimation(self.logo.darken)
        self.logo.anim_group.finished.connect(self.startLogoAnimation)

    def startLogoAnimation(self):
        self.logo.anim_group.start()

    def setRandomLogo(self):
        if (kamehouse_desktop_cfg.getBoolean('ztv_player_logo_widget', 'use_random_src')):
            random_logo = random.choice(self.logo.random_src)
            self.logo.img_src = QPixmap(random_logo["img_src"])
            self.logo.setPixmap(self.logo.img_src)
            self.logo.setGeometry(random_logo["pos_x"], random_logo["pos_y"], random_logo["width"], random_logo["height"])
        if (kamehouse_desktop_cfg.getBoolean('ztv_player_off_logo_widget', 'use_random_src')):
            random_off_logo = random.choice(self.ztv_player_off_logo.random_src)
            self.ztv_player_off_logo.img_src = QPixmap(random_off_logo["img_src"])
            self.ztv_player_off_logo.setPixmap(self.ztv_player_off_logo.img_src)
            self.ztv_player_off_logo.setGeometry(random_off_logo["pos_x"], random_off_logo["pos_y"], random_off_logo["width"], random_off_logo["height"])

class ZtvPlayerHttpSync(QObject):
    finished = pyqtSignal()
    progress = pyqtSignal(int)
    result = pyqtSignal(str)
    log_trace = False

    def __init__(self, window):
        super().__init__()
        self.window = window
        logger.info("Initializing ztv_player_http_sync")
        self.log_trace = kamehouse_desktop_cfg.getBoolean('ztv_player_widget', 'trace_log_enabled')

    def run(self):
        self.runHttpSyncLoop()
        logger.error("Something went wrong. VlcRcStatus http sync loop ended")
        self.result.emit("Exiting http sync loop thread")
        self.finished.emit()

    def runHttpSyncLoop(self):
        while True:
            websocket_max_sync_delay_ms = kamehouse_desktop_cfg.getInt('ztv_player_widget', 'websocket_max_sync_delay_ms')
            current_time = int(time.time())
            time_since_last_ws_update = (current_time - self.window.ztv_player.websocket_update_time) * 1000
            if (time_since_last_ws_update < websocket_max_sync_delay_ms):
                if (self.log_trace):
                    logger.trace("Websocket is connected. Skipping http vlc_rc_status sync")
            else:
                self.executeHttpRequest()
            http_sync_wait_sec = kamehouse_desktop_cfg.getInt('ztv_player_widget', 'http_sync_wait_sec')
            time.sleep(http_sync_wait_sec)

    def executeHttpRequest(self):
        if (self.log_trace):
            logger.trace("Executing http vlc_rc_status sync")
        protocol = kamehouse_desktop_cfg.get('ztv_player_widget', 'http_protocol')
        hostname = kamehouse_desktop_cfg.get('ztv_player_widget', 'hostname')
        port = kamehouse_desktop_cfg.get('ztv_player_widget', 'port')
        url = protocol + "://" + hostname + ":" + port + "/kame-house-vlcrc/api/v1/vlc-rc/players/localhost/status"
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
        verify_ssl = kamehouse_desktop_cfg.getBoolean('ztv_player_widget', 'verify_ssl')
        try:
            response = requests.get(url, verify=verify_ssl)
            response.raise_for_status() 
            vlc_rc_status = response.json()
            self.window.ztv_player.vlc_rc_status = vlc_rc_status
        except requests.exceptions.RequestException as error:
            if (self.log_trace):
                logger.error("Error getting vlc_rc_status via http")

class ZtvPlayerWebsocket(QObject):
    topic = "/topic/vlc-player/status-out"
    finished = pyqtSignal()
    progress = pyqtSignal(int)
    result = pyqtSignal(str)
    log_trace = False

    def __init__(self, window):
        super().__init__()
        self.window = window
        logger.info("Initializing ztv_player_websocket")
        self.log_trace = kamehouse_desktop_cfg.getBoolean('ztv_player_widget', 'trace_log_enabled')

    def run(self):
        self.runWebsocketLoop()
        logger.error("Something went wrong. VlcRcStatus websocket sync loop ended")
        self.result.emit("Exiting websocket loop thread")
        self.finished.emit()

    def runWebsocketLoop(self):
        while True:
            protocol = kamehouse_desktop_cfg.get('ztv_player_widget', 'ws_protocol')
            hostname = kamehouse_desktop_cfg.get('ztv_player_widget', 'hostname')
            port = kamehouse_desktop_cfg.get('ztv_player_widget', 'port')
            url = protocol + "://" + hostname + ":" + port + "/kame-house-vlcrc/api/ws/vlcrc/default"
            if (self.log_trace):
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
            websocket_reconnect_wait_sec = kamehouse_desktop_cfg.getInt('ztv_player_widget', 'websocket_reconnect_wait_sec')
            if (self.log_trace):
                logger.warning("Disconnected from ztv_player_websocket. Reconnecting in " + str(websocket_reconnect_wait_sec) + " seconds")
            time.sleep(websocket_reconnect_wait_sec)

    def onMessage(self, ws, message):
        frame = stomper.unpack_frame(message)
        if (self.isEmptyBody(frame)):
            self.window.ztv_player.vlc_rc_status = {}
        else:
            self.window.ztv_player.vlc_rc_status = json.loads(frame["body"])
        self.window.ztv_player.websocket_update_time = int(time.time())
        
    def onError(self, ws, error):
        if (self.log_trace):
            logger.error("Error receiving data from the ztv_player_websocket")

    def onClose(self, ws, close_status_code, close_msg):
        if (self.log_trace):
            logger.warning("Closed: status code: " + close_status_code + ", message: " + close_msg)

    def onOpen(self, ws):
        if (self.log_trace):
            logger.debug("Connection opened")
        connect_frame = stomper.connect('', '', '') 
        ws.send(connect_frame)
        subscribe_frame = stomper.subscribe(self.topic, socket.gethostname(), ack='auto')
        ws.send(subscribe_frame)
        if (self.log_trace):
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
    log_trace = False

    def __init__(self, window):
        super().__init__()
        self.window = window
        logger.info("Initializing vlc_player_fullscreen_setter")
        self.log_trace = kamehouse_desktop_cfg.getBoolean('ztv_player_widget', 'trace_log_enabled')

    def run(self):
        self.executeHttpRequest()
        time.sleep(2)
        self.executeHttpRequest()
        self.result.emit("Exiting vlc player fullscreen setter thread")
        self.finished.emit()

    def executeHttpRequest(self):
        logger.debug("Executing vlc player fullscreen toggle request")
        protocol = kamehouse_desktop_cfg.get('ztv_player_widget', 'http_protocol')
        hostname = kamehouse_desktop_cfg.get('ztv_player_widget', 'hostname')
        port = kamehouse_desktop_cfg.get('ztv_player_widget', 'port')
        url = protocol + "://" + hostname + ":" + port + "/kame-house-vlcrc/api/v1/vlc-rc/players/localhost/commands"
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
        verify_ssl = kamehouse_desktop_cfg.getBoolean('ztv_player_widget', 'verify_ssl')
        requestBody = {
          "name": "fullscreen",
          "val": None
        }
        try:
            requests.post(url, json=requestBody, verify=verify_ssl)
        except requests.exceptions.RequestException as error:
            logger.error("Error sending request to toggle vlc player fullscreen")
