/** 
 * VlcPlayer entity.
 * 
 * This prototype contains the public interface to interact with VlcPlayer. The logic is
 * implemented in the component prototypes mentioned above. It was designed this way because
 * VlcPlayer does a lot of functionality, so it seems best to split it into different
 * prototypes that each handle more specific functionality.
 * 
 * Call load() after instantiating VlcPlayer to connect the internal websocket
 * and start the sync loops.
 * 
 * @author nbrest
 */
function VlcPlayer(hostname) {

  this.load = load;
  this.loadStateFromApi = loadStateFromApi;
  this.getHostname = getHostname;
  this.getPlaylist = getPlaylist;
  this.openTab = openTab;
  this.playFile = playFile;
  this.execVlcRcCommand = execVlcRcCommand;
  this.updateSubtitleDelay = updateSubtitleDelay;
  this.updateAspectRatio = updateAspectRatio;
  this.seek = seek;
  this.setVolume = setVolume;
  this.close = close;
  this.getVlcRcStatus = getVlcRcStatus;
  this.pollVlcRcStatus = pollVlcRcStatus;
  this.setVlcRcStatus = setVlcRcStatus;
  this.setUpdatedPlaylist = setUpdatedPlaylist;
  this.reloadPlaylist = reloadPlaylist;
  this.scrollToCurrentlyPlaying = scrollToCurrentlyPlaying;
  this.filterPlaylistRows = filterPlaylistRows;
  this.toggleExpandPlaylistFilenames = toggleExpandPlaylistFilenames;
  this.updateView = updateView;
  this.resetView = resetView;
  this.updateCurrentTimeView = updateCurrentTimeView;
  this.updateVolumeView = updateVolumeView;
  this.getRestClient = getRestClient;
  this.getDebugger = getDebugger;
  this.unlockScreen = unlockScreen;
  this.wolMediaServer = wolMediaServer;

  const commandExecutor = new VlcPlayerCommandExecutor(this);
  const playlist = new VlcPlayerPlaylist(this);
  const restClient = new VlcPlayerRestClient(this);
  const mainViewUpdater = new VlcPlayerMainViewUpdater(this);
  const vlcPlayerDebugger = new VlcPlayerDebugger(this);
  let synchronizer = null;

  let vlcRcStatus = {};

  /** Load VlcPlayer extension. */
  function load() {
    kameHouse.logger.info("Started initializing VLC Player");
    mainViewUpdater.setStatefulButtons();
    kameHouse.util.module.loadKameHouseWebSocket();
    loadStateFromCookies();
    playlist.init();
    kameHouse.util.mobile.setMobileEventListeners(stopVlcPlayerLoops, restartVlcPlayerLoops);
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger"], () => {
      kameHouse.plugin.debugger.renderCustomDebugger("/kame-house/html-snippets/vlc-player/debug-mode-custom.html", () => {});
      kameHouse.util.mobile.exec(
        () => {loadStateFromApi();},
        () => {
          kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
            loadStateFromApi();
          });
        }
      );
    });
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger", "kameHouseWebSocket"], () => {
      synchronizer = new VlcPlayerSynchronizer(this);
      synchronizer.syncVlcPlayerHttpLoop();
      kameHouse.util.mobile.exec(
        () => {startSynchronizerLoops();},
        () => {
          kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
            // wait for the mobile config to be available before starting the websockets and sync loops
            startSynchronizerLoops();
          });
        }
      );
      kameHouse.util.module.setModuleLoaded("vlcPlayer");
    });
  }

  /**
   * Start synchronization loops.
   */
  function startSynchronizerLoops() {
    kameHouse.logger.info("Started initializing vlc player websockets and sync loops");
    synchronizer.initWebSockets();
    synchronizer.connectVlcRcStatus();
    synchronizer.connectPlaylist();
    synchronizer.syncVlcRcStatusLoop();
    synchronizer.syncPlaylistLoop();
    synchronizer.keepAliveWebSocketsLoop();
    synchronizer.syncLoopsHealthCheck();
  }

  /**
   * Stop synchronization loops.
   */
  function stopVlcPlayerLoops() {
    synchronizer.stopVlcPlayerLoops();
  }

  /**
   * Restart synchronization loops.
   */
  function restartVlcPlayerLoops() {
    synchronizer.restartVlcPlayerLoops();
  }

  /**
   * Load the current state from the cookies.
   */
  function loadStateFromCookies() {
    let currentTab = kameHouse.util.cookies.getCookie('kh-vlc-player-current-tab');
    if (!currentTab || currentTab == '') {
      currentTab = 'tab-playing';
    }
    openTab(currentTab);
  }

  /**
   * Load the vlc player state and refresh the view from API calls (not through websockets).
   */
  function loadStateFromApi() {
    vlcPlayerDebugger.getVlcRcStatusFromApi();
    vlcPlayerDebugger.getPlaylistFromApi();
  }

  /** Get the hostname for this instance of VlcPlayer */
  function getHostname() {
    return hostname;
  }

  /** Get internal object to manage the playlist */
  function getPlaylist() {
    return playlist;
  }

  /**
   * --------------------------------------------------------------------------
   * Tab manager
   */
  /**
   * Open vlc player tab.
   */
  function openTab(vlcPlayerTabDivId) {
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
   * --------------------------------------------------------------------------
   * Execute VlcPlayer commands
   */
  /**
   * Play the specified file in vlc.
   */
  function playFile(fileName) { commandExecutor.playFile(fileName); }

  /**
   * Execute the specified vlc command.
   */
  function execVlcRcCommand(name, val) { commandExecutor.execVlcRcCommand(name, val); }

  /**
   * Set the subtitle delay.
   */
  function updateSubtitleDelay(increment) {
    let subtitleDelay = getVlcRcStatus().subtitleDelay;
    if (!kameHouse.core.isEmpty(subtitleDelay)) {
      subtitleDelay = Number(subtitleDelay) + Number(increment);
    } else {
      subtitleDelay = 0 + Number(increment);
    }
    commandExecutor.execVlcRcCommand('subdelay', subtitleDelay);
  }

  /**
   * Set aspect ratio.
   */
  function updateAspectRatio(aspectRatio) {
    if (!kameHouse.core.isEmpty(aspectRatio)) {
      commandExecutor.execVlcRcCommand('aspectratio', aspectRatio);
    }
  }

  /**
   * Seek through the current playing file.
   */
  function seek(value) {
    mainViewUpdater.updateCurrentTimeView(value);
    commandExecutor.execVlcRcCommand('seek', value);
    mainViewUpdater.setTimeSliderLocked(false);
  }

  /**
   * Update the volume.
   */ 
  function setVolume(value) {
    mainViewUpdater.updateVolumeView(value);
    commandExecutor.execVlcRcCommand('volume', value);
    mainViewUpdater.setVolumeSliderLocked(false);
  }

  /**
   * Close vlc player.
   */
  function close() { commandExecutor.close(); }

  /**
   * --------------------------------------------------------------------------
   * VlcRcStatus synced from the backend
   */
  /**
   * Get the current vlcRc status.
   */
  function getVlcRcStatus() { return vlcRcStatus; }

  /**
   * Pol vlcrc status from the websocket.
   */
  function pollVlcRcStatus() { synchronizer.pollVlcRcStatus(); }

  /** 
   * Set the VlcRcStatus. vlcRcStatus must never be undefined or null.
   * If no value is passed, set an empty object. Always set vlcRcStatus
   * through this method.
   */
  function setVlcRcStatus(vlcRcStatusParam) {
    if (!kameHouse.core.isEmpty(vlcRcStatusParam)) {
      vlcRcStatus = vlcRcStatusParam;
    } else {
      vlcRcStatus = {};
    }
  }

  /**
   * --------------------------------------------------------------------------
   * Playlist functionality
   */
  /**
   * Set updated playlist.
   */
  function setUpdatedPlaylist(updatedPlaylist) { playlist.setUpdatedPlaylist(updatedPlaylist); }

  /**
   * Reload playlist.
   */
  function reloadPlaylist() { playlist.reload(); }

  /**
   * Scroll to currently playing item.
   */
  function scrollToCurrentlyPlaying() { playlist.scrollToCurrentlyPlaying(); }

  /**
   * Filter playlist rows.
   */
  function filterPlaylistRows() {
    const filterString = document.getElementById("playlist-filter-input").value;
    kameHouse.util.table.filterTableRows(filterString, 'playlist-table-body');
  }

  /**
   * Toggle expand/collapse playlist entries names.
   */
  function toggleExpandPlaylistFilenames() { playlist.toggleExpandPlaylistFilenames(); }

  /**
   * --------------------------------------------------------------------------
   * Update view functionality
   */
  /** Calls each internal module that has view logic to update it's view. */
  function updateView() {
    mainViewUpdater.updateView();
    playlist.updateView();
  }

  /** Calls each internal module that has view logic to reset it's view. */
  function resetView() {
    setVlcRcStatus({});
    mainViewUpdater.resetView();
    playlist.resetView();
  }

  /**
   * Update current time view.
   */
  function updateCurrentTimeView(value) {
    mainViewUpdater.setTimeSliderLocked(true);
    mainViewUpdater.updateCurrentTimeView(value);
  }

  /**
   * Update volume view.
   */
  function updateVolumeView(value) {
    mainViewUpdater.setVolumeSliderLocked(true);
    mainViewUpdater.updateVolumeView(value);
  }

  /**
   * --------------------------------------------------------------------------
   * Rest Client functionality
   */
  /**
   * Get REST client.
   * Use this getter internally from other components of VlcPlayer. Not externally.
   */
  function getRestClient() { return restClient; }

  /**
   * --------------------------------------------------------------------------
   * Debugger functionality
   */
  /**
   * Get vlc player debugger.
   */
  function getDebugger() { return vlcPlayerDebugger; }

  /**
   * --------------------------------------------------------------------------
   * Links to external resources
   */
  /**
   * Unlock screen.
   */
  function unlockScreen() {
    const UNLOCK_SCREEN_API_URL = "/kame-house-admin/api/v1/admin/screen/unlock";
    getRestClient().post(UNLOCK_SCREEN_API_URL, null, null);
  }

  /**
   * Wake on lan media server.
   */
  function wolMediaServer() {
    const requestParam =  {
      "server" : "media.server"
    };
    const WOL_MEDIA_SERVER_API_URL = "/kame-house-admin/api/v1/admin/power-management/wol";
    getRestClient().post(WOL_MEDIA_SERVER_API_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam);
  }
} // End VlcPlayer

/** 
 * Handles the execution of vlc commands, such as play, stop, next, close, etc.
 * 
 * This prototype is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
function VlcPlayerCommandExecutor(vlcPlayer) {

  this.execVlcRcCommand = execVlcRcCommand;
  this.playFile = playFile;
  this.close = close;

  const vlcPlayerProcessControlUrl = '/kame-house-vlcrc/api/v1/vlc-rc/vlc-process';
  const vlcRcCommandUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/commands';

  /** Create a vlcrc command with the parameters and execute the request to the server. */
  function execVlcRcCommand(name, val) {
    let requestBody;
    if (kameHouse.core.isEmpty(val)) {
      requestBody = {
        name: name
      };
    } else {
      requestBody = {
        name: name,
        val: val
      };
    }
    vlcPlayer.getRestClient().post(vlcRcCommandUrl, kameHouse.http.getApplicationJsonHeaders(), requestBody);
  }

  /** Play the selected file (or playlist) into vlc player and reload the current playlist. */
  function playFile(fileName) {
    kameHouse.logger.debug("File to play: " + fileName);
    const requestParam =  {
      "file" : fileName
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    vlcPlayer.getRestClient().post(vlcPlayerProcessControlUrl, kameHouse.http.getUrlEncodedHeaders(), requestParam);
  }

  /** Close vlc player. */
  function close() {
    vlcPlayer.getRestClient().delete(vlcPlayerProcessControlUrl, null, null);
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
function VlcPlayerMainViewUpdater(vlcPlayer) {

  this.setTimeSliderLocked = setTimeSliderLocked;
  this.setVolumeSliderLocked = setVolumeSliderLocked;
  this.updateView = updateView;
  this.resetView = resetView;
  this.updateCurrentTimeView = updateCurrentTimeView;
  this.updateVolumeView = updateVolumeView;
  this.setStatefulButtons = setStatefulButtons;

  const statefulButtons = [];

  let timeSliderLocked = false;
  let volumeSliderLocked = false;

  /**
   * Set stateful buttons state.
   */
  function setStatefulButtons() {
    statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-fullscreen', "fullscreen", true));
    statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-repeat-1', "repeat", true));
    statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-repeat', "loop", true));
    statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-shuffle', "random", true));
    statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-stop', "state", "stopped"));
    statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-mute', "volume", 0, 'btn-mute'));
    statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-16-9', "aspectRatio", "16:9"));
    statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-4-3', "aspectRatio", "4:3"));
  }

  /** Set time slider locked. */
  function setTimeSliderLocked(value) { timeSliderLocked = value; }

  /** Set volume slider locked. */
  function setVolumeSliderLocked(value) { volumeSliderLocked = value; }

  /** Update vlc player view for main view objects. */
  function updateView() {
    if (!kameHouse.core.isEmpty(vlcPlayer.getVlcRcStatus())) {
      updateMediaTitle();
      updateTimeSlider();
      updateVolumeSlider();
      updateSubtitleDelay();
      statefulButtons.forEach((statefulButton) => statefulButton.updateState());
    } else {
      resetView();
    }
  }

  /** Reset vlc player view for main view objects. */
  function resetView() {
    resetMediaTitle();
    resetTimeSlider();
    resetVolumeSlider();
    resetSubtitleDelay();
    statefulButtons.forEach(statefulButton => statefulButton.updateState());
  }

  /** Update the media title. */
  function updateMediaTitle() {
    const mediaName = {};
    mediaName.filename = "No media loaded";
    mediaName.title = "No media loaded";
    if (!kameHouse.core.isEmpty(vlcPlayer.getVlcRcStatus().information)) {
      mediaName.filename = vlcPlayer.getVlcRcStatus().information.meta.filename;
      mediaName.title = vlcPlayer.getVlcRcStatus().information.meta.title;
    }
    kameHouse.util.dom.setHtml($("#media-title"), mediaName.filename);
  }

  /** Reset the media title. */
  function resetMediaTitle() {
    const mediaName = {};
    mediaName.filename = "No media loaded";
    mediaName.title = "No media loaded";
    kameHouse.util.dom.setHtml($("#media-title"), mediaName.filename);
  }

  /** Update subtitle delay. */
  function updateSubtitleDelay() {
    let subtitleDelay = vlcPlayer.getVlcRcStatus().subtitleDelay;
    if (kameHouse.core.isEmpty(subtitleDelay)) {
      subtitleDelay = "0";
    }
    kameHouse.util.dom.setHtml($("#subtitle-delay-value"), String(subtitleDelay));
  }

  /** Reset subtitle delay. */
  function resetSubtitleDelay() {
    kameHouse.util.dom.setHtml($("#subtitle-delay-value"), "0");
  }

  /**
   * --------------------------------------------------------------------------
   * Update time Functionality
   */
  /** Update media time slider from VlcRcStatus and resets view when there's no input. */
  function updateTimeSlider() {
    if (!timeSliderLocked) {
      if (!kameHouse.core.isEmpty(vlcPlayer.getVlcRcStatus().time)) {
        updateCurrentTimeView(vlcPlayer.getVlcRcStatus().time);
        updateTotalTimeView(vlcPlayer.getVlcRcStatus().length);
      } else {
        resetTimeSlider();
      }
    }
  }

  /** Reset time slider. */
  function resetTimeSlider() {
    kameHouse.util.dom.setHtml($("#current-time"), "--:--:--");
    kameHouse.util.dom.setVal($("#time-slider"), 500);
    kameHouse.util.dom.setHtml($("#total-time"), "--:--:--");
    kameHouse.util.dom.setAttr($("#time-slider"),'max', 1000);
  }

  /** Update the displayed current time. */
  function updateCurrentTimeView(value) {
    const currentTime = document.getElementById("current-time");
    kameHouse.util.dom.setInnerHtml(currentTime, kameHouse.util.time.convertSecondsToHsMsSs(value));
    kameHouse.util.dom.setVal($("#time-slider"), value);
  }

  /** Update the displayed total time. */
  function updateTotalTimeView(value) {
    kameHouse.util.dom.setHtml($("#total-time"), kameHouse.util.time.convertSecondsToHsMsSs(value));
    kameHouse.util.dom.setAttr($("#time-slider"),'max', value);
  }

  /**
   * --------------------------------------------------------------------------
   * Update volume Functionality
   */
  /** Update volume slider from VlcRcStatus. */
  function updateVolumeSlider() {
    if (!volumeSliderLocked) {
      if (!kameHouse.core.isEmpty(vlcPlayer.getVlcRcStatus().volume)) {
        updateVolumeView(vlcPlayer.getVlcRcStatus().volume);
      } else {
        resetVolumeSlider();
      }
    }
  }

  /** Reset volume slider. */
  function resetVolumeSlider() { updateVolumeView(256); }

  /** Update volume percentage to display with the specified value. */
  function updateVolumeView(value) {
    kameHouse.util.dom.setVal($("#volume-slider"), value);
    const volumePercentaje = Math.floor(value * 200 / 512);
    const currentVolume = document.getElementById("current-volume");
    kameHouse.util.dom.setInnerHtml(currentVolume, volumePercentaje + "%");
  }
} // End VlcPlayerMainViewUpdater

/** 
 * Represents a media button that has state (pressed/unpressed).
 * 
 * This prototype is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
function StatefulMediaButton(vlcPlayer, id, pressedField, pressedCondition, btnPrefixClass) {

  this.updateState = updateState;

  const defaultBtnPrefixClass = 'media-btn';

  let buttonPrefixClass = btnPrefixClass;

  /**
   * StatefulMediaButton constructor.
   */
  function constructor() {
    if (kameHouse.core.isEmpty(btnPrefixClass)) {
      buttonPrefixClass = defaultBtnPrefixClass;
    }
  }
  constructor();

  /** Determines if the button is pressed or unpressed. */
  function isPressed() { return vlcPlayer.getVlcRcStatus()[pressedField] == pressedCondition; }

  /** Update the state of the button (pressed/unpressed) */
  function updateState() {
    if (isPressed()) {
      setMediaButtonPressed();
    } else {
      setMediaButtonUnpressed();
    }
  }

  /** Set media button pressed */
  function setMediaButtonPressed() {
    kameHouse.util.dom.removeClass($('#' + id), buttonPrefixClass + '-unpressed');
    kameHouse.util.dom.addClass($('#' + id), buttonPrefixClass + '-pressed');
  }

  /** Set media button unpressed */
  function setMediaButtonUnpressed() {
    kameHouse.util.dom.removeClass($('#' + id), buttonPrefixClass + '-pressed');
    kameHouse.util.dom.addClass($('#' + id), buttonPrefixClass + '-unpressed');
  }
} // End StatefulMediaButton

/** 
 * Manages the websocket connection, synchronization and keep alive loops. 
 * 
 * This prototype is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
function VlcPlayerSynchronizer(vlcPlayer) {

  this.initWebSockets = initWebSockets;
  this.pollVlcRcStatus = pollVlcRcStatus;
  this.connectVlcRcStatus = connectVlcRcStatus;
  this.connectPlaylist = connectPlaylist;
  this.syncVlcRcStatusLoop = syncVlcRcStatusLoop;
  this.syncPlaylistLoop = syncPlaylistLoop;
  this.keepAliveWebSocketsLoop = keepAliveWebSocketsLoop;
  this.stopVlcPlayerLoops = stopVlcPlayerLoops;
  this.restartVlcPlayerLoops = restartVlcPlayerLoops;
  this.syncVlcPlayerHttpLoop = syncVlcPlayerHttpLoop;
  this.syncLoopsHealthCheck = syncLoopsHealthCheck;

  const vlcRcStatusWebSocket = new KameHouseWebSocket();
  const playlistWebSocket = new KameHouseWebSocket();
  const syncLoopsConfig = {
    isRunningSyncVlcRcStatusLoop : false,
    isRunningSyncPlaylistLoop : false,
    isRunningKeepAliveWebSocketLoop : false,
    isRunningSyncVlcPlayerHttpLoop : false,
    vlcRcStatusLoopCount : 0,
    vlcPlaylistLoopCount : 0,    
    keepAliveWebSocketLoopCount : 0,
    syncVlcPlayerHttpLoopCount : 0
  };

  /**
   * Initialize web sockets.
   */
  function initWebSockets() {
    const vlcRcStatusWebSocketStatusUrl = '/kame-house-vlcrc/api/ws/vlc-player/status';
    const vlcRcStatusWebSocketPollUrl = "/app/vlc-player/status-in";
    const vlcRcStatusWebSocketTopicUrl = '/topic/vlc-player/status-out';
    vlcRcStatusWebSocket.statusUrl(vlcRcStatusWebSocketStatusUrl);
    vlcRcStatusWebSocket.pollUrl(vlcRcStatusWebSocketPollUrl);
    vlcRcStatusWebSocket.topicUrl(vlcRcStatusWebSocketTopicUrl);

    const playlistWebSocketStatusUrl = '/kame-house-vlcrc/api/ws/vlc-player/playlist';
    const playlistWebSocketPollUrl = "/app/vlc-player/playlist-in";
    const playlistWebSocketTopicUrl = '/topic/vlc-player/playlist-out';
    playlistWebSocket.statusUrl(playlistWebSocketStatusUrl);
    playlistWebSocket.pollUrl(playlistWebSocketPollUrl);
    playlistWebSocket.topicUrl(playlistWebSocketTopicUrl);
  }

  /**
   * --------------------------------------------------------------------------
   * VlcRcStatus WebSocket functionality
   */
  /** Poll for an update of vlcRcStatus through the web socket. */
  function pollVlcRcStatus() { vlcRcStatusWebSocket.poll(); }

  /** Connects the websocket to the backend. */
  function connectVlcRcStatus() {
    vlcRcStatusWebSocket.connect(function topicResponseCallback(topicResponse) {
      if (!kameHouse.core.isEmpty(topicResponse) && !kameHouse.core.isEmpty(topicResponse.body)) {
        vlcPlayer.setVlcRcStatus(kameHouse.json.parse(topicResponse.body));
      } else {
        vlcPlayer.setVlcRcStatus({});
      }
    });
  }

  /** Reconnects the VlcRcStatus websocket to the backend. */
  function reconnectVlcRcStatus() {
    vlcRcStatusWebSocket.disconnect();
    connectVlcRcStatus();
  }

  /**
   * --------------------------------------------------------------------------
   * Playlist WebSocket functionality
   */
  /** Connects the playlist websocket to the backend. */
  function connectPlaylist() {
    playlistWebSocket.connect(function topicResponseCallback(topicResponse) {
      if (!kameHouse.core.isEmpty(topicResponse) && !kameHouse.core.isEmpty(topicResponse.body)) {
        vlcPlayer.setUpdatedPlaylist(kameHouse.json.parse(topicResponse.body));
      } else {
        vlcPlayer.setUpdatedPlaylist(null);
      }
    });
  }

  /** Reconnects the playlist websocket to the backend. */
  function reconnectPlaylist() {
    playlistWebSocket.disconnect();
    connectPlaylist();
  }

  /**
   * --------------------------------------------------------------------------
   * Sychronization loops
   */

  /**
   * General logic shared by all sync loops.
   */
  function syncLoopExecution(config, loopName, loopRunFunction, isLoopRunningName, loopCountName) {
    setTimeout(async () => {
      kameHouse.logger.info("Started " + loopName);
      if (syncLoopsConfig[isLoopRunningName] || syncLoopsConfig[loopCountName] > 1) {
        const message = loopName + " is already running";
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        return;
      }
      syncLoopsConfig[isLoopRunningName] = true;
      syncLoopsConfig[loopCountName]++;
      if (config.syncLoopStartDelayMs) {
        kameHouse.logger.info("Started " + loopName + " with initial delay of " + config.syncLoopStartDelayMs + " ms");
        await kameHouse.core.sleep(config.syncLoopStartDelayMs);
      }
      while (syncLoopsConfig[isLoopRunningName]) {
        await loopRunFunction(config);
        if (syncLoopsConfig[loopCountName] > 1) {
          kameHouse.logger.info(loopName + ": Running multiple " + loopName + ", exiting this loop");
          break;
        }
      }
      syncLoopsConfig[loopCountName]--;
      kameHouse.logger.info("Finished " + loopName);
    }, 0);
  }

  /** 
   * Start infinite loop to pull VlcRcStatus from the server.
   * Break the loop setting isRunningSyncVlcRcStatusLoop to false.
   */
  function syncVlcRcStatusLoop() {
    const config = {
      vlcRcStatusPullWaitTimeMs : 1000
    };
    syncLoopExecution(config, "syncVlcRcStatusLoop", syncVlcRcStatusLoopRun, "isRunningSyncVlcRcStatusLoop", "vlcRcStatusLoopCount");
  }

  /**
   * VlcRcStatus loop run.
   */
  async function syncVlcRcStatusLoopRun(config) {
    kameHouse.logger.trace("syncVlcRcStatusLoop - vlcRcStatus: " + kameHouse.json.stringify(vlcPlayer.getVlcRcStatus()));
    setVlcRcStatusPullWaitTimeMs(config);
    updateViewSyncVlcRcStatusLoop();
    await kameHouse.core.sleep(config.vlcRcStatusPullWaitTimeMs);
    if (config.vlcRcStatusPullWaitTimeMs < -10000) { // fix sonar bug
      syncLoopsConfig.isRunningSyncVlcRcStatusLoop = false;
    }
  }

  /**
   * Set VlcRcStatus pull wait time.
   */
  function setVlcRcStatusPullWaitTimeMs(config) {
    const VLC_STATUS_CONNECTED_PLAYING_MS = 1000;
    const VLC_STATUS_CONNECTED_NOT_PLAYING_MS = 5000;
    const VLC_STATUS_DISCONNECTED_MS = 5000;
    if (!vlcRcStatusWebSocket.isConnected()) {
      config.vlcRcStatusPullWaitTimeMs = VLC_STATUS_DISCONNECTED_MS;
      return;
    }
    if (kameHouse.core.isEmpty(vlcPlayer.getVlcRcStatus().information)) {
      config.vlcRcStatusPullWaitTimeMs = VLC_STATUS_CONNECTED_NOT_PLAYING_MS;
      return;
    }
    config.vlcRcStatusPullWaitTimeMs = VLC_STATUS_CONNECTED_PLAYING_MS;
  }

  /**
   * Update vlc player view.
   */
  function updateViewSyncVlcRcStatusLoop() {
    if (vlcRcStatusWebSocket.isConnected()) {
      // poll VlcRcStatus from the websocket.
      vlcRcStatusWebSocket.poll();
      vlcPlayer.updateView();
    }
  }

  /** 
   * Start infinite loop to sync the current playlist from the server.
   * Break the loop setting isRunningSyncPlaylistLoop to false.
   */
  function syncPlaylistLoop() {
    const config = {
      playlistLoopWaitTimeMs : 5000
    };
    syncLoopExecution(config, "syncPlaylistLoop", syncPlaylistLoopRun, "isRunningSyncPlaylistLoop", "vlcPlaylistLoopCount");
  }

  /**
   * Playlist lopp run.
   */
  async function syncPlaylistLoopRun(config) {
    kameHouse.logger.trace("syncPlaylistLoop");
    if (playlistWebSocket.isConnected()) {
      // poll playlist from the websocket.
      playlistWebSocket.poll();
      vlcPlayer.reloadPlaylist();
    }
    await kameHouse.core.sleep(config.playlistLoopWaitTimeMs);
    if (config.playlistLoopWaitTimeMs < -10000) { // fix sonar bug
      syncLoopsConfig.isRunningSyncPlaylistLoop = false;
    }
  }

  /** 
   * Start infinite loop to keep alive the websocket connections.
   * Break the loop setting isRunningKeepAliveWebSocketLoop to false.
   */
  function keepAliveWebSocketsLoop() {
    const config = {
      keepAliveLoopWaitMs : 5000,
      syncLoopStartDelayMs : 15000
    };
    syncLoopExecution(config, "keepAliveWebSocketsLoop", keepAliveWebSocketsLoopRun, "isRunningKeepAliveWebSocketLoop", "keepAliveWebSocketLoopCount");
  }

  /**
   * Keep alive websockets loop run.
   */
  async function keepAliveWebSocketsLoopRun(config) {
    kameHouse.logger.trace("keepAliveWebSocketsLoop");
    if (!vlcRcStatusWebSocket.isConnected()) {
      kameHouse.logger.trace("keepAliveWebSocketsLoop: VlcRcStatus webSocket not connected. Reconnecting...");
      reconnectVlcRcStatus();
    }
    if (!playlistWebSocket.isConnected()) {
      kameHouse.logger.trace("keepAliveWebSocketsLoop: Playlist webSocket not connected. Reconnecting...");
      reconnectPlaylist();
    }
    await kameHouse.core.sleep(config.keepAliveLoopWaitMs);
    if (config.keepAliveLoopWaitMs < -10000) { // fix sonar bug
      syncLoopsConfig.isRunningKeepAliveWebSocketLoop = false;
    }
  }

  /** 
   * Start infinite loop to sync falling back to http calls when the websockets are disconnected.
   * Break the loop setting isRunningSyncVlcPlayerHttpLoop to false.
   */
  async function syncVlcPlayerHttpLoop() {
    const config = {
      syncVlcPlayerHttpWaitMs : 7000
    };
    syncLoopExecution(config, "syncVlcPlayerHttpLoop", syncVlcPlayerHttpLoopRun, "isRunningSyncVlcPlayerHttpLoop", "syncVlcPlayerHttpLoopCount");
  }

  /**
   * Http fallback sync loop run.
   */
  async function syncVlcPlayerHttpLoopRun(config) {
    setSyncVlcPlayerHttpWaitMs(config);
    loadStateFromApiSyncVlcPlayerHttpLoop();
    await kameHouse.core.sleep(config.syncVlcPlayerHttpWaitMs);
    if (config.syncVlcPlayerHttpWaitMs < -10000) { // fix sonar bug
      syncLoopsConfig.isRunningSyncVlcPlayerHttpLoop = false;
    }
  }

  /**
   * Set http fallback sync loop wait.
   */
  function setSyncVlcPlayerHttpWaitMs(config) {
    const WEB_SOCKETS_CONNECTED_WAIT_MS = 7000;
    const WEB_SOCKETS_DISCONNECTED_WAIT_MS = 2000;
    if (!vlcRcStatusWebSocket.isConnected() || !playlistWebSocket.isConnected()) {
      config.syncVlcPlayerHttpWaitMs = WEB_SOCKETS_DISCONNECTED_WAIT_MS;
      return;
    }
    config.syncVlcPlayerHttpWaitMs = WEB_SOCKETS_CONNECTED_WAIT_MS;
  }

  /**
   * Load vlc player state from http api.
   */
  function loadStateFromApiSyncVlcPlayerHttpLoop() {
    if (!vlcRcStatusWebSocket.isConnected() || !playlistWebSocket.isConnected()) {
      kameHouse.logger.debug("syncVlcPlayerHttpLoop: Websockets disconnected, synchronizing vlc player through http requests");
      vlcPlayer.loadStateFromApi();
    } else {
      kameHouse.logger.trace("syncVlcPlayerHttpLoop: Websockets connected. Skipping synchronization through http requests");
    }
  }

  /**
   * Run periodic health checks on all sync loops.
   */
  function syncLoopsHealthCheck() {
    const PERIODIC_HEALTH_CHECK_WAIT_MS = 15000;
    setTimeout(async () => {
      let continueLoop = true;
      let printLoopStatusCount = 0;
      while (continueLoop) {
        printLoopStatusCount++;
        if (printLoopStatusCount >= 4) {
          printLoopStatus();
          printLoopStatusCount = 0;
        }
        executeSyncLoopsHealthCheck();
        await kameHouse.core.sleep(PERIODIC_HEALTH_CHECK_WAIT_MS);
        if (PERIODIC_HEALTH_CHECK_WAIT_MS < -10000) { // fix sonar bug
          continueLoop = false;
        }
      }
    }, 0);
  }

  /**
   * Print all sync loops statuses.
   */
  function printLoopStatus() {
    const separator = "---------------------------------------------";
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
    const loopsStatus = "Sync loops status:";
    kameHouse.logger.trace(loopsStatus, kameHouse.logger.getYellowText(loopsStatus));
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
    kameHouse.logger.trace("isRunningSyncVlcRcStatusLoop: " + syncLoopsConfig.isRunningSyncVlcRcStatusLoop);
    kameHouse.logger.trace("isRunningSyncPlaylistLoop: " + syncLoopsConfig.isRunningSyncPlaylistLoop);
    kameHouse.logger.trace("isRunningKeepAliveWebSocketLoop: " + syncLoopsConfig.isRunningKeepAliveWebSocketLoop);
    kameHouse.logger.trace("isRunningSyncVlcPlayerHttpLoop: " + syncLoopsConfig.isRunningSyncVlcPlayerHttpLoop);
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
    kameHouse.logger.trace("vlcRcStatusLoopCount: " + syncLoopsConfig.vlcRcStatusLoopCount);
    kameHouse.logger.trace("vlcPlaylistLoopCount: " + syncLoopsConfig.vlcPlaylistLoopCount);
    kameHouse.logger.trace("keepAliveWebSocketLoopCount: " + syncLoopsConfig.keepAliveWebSocketLoopCount);
    kameHouse.logger.trace("syncVlcPlayerHttpLoopCount: " + syncLoopsConfig.syncVlcPlayerHttpLoopCount);        
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
  }

  /**
   * Execute health checks on all sync loops.
   */
  function executeSyncLoopsHealthCheck() {
    kameHouse.logger.trace("Checking state of sync loops");
    if (syncLoopsConfig.vlcRcStatusLoopCount <= 0) {
      restartSyncVlcRcStatusLoop(getRestartLoopConfig());
    }
    if (syncLoopsConfig.vlcPlaylistLoopCount <= 0) {
      restartSyncPlaylistLoop(getRestartLoopConfig());
    }
    if (syncLoopsConfig.keepAliveWebSocketLoopCount <= 0) {
      restartKeepAliveWebSocketsLoop(getRestartLoopConfig());
    }
    if (syncLoopsConfig.syncVlcPlayerHttpLoopCount <= 0) {
      restartSyncVlcPlayerHttpLoop(getRestartLoopConfig());
    }
  }

  /**
   * Stop all sync loops.
   */
  function stopVlcPlayerLoops() {
    const message = "KameHouse sent to background. Stopping sync loops and disconnecting websockets";
    kameHouse.logger.info(message, kameHouse.logger.getGreenText(message));
    syncLoopsConfig.isRunningSyncVlcRcStatusLoop = false;
    syncLoopsConfig.isRunningSyncPlaylistLoop = false;
    syncLoopsConfig.isRunningKeepAliveWebSocketLoop = false;
    syncLoopsConfig.isRunningSyncVlcPlayerHttpLoop = false;
    vlcRcStatusWebSocket.disconnect();
    playlistWebSocket.disconnect(); 
  }

  /**
   * Restart all sync loops.
   */
  function restartVlcPlayerLoops() {
    const message = "KameHouse sent to foreground. Restarting sync loops and reconnecting websockets";
    kameHouse.logger.info(message, kameHouse.logger.getCyanText(message));
    vlcRcStatusWebSocket.disconnect();
    playlistWebSocket.disconnect();
    restartSyncVlcPlayerHttpLoop(getRestartLoopConfig());
    restartSyncVlcRcStatusLoop(getRestartLoopConfig());
    restartSyncPlaylistLoop(getRestartLoopConfig());
    restartKeepAliveWebSocketsLoop(getRestartLoopConfig());
  }

  /**
   * Get restart loop default config.
   */
  function getRestartLoopConfig() {
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
  function restartSyncLoopExecution(config, loopName, restartLoopFunction, loopCountName) {
    setTimeout(async () => {
      kameHouse.logger.info("Restarting " + loopName);
      let retriesLeft = config.maxRetries;
      let startLoop = true;
      while (syncLoopsConfig[loopCountName] > 0) {
        retriesLeft--;
        kameHouse.logger.trace("waiting for " + loopName + " to finish before restarting");
        await kameHouse.core.sleep(config.restartLoopWaitMs);
        if (retriesLeft <= 0) {
          kameHouse.logger.info("too many attempts to restart " + loopName + ". It seems to be running already. Skipping restart");
          startLoop = false;
          break;
        }
        if (maxRetries < -10000) { // fix sonar bug
          syncLoopsConfig[loopCountName] = 0;
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
  function restartSyncVlcRcStatusLoop(config) {
    const firstRestartConfig = getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 5000;
    restartSyncLoopExecution(firstRestartConfig, "vlcRcStatusLoop", vlcRcStatusRestartFunction, "vlcRcStatusLoopCount");
    restartSyncLoopExecution(config, "vlcRcStatusLoop", vlcRcStatusRestartFunction, "vlcRcStatusLoopCount");
  }

  /**
   * VlcRcStatus loop restart function.
   */
  function vlcRcStatusRestartFunction() {
    reconnectVlcRcStatus();
    syncVlcRcStatusLoop();
  }

  /**
   * Restart playlist loop.
   */
  function restartSyncPlaylistLoop(config) {
    const firstRestartConfig = getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 5000;
    restartSyncLoopExecution(firstRestartConfig, "vlcPlaylistLoop", playlistRestartFunction, "vlcPlaylistLoopCount");
    restartSyncLoopExecution(config, "vlcPlaylistLoop", playlistRestartFunction, "vlcPlaylistLoopCount");
  }

  /**
   * Playlist loop restart function.
   */
  function playlistRestartFunction() {
    reconnectPlaylist();
    syncPlaylistLoop();
  }

  /**
   * Restart keep alive websockets loop.
   */
  function restartKeepAliveWebSocketsLoop(config) {
    const firstRestartConfig = getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 5000;
    restartSyncLoopExecution(firstRestartConfig, "keepAliveWebSocketLoop", keepAliveWebSocketsLoop, "keepAliveWebSocketLoopCount");
    restartSyncLoopExecution(config, "keepAliveWebSocketLoop", keepAliveWebSocketsLoop, "keepAliveWebSocketLoopCount");
  }
  
  /**
   * Restart http fallback sync loop.
   */
  function restartSyncVlcPlayerHttpLoop(config) {
    const firstRestartConfig = getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 2000;
    restartSyncLoopExecution(firstRestartConfig, "syncVlcPlayerHttpLoop", syncVlcPlayerHttpLoop, "syncVlcPlayerHttpLoopCount");
    config.restartLoopDelayMs = 9000;
    restartSyncLoopExecution(config, "syncVlcPlayerHttpLoop", syncVlcPlayerHttpLoop, "syncVlcPlayerHttpLoopCount");
  }

} // End VlcPlayerSynchronizer

/** 
 * Represents the Playlist component in vlc-player page. 
 * It also handles the updates to the view of the playlist.
 * 
 * This prototype is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
function VlcPlayerPlaylist(vlcPlayer) {

  this.init = init;
  this.setUpdatedPlaylist = setUpdatedPlaylist;
  this.reload = reload;
  this.scrollToCurrentlyPlaying = scrollToCurrentlyPlaying;
  this.updateView = updateView;
  this.resetView = resetView;

  const playSelectedUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/commands';
  const dobleLeftImg = createDoubleArrowImg("left");
  const dobleRightImg = createDoubleArrowImg("right");

  let currentPlaylist = null;
  let updatedPlaylist = null;
  let tbodyAbsolutePaths = null;
  let tbodyFilenames = null;

  /** Init Playlist. */
  function init() {
    kameHouse.util.dom.replaceWith($("#toggle-playlist-filenames-img"), dobleRightImg);
  }

  /** Create an image object to toggle when expanding/collapsing playlist browser filenames. */
  function createDoubleArrowImg(direction) {
    return kameHouse.util.dom.getImgBtn({
      id: "toggle-playlist-filenames-img",
      src: "/kame-house/img/other/double-" + direction + "-green.png",
      className: "img-btn-kh img-btn-s-kh btn-playlist-controls",
      alt: "Expand/Collapse Filename",
      onClick: () => toggleExpandPlaylistFilenames()
    });
  }

  /** Set updated playlist: Temporary storage for the playlist I receive from the websocket */
  function setUpdatedPlaylist(updatedPlaylistParam) { 
    updatedPlaylist = updatedPlaylistParam; 
  }

  /** Reload playlist updating the playlist view. */
  function reload() {
    if (!isPlaylistUpdated(currentPlaylist, updatedPlaylist)) {
      // Playlist content not updated, just update currently playing element and return
      highlightCurrentPlayingItem();
      return;
    }
    currentPlaylist = updatedPlaylist;
    // Clear playlist content. 
    kameHouse.util.dom.empty($("#playlist-table-body"));
    // Add the new playlist items received from the server.
    const $playlistTableBody = $('#playlist-table-body');
    if (kameHouse.core.isEmpty(currentPlaylist) || kameHouse.core.isEmpty(currentPlaylist.length) ||
      currentPlaylist.length <= 0) {
      kameHouse.util.dom.append($playlistTableBody, getEmptyPlaylistTr());
    } else {
      tbodyFilenames = getPlaylistTbody();
      tbodyAbsolutePaths = getPlaylistTbody();
      for (const currentPlaylistElement of currentPlaylist) {
        const absolutePath = currentPlaylistElement.filename;
        const filename = kameHouse.util.file.getShortFilename(absolutePath);
        const playlistElementId = currentPlaylistElement.id;
        kameHouse.util.dom.append(tbodyFilenames, getPlaylistTr(filename, playlistElementId));
        kameHouse.util.dom.append(tbodyAbsolutePaths, getPlaylistTr(absolutePath, playlistElementId));
      }
      kameHouse.util.dom.replaceWith($playlistTableBody, tbodyFilenames);
      highlightCurrentPlayingItem();
      vlcPlayer.filterPlaylistRows();
    }
  }

  /** Compares two playlists. Returns true if they are different or empty. Expects 2 vlc playlist arrays */
  function isPlaylistUpdated(currentPls, updatedPls) {
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
  function clickEventOnPlaylistRow(event) {
    kameHouse.logger.debug("Play playlist id: " + event.data.id);
    const requestBody = {
      name: 'pl_play',
      id: event.data.id
    };
    vlcPlayer.getRestClient().post(playSelectedUrl, kameHouse.http.getApplicationJsonHeaders(), requestBody);
  }

  /** Highlight currently playing item in the playlist. */
  function highlightCurrentPlayingItem() {
    const currentPlId = vlcPlayer.getVlcRcStatus().currentPlId;
    const currentPlIdAsRowId = 'playlist-table-row-id-' + currentPlId;
    kameHouse.util.dom.removeClass($('#playlist-table-body tr td button'), "active");
    kameHouse.util.dom.addClass($("#" + currentPlIdAsRowId).children().children(), "active");
  }

  /** Toggle expand or collapse filenames in the playlist */
  function toggleExpandPlaylistFilenames() {
    const filenamesFirstFile = $(tbodyFilenames).children().first().text();
    const currentFirstFile = $('#playlist-table-body tr:first').text();
    const $playlistTable = $('#playlist-table');
    let isExpandedFilename = null;

    if (currentFirstFile == filenamesFirstFile) {
      // currently displaying filenames, switch to absolute paths 
      if (!kameHouse.core.isEmpty(tbodyFilenames)) {
        kameHouse.util.dom.detach(tbodyFilenames);
      }
      kameHouse.util.dom.append($playlistTable, tbodyAbsolutePaths);
      isExpandedFilename = true;
    } else {
      // currently displaying absolute paths, switch to filenames 
      if (!kameHouse.core.isEmpty(tbodyAbsolutePaths)) {
        kameHouse.util.dom.detach(tbodyAbsolutePaths);
      }
      kameHouse.util.dom.append($playlistTable, tbodyFilenames);
      isExpandedFilename = false;
    }
    highlightCurrentPlayingItem();
    updateExpandPlaylistFilenamesIcon(isExpandedFilename);
    vlcPlayer.filterPlaylistRows();
  }

  /** Update the icon to expand or collapse the playlist filenames */
  function updateExpandPlaylistFilenamesIcon(isExpandedFilename) {
    if (isExpandedFilename) {
      kameHouse.util.dom.replaceWith($("#toggle-playlist-filenames-img"), dobleLeftImg);
    } else {
      kameHouse.util.dom.replaceWith($("#toggle-playlist-filenames-img"), dobleRightImg);
    }
  }

  /** Scroll to the current playing element in the playlist. */
  function scrollToCurrentlyPlaying() {
    const currentPlId = vlcPlayer.getVlcRcStatus().currentPlId;
    const $currentPlayingRow = $('#playlist-table-row-id-' + currentPlId);
    if (!kameHouse.core.isEmpty($currentPlayingRow.length) && $currentPlayingRow.length != 0) {
      const playlistTableWrapper = $('#playlist-table-wrapper');
      playlistTableWrapper.scrollTop(0);
      const scrollToOffset = $currentPlayingRow.offset().top - playlistTableWrapper.offset().top;
      playlistTableWrapper.scrollTop(scrollToOffset);
    }
  }

  /** 
   * Update the playlist view. Add all the functionality that needs to happen 
   * to update the view of the playlist when vlcRcStatus changes  
   */
  function updateView() {
    if (!kameHouse.core.isEmpty(vlcPlayer.getVlcRcStatus())) {
      highlightCurrentPlayingItem();
    } else {
      resetView();
    }
  }

  /** 
   * Reset the playlist view.
   */
  function resetView() {
    updatedPlaylist = null;
    reload();
  }

  /**
   * Get empty playlist table row.
   */
  function getEmptyPlaylistTr() {
    const madaMadaDane = 'まだまだだね';
    return kameHouse.util.dom.getTrTd("No playlist to browse loaded yet or unable to sync." + madaMadaDane + " :)");
  }
  
  /**
   * Get playlist table body.
   */
  function getPlaylistTbody() {
    return kameHouse.util.dom.getTbody({
      id: "playlist-table-body"
    });
  }

  /**
   * Get playlist table row.
   */
  function getPlaylistTr(displayName, playlistElementId) {
    return kameHouse.util.dom.getTr({
      id: "playlist-table-row-id-" + playlistElementId
    }, kameHouse.util.dom.getTd({}, getPlaylistTrBtn(displayName, playlistElementId)));
  }

  /**
   * Get playlist table row button.
   */
  function getPlaylistTrBtn(displayName, playlistElementId) {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "playlist-table-btn",
      },
      html: displayName,
      clickData: {
        id: playlistElementId
      },
      click: clickEventOnPlaylistRow
    });
  }
} // End VlcPlayerPlaylist

/** 
 * Represents an internal rest client for the VlcPlayer to split functionality. 
 * 
 * This prototype is meant to be instantiated by VlcPlayer() constructor
 * and added as a property to VlcPlayer.restClient inside that constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
function VlcPlayerRestClient(vlcPlayer) {

  this.get = get;
  this.post = httpPost;
  this.delete = httpDelete;

  /** Execute GET on the specified url and display the output in the debug table. */
  function get(url, requestHeaders, requestBody, updateCursor, successCallback, errorCallback) {
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
          apiCallSuccessDefault(responseBody);
        }
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        if (!kameHouse.core.isEmpty(errorCallback)) {
          errorCallback(responseBody, responseCode, responseDescription, responseHeaders);
        } else {
          apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders);
          if (responseCode == "404") {
            kameHouse.plugin.debugger.displayResponseData("Could not connect to VLC player to get the status.", responseCode, responseDescription, responseHeaders);
          }
        }
      });
  }

  /** Execute a POST request to the specified url with the specified request body. */
  function httpPost(url, requestHeaders, requestBody) {
    kameHouse.util.cursor.setCursorWait();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => apiCallSuccessDefault(responseBody),
      (responseBody, responseCode, responseDescription, responseHeaders) => apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders)
    );
  }

  /** Execute a DELETE request to the specified url with the specified request body. */
  function httpDelete(url, requestHeaders, requestBody) {
    kameHouse.util.cursor.setCursorWait();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => apiCallSuccessDefault(responseBody),
      (responseBody, responseCode, responseDescription, responseHeaders) => apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders)
    );
  }

  /** Default actions for succesful api responses */
  function apiCallSuccessDefault(responseBody) {
    kameHouse.util.cursor.setCursorDefault();
    kameHouse.plugin.modal.loadingWheelModal.close();
    vlcPlayer.pollVlcRcStatus();
  }

  /** Default actions for error api responses */
  function apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.util.cursor.setCursorDefault();
    kameHouse.plugin.modal.loadingWheelModal.close();
    // Don't display api errors for not found or service not available errors
    if (responseCode != 404 && responseCode != 503 && responseCode > 300) {
      kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    }
  }
} // End VlcPlayerRestClient

/** 
 * Handles the debugger functionality of vlc player in the debugger's custom area.
 * 
 * This prototype is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains prototype.
 * 
 * @author nbrest
 */
function VlcPlayerDebugger(vlcPlayer) {

  this.getVlcRcStatusFromApi = getVlcRcStatusFromApi;
  this.getPlaylistFromApi = getPlaylistFromApi;

  const vlcRcStatusApiUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/status';
  const playlistApiUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/playlist';

  /** Get the vlcRcStatus from an http api call instead of from the websocket. */
  function getVlcRcStatusFromApi() { 
    vlcPlayer.getRestClient().get(vlcRcStatusApiUrl, null, null, false, getVlcRcStatusApiSuccessCallback, getVlcRcStatusApiErrorCallback); 
  }

  /** Get the playlist from an http api call instead of from the websocket. */
  function getPlaylistFromApi() { 
    vlcPlayer.getRestClient().get(playlistApiUrl, null, null, false, getPlaylistApiSuccessCallback, getPlaylistApiErrorCallback); 
  }

  /** Update the main player view. */
  function getVlcRcStatusApiSuccessCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.util.cursor.setCursorDefault();
    vlcPlayer.setVlcRcStatus(responseBody);
    vlcPlayer.updateView();
  }

  /** Reset view if there's an error getting the vlcRcStatus from the api. */
  function getVlcRcStatusApiErrorCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.util.cursor.setCursorDefault();
    kameHouse.logger.warn("Unable to get vlcRcStatus from an API call. This can happen if vlc player process isn't running");
    vlcPlayer.setVlcRcStatus({});
    vlcPlayer.updateView();
  }

  /** Update the playlist view. */
  function getPlaylistApiSuccessCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.util.cursor.setCursorDefault();
    vlcPlayer.getPlaylist().setUpdatedPlaylist(responseBody);
    vlcPlayer.getPlaylist().reload();
  }

  /** Reset playlist view if there's an error getting the playlist from the api. */
  function getPlaylistApiErrorCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.util.cursor.setCursorDefault();
    kameHouse.logger.warn("Unable to get the playlist from an API call. This can happen if vlc player process isn't running");
    vlcPlayer.getPlaylist().setUpdatedPlaylist(null);
    vlcPlayer.getPlaylist().reload();
  }
  
} // End VlcPlayerDebugger

$(document).ready(() => {kameHouse.addExtension("vlcPlayer", new VlcPlayer("localhost"))});