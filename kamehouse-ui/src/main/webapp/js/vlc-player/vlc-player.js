const { element } = require("angular");

/** 
 * VlcPlayer entity.
 * 
 * This class contains the public interface to interact with VlcPlayer. Most of the logic is
 * implemented in the component classes.
 * 
 * Call load() after instantiating VlcPlayer to connect the internal websocket
 * and start the sync loops.
 * 
 * @author nbrest
 */
class VlcPlayer {

  #commandExecutor = null;
  #playlist = null;
  #restClient = null;
  #mainViewUpdater = null;
  #vlcPlayerDebugger = null;
  #synchronizer = null;

  #vlcRcStatus = {};
  #hostname = null;

  constructor(hostname) {
    this.#hostname = hostname;
    this.#commandExecutor = new VlcPlayerCommandExecutor(this);
    this.#playlist = new VlcPlayerPlaylist(this);
    this.#restClient = new VlcPlayerRestClient(this);
    this.#mainViewUpdater = new VlcPlayerMainViewUpdater(this);
    this.#vlcPlayerDebugger = new VlcPlayerDebugger(this);
  }

  /** Load VlcPlayer extension. */
  load() {
    kameHouse.logger.info("Started initializing VLC Player");
    this.#mainViewUpdater.setStatefulButtons();
    kameHouse.core.loadKameHouseWebSocket();
    this.#loadStateFromCookies();
    this.#playlist.init();
    kameHouse.util.mobile.setMobileEventListeners(() => {this.#stopVlcPlayerLoops()}, () => {this.#restartVlcPlayerLoops()});
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger"], () => {
      kameHouse.plugin.debugger.renderCustomDebugger("/kame-house/html-snippets/vlc-player/debug-mode-custom.html", () => {});
      kameHouse.util.mobile.exec(
        () => {this.loadStateFromApi();},
        () => {
          kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
            this.loadStateFromApi();
          });
        }
      );
    });
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger", "kameHouseWebSocket"], () => {
      this.#synchronizer = new VlcPlayerSynchronizer(this);
      this.#synchronizer.syncVlcPlayerHttpLoop();
      kameHouse.util.mobile.exec(
        () => {this.#startSynchronizerLoops();},
        () => {
          kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
            // wait for the mobile config to be available before starting the websockets and sync loops
            this.#startSynchronizerLoops();
          });
        }
      );
      kameHouse.util.module.setModuleLoaded("vlcPlayer");
    });
  }

  /**
   * Load the vlc player state and refresh the view from API calls (not through websockets).
   */
  loadStateFromApi() {
    this.#vlcPlayerDebugger.getVlcRcStatusFromApi();
    this.#vlcPlayerDebugger.getPlaylistFromApi();
  }

  /** Get the hostname for this instance of VlcPlayer */
  getHostname() {
    return this.#hostname;
  }

  /** Get internal object to manage the playlist */
  getPlaylist() {
    return this.#playlist;
  }

  /**
   * Open vlc player tab.
   */
  openTab(vlcPlayerTabDivId) {
    // Set kh-vlc-player-current-tab cookie
    kameHouse.util.cookies.setCookie('kh-vlc-player-current-tab', vlcPlayerTabDivId);
    // Update tab links
    const vlcPlayerTabLinks = document.getElementsByClassName("vlc-player-tab-link");
    for (const vlcPlayerTabLinkElement of vlcPlayerTabLinks) {
      kameHouse.util.dom.classListRemove(vlcPlayerTabLinkElement, "active");
    }
    const vlcPlayerTabLink = document.getElementById(vlcPlayerTabDivId + '-link');
    kameHouse.util.dom.classListAdd(vlcPlayerTabLink, "active");

    // Update tab content visibility
    const vlcPlayerTabContent = document.getElementsByClassName("vlc-player-tab-content");
    for (const vlcPlayerTabContentElement of vlcPlayerTabContent) {
      kameHouse.util.dom.setDisplay(vlcPlayerTabContentElement, "none");
    }
    const vlcPlayerTabDiv = document.getElementById(vlcPlayerTabDivId);
    kameHouse.util.dom.setDisplay(vlcPlayerTabDiv, "block");

    setTimeout(() => {
      // Asynchronously show or hide playlist and playlist browser content
      const playlistTable = document.getElementById("playlist-table");
      if ("tab-playlist" == vlcPlayerTabDivId) {
        kameHouse.util.dom.setDisplay(playlistTable, "table");
      } else {
        kameHouse.util.dom.setDisplay(playlistTable, "none");
      }

      const playlistBrowserTable = document.getElementById("playlist-browser-table");
      if ("tab-playlist-browser" == vlcPlayerTabDivId) {
        kameHouse.util.dom.setDisplay(playlistBrowserTable, "table");
      } else {
        kameHouse.util.dom.setDisplay(playlistBrowserTable, "none");
      }
    }, 0);
  }

  /**
   * Play the specified file in vlc.
   */
  playFile(fileName) { this.#commandExecutor.playFile(fileName); }

  /**
   * Execute the specified vlc command.
   */
  execVlcRcCommand(name, val) { this.#commandExecutor.execVlcRcCommand(name, val); }

  /**
   * Set the subtitle delay.
   */
  updateSubtitleDelay(increment) {
    let subtitleDelay = this.getVlcRcStatus().subtitleDelay;
    if (!kameHouse.core.isEmpty(subtitleDelay)) {
      subtitleDelay = Number(subtitleDelay) + Number(increment);
    } else {
      subtitleDelay = 0 + Number(increment);
    }
    this.#commandExecutor.execVlcRcCommand('subdelay', subtitleDelay);
  }

  /**
   * Set aspect ratio.
   */
  updateAspectRatio(aspectRatio) {
    if (!kameHouse.core.isEmpty(aspectRatio)) {
      this.#commandExecutor.execVlcRcCommand('aspectratio', aspectRatio);
    }
  }

  /**
   * Seek through the current playing file.
   */
  seek(value) {
    this.#mainViewUpdater.updateCurrentTimeView(value);
    this.#commandExecutor.execVlcRcCommand('seek', value);
    this.#mainViewUpdater.timeSliderLocked(false);
  }

  /**
   * Update the volume.
   */ 
  setVolume(value) {
    this.#mainViewUpdater.updateVolumeView(value);
    this.#commandExecutor.execVlcRcCommand('volume', value);
    this.#mainViewUpdater.volumeSliderLocked(false);
  }

  /**
   * Close vlc player.
   */
  close() { this.#commandExecutor.close(); }

  /**
   * Get the current vlcRc status.
   */
  getVlcRcStatus() { return this.#vlcRcStatus; }

  /** 
   * Set the VlcRcStatus. vlcRcStatus must never be undefined or null.
   * If no value is passed, set an empty object. Always set vlcRcStatus
   * through this method.
   */
  setVlcRcStatus(vlcRcStatusParam) {
    if (!kameHouse.core.isEmpty(vlcRcStatusParam)) {
      this.#vlcRcStatus = vlcRcStatusParam;
    } else {
      this.#vlcRcStatus = {};
    }
  }

  /**
   * Pol vlcrc status from the websocket.
   */
  pollVlcRcStatus() { this.#synchronizer.pollVlcRcStatus(); }

  /**
   * Set updated playlist.
   */
  setUpdatedPlaylist(updatedPlaylist) { this.#playlist.setUpdatedPlaylist(updatedPlaylist); }

  /**
   * Reload playlist.
   */
  reloadPlaylist() { this.#playlist.reload(); }

  /**
   * Scroll to currently playing item.
   */
  scrollToCurrentlyPlaying() { this.#playlist.scrollToCurrentlyPlaying(); }

  /**
   * Filter playlist rows.
   */
  filterPlaylistRows() {
    const filterString = document.getElementById("playlist-filter-input").value;
    kameHouse.util.table.filterTableRows(filterString, 'playlist-table-body');
  }

  /**
   * Toggle expand/collapse playlist entries names.
   */
  toggleExpandPlaylistFilenames() { this.#playlist.toggleExpandPlaylistFilenames(); }

  /**
   * --------------------------------------------------------------------------
   * Update view functionality
   */
  /** Calls each internal module that has view logic to update it's view. */
  updateView() {
    this.#mainViewUpdater.updateView();
    this.#playlist.updateView();
  }

  /** Calls each internal module that has view logic to reset it's view. */
  resetView() {
    this.setVlcRcStatus({});
    this.#mainViewUpdater.resetView();
    this.#playlist.resetView();
  }

  /**
   * Update current time view.
   */
  updateCurrentTimeView(value) {
    this.#mainViewUpdater.timeSliderLocked(true);
    this.#mainViewUpdater.updateCurrentTimeView(value);
  }

  /**
   * Update volume view.
   */
  updateVolumeView(value) {
    this.#mainViewUpdater.volumeSliderLocked(true);
    this.#mainViewUpdater.updateVolumeView(value);
  }

  /**
   * Get REST client.
   * Use this getter internally from other components of VlcPlayer. Not externally.
   */
  getRestClient() { return this.#restClient; }

  /**
   * Get vlc player debugger.
   */
  getDebugger() { return this.#vlcPlayerDebugger; }

  /**
   * Unlock screen.
   */
  unlockScreen() {
    const UNLOCK_SCREEN_API_URL = "/kame-house-admin/api/v1/admin/screen/unlock";
    this.getRestClient().post(UNLOCK_SCREEN_API_URL, null, null);
  }

  /**
   * Wake on lan media server.
   */
  wolMediaServer() {
    const requestParam =  {
      server : "media.server"
    };
    const WOL_MEDIA_SERVER_API_URL = "/kame-house-admin/api/v1/admin/power-management/wol";
    this.getRestClient().post(WOL_MEDIA_SERVER_API_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam);
  }

  /**
   * Suspend the current server.
   */
  suspendServer() {
    const requestParam =  {
      delay : 0
    };
    const SUSPEND_SERVER_URL = "/kame-house-admin/api/v1/admin/power-management/suspend";
    this.getRestClient().post(SUSPEND_SERVER_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam);
  }

  /**
   * Single left click.
   */
  mouseSingleClick() {
    const params = {
      mouseButton: "LEFT",
      positionX: 500,
      positionY: 500,
      clickCount: 1
    };
    const WOL_MEDIA_SERVER_API_URL = "/kame-house-admin/api/v1/admin/screen/mouse-click";
    this.getRestClient().post(WOL_MEDIA_SERVER_API_URL, kameHouse.http.getUrlEncodedHeaders(), params);
  }

  /**
   * Trigger a mouse left double click.
   */
  mouseDoubleClick() {
    const requestParam =  {
      mouseButton : "LEFT",
      positionX: 500,
      positionY: 500,
      clickCount: 2
    };
    const MOUSE_CLICK_API_URL = "/kame-house-admin/api/v1/admin/screen/mouse-click";
    this.getRestClient().post(MOUSE_CLICK_API_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam);
  }

  /**
   * Start synchronization loops.
   */
  #startSynchronizerLoops() {
    kameHouse.logger.info("Started initializing vlc player websockets and sync loops");
    this.#synchronizer.initWebSockets();
    this.#synchronizer.connectVlcRcStatus();
    this.#synchronizer.connectPlaylist();
    this.#synchronizer.syncVlcRcStatusLoop();
    this.#synchronizer.syncPlaylistLoop();
    this.#synchronizer.keepAliveWebSocketsLoop();
    this.#synchronizer.syncLoopsHealthCheck();
  }

  /**
   * Stop synchronization loops.
   */
  #stopVlcPlayerLoops() {
    this.#synchronizer.stopVlcPlayerLoops();
  }

  /**
   * Restart synchronization loops.
   */
  #restartVlcPlayerLoops() {
    this.#synchronizer.restartVlcPlayerLoops();
  }

  /**
   * Load the current state from the cookies.
   */
  #loadStateFromCookies() {
    let currentTab = kameHouse.util.cookies.getCookie('kh-vlc-player-current-tab');
    if (!currentTab || currentTab == '') {
      currentTab = 'tab-playing';
    }
    this.openTab(currentTab);
  }
} // End VlcPlayer

/** 
 * Handles the execution of vlc commands, such as play, stop, next, close, etc.
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class VlcPlayerCommandExecutor {

  static #VLC_PLAYER_PROCESS_CONTROL_URL = '/kame-house-vlcrc/api/v1/vlc-rc/vlc-process';
  
  #vlcPlayer = null;
  #vlcRcCommandUrl = null;

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
    this.#vlcRcCommandUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/commands';
  }

  /** Create a vlcrc command with the parameters and execute the request to the server. */
  execVlcRcCommand(name, val) {
    const requestBody = {
      name : name
    };
    if (!kameHouse.core.isEmpty(val) || val == 0) {
      requestBody.val = val;
    }
    this.#vlcPlayer.getRestClient().post(this.#vlcRcCommandUrl, kameHouse.http.getApplicationJsonHeaders(), requestBody);
  }

  /** Play the selected file (or playlist) into vlc player and reload the current playlist. */
  playFile(fileName) {
    kameHouse.logger.debug("File to play: " + fileName);
    const requestParam =  {
      file : fileName
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    this.#vlcPlayer.getRestClient().post(VlcPlayerCommandExecutor.#VLC_PLAYER_PROCESS_CONTROL_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam);
  }

  /** Close vlc player. */
  close() {
    this.#vlcPlayer.getRestClient().delete(VlcPlayerCommandExecutor.#VLC_PLAYER_PROCESS_CONTROL_URL, null, null);
  }
} // End VlcPlayerCommandExecutor

/** 
 * Handles the updates to the VlcPlayer main view elements. It consists of
 * title, timer and volume sliders and stateful media buttons.
 * 
 * This prototype is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class VlcPlayerMainViewUpdater {

  #vlcPlayer = null;
  #statefulButtons = [];
  #timeSliderLocked = false;
  #volumeSliderLocked = false;

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
  }

  /** Set time slider locked. */
  timeSliderLocked(value) { this.#timeSliderLocked = value; }

  /** Set volume slider locked. */
  volumeSliderLocked(value) { this.#volumeSliderLocked = value; }

  /** Update vlc player view for main view objects. */
  updateView() {
    if (!kameHouse.core.isEmpty(this.#vlcPlayer.getVlcRcStatus())) {
      this.#updateMediaTitle();
      this.#updateTimeSlider();
      this.#updateVolumeSlider();
      this.#updateSubtitleDelay();
      this.#statefulButtons.forEach((statefulButton) => statefulButton.updateState());
    } else {
      this.resetView();
    }
  }

  /** Reset vlc player view for main view objects. */
  resetView() {
    this.#resetMediaTitle();
    this.#resetTimeSlider();
    this.#resetVolumeSlider();
    this.#resetSubtitleDelay();
    this.#statefulButtons.forEach(statefulButton => statefulButton.updateState());
  }

  /** Update the displayed current time. */
  updateCurrentTimeView(value) {
    const currentTime = document.getElementById("current-time");
    kameHouse.util.dom.setInnerHtml(currentTime, kameHouse.util.time.convertSecondsToHsMsSs(value));
    kameHouse.util.dom.setVal(document.getElementById("time-slider"), value);
  }

  /** Update volume percentage to display with the specified value. */
  updateVolumeView(value) {
    kameHouse.util.dom.setVal(document.getElementById("volume-slider"), value);
    const volumePercentaje = Math.floor(value * 200 / 512);
    const currentVolume = document.getElementById("current-volume");
    kameHouse.util.dom.setInnerHtml(currentVolume, volumePercentaje + "%");
  }

  /**
   * Set stateful buttons state.
   */
  setStatefulButtons() {
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-fullscreen', "fullscreen", true));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-repeat-1', "repeat", true));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-repeat', "loop", true));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-shuffle', "random", true));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-stop', "state", "stopped"));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-mute', "volume", 0, 'btn-mute'));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-16-9', "aspectRatio", "16:9"));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-4-3', "aspectRatio", "4:3"));
  }

  /** Update the media title. */
  #updateMediaTitle() {
    const mediaName = {};
    mediaName.filename = "No media loaded";
    mediaName.title = "No media loaded";
    if (!kameHouse.core.isEmpty(this.#vlcPlayer.getVlcRcStatus().information)) {
      mediaName.filename = this.#vlcPlayer.getVlcRcStatus().information.meta.filename;
      mediaName.title = this.#vlcPlayer.getVlcRcStatus().information.meta.title;
    }
    kameHouse.util.dom.setHtml(document.getElementById("media-title"), mediaName.filename);
  }

  /** Reset the media title. */
  #resetMediaTitle() {
    const mediaName = {};
    mediaName.filename = "No media loaded";
    mediaName.title = "No media loaded";
    kameHouse.util.dom.setHtml(document.getElementById("media-title"), mediaName.filename);
  }

  /** Update subtitle delay. */
  #updateSubtitleDelay() {
    let subtitleDelay = this.#vlcPlayer.getVlcRcStatus().subtitleDelay;
    if (kameHouse.core.isEmpty(subtitleDelay)) {
      subtitleDelay = "0";
    }
    kameHouse.util.dom.setHtml(document.getElementById("subtitle-delay-value"), String(subtitleDelay));
  }

  /** Reset subtitle delay. */
  #resetSubtitleDelay() {
    kameHouse.util.dom.setHtml(document.getElementById("subtitle-delay-value"), "0");
  }

  /**
   * --------------------------------------------------------------------------
   * Update time Functionality
   */
  /** Update media time slider from VlcRcStatus and resets view when there's no input. */
  #updateTimeSlider() {
    if (!this.#timeSliderLocked) {
      if (!kameHouse.core.isEmpty(this.#vlcPlayer.getVlcRcStatus().time)) {
        this.updateCurrentTimeView(this.#vlcPlayer.getVlcRcStatus().time);
        this.#updateTotalTimeView(this.#vlcPlayer.getVlcRcStatus().length);
      } else {
        this.#resetTimeSlider();
      }
    }
  }

  /** Reset time slider. */
  #resetTimeSlider() {
    kameHouse.util.dom.setHtml(document.getElementById("current-time"), "--:--:--");
    kameHouse.util.dom.setVal(document.getElementById("time-slider"), 500);
    kameHouse.util.dom.setHtml(document.getElementById("total-time"), "--:--:--");
    kameHouse.util.dom.setAttr(document.getElementById("time-slider"),'max', 1000);
  }

  /** Update the displayed total time. */
  #updateTotalTimeView(value) {
    kameHouse.util.dom.setHtml(document.getElementById("total-time"), kameHouse.util.time.convertSecondsToHsMsSs(value));
    kameHouse.util.dom.setAttr(document.getElementById("time-slider"),'max', value);
  }

  /**
   * --------------------------------------------------------------------------
   * Update volume Functionality
   */
  /** Update volume slider from VlcRcStatus. */
  #updateVolumeSlider() {
    if (!this.#volumeSliderLocked) {
      const volume = this.#vlcPlayer.getVlcRcStatus().volume;
      if (!kameHouse.core.isEmpty(volume) || volume == 0) {
        this.updateVolumeView(volume);
      } else {
        this.#resetVolumeSlider();
      }
    }
  }

  /** Reset volume slider. */
  #resetVolumeSlider() { this.updateVolumeView(256); }

} // End VlcPlayerMainViewUpdater

/** 
 * Represents a media button that has state (pressed/unpressed).
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class StatefulMediaButton {

  static #DEFAULT_BTN_PREFIX_CLASS = 'media-btn';

  #vlcPlayer = null;
  #id = null;
  #pressedField = null;
  #pressedCondition = null;
  #buttonPrefixClass = null;

  constructor(vlcPlayer, id, pressedField, pressedCondition, btnPrefixClass) {
    this.#vlcPlayer = vlcPlayer;
    this.#id = id;
    this.#pressedField = pressedField;
    this.#pressedCondition = pressedCondition;
    if (kameHouse.core.isEmpty(btnPrefixClass)) {
      this.#buttonPrefixClass = StatefulMediaButton.#DEFAULT_BTN_PREFIX_CLASS;
    } else {
      this.#buttonPrefixClass = btnPrefixClass;
    }
  }

  /** Update the state of the button (pressed/unpressed) */
  updateState() {
    if (this.#isPressed()) {
      this.#setMediaButtonPressed();
    } else {
      this.#setMediaButtonUnpressed();
    }
  }  

  /** Determines if the button is pressed or unpressed. */
  #isPressed() { return this.#vlcPlayer.getVlcRcStatus()[this.#pressedField] == this.#pressedCondition; }

  /** Set media button pressed */
  #setMediaButtonPressed() {
    kameHouse.util.dom.removeClass(document.getElementById(this.#id), this.#buttonPrefixClass + '-unpressed');
    kameHouse.util.dom.addClass(document.getElementById(this.#id), this.#buttonPrefixClass + '-pressed');
  }

  /** Set media button unpressed */
  #setMediaButtonUnpressed() {
    kameHouse.util.dom.removeClass(document.getElementById(this.#id), this.#buttonPrefixClass + '-pressed');
    kameHouse.util.dom.addClass(document.getElementById(this.#id), this.#buttonPrefixClass + '-unpressed');
  }
} // End StatefulMediaButton

/** 
 * Manages the websocket connection, synchronization and keep alive loops. 
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class VlcPlayerSynchronizer {

  #vlcPlayer = null;
  #vlcRcStatusWebSocket = null;
  #playlistWebSocket = null;
  #syncLoopsConfig = {
    isRunningSyncVlcRcStatusLoop : false,
    isRunningSyncPlaylistLoop : false,
    isRunningKeepAliveWebSocketLoop : false,
    isRunningSyncVlcPlayerHttpLoop : false,
    vlcRcStatusLoopCount : 0,
    vlcPlaylistLoopCount : 0,    
    keepAliveWebSocketLoopCount : 0,
    syncVlcPlayerHttpLoopCount : 0
  };

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
    this.#vlcRcStatusWebSocket = new KameHouseWebSocket();
    this.#playlistWebSocket = new KameHouseWebSocket();
  }

  /**
   * Initialize web sockets.
   */
  initWebSockets() {
    const vlcRcStatusWebSocketStatusUrl = '/kame-house-vlcrc/api/ws/vlc-player/status';
    const vlcRcStatusWebSocketPollUrl = "/app/vlc-player/status-in";
    const vlcRcStatusWebSocketTopicUrl = '/topic/vlc-player/status-out';
    this.#vlcRcStatusWebSocket.statusUrl(vlcRcStatusWebSocketStatusUrl);
    this.#vlcRcStatusWebSocket.pollUrl(vlcRcStatusWebSocketPollUrl);
    this.#vlcRcStatusWebSocket.topicUrl(vlcRcStatusWebSocketTopicUrl);

    const playlistWebSocketStatusUrl = '/kame-house-vlcrc/api/ws/vlc-player/playlist';
    const playlistWebSocketPollUrl = "/app/vlc-player/playlist-in";
    const playlistWebSocketTopicUrl = '/topic/vlc-player/playlist-out';
    this.#playlistWebSocket.statusUrl(playlistWebSocketStatusUrl);
    this.#playlistWebSocket.pollUrl(playlistWebSocketPollUrl);
    this.#playlistWebSocket.topicUrl(playlistWebSocketTopicUrl);
  }

  /** Poll for an update of vlcRcStatus through the web socket. */
  pollVlcRcStatus() { this.#vlcRcStatusWebSocket.poll(); }

  /** Connects the websocket to the backend. */
  connectVlcRcStatus() {
    this.#vlcRcStatusWebSocket.connect((topicResponse) => {
      if (!kameHouse.core.isEmpty(topicResponse) && !kameHouse.core.isEmpty(topicResponse.body)) {
        this.#vlcPlayer.setVlcRcStatus(kameHouse.json.parse(topicResponse.body));
      } else {
        this.#vlcPlayer.setVlcRcStatus({});
      }
    });
  }

  /** Connects the playlist websocket to the backend. */
  connectPlaylist() {
    this.#playlistWebSocket.connect((topicResponse) => {
      if (!kameHouse.core.isEmpty(topicResponse) && !kameHouse.core.isEmpty(topicResponse.body)) {
        this.#vlcPlayer.setUpdatedPlaylist(kameHouse.json.parse(topicResponse.body));
      } else {
        this.#vlcPlayer.setUpdatedPlaylist(null);
      }
    });
  }

  /** 
   * Start infinite loop to pull VlcRcStatus from the server.
   * Break the loop setting isRunningSyncVlcRcStatusLoop to false.
   */
  syncVlcRcStatusLoop() {
    const config = {
      vlcRcStatusPullWaitTimeMs : 1000
    };
    this.#syncLoopExecution(config, "syncVlcRcStatusLoop", async (config) => {await this.#syncVlcRcStatusLoopRun(config)}, "isRunningSyncVlcRcStatusLoop", "vlcRcStatusLoopCount");
  }

  /** 
   * Start infinite loop to sync the current playlist from the server.
   * Break the loop setting isRunningSyncPlaylistLoop to false.
   */
  syncPlaylistLoop() {
    const config = {
      playlistLoopWaitTimeMs : 5000
    };
    this.#syncLoopExecution(config, "syncPlaylistLoop", async (config) => {await this.#syncPlaylistLoopRun(config)}, "isRunningSyncPlaylistLoop", "vlcPlaylistLoopCount");
  }

  /** 
   * Start infinite loop to keep alive the websocket connections.
   * Break the loop setting isRunningKeepAliveWebSocketLoop to false.
   */
  keepAliveWebSocketsLoop() {
    const config = {
      keepAliveLoopWaitMs : 5000,
      syncLoopStartDelayMs : 15000
    };
    this.#syncLoopExecution(config, "keepAliveWebSocketsLoop", async (config) => {await this.#keepAliveWebSocketsLoopRun(config)}, "isRunningKeepAliveWebSocketLoop", "keepAliveWebSocketLoopCount");
  }

  /** 
   * Start infinite loop to sync falling back to http calls when the websockets are disconnected.
   * Break the loop setting isRunningSyncVlcPlayerHttpLoop to false.
   */
  syncVlcPlayerHttpLoop() {
    const config = {
      syncVlcPlayerHttpWaitMs : 7000
    };
    this.#syncLoopExecution(config, "syncVlcPlayerHttpLoop", async (config) => {await this.#syncVlcPlayerHttpLoopRun(config)}, "isRunningSyncVlcPlayerHttpLoop", "syncVlcPlayerHttpLoopCount");
  }

  /**
   * Stop all sync loops.
   */
  stopVlcPlayerLoops() {
    const message = "KameHouse sent to background. Stopping sync loops and disconnecting websockets";
    kameHouse.logger.info(message, kameHouse.logger.getGreenText(message));
    this.#syncLoopsConfig.isRunningSyncVlcRcStatusLoop = false;
    this.#syncLoopsConfig.isRunningSyncPlaylistLoop = false;
    this.#syncLoopsConfig.isRunningKeepAliveWebSocketLoop = false;
    this.#syncLoopsConfig.isRunningSyncVlcPlayerHttpLoop = false;
    this.#vlcRcStatusWebSocket.disconnect();
    this.#playlistWebSocket.disconnect(); 
  }

  /**
   * Restart all sync loops.
   */
  restartVlcPlayerLoops() {
    const message = "KameHouse sent to foreground. Restarting sync loops and reconnecting websockets";
    kameHouse.logger.info(message, kameHouse.logger.getCyanText(message));
    this.#vlcRcStatusWebSocket.disconnect();
    this.#playlistWebSocket.disconnect();
    this.#restartSyncVlcPlayerHttpLoop(this.#getRestartLoopConfig());
    this.#restartSyncVlcRcStatusLoop(this.#getRestartLoopConfig());
    this.#restartSyncPlaylistLoop(this.#getRestartLoopConfig());
    this.#restartKeepAliveWebSocketsLoop(this.#getRestartLoopConfig());
  }

  /**
   * Run periodic health checks on all sync loops.
   */
  async syncLoopsHealthCheck() {
    const PERIODIC_HEALTH_CHECK_WAIT_MS = 15000;
    setTimeout(async () => {
      let continueLoop = true;
      let printLoopStatusCount = 0;
      while (continueLoop) {
        printLoopStatusCount++;
        if (printLoopStatusCount >= 4) {
          this.#printLoopStatus();
          printLoopStatusCount = 0;
        }
        this.#executeSyncLoopsHealthCheck();
        await kameHouse.core.sleep(PERIODIC_HEALTH_CHECK_WAIT_MS);
        if (PERIODIC_HEALTH_CHECK_WAIT_MS < -10000) { // fix sonar bug
          continueLoop = false;
        }
      }
    }, 0);
  }  

  /**
   * --------------------------------------------------------------------------
   * VlcRcStatus WebSocket functionality
   */
  /** Reconnects the VlcRcStatus websocket to the backend. */
  #reconnectVlcRcStatus() {
    this.#vlcRcStatusWebSocket.disconnect();
    this.connectVlcRcStatus();
  }

  /**
   * --------------------------------------------------------------------------
   * Playlist WebSocket functionality
   */
  /** Reconnects the playlist websocket to the backend. */
  #reconnectPlaylist() {
    this.#playlistWebSocket.disconnect();
    this.connectPlaylist();
  }

  /**
   * --------------------------------------------------------------------------
   * Sychronization loops
   */

  /**
   * General logic shared by all sync loops.
   */
  async #syncLoopExecution(config, loopName, loopRunFunction, isLoopRunningName, loopCountName) {
    setTimeout(async () => {
      kameHouse.logger.info("Started " + loopName);
      if (this.#syncLoopsConfig[isLoopRunningName] || this.#syncLoopsConfig[loopCountName] > 1) {
        const message = loopName + " is already running";
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        return;
      }
      this.#syncLoopsConfig[isLoopRunningName] = true;
      this.#syncLoopsConfig[loopCountName]++;
      if (config.syncLoopStartDelayMs) {
        kameHouse.logger.info("Started " + loopName + " with initial delay of " + config.syncLoopStartDelayMs + " ms");
        await kameHouse.core.sleep(config.syncLoopStartDelayMs);
      }
      while (this.#syncLoopsConfig[isLoopRunningName]) {
        await loopRunFunction(config);
        if (this.#syncLoopsConfig[loopCountName] > 1) {
          kameHouse.logger.info(loopName + ": Running multiple " + loopName + ", exiting this loop");
          break;
        }
      }
      this.#syncLoopsConfig[loopCountName]--;
      kameHouse.logger.info("Finished " + loopName);
    }, 0);
  }

  /**
   * VlcRcStatus loop run.
   */
  async #syncVlcRcStatusLoopRun(config) {
    kameHouse.logger.trace("syncVlcRcStatusLoop - vlcRcStatus: " + kameHouse.json.stringify(this.#vlcPlayer.getVlcRcStatus()));
    this.#setVlcRcStatusPullWaitTimeMs(config);
    this.#updateViewSyncVlcRcStatusLoop();
    await kameHouse.core.sleep(config.vlcRcStatusPullWaitTimeMs);
    if (config.vlcRcStatusPullWaitTimeMs < -10000) { // fix sonar bug
      this.#syncLoopsConfig.isRunningSyncVlcRcStatusLoop = false;
    }
  }

  /**
   * Set VlcRcStatus pull wait time.
   */
  #setVlcRcStatusPullWaitTimeMs(config) {
    const VLC_STATUS_CONNECTED_PLAYING_MS = 1000;
    const VLC_STATUS_CONNECTED_NOT_PLAYING_MS = 5000;
    const VLC_STATUS_DISCONNECTED_MS = 5000;
    if (!this.#vlcRcStatusWebSocket.isConnected()) {
      config.vlcRcStatusPullWaitTimeMs = VLC_STATUS_DISCONNECTED_MS;
      return;
    }
    if (kameHouse.core.isEmpty(this.#vlcPlayer.getVlcRcStatus().information)) {
      config.vlcRcStatusPullWaitTimeMs = VLC_STATUS_CONNECTED_NOT_PLAYING_MS;
      return;
    }
    config.vlcRcStatusPullWaitTimeMs = VLC_STATUS_CONNECTED_PLAYING_MS;
  }

  /**
   * Update vlc player view.
   */
  #updateViewSyncVlcRcStatusLoop() {
    if (this.#vlcRcStatusWebSocket.isConnected()) {
      // poll VlcRcStatus from the websocket.
      this.#vlcRcStatusWebSocket.poll();
      this.#vlcPlayer.updateView();
    }
  }

  /**
   * Playlist lopp run.
   */
  async #syncPlaylistLoopRun(config) {
    kameHouse.logger.trace("syncPlaylistLoop");
    if (this.#playlistWebSocket.isConnected()) {
      // poll playlist from the websocket.
      this.#playlistWebSocket.poll();
      this.#vlcPlayer.reloadPlaylist();
    }
    await kameHouse.core.sleep(config.playlistLoopWaitTimeMs);
    if (config.playlistLoopWaitTimeMs < -10000) { // fix sonar bug
      this.#syncLoopsConfig.isRunningSyncPlaylistLoop = false;
    }
  }

  /**
   * Keep alive websockets loop run.
   */
  async #keepAliveWebSocketsLoopRun(config) {
    kameHouse.logger.trace("keepAliveWebSocketsLoop");
    if (!this.#vlcRcStatusWebSocket.isConnected()) {
      kameHouse.logger.trace("keepAliveWebSocketsLoop: VlcRcStatus webSocket not connected. Reconnecting...");
      this.#reconnectVlcRcStatus();
    }
    if (!this.#playlistWebSocket.isConnected()) {
      kameHouse.logger.trace("keepAliveWebSocketsLoop: Playlist webSocket not connected. Reconnecting...");
      this.#reconnectPlaylist();
    }
    await kameHouse.core.sleep(config.keepAliveLoopWaitMs);
    if (config.keepAliveLoopWaitMs < -10000) { // fix sonar bug
      this.#syncLoopsConfig.isRunningKeepAliveWebSocketLoop = false;
    }
  }

  /**
   * Http fallback sync loop run.
   */
  async #syncVlcPlayerHttpLoopRun(config) {
    this.#setSyncVlcPlayerHttpWaitMs(config);
    this.#loadStateFromApiSyncVlcPlayerHttpLoop();
    await kameHouse.core.sleep(config.syncVlcPlayerHttpWaitMs);
    if (config.syncVlcPlayerHttpWaitMs < -10000) { // fix sonar bug
      this.#syncLoopsConfig.isRunningSyncVlcPlayerHttpLoop = false;
    }
  }

  /**
   * Set http fallback sync loop wait.
   */
  #setSyncVlcPlayerHttpWaitMs(config) {
    const WEB_SOCKETS_CONNECTED_WAIT_MS = 7000;
    const WEB_SOCKETS_DISCONNECTED_WAIT_MS = 2000;
    if (!this.#vlcRcStatusWebSocket.isConnected() || !this.#playlistWebSocket.isConnected()) {
      config.syncVlcPlayerHttpWaitMs = WEB_SOCKETS_DISCONNECTED_WAIT_MS;
      return;
    }
    config.syncVlcPlayerHttpWaitMs = WEB_SOCKETS_CONNECTED_WAIT_MS;
  }

  /**
   * Load vlc player state from http api.
   */
  #loadStateFromApiSyncVlcPlayerHttpLoop() {
    if (!this.#vlcRcStatusWebSocket.isConnected() || !this.#playlistWebSocket.isConnected()) {
      kameHouse.logger.debug("syncVlcPlayerHttpLoop: Websockets disconnected, synchronizing vlc player through http requests");
      this.#vlcPlayer.loadStateFromApi();
    } else {
      kameHouse.logger.trace("syncVlcPlayerHttpLoop: Websockets connected. Skipping synchronization through http requests");
    }
  }

  /**
   * Print all sync loops statuses.
   */
  #printLoopStatus() {
    const separator = "---------------------------------------------";
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
    const loopsStatus = "Sync loops status:";
    kameHouse.logger.trace(loopsStatus, kameHouse.logger.getYellowText(loopsStatus));
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
    kameHouse.logger.trace("isRunningSyncVlcRcStatusLoop: " + this.#syncLoopsConfig.isRunningSyncVlcRcStatusLoop);
    kameHouse.logger.trace("isRunningSyncPlaylistLoop: " + this.#syncLoopsConfig.isRunningSyncPlaylistLoop);
    kameHouse.logger.trace("isRunningKeepAliveWebSocketLoop: " + this.#syncLoopsConfig.isRunningKeepAliveWebSocketLoop);
    kameHouse.logger.trace("isRunningSyncVlcPlayerHttpLoop: " + this.#syncLoopsConfig.isRunningSyncVlcPlayerHttpLoop);
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
    kameHouse.logger.trace("vlcRcStatusLoopCount: " + this.#syncLoopsConfig.vlcRcStatusLoopCount);
    kameHouse.logger.trace("vlcPlaylistLoopCount: " + this.#syncLoopsConfig.vlcPlaylistLoopCount);
    kameHouse.logger.trace("keepAliveWebSocketLoopCount: " + this.#syncLoopsConfig.keepAliveWebSocketLoopCount);
    kameHouse.logger.trace("syncVlcPlayerHttpLoopCount: " + this.#syncLoopsConfig.syncVlcPlayerHttpLoopCount);        
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
  }

  /**
   * Execute health checks on all sync loops.
   */
  #executeSyncLoopsHealthCheck() {
    kameHouse.logger.trace("Checking state of sync loops");
    if (this.#syncLoopsConfig.vlcRcStatusLoopCount <= 0) {
      this.#restartSyncVlcRcStatusLoop(this.#getRestartLoopConfig());
    }
    if (this.#syncLoopsConfig.vlcPlaylistLoopCount <= 0) {
      this.#restartSyncPlaylistLoop(this.#getRestartLoopConfig());
    }
    if (this.#syncLoopsConfig.keepAliveWebSocketLoopCount <= 0) {
      this.#restartKeepAliveWebSocketsLoop(this.#getRestartLoopConfig());
    }
    if (this.#syncLoopsConfig.syncVlcPlayerHttpLoopCount <= 0) {
      this.#restartSyncVlcPlayerHttpLoop(this.#getRestartLoopConfig());
    }
  }

  /**
   * Get restart loop default config.
   */
  #getRestartLoopConfig() {
    const MAX_RETRIES = 30;
    const RESTART_LOOPS_WAIT_MS = 1000;
    return {
      maxRetries : MAX_RETRIES,
      restartLoopWaitMs : RESTART_LOOPS_WAIT_MS,
      restartLoopDelayMs : 7000
    };
  }

  /**
   * Shared logic between all restart loop executions.
   */
  #restartSyncLoopExecution(config, loopName, restartLoopFunction, loopCountName) {
    setTimeout(async () => {
      kameHouse.logger.info("Restarting " + loopName);
      let retriesLeft = config.maxRetries;
      let startLoop = true;
      while (this.#syncLoopsConfig[loopCountName] > 0) {
        retriesLeft--;
        kameHouse.logger.trace("waiting for " + loopName + " to finish before restarting");
        await kameHouse.core.sleep(config.restartLoopWaitMs);
        if (retriesLeft <= 0) {
          kameHouse.logger.info("too many attempts to restart " + loopName + ". It seems to be running already. Skipping restart");
          startLoop = false;
          break;
        }
        if (maxRetries < -10000) { // fix sonar bug
          this.#syncLoopsConfig[loopCountName] = 0;
        }
      }
      if (startLoop) {
        restartLoopFunction();
      }
    }, config.restartLoopDelayMs);
  }

  /**
   * Restart vlcRcStatus loop.
   */
  #restartSyncVlcRcStatusLoop(config) {
    const firstRestartConfig = this.#getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 5000;
    this.#restartSyncLoopExecution(firstRestartConfig, "vlcRcStatusLoop", () => {this.#vlcRcStatusRestartFunction()}, "vlcRcStatusLoopCount");
    this.#restartSyncLoopExecution(config, "vlcRcStatusLoop", () => {this.#vlcRcStatusRestartFunction()}, "vlcRcStatusLoopCount");
  }

  /**
   * VlcRcStatus loop restart function.
   */
  #vlcRcStatusRestartFunction() {
    this.#reconnectVlcRcStatus();
    this.syncVlcRcStatusLoop();
  }

  /**
   * Restart playlist loop.
   */
  #restartSyncPlaylistLoop(config) {
    const firstRestartConfig = this.#getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 5000;
    this.#restartSyncLoopExecution(firstRestartConfig, "vlcPlaylistLoop", () => {this.#playlistRestartFunction()}, "vlcPlaylistLoopCount");
    this.#restartSyncLoopExecution(config, "vlcPlaylistLoop", () => {this.#playlistRestartFunction()}, "vlcPlaylistLoopCount");
  }

  /**
   * Playlist loop restart function.
   */
  #playlistRestartFunction() {
    this.#reconnectPlaylist();
    this.syncPlaylistLoop();
  }

  /**
   * Restart keep alive websockets loop.
   */
  #restartKeepAliveWebSocketsLoop(config) {
    const firstRestartConfig = this.#getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 5000;
    this.#restartSyncLoopExecution(firstRestartConfig, "keepAliveWebSocketLoop", () => {this.keepAliveWebSocketsLoop()}, "keepAliveWebSocketLoopCount");
    this.#restartSyncLoopExecution(config, "keepAliveWebSocketLoop", () => {this.keepAliveWebSocketsLoop()}, "keepAliveWebSocketLoopCount");
  }
  
  /**
   * Restart http fallback sync loop.
   */
  #restartSyncVlcPlayerHttpLoop(config) {
    const firstRestartConfig = this.#getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 2000;
    this.#restartSyncLoopExecution(firstRestartConfig, "syncVlcPlayerHttpLoop", () => {this.syncVlcPlayerHttpLoop()}, "syncVlcPlayerHttpLoopCount");
    config.restartLoopDelayMs = 9000;
    this.#restartSyncLoopExecution(config, "syncVlcPlayerHttpLoop", () => {this.syncVlcPlayerHttpLoop()}, "syncVlcPlayerHttpLoopCount");
  }

} // End VlcPlayerSynchronizer

/** 
 * Represents the Playlist component in vlc-player page. 
 * It also handles the updates to the view of the playlist.
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class VlcPlayerPlaylist {

  #vlcPlayer = null;
  #playSelectedUrl = null;
  #dobleLeftImg = null;
  #dobleRightImg = null;

  #currentPlaylist = null;
  #updatedPlaylist = null;
  #tbodyAbsolutePaths = null;
  #tbodyFilenames = null;

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
    this.#playSelectedUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/commands';
    this.#dobleLeftImg = this.#createDoubleArrowImg("left");
    this.#dobleRightImg = this.#createDoubleArrowImg("right");
  }

  /** Init Playlist. */
  init() {
    kameHouse.util.dom.replaceWith(document.getElementById("toggle-playlist-filenames-img"), this.#dobleRightImg);
  }

  /** Set updated playlist: Temporary storage for the playlist I receive from the websocket */
  setUpdatedPlaylist(updatedPlaylistParam) { 
    this.#updatedPlaylist = updatedPlaylistParam; 
  }  

  /** Reload playlist updating the playlist view. */
  reload() {
    if (!this.#isPlaylistUpdated(this.#currentPlaylist, this.#updatedPlaylist)) {
      // Playlist content not updated, just update currently playing element and return
      this.#highlightCurrentPlayingItem();
      return;
    }
    this.#currentPlaylist = this.#updatedPlaylist;
    const playlistTableBody = document.getElementById('playlist-table-body');
    // Clear playlist content. 
    kameHouse.util.dom.empty(playlistTableBody);
    // Add the new playlist items received from the server.
    if (kameHouse.core.isEmpty(this.#currentPlaylist) || kameHouse.core.isEmpty(this.#currentPlaylist.length) ||
    this.#currentPlaylist.length <= 0) {
      kameHouse.util.dom.append(playlistTableBody, this.#getEmptyPlaylistTr());
    } else {
      this.#tbodyFilenames = this.#getPlaylistTbody();
      this.#tbodyAbsolutePaths = this.#getPlaylistTbody();
      for (const currentPlaylistElement of this.#currentPlaylist) {
        const absolutePath = currentPlaylistElement.filename;
        const filename = kameHouse.util.file.getShortFilename(absolutePath);
        const playlistElementId = currentPlaylistElement.id;
        kameHouse.util.dom.append(this.#tbodyFilenames, this.#getPlaylistTr(filename, playlistElementId));
        kameHouse.util.dom.append(this.#tbodyAbsolutePaths, this.#getPlaylistTr(absolutePath, playlistElementId));
      }
      kameHouse.util.dom.replaceWith(playlistTableBody, this.#tbodyFilenames);
      this.#highlightCurrentPlayingItem();
      this.#vlcPlayer.filterPlaylistRows();
    }
  }

  /** Scroll to the current playing element in the playlist. */
  scrollToCurrentlyPlaying() {
    const currentPlId = this.#vlcPlayer.getVlcRcStatus().currentPlId;
    const currentPlayingRowId = 'playlist-table-row-id-' + currentPlId;
    const currentPlayingRow = document.getElementById(currentPlayingRowId);
    kameHouse.logger.debug("Scroll to " + currentPlayingRowId);
    if (!kameHouse.core.isEmpty(currentPlayingRow)) {
      const playlistTableWrapper = document.getElementById('playlist-table-wrapper');
      kameHouse.core.scrollTop(playlistTableWrapper, 0);
      const scrollToOffset = kameHouse.core.offset(currentPlayingRowId).top - kameHouse.core.offset('playlist-table-wrapper').top;
      kameHouse.core.scrollTop(playlistTableWrapper, scrollToOffset);
    }
  }

  /** 
   * Update the playlist view. Add all the functionality that needs to happen 
   * to update the view of the playlist when vlcRcStatus changes  
   */
  updateView() {
    if (!kameHouse.core.isEmpty(this.#vlcPlayer.getVlcRcStatus())) {
      this.#highlightCurrentPlayingItem();
    } else {
      this.resetView();
    }
  }  

  /** 
   * Reset the playlist view.
   */
  resetView() {
    this.#updatedPlaylist = null;
    this.reload();
  }

  /** Create an image object to toggle when expanding/collapsing playlist browser filenames. */
  #createDoubleArrowImg(direction) {
    return kameHouse.util.dom.getImgBtn({
      id: "toggle-playlist-filenames-img",
      src: "/kame-house/img/other/double-" + direction + "-green.png",
      className: "img-btn-kh img-btn-s-kh btn-playlist-controls",
      alt: "Expand/Collapse Filename",
      onClick: () => this.#toggleExpandPlaylistFilenames()
    });
  }

  /** Compares two playlists. Returns true if they are different or empty. Expects 2 vlc playlist arrays */
  #isPlaylistUpdated(currentPls, updatedPls) {
    const MAX_COMPARISONS = 30;
    // For empty playlists, return true, so it updates the UI
    if (kameHouse.core.isEmpty(currentPls) || kameHouse.core.isEmpty(updatedPls)) {
      return true;
    }
    // If the sizes don't match, it's updated
    if (currentPls.length != updatedPls.length) {
      return true;
    }
    // If the sizes match, compare playlists elements in the specified increment. 
    // Don't check all filenames to avoid doing too many comparisons in very large playlists
    let step = 0;
    if (currentPls.length <= MAX_COMPARISONS) {
      step = 1;
    } else if ((currentPls.length > MAX_COMPARISONS) && (currentPls.length <= MAX_COMPARISONS * 2)) {
      step = 2;
    }
    if (step == 0) {
      step = Math.round(currentPls.length / MAX_COMPARISONS);
    }
    for (let i = 0; i < currentPls.length; i = i + step) {
      if (currentPls[i].filename != updatedPls[i].filename) {
        return true;
      }
    }
    // Playlist is not updated
    return false;
  }

  /** Play the clicked element from the playlist. */
  #clickEventOnPlaylistRow(event) {
    kameHouse.logger.debug("Play playlist id: " + event.data.id);
    const requestBody = {
      name: 'pl_play',
      id: event.data.id
    };
    this.#vlcPlayer.getRestClient().post(this.#playSelectedUrl, kameHouse.http.getApplicationJsonHeaders(), requestBody);
  }

  /** Highlight currently playing item in the playlist. */
  #highlightCurrentPlayingItem() {
    const currentPlId = this.#vlcPlayer.getVlcRcStatus().currentPlId;
    const currentPlIdAsRowId = 'playlist-table-row-id-' + currentPlId;
    document.querySelectorAll('#playlist-table-body tr td button').forEach((element) => {
      kameHouse.util.dom.removeClass(element, "active");
    });
    const currentPlaylistElement = document.querySelector("#" + currentPlIdAsRowId + " td button");
    if (currentPlaylistElement) {
      kameHouse.util.dom.addClass(currentPlaylistElement, "active");
    }
  }

  /** Toggle expand or collapse filenames in the playlist */
  #toggleExpandPlaylistFilenames() {
    if (kameHouse.core.isEmpty(this.#tbodyFilenames) || kameHouse.core.isEmpty(this.#tbodyFilenames.firstElementChild)) {
      return;
    }
    const filenamesFirstFile = this.#tbodyFilenames.firstElementChild.textContent;
    const currentFirstFile = document.getElementById('playlist-table-body').firstElementChild.textContent;
    const playlistTable = document.getElementById('playlist-table');
    let isExpandedFilename = null;

    if (currentFirstFile == filenamesFirstFile) {
      // currently displaying filenames, switch to absolute paths 
      if (!kameHouse.core.isEmpty(this.#tbodyFilenames)) {
        kameHouse.util.dom.detach(this.#tbodyFilenames);
      }
      kameHouse.util.dom.append(playlistTable, this.#tbodyAbsolutePaths);
      isExpandedFilename = true;
    } else {
      // currently displaying absolute paths, switch to filenames 
      if (!kameHouse.core.isEmpty(this.#tbodyAbsolutePaths)) {
        kameHouse.util.dom.detach(this.#tbodyAbsolutePaths);
      }
      kameHouse.util.dom.append(playlistTable, this.#tbodyFilenames);
      isExpandedFilename = false;
    }
    this.#highlightCurrentPlayingItem();
    this.#updateExpandPlaylistFilenamesIcon(isExpandedFilename);
    this.#vlcPlayer.filterPlaylistRows();
  }

  /** Update the icon to expand or collapse the playlist filenames */
  #updateExpandPlaylistFilenamesIcon(isExpandedFilename) {
    if (isExpandedFilename) {
      kameHouse.util.dom.replaceWith(document.getElementById("toggle-playlist-filenames-img"), this.#dobleLeftImg);
    } else {
      kameHouse.util.dom.replaceWith(document.getElementById("toggle-playlist-filenames-img"), this.#dobleRightImg);
    }
  }

  /**
   * Get empty playlist table row.
   */
  #getEmptyPlaylistTr() {
    const madaMadaDane = 'まだまだだね';
    return kameHouse.util.dom.getTrTd("No playlist to browse loaded yet or unable to sync." + madaMadaDane + " :)");
  }
  
  /**
   * Get playlist table body.
   */
  #getPlaylistTbody() {
    return kameHouse.util.dom.getTbody({
      id: "playlist-table-body"
    });
  }

  /**
   * Get playlist table row.
   */
  #getPlaylistTr(displayName, playlistElementId) {
    return kameHouse.util.dom.getTr({
      id: "playlist-table-row-id-" + playlistElementId
    }, kameHouse.util.dom.getTd({}, this.#getPlaylistTrBtn(displayName, playlistElementId)));
  }

  /**
   * Get playlist table row button.
   */
  #getPlaylistTrBtn(displayName, playlistElementId) {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "playlist-table-btn",
      },
      html: displayName,
      clickData: {
        id: playlistElementId
      },
      click: (event) => {this.#clickEventOnPlaylistRow(event)}
    });
  }
} // End VlcPlayerPlaylist

/** 
 * Represents an internal rest client for the VlcPlayer to split functionality. 
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor
 * and added as a property to VlcPlayer.restClient inside that constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class VlcPlayerRestClient {

  #vlcPlayer = null;

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
  }

  /** Execute GET on the specified url and display the output in the debug table. */
  get(url, requestHeaders, requestBody, updateCursor, successCallback, errorCallback) {
    if (updateCursor) {
      kameHouse.util.cursor.setCursorWait();
    }
    const config = kameHouse.http.getConfig();
    config.timeout = 10;
    kameHouse.plugin.debugger.http.get(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        if (!kameHouse.core.isEmpty(successCallback)) {
          successCallback(responseBody, responseCode, responseDescription, responseHeaders);
        } else {
          this.#apiCallSuccessDefault(responseBody);
        }
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        if (!kameHouse.core.isEmpty(errorCallback)) {
          errorCallback(responseBody, responseCode, responseDescription, responseHeaders);
        } else {
          this.#apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders);
          if (responseCode == "404") {
            kameHouse.plugin.debugger.displayResponseData("Could not connect to VLC player to get the status.", responseCode, responseDescription, responseHeaders);
          }
        }
      });
  }

  /** Execute a POST request to the specified url with the specified request body. */
  post(url, requestHeaders, requestBody) {
    kameHouse.util.cursor.setCursorWait();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#apiCallSuccessDefault(responseBody),
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders)
    );
  }

  /** Execute a DELETE request to the specified url with the specified request body. */
  delete(url, requestHeaders, requestBody) {
    kameHouse.util.cursor.setCursorWait();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#apiCallSuccessDefault(responseBody),
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders)
    );
  }

  /** Default actions for succesful api responses */
  #apiCallSuccessDefault(responseBody) {
    kameHouse.util.cursor.setCursorDefault();
    kameHouse.plugin.modal.loadingWheelModal.close();
    this.#vlcPlayer.pollVlcRcStatus();
  }

  /** Default actions for error api responses */
  #apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.util.cursor.setCursorDefault();
    kameHouse.plugin.modal.loadingWheelModal.close();
    // Don't display api errors for not found or service not available errors or cordova mock
    if (responseCode != 404 && responseCode != 503 && responseCode != 999 && responseCode > 300) {
      kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    }
  }
} // End VlcPlayerRestClient

/** 
 * Handles the debugger functionality of vlc player in the debugger's custom area.
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains prototype.
 * 
 * @author nbrest
 */
class VlcPlayerDebugger {

  #vlcPlayer = null;
  #vlcRcStatusApiUrl = null;
  #playlistApiUrl = null;

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
    this.#vlcRcStatusApiUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/status';
    this.#playlistApiUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/playlist';
  }

  /** Get the vlcRcStatus from an http api call instead of from the websocket. */
  getVlcRcStatusFromApi() { 
    this.#vlcPlayer.getRestClient().get(this.#vlcRcStatusApiUrl, null, null, false, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#getVlcRcStatusApiSuccessCallback(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#getVlcRcStatusApiErrorCallback(responseBody, responseCode, responseDescription, responseHeaders)}); 
  }

  /** Get the playlist from an http api call instead of from the websocket. */
  getPlaylistFromApi() { 
    this.#vlcPlayer.getRestClient().get(this.#playlistApiUrl, null, null, false, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#getPlaylistApiSuccessCallback(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#getPlaylistApiErrorCallback(responseBody, responseCode, responseDescription, responseHeaders)}); 
  }

  /** Update the main player view. */
  #getVlcRcStatusApiSuccessCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    this.#vlcPlayer.setVlcRcStatus(responseBody);
    this.#vlcPlayer.updateView();
  }

  /** Reset view if there's an error getting the vlcRcStatus from the api. */
  #getVlcRcStatusApiErrorCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.logger.trace("Unable to get vlcRcStatus from an API call. This can happen if vlc player process isn't running");
    this.#vlcPlayer.setVlcRcStatus({});
    this.#vlcPlayer.updateView();
  }

  /** Update the playlist view. */
  #getPlaylistApiSuccessCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    this.#vlcPlayer.getPlaylist().setUpdatedPlaylist(responseBody);
    this.#vlcPlayer.getPlaylist().reload();
  }

  /** Reset playlist view if there's an error getting the playlist from the api. */
  #getPlaylistApiErrorCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.logger.trace("Unable to get the playlist from an API call. This can happen if vlc player process isn't running");
    this.#vlcPlayer.getPlaylist().setUpdatedPlaylist(null);
    this.#vlcPlayer.getPlaylist().reload();
  }
  
} // End VlcPlayerDebugger

kameHouse.ready(() => {kameHouse.addExtension("vlcPlayer", new VlcPlayer("localhost"))});