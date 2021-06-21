/** 
 * VlcPlayer entity.
 * 
 * Dependencies: fileUtils, tableUtils, cursorUtils, timeUtils, logger, apiCallTable, websocket
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
  let self = this;
  this.hostname = hostname;
  this.commandExecutor = new VlcPlayerCommandExecutor(self);
  this.playlist = new VlcPlayerPlaylist(self);
  this.restClient = new VlcPlayerRestClient(self);
  this.synchronizer = new VlcPlayerSynchronizer(self);
  this.mainViewUpdater = new VlcPlayerMainViewUpdater(self);
  this.debugger = new VlcPlayerDebugger(self);
  this.vlcRcStatus = {};

  /** Init VlcPlayer */
  this.init = function init() {
    logger.debug(arguments.callee.name);
    self.playlist.init();
    self.synchronizer.connectVlcRcStatus();
    self.synchronizer.connectPlaylist();
    self.synchronizer.syncVlcRcStatusLoop();
    self.synchronizer.syncPlaylistLoop();
    self.synchronizer.keepAliveWebSocketsLoop();
    self.openTab('tab-playing');
  }

  /**
   * --------------------------------------------------------------------------
   * Tab manager
   */
  this.openTab = (vlcPlayerTabDivId) => {
    // Update tab links
    let vlcPlayerTabLinks = document.getElementsByClassName("vlc-player-tab-link");
    for (let i = 0; i < vlcPlayerTabLinks.length; i++) {
      vlcPlayerTabLinks[i].className = vlcPlayerTabLinks[i].className.replace(" active", "");
    }
    let vlcPlayerTabLink = document.getElementById(vlcPlayerTabDivId + '-link');
    vlcPlayerTabLink.classList.add("active");

    // Update tab content visibility
    let vlcPlayerTabContent = document.getElementsByClassName("vlc-player-tab-content");
    for (let i = 0; i < vlcPlayerTabContent.length; i++) {
      vlcPlayerTabContent[i].style.display = "none";
    }
    let vlcPlayerTabDiv = document.getElementById(vlcPlayerTabDivId);
    vlcPlayerTabDiv.style.display = "block";

    setTimeout(() => {
      // Asynchronously show or hide playlist and playlist browser content
      let playlistTable = document.getElementById("playlist-table");
      if ("tab-playlist" == vlcPlayerTabDivId) {
        playlistTable.style.display = "table";
      } else {
        playlistTable.style.display = "none";
      }

      let playlistBrowserTable = document.getElementById("playlist-browser-table");
      if ("tab-playlist-browser" == vlcPlayerTabDivId) {
        playlistBrowserTable.style.display = "table";
      } else {
        playlistBrowserTable.style.display = "none";
      }
    }, 0);
  }

  /**
   * --------------------------------------------------------------------------
   * Execute VlcPlayer commands
   */
  this.playFile = (fileName) => self.commandExecutor.playFile(fileName);

  this.execVlcRcCommand = (name, val) => self.commandExecutor.execVlcRcCommand(name, val);

  this.updateSubtitleDelay = (increment) => {
    let subtitleDelay = self.getVlcRcStatus().subtitleDelay;
    if (!isNullOrUndefined(subtitleDelay)) {
      subtitleDelay = Number(subtitleDelay) + Number(increment);
    } else {
      subtitleDelay = 0 + Number(increment);
    }
    self.commandExecutor.execVlcRcCommand('subdelay', subtitleDelay);
  }

  this.updateAspectRatio = (aspectRatio) => {
    if (!isNullOrUndefined(aspectRatio)) {
      self.commandExecutor.execVlcRcCommand('aspectratio', aspectRatio);
    }
  }

  this.seek = (value) => {
    self.mainViewUpdater.updateCurrentTimeView(value);
    self.commandExecutor.execVlcRcCommand('seek', value);
    self.mainViewUpdater.setTimeSliderLocked(false);
  }

  this.setVolume = (value) => {
    self.mainViewUpdater.updateVolumeView(value);
    self.commandExecutor.execVlcRcCommand('volume', value);
    self.mainViewUpdater.setVolumeSliderLocked(false);
  }

  this.close = () => self.commandExecutor.close();

  /**
   * --------------------------------------------------------------------------
   * VlcRcStatus synced from the backend
   */
  this.getVlcRcStatus = () => self.vlcRcStatus;

  this.pollVlcRcStatus = () => self.synchronizer.pollVlcRcStatus();

  /** 
   * Set the VlcRcStatus. vlcRcStatus must never be undefined or null.
   * If no value is passed, set an empty object. Always set vlcRcStatus
   * through this method.
   */
  this.setVlcRcStatus = (vlcRcStatus) => {
    //logger.trace("vlcRcStatus " + JSON.stringify(vlcRcStatus));
    if (!isNullOrUndefined(vlcRcStatus)) {
      self.vlcRcStatus = vlcRcStatus;
    } else {
      self.vlcRcStatus = {};
    }
  }

  /**
   * --------------------------------------------------------------------------
   * Playlist functionality
   */
  this.setUpdatedPlaylist = (updatedPlaylist) => self.playlist.setUpdatedPlaylist(updatedPlaylist);

  this.reloadPlaylist = () => self.playlist.reload();

  this.scrollToCurrentlyPlaying = () => self.playlist.scrollToCurrentlyPlaying();

  this.filterPlaylistRows = () => {
    let filterString = document.getElementById("playlist-filter-input").value;
    tableUtils.filterTableRows(filterString, 'playlist-table-body');
  }

  this.toggleExpandPlaylistFilenames = () => self.playlist.toggleExpandPlaylistFilenames();

  /**
   * --------------------------------------------------------------------------
   * Update view functionality
   */
  /** Calls each internal module that has view logic to update it's view. */
  this.updateView = () => {
    self.mainViewUpdater.updateView();
    self.playlist.updateView();
  }

  /** Calls each internal module that has view logic to reset it's view. */
  this.resetView = function resetView() {
    logger.debug(arguments.callee.name);
    self.setVlcRcStatus({});
    self.mainViewUpdater.resetView();
    self.playlist.resetView();
  }

  this.updateCurrentTimeView = (value) => {
    self.mainViewUpdater.setTimeSliderLocked(true);
    self.mainViewUpdater.updateCurrentTimeView(value);
  }

  this.updateVolumeView = (value) => {
    self.mainViewUpdater.setVolumeSliderLocked(true);
    self.mainViewUpdater.updateVolumeView(value);
  }

  /**
   * --------------------------------------------------------------------------
   * Rest Client functionality
   */
  // Use this getter internally from other components of VlcPlayer. Not externally.
  this.getRestClient = () => self.restClient;

  /**
   * --------------------------------------------------------------------------
   * Debugger functionality
   */
  this.getDebugger = () => self.debugger;

  /**
   * --------------------------------------------------------------------------
   * Links to external resources
   */
  this.openServerManagement = () => {
    cursorUtils.setCursorWait();
    window.location.href = '/kame-house/admin/server-management';
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
  let self = this;
  const vlcPlayerProcessControlUrl = '/kame-house-vlcrc/api/v1/vlc-rc/vlc-process';
  this.vlcPlayer = vlcPlayer;
  this.vlcRcCommandUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.hostname + '/commands';

  /** Create a vlcrc command with the parameters and execute the request to the server. */
  this.execVlcRcCommand = function execVlcRcCommand(name, val) {
    logger.debug(arguments.callee.name);
    let requestBody;
    if (isNullOrUndefined(val)) {
      requestBody = {
        name: name
      };
    } else {
      requestBody = {
        name: name,
        val: val
      };
    }
    self.vlcPlayer.getRestClient().post(self.vlcRcCommandUrl, requestBody);
  }

  /** Play the selected file (or playlist) into vlc player and reload the current playlist. */
  this.playFile = function playFile(fileName) {
    logger.debug(arguments.callee.name);
    logger.debug("File to play: " + fileName);
    let requestParam = "file=" + fileName;
    loadingWheelModal.open();
    self.vlcPlayer.getRestClient().postUrlEncoded(vlcPlayerProcessControlUrl, requestParam);
  }

  /** Close vlc player. */
  this.close = function close() {
    logger.debug(arguments.callee.name);
    self.vlcPlayer.getRestClient().delete(vlcPlayerProcessControlUrl, null);
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
  let self = this;
  this.vlcPlayer = vlcPlayer;
  this.statefulButtons = [];
  this.timeSliderLocked = false;
  this.volumeSliderLocked = false;

  function setStatefulButtons() {
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-fullscreen', "fullscreen", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-repeat-1', "repeat", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-repeat', "loop", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-shuffle', "random", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-stop', "state", "stopped"));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-mute', "volume", 0, 'btn-mute'));
  }
  setStatefulButtons();

  /** Set time slider locked. */
  this.setTimeSliderLocked = (value) => self.timeSliderLocked = value;

  /** Set volume slider locked. */
  this.setVolumeSliderLocked = (value) => self.volumeSliderLocked = value;

  /** Update vlc player view for main view objects. */
  this.updateView = () => {
    //logger.trace(arguments.callee.name);
    if (!isNullOrUndefined(self.vlcPlayer.getVlcRcStatus())) {
      self.updateMediaTitle();
      self.updateTimeSlider();
      self.updateVolumeSlider();
      self.updateSubtitleDelay();
      self.statefulButtons.forEach(statefulButton => statefulButton.updateState());
    } else {
      self.resetView();
    }
  }

  /** Reset vlc player view for main view objects. */
  this.resetView = () => {
    //logger.trace(arguments.callee.name);
    self.resetMediaTitle();
    self.resetTimeSlider();
    self.resetVolumeSlider();
    self.resetSubtitleDelay();
    self.statefulButtons.forEach(statefulButton => statefulButton.updateState());
  }

  /** Update the media title. */
  this.updateMediaTitle = () => {
    let mediaName = {};
    mediaName.filename = "No media loaded";
    mediaName.title = "No media loaded";
    if (!isNullOrUndefined(self.vlcPlayer.getVlcRcStatus().information)) {
      mediaName.filename = self.vlcPlayer.getVlcRcStatus().information.meta.filename;
      mediaName.title = self.vlcPlayer.getVlcRcStatus().information.meta.title;
    }
    $("#media-title").text(mediaName.filename);
  }

  /** Reset the media title. */
  this.resetMediaTitle = () => {
    let mediaName = {};
    mediaName.filename = "No media loaded";
    mediaName.title = "No media loaded";
    $("#media-title").text(mediaName.filename);
  }

  /** Update subtitle delay. */
  this.updateSubtitleDelay = () => {
    let subtitleDelay = self.vlcPlayer.getVlcRcStatus().subtitleDelay;
    if (isNullOrUndefined(subtitleDelay)) {
      subtitleDelay = "0";
    }
    $("#subtitle-delay-value").text(subtitleDelay);
  }

  /** Reset subtitle delay. */
  this.resetSubtitleDelay = () => {
    $("#subtitle-delay-value").text("0");
  }

  /**
   * --------------------------------------------------------------------------
   * Update time Functionality
   */
  /** Update media time slider from VlcRcStatus and resets view when there's no input. */
  this.updateTimeSlider = () => {
    if (!self.timeSliderLocked) {
      if (!isNullOrUndefined(self.vlcPlayer.getVlcRcStatus().time)) {
        self.updateCurrentTimeView(self.vlcPlayer.getVlcRcStatus().time);
        self.updateTotalTimeView(self.vlcPlayer.getVlcRcStatus().length);
      } else {
        self.resetTimeSlider();
      }
    }
  }

  /** Reset time slider. */
  this.resetTimeSlider = () => {
    $("#current-time").text("--:--:--");
    $("#time-slider").val(500);
    $("#total-time").text("--:--:--");
    $("#time-slider").attr('max', 1000);
  }

  /** Update the displayed current time. */
  this.updateCurrentTimeView = (value) => {
    //logger.trace("Current time: " + value);
    let currentTime = document.getElementById("current-time");
    currentTime.innerHTML = timeUtils.convertSecondsToHsMsSs(value);
    $("#time-slider").val(value);
  }

  /** Update the displayed total time. */
  this.updateTotalTimeView = (value) => {
    //logger.trace("Total time: " + value);
    $("#total-time").text(timeUtils.convertSecondsToHsMsSs(value));
    $("#time-slider").attr('max', value);
  }

  /**
   * --------------------------------------------------------------------------
   * Update volume Functionality
   */
  /** Update volume slider from VlcRcStatus. */
  this.updateVolumeSlider = () => {
    if (!self.volumeSliderLocked) {
      if (!isNullOrUndefined(self.vlcPlayer.getVlcRcStatus().volume)) {
        self.updateVolumeView(self.vlcPlayer.getVlcRcStatus().volume);
      } else {
        self.resetVolumeSlider();
      }
    }
  }

  /** Reset volume slider. */
  this.resetVolumeSlider = () => self.updateVolumeView(256);

  /** Update volume percentage to display with the specified value. */
  this.updateVolumeView = (value) => {
    //logger.trace("Current volume value: " + value);
    $("#volume-slider").val(value);
    let volumePercentaje = Math.floor(value * 200 / 512);
    let currentVolume = document.getElementById("current-volume");
    currentVolume.innerHTML = volumePercentaje + "%";
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
  let self = this;
  const defaultBtnPrefixClass = 'media-btn';
  this.vlcPlayer = vlcPlayer;
  this.id = id;
  this.pressedField = pressedField;
  this.pressedCondition = pressedCondition;
  this.btnPrefixClass = null;
  if (!isNullOrUndefined(btnPrefixClass)) {
    this.btnPrefixClass = btnPrefixClass;
  } else {
    this.btnPrefixClass = defaultBtnPrefixClass;
  }

  /** Determines if the button is pressed or unpressed. */
  this.isPressed = () => self.vlcPlayer.getVlcRcStatus()[pressedField] == self.pressedCondition;

  /** Update the state of the button (pressed/unpressed) */
  this.updateState = () => {
    if (self.isPressed()) {
      self.setMediaButtonPressed();
    } else {
      self.setMediaButtonUnpressed();
    }
  }

  /** Set media button pressed */
  this.setMediaButtonPressed = () => {
    $('#' + self.id).removeClass(self.btnPrefixClass + '-unpressed');
    $('#' + self.id).addClass(self.btnPrefixClass + '-pressed');
  }

  /** Set media button unpressed */
  this.setMediaButtonUnpressed = () => {
    $('#' + self.id).removeClass(self.btnPrefixClass + '-pressed');
    $('#' + self.id).addClass(self.btnPrefixClass + '-unpressed');
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
  let self = this;
  this.vlcPlayer = vlcPlayer;
  this.vlcRcStatusWebSocket = new WebSocketKameHouse();
  this.playlistWebSocket = new WebSocketKameHouse();
  this.isRunningSyncVlcRcStatusLoop = false;
  this.isRunningSyncPlaylistLoop = false;
  this.isRunningKeepAliveWebSocketLoop = false;

  function setWebSockets() {
    logger.trace(arguments.callee.name);
    const vlcRcStatusWebSocketStatusUrl = '/kame-house-vlcrc/api/ws/vlc-player/status';
    const vlcRcStatusWebSocketPollUrl = "/app/vlc-player/status-in";
    const vlcRcStatusWebSocketTopicUrl = '/topic/vlc-player/status-out';
    self.vlcRcStatusWebSocket.setStatusUrl(vlcRcStatusWebSocketStatusUrl);
    self.vlcRcStatusWebSocket.setPollUrl(vlcRcStatusWebSocketPollUrl);
    self.vlcRcStatusWebSocket.setTopicUrl(vlcRcStatusWebSocketTopicUrl);

    const playlistWebSocketStatusUrl = '/kame-house-vlcrc/api/ws/vlc-player/playlist';
    const playlistWebSocketPollUrl = "/app/vlc-player/playlist-in";
    const playlistWebSocketTopicUrl = '/topic/vlc-player/playlist-out';
    self.playlistWebSocket.setStatusUrl(playlistWebSocketStatusUrl);
    self.playlistWebSocket.setPollUrl(playlistWebSocketPollUrl);
    self.playlistWebSocket.setTopicUrl(playlistWebSocketTopicUrl);
  }
  setWebSockets();

  /**
   * --------------------------------------------------------------------------
   * VlcRcStatus WebSocket functionality
   */
  /** Poll for an update of vlcRcStatus through the web socket. */
  this.pollVlcRcStatus = () => self.vlcRcStatusWebSocket.poll();

  /** Connects the websocket to the backend. */
  this.connectVlcRcStatus = function connectVlcRcStatus() {
    logger.debug(arguments.callee.name);
    self.vlcRcStatusWebSocket.connect(function topicResponseCallback(topicResponse) {
      if (!isNullOrUndefined(topicResponse) && !isNullOrUndefined(topicResponse.body)) {
        self.vlcPlayer.setVlcRcStatus(JSON.parse(topicResponse.body));
      } else {
        self.vlcPlayer.setVlcRcStatus({});
      }
    });
  }

  /** Reconnects the VlcRcStatus websocket to the backend. */
  this.reconnectVlcRcStatus = function reconnectVlcRcStatus() {
    logger.debug(arguments.callee.name);
    self.vlcRcStatusWebSocket.disconnect();
    self.connectVlcRcStatus();
  }

  /**
   * --------------------------------------------------------------------------
   * Playlist WebSocket functionality
   */
  /** Connects the playlist websocket to the backend. */
  this.connectPlaylist = function connectPlaylist() {
    logger.debug(arguments.callee.name);
    self.playlistWebSocket.connect(function topicResponseCallback(topicResponse) {
      if (!isNullOrUndefined(topicResponse) && !isNullOrUndefined(topicResponse.body)) {
        self.vlcPlayer.setUpdatedPlaylist(JSON.parse(topicResponse.body));
      } else {
        self.vlcPlayer.setUpdatedPlaylist(null);
      }
    });
  }

  /** Reconnects the playlist websocket to the backend. */
  this.reconnectPlaylist = function reconnectPlaylist() {
    logger.debug(arguments.callee.name);
    self.playlistWebSocket.disconnect();
    self.connectPlaylist();
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
  this.syncVlcRcStatusLoop = async function syncVlcRcStatusLoop() {
    logger.info("Started syncVlcRcStatusLoop");
    if (self.isRunningSyncVlcRcStatusLoop) {
      logger.error("syncVlcRcStatusLoop is already running");
      return;
    }
    self.isRunningSyncVlcRcStatusLoop = true;
    let vlcRcStatusPullWaitTimeMs = 1000;
    let failedCount = 0;
    while (self.isRunningSyncVlcRcStatusLoop) {
      logger.trace("Poll vlcRcStatus loop");
      //logger.trace("InfiniteLoop - vlcRcStatus: " + JSON.stringify(self.vlcPlayer.getVlcRcStatus()));
      if (self.vlcRcStatusWebSocket.isConnected()) {
        // poll VlcRcStatus from the websocket.
        self.vlcRcStatusWebSocket.poll();
        self.vlcPlayer.updateView();
        if (!isNullOrUndefined(self.vlcPlayer.getVlcRcStatus().information)) {
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
        //logger.trace("WebSocket is disconnected. Resetting view and waiting " + vlcRcStatusPullWaitTimeMs + " ms to sync again.");
        self.vlcPlayer.resetView();
      }
      await sleep(vlcRcStatusPullWaitTimeMs);
    }
    logger.info("Finished syncVlcRcStatusLoop");
  }

  /** 
   * Start infinite loop to sync the current playlist from the server.
   * Break the loop setting isRunningSyncPlaylistLoop to false.
   */
  this.syncPlaylistLoop = async function syncPlaylistLoop() {
    logger.info("Started syncPlaylistLoop");
    if (self.isRunningSyncPlaylistLoop) {
      logger.error("syncPlaylistLoop is already running");
      return;
    }
    self.isRunningSyncPlaylistLoop = true;
    let playlistSyncWaitTimeMs = 5000;
    while (self.isRunningSyncPlaylistLoop) {
      logger.trace("Poll playlist loop");
      if (self.playlistWebSocket.isConnected()) {
        // poll playlist from the websocket.
        self.playlistWebSocket.poll();
        self.vlcPlayer.reloadPlaylist();
      }
      await sleep(playlistSyncWaitTimeMs);
    }
    logger.info("Finished syncPlaylistLoop");
  }

  /** 
   * Start infinite loop to keep alive the websocket connections.
   * Break the loop setting isRunningKeepAliveWebSocketLoop to false.
   */
  this.keepAliveWebSocketsLoop = async function keepAliveWebSocketsLoop() {
    logger.info("Started keepAliveWebSocketsLoop");
    if (self.isRunningKeepAliveWebSocketLoop) {
      logger.error("keepAliveWebSocketsLoop is already running");
      return;
    }
    self.isRunningKeepAliveWebSocketLoop = true;
    let keepAliveWebSocketWaitTimeMs = 5000;
    while (self.isRunningKeepAliveWebSocketLoop) {
      logger.trace("Keep websockets connected loop");
      await sleep(keepAliveWebSocketWaitTimeMs);
      if (!self.vlcRcStatusWebSocket.isConnected()) {
        logger.debug("VlcRcStatus webSocket not connected. Reconnecting.");
        self.reconnectVlcRcStatus();
      }
      if (!self.playlistWebSocket.isConnected()) {
        logger.debug("Playlist webSocket not connected. Reconnecting.");
        self.reconnectPlaylist();
      }
    }
    logger.info("Finished keepAliveWebSocketsLoop");
  }
}

/** 
 * Represents the Playlist component in vlc-player page. 
 * It also handles the updates to the view of the playlist.
 * 
 * This prototype is meant to be instantiated by VlcPlayer() constructor
 * and added as a property to VlcPlayer.playlist inside that constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
function VlcPlayerPlaylist(vlcPlayer) {
  let self = this;
  this.vlcPlayer = vlcPlayer;
  this.currentPlaylist = null;
  this.updatedPlaylist = null;
  const playSelectedUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/localhost/commands';
  this.tbodyAbsolutePaths = null;
  this.tbodyFilenames = null;
  this.dobleLeftImg = null;
  this.dobleRightImg = null;

  /** Init Playlist. */
  this.init = function init() {
    logger.debug(arguments.callee.name);
    self.dobleLeftImg = self.createDoubleArrowImg("left");
    self.dobleRightImg = self.createDoubleArrowImg("right");
    $("#toggle-playlist-filenames-img").replaceWith(self.dobleRightImg);
  }

  /** Create an image object to toggle when expanding/collapsing playlist browser filenames. */
  this.createDoubleArrowImg = (direction) => {
    let dobleArrowImg = new Image();
    dobleArrowImg.id = "toggle-playlist-filenames-img";
    dobleArrowImg.src = "/kame-house/img/other/double-" + direction + "-green.png";
    dobleArrowImg.className = "vlc-player-btn-img vlc-player-btn-img-s vlc-player-btn-green btn-playlist-controls";
    dobleArrowImg.alt = "Expand/Collapse Filename";
    dobleArrowImg.title = "Expand/Collapse Filename";
    dobleArrowImg.onclick = () => self.toggleExpandPlaylistFilenames();
    return dobleArrowImg;
  }

  /** Set updated playlist: Temporary storage for the playlist I receive from the websocket */
  this.setUpdatedPlaylist = (updatedPlaylist) => self.updatedPlaylist = updatedPlaylist;

  /** Reload playlist updating the playlist view. */
  this.reload = () => {
    if (!self.isPlaylistUpdated(self.currentPlaylist, self.updatedPlaylist)) {
      // Playlist content not updated, just update currently playing element and return
      self.highlightCurrentPlayingItem();
      return;
    }
    self.currentPlaylist = self.updatedPlaylist;
    // Clear playlist content. 
    $("#playlist-table-body").empty();
    // Add the new playlist items received from the server.
    let $playlistTableBody = $('#playlist-table-body');
    if (isNullOrUndefined(self.currentPlaylist) || isNullOrUndefined(self.currentPlaylist.length) ||
      self.currentPlaylist.length <= 0) {
      let madaMadaDane = 'まだまだだね';
      let playlistTableRow = $('<tr>').append($('<td>').text("No playlist loaded yet or unable to sync. " + madaMadaDane + " :)"));
      $playlistTableBody.append(playlistTableRow);
    } else {
      self.tbodyFilenames = $('<tbody id="playlist-table-body">');
      self.tbodyAbsolutePaths = $('<tbody id="playlist-table-body">');
      for (let i = 0; i < self.currentPlaylist.length; i++) {
        let absolutePath = self.currentPlaylist[i].filename;
        let filename = fileUtils.getShortFilename(absolutePath);
        let playlistElementId = self.currentPlaylist[i].id
        self.tbodyFilenames.append(self.getPlaylistTableRow(filename, playlistElementId));
        self.tbodyAbsolutePaths.append(self.getPlaylistTableRow(absolutePath, playlistElementId));
      }
      $playlistTableBody.replaceWith(self.tbodyFilenames);
      self.highlightCurrentPlayingItem();
      self.vlcPlayer.filterPlaylistRows();
    }
  }

  /** Create a playlist table row */
  this.getPlaylistTableRow = (displayName, playlistElementId) => {
    let playlistElementButton = $('<button>');
    playlistElementButton.addClass("playlist-table-btn");
    playlistElementButton.text(displayName);
    playlistElementButton.click({
      id: playlistElementId
    }, self.clickEventOnPlaylistRow);
    let playlistTableRow = $('<tr id=playlist-table-row-id-' + playlistElementId + '>').append($('<td>').append(playlistElementButton));
    return playlistTableRow;
  }

  /** Compares two playlists. Returns true if they are different or empty. Expects 2 vlc playlist arrays */
  this.isPlaylistUpdated = (currentPlaylist, updatedPlaylist) => {
    let MAX_COMPARISONS = 30;
    // For empty playlists, return true, so it updates the UI
    if (isNullOrUndefined(currentPlaylist) || isNullOrUndefined(updatedPlaylist)) {
      return true;
    }
    // If the sizes don't match, it's updated
    if (currentPlaylist.length != updatedPlaylist.length) {
      return true;
    }
    // If the sizes match, compare playlists elements in the specified increment. 
    // Don't check all filenames to avoid doing too many comparisons in very large playlists
    let step = 0;
    if (currentPlaylist.length <= MAX_COMPARISONS) {
      step = 1;
    } else {
      if ((currentPlaylist.length > MAX_COMPARISONS) &&
        (currentPlaylist.length <= MAX_COMPARISONS * 2)) {
        step = 2;
      }
    }
    if (step == 0) {
      step = Math.round(currentPlaylist.length / MAX_COMPARISONS);
    }
    for (let i = 0; i < currentPlaylist.length; i = i + step) {
      if (currentPlaylist[i].filename != updatedPlaylist[i].filename) {
        return true;
      }
    }
    // Playlist is not updated
    return false;
  }

  /** Play the clicked element from the playlist. */
  this.clickEventOnPlaylistRow = (event) => {
    logger.debug("Play playlist id: " + event.data.id);
    let requestBody = {
      name: 'pl_play',
      id: event.data.id
    };
    self.vlcPlayer.getRestClient().post(playSelectedUrl, requestBody);
  }

  /** Highlight currently playing item in the playlist. */
  this.highlightCurrentPlayingItem = () => {
    let currentPlId = self.vlcPlayer.getVlcRcStatus().currentPlId;
    //logger.trace("currentPlId: " + currentPlId);
    let currentPlIdAsRowId = 'playlist-table-row-id-' + currentPlId;
    $('#playlist-table-body tr td button').removeClass("active");
    $("#" + currentPlIdAsRowId).children().children().addClass("active");
  }

  /** Toggle expand or collapse filenames in the playlist */
  this.toggleExpandPlaylistFilenames = function toggleExpandPlaylistFilenames() {
    logger.debug(arguments.callee.name);
    let isExpandedFilename = null;
    let filenamesFirstFile = $(self.tbodyFilenames).children().first().text();
    let currentFirstFile = $('#playlist-table-body tr:first').text();
    let $playlistTable = $('#playlist-table');

    if (currentFirstFile == filenamesFirstFile) {
      // currently displaying filenames, switch to absolute paths 
      if (!isNullOrUndefined(self.tbodyFilenames)) {
        self.tbodyFilenames.detach();
      }
      $playlistTable.append(self.tbodyAbsolutePaths);
      isExpandedFilename = true;
    } else {
      // currently displaying absolute paths, switch to filenames 
      if (!isNullOrUndefined(self.tbodyAbsolutePaths)) {
        self.tbodyAbsolutePaths.detach();
      }
      $playlistTable.append(self.tbodyFilenames);
      isExpandedFilename = false;
    }
    self.highlightCurrentPlayingItem();
    self.updateExpandPlaylistFilenamesIcon(isExpandedFilename);
    self.vlcPlayer.filterPlaylistRows();
  }

  /** Update the icon to expand or collapse the playlist filenames */
  this.updateExpandPlaylistFilenamesIcon = (isExpandedFilename) => {
    if (isExpandedFilename) {
      $("#toggle-playlist-filenames-img").replaceWith(self.dobleLeftImg);
    } else {
      $("#toggle-playlist-filenames-img").replaceWith(self.dobleRightImg);
    }
  }

  /** Scroll to the current playing element in the playlist. */
  this.scrollToCurrentlyPlaying = () => {
    //logger.debug(arguments.callee.name);
    let currentPlId = self.vlcPlayer.getVlcRcStatus().currentPlId;
    let $currentPlayingRow = $('#playlist-table-row-id-' + currentPlId);
    if ($currentPlayingRow.length) {
      let playlistTableWrapper = $('#playlist-table-wrapper');
      playlistTableWrapper.scrollTop(0);
      let scrollToOffset = $currentPlayingRow.offset().top - playlistTableWrapper.offset().top;
      playlistTableWrapper.scrollTop(scrollToOffset);
    }
  }

  /** 
   * Update the playlist view. Add all the functionality that needs to happen 
   * to update the view of the playlist when vlcRcStatus changes  
   */
  this.updateView = () => {
    if (!isNullOrUndefined(self.vlcPlayer.getVlcRcStatus())) {
      self.highlightCurrentPlayingItem();
    } else {
      self.resetView();
    }
  }

  /** 
   * Reset the playlist view.
   */
  this.resetView = () => {
    self.updatedPlaylist = null;
    self.reload();
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
  let self = this;
  this.vlcPlayer = vlcPlayer;

  /** Execute GET on the specified url and display the output in the debug table. */
  this.get = function httpGet(url) {
    logger.debug(arguments.callee.name);
    cursorUtils.setCursorWait();
    apiCallTable.get(url,
      (responseBody, responseCode, responseDescription) => apiCallSuccessDefault(responseBody),
      (responseBody, responseCode, responseDescription) => {
        apiCallErrorDefault(responseBody, responseCode, responseDescription);
        if (responseCode == "404") {
          apiCallTable.displayResponseData("Could not connect to VLC player to get the status.", responseCode);
        }
      });
  }

  /** Execute a POST request to the specified url with the specified request body. */
  this.post = function httpPost(url, requestBody) {
    logger.debug(arguments.callee.name);
    cursorUtils.setCursorWait();
    apiCallTable.post(url, requestBody,
      (responseBody, responseCode, responseDescription) => apiCallSuccessDefault(responseBody),
      (responseBody, responseCode, responseDescription) => apiCallErrorDefault(responseBody, responseCode, responseDescription)
    );
  }

  /** Execute a POST request to the specified url with the specified request url parameters. */
  this.postUrlEncoded = function httpPostUrlEncoded(url, requestParam) {
    logger.debug(arguments.callee.name);
    cursorUtils.setCursorWait();
    apiCallTable.postUrlEncoded(url, requestParam,
      (responseBody, responseCode, responseDescription) => {
        apiCallSuccessDefault(responseBody);
        // Modal opened from playFile
        loadingWheelModal.close();
      },
      (responseBody, responseCode, responseDescription) => {
        apiCallErrorDefault(responseBody, responseCode, responseDescription);
        // Modal opened from playFile
        loadingWheelModal.close();
      });
  }

  /** Execute a DELETE request to the specified url with the specified request body. */
  this.delete = function httpDelete(url, requestBody) {
    logger.debug(arguments.callee.name);
    cursorUtils.setCursorWait();
    apiCallTable.delete(url, requestBody,
      (responseBody, responseCode, responseDescription) => apiCallSuccessDefault(responseBody),
      (responseBody, responseCode, responseDescription) => apiCallErrorDefault(responseBody, responseCode, responseDescription)
    );
  }

  /** Default actions for succesful api responses */
  function apiCallSuccessDefault(responseBody) {
    cursorUtils.setCursorDefault();
    logger.debug("Response: " + JSON.stringify(responseBody));
    self.vlcPlayer.pollVlcRcStatus();
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
  let self = this;
  this.vlcPlayer = vlcPlayer;
  this.vlcRcStatusHttpUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.hostname + '/status';

  /** 
   * Get VlcRcStatus via http get. It doesn't sync the media player status. 
   * It just updates the debug table to see the current status. 
   */
  this.getVlcRcStatusHttp = () => self.vlcPlayer.getRestClient().get(self.vlcRcStatusHttpUrl);
}