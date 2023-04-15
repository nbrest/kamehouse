/** 
 * VlcPlayer entity.
 * 
 * Dependencies: fileUtils, tableUtils, cursorUtils, timeUtils, logger, debuggerHttpClient, websocket
 * 
 * Dependencies in same file: VlcPlayerPlaylist, VlcPlayerRestClient, VlcPlayerCommandExecutor, 
 * VlcPlayerSynchronizer, VlcPlayerMainViewUpdater, VlcPlayerDebugger
 * 
 * This prototype contains the public interface to interact with VlcPlayer. The logic is
 * implemented in the component prototypes mentioned above. It was designed this way because
 * VlcPlayer does a lot of functionality, so it seems best to split it into different
 * prototypes that each handle more specific functionality.
 * 
 * Call init() after instantiating VlcPlayer to connect the internal websocket
 * and start the sync loops.
 * 
 * @author nbrest
 */
function VlcPlayer(hostname) {

  this.init = init;
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
  const synchronizer = new VlcPlayerSynchronizer(this);
  const mainViewUpdater = new VlcPlayerMainViewUpdater(this);
  const vlcPlayerDebugger = new VlcPlayerDebugger(this);

  let vlcRcStatus = {};

  /** Init VlcPlayer */
  function init() {
    loadStateFromCookies();
    playlist.init();
    loadStateFromApi();
    synchronizer.connectVlcRcStatus();
    synchronizer.connectPlaylist();
    synchronizer.syncVlcRcStatusLoop();
    synchronizer.syncPlaylistLoop();
    synchronizer.keepAliveWebSocketsLoop();
    synchronizer.syncVlcPlayerHttpLoop();
  }

  /**
   * Load the current state from the cookies.
   */
  function loadStateFromCookies() {
    let currentTab = cookiesUtils.getCookie('kh-vlc-player-current-tab');
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
  function openTab(vlcPlayerTabDivId) {
    // Set kh-vlc-player-current-tab cookie
    cookiesUtils.setCookie('kh-vlc-player-current-tab', vlcPlayerTabDivId);
    // Update tab links
    const vlcPlayerTabLinks = document.getElementsByClassName("vlc-player-tab-link");
    for (const vlcPlayerTabLinkElement of vlcPlayerTabLinks) {
      domUtils.classListRemove(vlcPlayerTabLinkElement, "active");
    }
    const vlcPlayerTabLink = document.getElementById(vlcPlayerTabDivId + '-link');
    domUtils.classListAdd(vlcPlayerTabLink, "active");

    // Update tab content visibility
    const vlcPlayerTabContent = document.getElementsByClassName("vlc-player-tab-content");
    for (const vlcPlayerTabContentElement of vlcPlayerTabContent) {
      domUtils.setDisplay(vlcPlayerTabContentElement, "none");
    }
    const vlcPlayerTabDiv = document.getElementById(vlcPlayerTabDivId);
    domUtils.setDisplay(vlcPlayerTabDiv, "block");

    setTimeout(() => {
      // Asynchronously show or hide playlist and playlist browser content
      const playlistTable = document.getElementById("playlist-table");
      if ("tab-playlist" == vlcPlayerTabDivId) {
        domUtils.setDisplay(playlistTable, "table");
      } else {
        domUtils.setDisplay(playlistTable, "none");
      }

      const playlistBrowserTable = document.getElementById("playlist-browser-table");
      if ("tab-playlist-browser" == vlcPlayerTabDivId) {
        domUtils.setDisplay(playlistBrowserTable, "table");
      } else {
        domUtils.setDisplay(playlistBrowserTable, "none");
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
    if (!isEmpty(subtitleDelay)) {
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
    if (!isEmpty(aspectRatio)) {
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
    if (!isEmpty(vlcRcStatusParam)) {
      vlcRcStatus = vlcRcStatusParam;
    } else {
      vlcRcStatus = {};
    }
  }

  /**
   * --------------------------------------------------------------------------
   * Playlist functionality
   */
  function setUpdatedPlaylist(updatedPlaylist) { playlist.setUpdatedPlaylist(updatedPlaylist); }

  function reloadPlaylist() { playlist.reload(); }

  function scrollToCurrentlyPlaying() { playlist.scrollToCurrentlyPlaying(); }

  function filterPlaylistRows() {
    const filterString = document.getElementById("playlist-filter-input").value;
    tableUtils.filterTableRows(filterString, 'playlist-table-body');
  }

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

  function updateCurrentTimeView(value) {
    mainViewUpdater.setTimeSliderLocked(true);
    mainViewUpdater.updateCurrentTimeView(value);
  }

  function updateVolumeView(value) {
    mainViewUpdater.setVolumeSliderLocked(true);
    mainViewUpdater.updateVolumeView(value);
  }

  /**
   * --------------------------------------------------------------------------
   * Rest Client functionality
   */
  // Use this getter internally from other components of VlcPlayer. Not externally.
  function getRestClient() { return restClient; }

  /**
   * --------------------------------------------------------------------------
   * Debugger functionality
   */
  function getDebugger() { return vlcPlayerDebugger; }

  /**
   * --------------------------------------------------------------------------
   * Links to external resources
   */
  function unlockScreen() {
    const UNLOCK_SCREEN_API_URL = "/kame-house-admin/api/v1/admin/screen/unlock";
    restClient.post(UNLOCK_SCREEN_API_URL, null);
  }

  function wolMediaServer() {
    const requestParam =  {
      "server" : "media.server"
    };
    const WOL_MEDIA_SERVER_API_URL = "/kame-house-admin/api/v1/admin/power-management/wol";
    restClient.postUrlEncoded(WOL_MEDIA_SERVER_API_URL, requestParam);
  }
}

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
    if (isEmpty(val)) {
      requestBody = {
        name: name
      };
    } else {
      requestBody = {
        name: name,
        val: val
      };
    }
    vlcPlayer.getRestClient().post(vlcRcCommandUrl, requestBody);
  }

  /** Play the selected file (or playlist) into vlc player and reload the current playlist. */
  function playFile(fileName) {
    logger.debug("File to play: " + fileName);
    const requestParam =  {
      "file" : fileName
    };
    loadingWheelModal.open();
    vlcPlayer.getRestClient().postUrlEncoded(vlcPlayerProcessControlUrl, requestParam, vlcPlayer.loadStateFromApi);
  }

  /** Close vlc player. */
  function close() {
    vlcPlayer.getRestClient().delete(vlcPlayerProcessControlUrl, null);
  }
}

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

  const statefulButtons = [];

  let timeSliderLocked = false;
  let volumeSliderLocked = false;

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
  setStatefulButtons();

  /** Set time slider locked. */
  function setTimeSliderLocked(value) { timeSliderLocked = value; }

  /** Set volume slider locked. */
  function setVolumeSliderLocked(value) { volumeSliderLocked = value; }

  /** Update vlc player view for main view objects. */
  function updateView() {
    if (!isEmpty(vlcPlayer.getVlcRcStatus())) {
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
    if (!isEmpty(vlcPlayer.getVlcRcStatus().information)) {
      mediaName.filename = vlcPlayer.getVlcRcStatus().information.meta.filename;
      mediaName.title = vlcPlayer.getVlcRcStatus().information.meta.title;
    }
    domUtils.setHtml($("#media-title"), mediaName.filename);
  }

  /** Reset the media title. */
  function resetMediaTitle() {
    const mediaName = {};
    mediaName.filename = "No media loaded";
    mediaName.title = "No media loaded";
    domUtils.setHtml($("#media-title"), mediaName.filename);
  }

  /** Update subtitle delay. */
  function updateSubtitleDelay() {
    let subtitleDelay = vlcPlayer.getVlcRcStatus().subtitleDelay;
    if (isEmpty(subtitleDelay)) {
      subtitleDelay = "0";
    }
    domUtils.setHtml($("#subtitle-delay-value"), String(subtitleDelay));
  }

  /** Reset subtitle delay. */
  function resetSubtitleDelay() {
    domUtils.setHtml($("#subtitle-delay-value"), "0");
  }

  /**
   * --------------------------------------------------------------------------
   * Update time Functionality
   */
  /** Update media time slider from VlcRcStatus and resets view when there's no input. */
  function updateTimeSlider() {
    if (!timeSliderLocked) {
      if (!isEmpty(vlcPlayer.getVlcRcStatus().time)) {
        updateCurrentTimeView(vlcPlayer.getVlcRcStatus().time);
        updateTotalTimeView(vlcPlayer.getVlcRcStatus().length);
      } else {
        resetTimeSlider();
      }
    }
  }

  /** Reset time slider. */
  function resetTimeSlider() {
    domUtils.setHtml($("#current-time"), "--:--:--");
    domUtils.setVal($("#time-slider"), 500);
    domUtils.setHtml($("#total-time"), "--:--:--");
    domUtils.setAttr($("#time-slider"),'max', 1000);
  }

  /** Update the displayed current time. */
  function updateCurrentTimeView(value) {
    const currentTime = document.getElementById("current-time");
    domUtils.setInnerHtml(currentTime, timeUtils.convertSecondsToHsMsSs(value));
    domUtils.setVal($("#time-slider"), value);
  }

  /** Update the displayed total time. */
  function updateTotalTimeView(value) {
    domUtils.setHtml($("#total-time"), timeUtils.convertSecondsToHsMsSs(value));
    domUtils.setAttr($("#time-slider"),'max', value);
  }

  /**
   * --------------------------------------------------------------------------
   * Update volume Functionality
   */
  /** Update volume slider from VlcRcStatus. */
  function updateVolumeSlider() {
    if (!volumeSliderLocked) {
      if (!isEmpty(vlcPlayer.getVlcRcStatus().volume)) {
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
    domUtils.setVal($("#volume-slider"), value);
    const volumePercentaje = Math.floor(value * 200 / 512);
    const currentVolume = document.getElementById("current-volume");
    domUtils.setInnerHtml(currentVolume, volumePercentaje + "%");
  }
}

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

  if (isEmpty(btnPrefixClass)) {
    btnPrefixClass = defaultBtnPrefixClass;
  }

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
    domUtils.removeClass($('#' + id), btnPrefixClass + '-unpressed');
    domUtils.addClass($('#' + id), btnPrefixClass + '-pressed');
  }

  /** Set media button unpressed */
  function setMediaButtonUnpressed() {
    domUtils.removeClass($('#' + id), btnPrefixClass + '-pressed');
    domUtils.addClass($('#' + id), btnPrefixClass + '-unpressed');
  }
}

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

  this.pollVlcRcStatus = pollVlcRcStatus;
  this.connectVlcRcStatus = connectVlcRcStatus;
  this.connectPlaylist = connectPlaylist;
  this.syncVlcRcStatusLoop = syncVlcRcStatusLoop;
  this.syncPlaylistLoop = syncPlaylistLoop;
  this.keepAliveWebSocketsLoop = keepAliveWebSocketsLoop;
  this.syncVlcPlayerHttpLoop = syncVlcPlayerHttpLoop;

  const vlcRcStatusWebSocket = new WebSocketKameHouse();
  const playlistWebSocket = new WebSocketKameHouse();
  let isRunningSyncVlcRcStatusLoop = false;
  let isRunningSyncPlaylistLoop = false;
  let isRunningKeepAliveWebSocketLoop = false;
  let isRunningSyncVlcPlayerHttpLoop = false;

  function setWebSockets() {
    const vlcRcStatusWebSocketStatusUrl = '/kame-house-vlcrc/api/ws/vlc-player/status';
    const vlcRcStatusWebSocketPollUrl = "/app/vlc-player/status-in";
    const vlcRcStatusWebSocketTopicUrl = '/topic/vlc-player/status-out';
    vlcRcStatusWebSocket.setStatusUrl(vlcRcStatusWebSocketStatusUrl);
    vlcRcStatusWebSocket.setPollUrl(vlcRcStatusWebSocketPollUrl);
    vlcRcStatusWebSocket.setTopicUrl(vlcRcStatusWebSocketTopicUrl);

    const playlistWebSocketStatusUrl = '/kame-house-vlcrc/api/ws/vlc-player/playlist';
    const playlistWebSocketPollUrl = "/app/vlc-player/playlist-in";
    const playlistWebSocketTopicUrl = '/topic/vlc-player/playlist-out';
    playlistWebSocket.setStatusUrl(playlistWebSocketStatusUrl);
    playlistWebSocket.setPollUrl(playlistWebSocketPollUrl);
    playlistWebSocket.setTopicUrl(playlistWebSocketTopicUrl);
  }
  setWebSockets();

  /**
   * --------------------------------------------------------------------------
   * VlcRcStatus WebSocket functionality
   */
  /** Poll for an update of vlcRcStatus through the web socket. */
  function pollVlcRcStatus() { vlcRcStatusWebSocket.poll(); }

  /** Connects the websocket to the backend. */
  function connectVlcRcStatus() {
    vlcRcStatusWebSocket.connect(function topicResponseCallback(topicResponse) {
      if (!isEmpty(topicResponse) && !isEmpty(topicResponse.body)) {
        vlcPlayer.setVlcRcStatus(JSON.parse(topicResponse.body));
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
      if (!isEmpty(topicResponse) && !isEmpty(topicResponse.body)) {
        vlcPlayer.setUpdatedPlaylist(JSON.parse(topicResponse.body));
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
   * Start infinite loop to pull VlcRcStatus from the server.
   * Break the loop setting isRunningSyncVlcRcStatusLoop to false.
   * If it fails to get an update from the websocket for 10 seconds in a row,
   * it increases the wait time between syncs, until the first succesful sync.
   */
  async function syncVlcRcStatusLoop() {
    logger.info("Started syncVlcRcStatusLoop");
    if (isRunningSyncVlcRcStatusLoop) {
      logger.error("syncVlcRcStatusLoop is already running");
      return;
    }
    isRunningSyncVlcRcStatusLoop = true;
    let vlcRcStatusPullWaitTimeMs = 1000;
    let failedCount = 0;
    while (isRunningSyncVlcRcStatusLoop) {
      logger.trace("Poll vlcRcStatus loop");
      logger.trace("InfiniteLoop - vlcRcStatus: " + JSON.stringify(vlcPlayer.getVlcRcStatus()));
      if (vlcRcStatusWebSocket.isConnected()) {
        // poll VlcRcStatus from the websocket.
        vlcRcStatusWebSocket.poll();
        vlcPlayer.updateView();
        if (!isEmpty(vlcPlayer.getVlcRcStatus().information)) {
          vlcRcStatusPullWaitTimeMs = 1000;
          failedCount = 0;
        } else {
          failedCount++;
          if (failedCount >= 10) {
            vlcRcStatusPullWaitTimeMs = 5000;
          }
        }
      } else {
        vlcRcStatusPullWaitTimeMs = 3000;
        logger.trace("WebSocket is disconnected. Resetting view and waiting " + vlcRcStatusPullWaitTimeMs + " ms to sync again.");
        vlcPlayer.resetView();
      }
      await sleep(vlcRcStatusPullWaitTimeMs);
    }
    logger.info("Finished syncVlcRcStatusLoop");
  }

  /** 
   * Start infinite loop to sync the current playlist from the server.
   * Break the loop setting isRunningSyncPlaylistLoop to false.
   */
  async function syncPlaylistLoop() {
    logger.info("Started syncPlaylistLoop");
    if (isRunningSyncPlaylistLoop) {
      logger.error("syncPlaylistLoop is already running");
      return;
    }
    isRunningSyncPlaylistLoop = true;
    const playlistSyncWaitTimeMs = 5000;
    while (isRunningSyncPlaylistLoop) {
      logger.trace("Poll playlist loop");
      if (playlistWebSocket.isConnected()) {
        // poll playlist from the websocket.
        playlistWebSocket.poll();
        vlcPlayer.reloadPlaylist();
      }
      await sleep(playlistSyncWaitTimeMs);
    }
    logger.info("Finished syncPlaylistLoop");
  }

  /** 
   * Start infinite loop to keep alive the websocket connections.
   * Break the loop setting isRunningKeepAliveWebSocketLoop to false.
   */
  async function keepAliveWebSocketsLoop() {
    logger.info("Started keepAliveWebSocketsLoop");
    if (isRunningKeepAliveWebSocketLoop) {
      logger.error("keepAliveWebSocketsLoop is already running");
      return;
    }
    isRunningKeepAliveWebSocketLoop = true;
    const keepAliveWebSocketWaitTimeMs = 5000;
    while (isRunningKeepAliveWebSocketLoop) {
      logger.trace("Keep websockets connected loop");
      await sleep(keepAliveWebSocketWaitTimeMs);
      if (!vlcRcStatusWebSocket.isConnected()) {
        logger.debug("VlcRcStatus webSocket not connected. Reconnecting.");
        reconnectVlcRcStatus();
      }
      if (!playlistWebSocket.isConnected()) {
        logger.debug("Playlist webSocket not connected. Reconnecting.");
        reconnectPlaylist();
      }
    }
    logger.info("Finished keepAliveWebSocketsLoop");
  }

  /** 
   * Start infinite loop to sync falling back to http calls when the websockets are disconnected.
   */
  async function syncVlcPlayerHttpLoop() {
    logger.info("Started syncVlcPlayerHttpLoop");
    if (isRunningSyncVlcPlayerHttpLoop) {
      logger.error("syncVlcPlayerHttpLoop is already running");
      return;
    }
    isRunningSyncVlcPlayerHttpLoop = true;
    const syncVlcPlayerHttpWaitMs = 30000;
    while (isRunningSyncVlcPlayerHttpLoop) {
      logger.trace("sync vlc player through fallback to http requests loop");
      await sleep(syncVlcPlayerHttpWaitMs);
      if (!vlcRcStatusWebSocket.isConnected() || !playlistWebSocket.isConnected()) {
        logger.debug("Websockets disconnected, synchronizing vlc player through http requests");
        vlcPlayer.loadStateFromApi();
      } else {
        logger.trace("Websockets connected. Skipping synchronization through http requests");
      }
    }
    logger.info("Finished syncVlcPlayerHttpLoop");
  }
}

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
    domUtils.replaceWith($("#toggle-playlist-filenames-img"), dobleRightImg);
  }

  /** Create an image object to toggle when expanding/collapsing playlist browser filenames. */
  function createDoubleArrowImg(direction) {
    return domUtils.getImgBtn({
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
    domUtils.empty($("#playlist-table-body"));
    // Add the new playlist items received from the server.
    const $playlistTableBody = $('#playlist-table-body');
    if (isEmpty(currentPlaylist) || isEmpty(currentPlaylist.length) ||
      currentPlaylist.length <= 0) {
      domUtils.append($playlistTableBody, getEmptyPlaylistTr());
    } else {
      tbodyFilenames = getPlaylistTbody();
      tbodyAbsolutePaths = getPlaylistTbody();
      for (const currentPlaylistElement of currentPlaylist) {
        const absolutePath = currentPlaylistElement.filename;
        const filename = fileUtils.getShortFilename(absolutePath);
        const playlistElementId = currentPlaylistElement.id;
        domUtils.append(tbodyFilenames, getPlaylistTr(filename, playlistElementId));
        domUtils.append(tbodyAbsolutePaths, getPlaylistTr(absolutePath, playlistElementId));
      }
      domUtils.replaceWith($playlistTableBody, tbodyFilenames);
      highlightCurrentPlayingItem();
      vlcPlayer.filterPlaylistRows();
    }
  }

  /** Compares two playlists. Returns true if they are different or empty. Expects 2 vlc playlist arrays */
  function isPlaylistUpdated(currentPls, updatedPls) {
    const MAX_COMPARISONS = 30;
    // For empty playlists, return true, so it updates the UI
    if (isEmpty(currentPls) || isEmpty(updatedPls)) {
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
    } else {
      if ((currentPls.length > MAX_COMPARISONS) &&
        (currentPls.length <= MAX_COMPARISONS * 2)) {
        step = 2;
      }
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
    logger.debug("Play playlist id: " + event.data.id);
    const requestBody = {
      name: 'pl_play',
      id: event.data.id
    };
    vlcPlayer.getRestClient().post(playSelectedUrl, requestBody);
  }

  /** Highlight currently playing item in the playlist. */
  function highlightCurrentPlayingItem() {
    const currentPlId = vlcPlayer.getVlcRcStatus().currentPlId;
    const currentPlIdAsRowId = 'playlist-table-row-id-' + currentPlId;
    domUtils.removeClass($('#playlist-table-body tr td button'), "active");
    domUtils.addClass($("#" + currentPlIdAsRowId).children().children(), "active");
  }

  /** Toggle expand or collapse filenames in the playlist */
  function toggleExpandPlaylistFilenames() {
    const filenamesFirstFile = $(tbodyFilenames).children().first().text();
    const currentFirstFile = $('#playlist-table-body tr:first').text();
    const $playlistTable = $('#playlist-table');
    let isExpandedFilename = null;

    if (currentFirstFile == filenamesFirstFile) {
      // currently displaying filenames, switch to absolute paths 
      if (!isEmpty(tbodyFilenames)) {
        domUtils.detach(tbodyFilenames);
      }
      domUtils.append($playlistTable, tbodyAbsolutePaths);
      isExpandedFilename = true;
    } else {
      // currently displaying absolute paths, switch to filenames 
      if (!isEmpty(tbodyAbsolutePaths)) {
        domUtils.detach(tbodyAbsolutePaths);
      }
      domUtils.append($playlistTable, tbodyFilenames);
      isExpandedFilename = false;
    }
    highlightCurrentPlayingItem();
    updateExpandPlaylistFilenamesIcon(isExpandedFilename);
    vlcPlayer.filterPlaylistRows();
  }

  /** Update the icon to expand or collapse the playlist filenames */
  function updateExpandPlaylistFilenamesIcon(isExpandedFilename) {
    if (isExpandedFilename) {
      domUtils.replaceWith($("#toggle-playlist-filenames-img"), dobleLeftImg);
    } else {
      domUtils.replaceWith($("#toggle-playlist-filenames-img"), dobleRightImg);
    }
  }

  /** Scroll to the current playing element in the playlist. */
  function scrollToCurrentlyPlaying() {
    const currentPlId = vlcPlayer.getVlcRcStatus().currentPlId;
    const $currentPlayingRow = $('#playlist-table-row-id-' + currentPlId);
    if (!isEmpty($currentPlayingRow.length) && $currentPlayingRow.length != 0) {
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
    if (!isEmpty(vlcPlayer.getVlcRcStatus())) {
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

  function getEmptyPlaylistTr() {
    const madaMadaDane = 'まだまだだね';
    return domUtils.getTrTd("No playlist to browse loaded yet or unable to sync." + madaMadaDane + " :)");
  }
  
  function getPlaylistTbody() {
    return domUtils.getTbody({
      id: "playlist-table-body"
    });
  }

  function getPlaylistTr(displayName, playlistElementId) {
    return domUtils.getTr({
      id: "playlist-table-row-id-" + playlistElementId
    }, domUtils.getTd({}, getPlaylistTrBtn(displayName, playlistElementId)));
  }

  function getPlaylistTrBtn(displayName, playlistElementId) {
    return domUtils.getButton({
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
}

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
  this.postUrlEncoded = httpPostUrlEncoded;
  this.delete = httpDelete;

  /** Execute GET on the specified url and display the output in the debug table. */
  function get(url, updateCursor, successCallback, errorCallback) {
    if (updateCursor) {
      cursorUtils.setCursorWait();
    }
    debuggerHttpClient.get(url,
      (responseBody, responseCode, responseDescription) => {
        if (!isEmpty(successCallback)) {
          successCallback(responseBody, responseCode, responseDescription);
        } else {
          apiCallSuccessDefault(responseBody);
        }
      },
      (responseBody, responseCode, responseDescription) => {
        if (!isEmpty(errorCallback)) {
          errorCallback(responseBody, responseCode, responseDescription);
        } else {
          apiCallErrorDefault(responseBody, responseCode, responseDescription);
          if (responseCode == "404") {
            kameHouseDebugger.displayResponseData("Could not connect to VLC player to get the status.", responseCode);
          }
        }
      });
  }

  /** Execute a POST request to the specified url with the specified request body. */
  function httpPost(url, requestBody) {
    cursorUtils.setCursorWait();
    debuggerHttpClient.post(url, requestBody,
      (responseBody, responseCode, responseDescription) => apiCallSuccessDefault(responseBody),
      (responseBody, responseCode, responseDescription) => apiCallErrorDefault(responseBody, responseCode, responseDescription)
    );
  }

  /** Execute a POST request to the specified url with the specified request url parameters. */
  function httpPostUrlEncoded(url, requestParam, successCallback, errorCallback) {
    cursorUtils.setCursorWait();
    debuggerHttpClient.postUrlEncoded(url, requestParam,
      (responseBody, responseCode, responseDescription) => {
        if (!isEmpty(successCallback)) {
          successCallback(responseBody, responseCode, responseDescription);
        } else {
          apiCallSuccessDefault(responseBody);
        }
        // Modal opened from playFile
        loadingWheelModal.close();
      },
      (responseBody, responseCode, responseDescription) => {
        if (!isEmpty(errorCallback)) {
          errorCallback(responseBody, responseCode, responseDescription);
        } else {
          apiCallErrorDefault(responseBody, responseCode, responseDescription);
        }
        // Modal opened from playFile
        loadingWheelModal.close();
      });
  }

  /** Execute a DELETE request to the specified url with the specified request body. */
  function httpDelete(url, requestBody) {
    cursorUtils.setCursorWait();
    debuggerHttpClient.delete(url, requestBody,
      (responseBody, responseCode, responseDescription) => apiCallSuccessDefault(responseBody),
      (responseBody, responseCode, responseDescription) => apiCallErrorDefault(responseBody, responseCode, responseDescription)
    );
  }

  /** Default actions for succesful api responses */
  function apiCallSuccessDefault(responseBody) {
    cursorUtils.setCursorDefault();
    logger.debug("Response: " + JSON.stringify(responseBody));
    vlcPlayer.pollVlcRcStatus();
  }

  /** Default actions for error api responses */
  function apiCallErrorDefault(responseBody, responseCode, responseDescription) {
    cursorUtils.setCursorDefault();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
  }
}

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
    vlcPlayer.getRestClient().get(vlcRcStatusApiUrl, false, getVlcRcStatusApiSuccessCallback, getVlcRcStatusApiErrorCallback); 
  }

  /** Get the playlist from an http api call instead of from the websocket. */
  function getPlaylistFromApi() { 
    vlcPlayer.getRestClient().get(playlistApiUrl, false, getPlaylistApiSuccessCallback, null); 
  }

  /** Update the main player view. */
  function getVlcRcStatusApiSuccessCallback(responseBody, responseCode, responseDescription) {
    cursorUtils.setCursorDefault();
    vlcPlayer.setVlcRcStatus(responseBody);
    vlcPlayer.updateView();
  }

  /** Don't update anything if there's an error getting the vlcRcStatus. */
  function getVlcRcStatusApiErrorCallback(responseBody, responseCode, responseDescription) {
    cursorUtils.setCursorDefault();
    logger.warn("Unable to get vlcRcStatus from an API call. This can happen if vlc player process isn't running");
  }

  /** Update the playlist view. */
  function getPlaylistApiSuccessCallback(responseBody, responseCode, responseDescription) {
    cursorUtils.setCursorDefault();
    vlcPlayer.getPlaylist().setUpdatedPlaylist(responseBody);
    vlcPlayer.getPlaylist().reload();
  }
}