/** 
 * VlcPlayer entity.
 * 
 * Dependencies: timeUtils, logger, apiCallTable, websocket
 * 
 * Dependencies in same file: VlcPlayerPlaylist, VlcPlayerRestClient, VlcPlayerCommandExecutor, 
 * VlcPlayerSynchronizer, VlcPlayerMainViewUpdater, VlcPlayerDebugger
 * 
 * This prototype contains the public interface to interact with VlcPlayer. The logic is
 * implemented in the component prototypes mentioned below. It was designed this way because
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
    logger.debugFunctionCall();
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
  this.openTab = function openTab(vlcPlayerTabDivId) {
    let vlcPlayerTabContent = document.getElementsByClassName("vlc-player-tab-content");
    for (i = 0; i < vlcPlayerTabContent.length; i++) {
      vlcPlayerTabContent[i].style.display = "none";
    }
    let vlcPlayerTabLinks = document.getElementsByClassName("vlc-player-tab-link");
    for (var i = 0; i < vlcPlayerTabLinks.length; i++) {
      vlcPlayerTabLinks[i].className = vlcPlayerTabLinks[i].className.replace(" active", "");
    }
    let vlcPlayerTabLink = document.getElementById(vlcPlayerTabDivId + '-link');
    vlcPlayerTabLink.classList.add("active");

    let vlcPlayerTabDiv = document.getElementById(vlcPlayerTabDivId);
    vlcPlayerTabDiv.style.display = "block";
    vlcPlayerTabDiv.classList.add("active");
  }

  /**
   * --------------------------------------------------------------------------
   * Execute VlcPlayer commands
   */
  this.playFile = function playFile(fileName) {
    self.commandExecutor.playFile(fileName);
  }

  this.execVlcRcCommand = function execVlcRcCommand(name, val) {
    self.commandExecutor.execVlcRcCommand(name, val);
  }

  this.updateAspectRatio = function updateAspectRatio(aspectRatio) {
    if (!isEmpty(aspectRatio)) {
      self.commandExecutor.execVlcRcCommand('aspectratio', aspectRatio);
    }
  }

  this.seek = function seek(value) {
    self.mainViewUpdater.updateCurrentTimeView(value);
    self.commandExecutor.execVlcRcCommand('seek', value);
  }

  this.setVolume = function setVolume(value) {
    self.mainViewUpdater.updateVolumeView(value);
    self.commandExecutor.execVlcRcCommand('volume', value);
  }

  this.close = function close() {
    self.commandExecutor.close();
  }

  /**
   * --------------------------------------------------------------------------
   * VlcRcStatus synced from the backend
   */
  this.getVlcRcStatus = function getVlcRcStatus() {
    return self.vlcRcStatus;
  }

  this.pollVlcRcStatus = function pollVlcRcStatus() {
    self.synchronizer.pollVlcRcStatus();
  }

  /** 
   * Set the VlcRcStatus. vlcRcStatus must never be undefined or null.
   * If no value is passed, set an empty object. Always set vlcRcStatus
   * through this method.
   */
  this.setVlcRcStatus = function setVlcRcStatus(vlcRcStatus) {
    //logger.trace("vlcRcStatus " + JSON.stringify(vlcRcStatus));
    if (!isEmpty(vlcRcStatus)) {
      self.vlcRcStatus = vlcRcStatus;
    } else {
      self.vlcRcStatus = {};
    }
  }

  /**
   * --------------------------------------------------------------------------
   * Playlist functionality
   */
  this.reloadPlaylist = function reloadPlaylist(playlistArray) {
    self.playlist.reload(playlistArray);
  }

  this.scrollToCurrentlyPlaying = function scrollToCurrentlyPlaying() {
    self.playlist.scrollToCurrentlyPlaying();
  }

  this.filterPlaylistRows = function filterPlaylistRows(filterString) {
    filterTableRows(filterString, 'playlist-table-body');
  }

  this.toggleExpandPlaylistFilenames = function toggleExpandPlaylistFilenames() {
    self.playlist.toggleExpandPlaylistFilenames();
  }

  /**
   * --------------------------------------------------------------------------
   * Update view functionality
   */
  /** Calls each internal module that has view logic to update it's view. */
  this.updateView = function updateView() {
    self.mainViewUpdater.updateView();
    self.playlist.updateView();
  }

  /** Calls each internal module that has view logic to reset it's view. */
  this.resetView = function resetView() {
    logger.debugFunctionCall();
    self.setVlcRcStatus({});
    self.mainViewUpdater.resetView();
    self.playlist.resetView();
  }

  this.updateCurrentTimeView = function updateCurrentTimeView(value) {
    self.mainViewUpdater.updateCurrentTimeView(value);
  }

  this.updateVolumeView = function updateVolumeView(value) {
    self.mainViewUpdater.updateVolumeView(value);
  }

  /**
   * --------------------------------------------------------------------------
   * Rest Client functionality
   */
  this.getRestClient = function getRestClient() {
    // Use this getter internally from other components of VlcPlayer. Not externally.
    return self.restClient;
  }

  /**
   * --------------------------------------------------------------------------
   * Debugger functionality
   */
  this.getDebugger = function getDebugger() {
    return self.debugger;
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
  const adminVlcUrl = '/kame-house/api/v1/admin/vlc';
  this.vlcPlayer = vlcPlayer;
  this.vlcRcCommandUrl = '/kame-house/api/v1/vlc-rc/players/' + vlcPlayer.hostname + '/commands';

  /** Create a vlcrc command with the parameters and execute the request to the server. */
  this.execVlcRcCommand = function execVlcRcCommand(name, val) {
    logger.debugFunctionCall();
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
    self.vlcPlayer.getRestClient().post(self.vlcRcCommandUrl, requestBody);
  }

  /** Play the selected file (or playlist) into vlc player and reload the current playlist. */
  this.playFile = function playFile(fileName) {
    logger.debugFunctionCall();
    logger.debug("File to play: " + fileName);
    let requestParam = "file=" + fileName;
    self.vlcPlayer.getRestClient().postUrlEncoded(adminVlcUrl, requestParam);
  }

  /** Close vlc player. */
  this.close = function close() {
    logger.debugFunctionCall();
    self.vlcPlayer.getRestClient().delete(adminVlcUrl, null);
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

  function setStatefulButtons() {
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-fullscreen', "fullscreen", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-repeat-1', "repeat", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-repeat', "loop", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-shuffle', "random", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-stop', "state", "stopped"));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-mute', "volume", 0, 'btn-mute'));
  }
  setStatefulButtons();

  /** Update vlc player view for main view objects. */
  this.updateView = function updateView() {
    //logger.traceFunctionCall();
    if (!isEmpty(self.vlcPlayer.getVlcRcStatus())) {
      self.updateMediaTitle();
      self.updateTimeSlider();
      self.updateVolumeSlider();
      self.statefulButtons.forEach(statefulButton => statefulButton.updateState());
    } else {
      self.resetView();
    }
  }

  /** Reset vlc player view for main view objects. */
  this.resetView = function resetView() {
    //logger.traceFunctionCall();
    self.resetMediaTitle();
    self.resetTimeSlider();
    self.resetVolumeSlider();
    self.statefulButtons.forEach(statefulButton => statefulButton.updateState());
  }

  /** Update the media title. */
  this.updateMediaTitle = function updateMediaTitle() {
    let mediaName = {};
    mediaName.filename = "No media loaded";
    mediaName.title = "No media loaded";
    if (!isEmpty(self.vlcPlayer.getVlcRcStatus().information)) {
      mediaName.filename = self.vlcPlayer.getVlcRcStatus().information.meta.filename;
      mediaName.title = self.vlcPlayer.getVlcRcStatus().information.meta.title;
    }
    $("#media-title").text(mediaName.filename);
  }

  /** Reset the media title. */
  this.resetMediaTitle = function resetMediaTitle() {
    let mediaName = {};
    mediaName.filename = "No media loaded";
    mediaName.title = "No media loaded";
    $("#media-title").text(mediaName.filename);
  }

  /**
   * --------------------------------------------------------------------------
   * Update time Functionality
   */
  /** Update media time slider from VlcRcStatus and resets view when there's no input. */
  this.updateTimeSlider = function updateTimeSlider() {
    if (!isEmpty(self.vlcPlayer.getVlcRcStatus().time)) {
      self.updateCurrentTimeView(self.vlcPlayer.getVlcRcStatus().time);
      self.updateTotalTimeView(self.vlcPlayer.getVlcRcStatus().length);
    } else {
      self.resetTimeSlider();
    }
  }

  /** Reset time slider. */
  this.resetTimeSlider = function resetTimeSlider() {
    $("#current-time").text("--:--:--");
    $("#time-slider").val(500);
    $("#total-time").text("--:--:--");
    $("#time-slider").attr('max', 1000);
  }

  /** Update the displayed current time. */
  this.updateCurrentTimeView = function updateCurrentTimeView(value) {
    //logger.trace("Current time: " + value);
    let currentTime = document.getElementById("current-time");
    currentTime.innerHTML = timeUtils.convertSecondsToHsMsSs(value);
    $("#time-slider").val(value);
  }

  /** Update the displayed total time. */
  this.updateTotalTimeView = function updateTotalTimeView(value) {
    //logger.trace("Total time: " + value);
    $("#total-time").text(timeUtils.convertSecondsToHsMsSs(value));
    $("#time-slider").attr('max', value);
  }

  /**
   * --------------------------------------------------------------------------
   * Update volume Functionality
   */
  /** Update volume slider from VlcRcStatus. */
  this.updateVolumeSlider = function updateVolumeSlider() {
    if (!isEmpty(self.vlcPlayer.getVlcRcStatus().volume)) {
      self.updateVolumeView(self.vlcPlayer.getVlcRcStatus().volume);
    } else {
      self.resetVolumeSlider();
    }
  }

  /** Reset volume slider. */
  this.resetVolumeSlider = function resetVolumeSlider() {
    self.updateVolumeView(256);
  }

  /** Update volume percentage to display with the specified value. */
  this.updateVolumeView = function updateVolumeView(value) {
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
  if (!isEmpty(btnPrefixClass)) {
    this.btnPrefixClass = btnPrefixClass;
  } else {
    this.btnPrefixClass = defaultBtnPrefixClass;
  }

  /** Determines if the button is pressed or unpressed. */
  this.isPressed = function isPressed() {
    return self.vlcPlayer.getVlcRcStatus()[pressedField] == self.pressedCondition;
  }

  /** Update the state of the button (pressed/unpressed) */
  this.updateState = function updateState() {
    if (self.isPressed()) {
      self.setMediaButtonPressed();
    } else {
      self.setMediaButtonUnpressed();
    }
  }

  /** Set media button pressed */
  this.setMediaButtonPressed = function setMediaButtonPressed() {
    $('#' + self.id).removeClass(self.btnPrefixClass + '-unpressed');
    $('#' + self.id).addClass(self.btnPrefixClass + '-pressed');
  }

  /** Set media button unpressed */
  this.setMediaButtonUnpressed = function setMediaButtonUnpressed() {
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
    logger.traceFunctionCall();
    const vlcRcStatusWebSocketStatusUrl = '/kame-house/api/ws/vlc-player/status';
    const vlcRcStatusWebSocketPollUrl = "/app/vlc-player/status-in";
    const vlcRcStatusWebSocketTopicUrl = '/topic/vlc-player/status-out';
    self.vlcRcStatusWebSocket.setStatusUrl(vlcRcStatusWebSocketStatusUrl);
    self.vlcRcStatusWebSocket.setPollUrl(vlcRcStatusWebSocketPollUrl);
    self.vlcRcStatusWebSocket.setTopicUrl(vlcRcStatusWebSocketTopicUrl);

    const playlistWebSocketStatusUrl = '/kame-house/api/ws/vlc-player/playlist';
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
  this.pollVlcRcStatus = function pollVlcRcStatus() {
    self.vlcRcStatusWebSocket.poll();
  }

  /** Connects the websocket to the backend. */
  this.connectVlcRcStatus = function connectVlcRcStatus() {
    logger.debugFunctionCall();
    self.vlcRcStatusWebSocket.connect(function topicResponseCallback(topicResponse) {
      if (!isEmpty(topicResponse) && !isEmpty(topicResponse.body)) {
        self.vlcPlayer.setVlcRcStatus(JSON.parse(topicResponse.body));
      } else {
        self.vlcPlayer.setVlcRcStatus({});
      }
      self.vlcPlayer.updateView();
    });
  }

  /** Reconnects the VlcRcStatus websocket to the backend. */
  this.reconnectVlcRcStatus = function reconnectVlcRcStatus() {
    logger.debugFunctionCall();
    self.vlcRcStatusWebSocket.disconnect();
    self.connectVlcRcStatus();
  }

  /**
   * --------------------------------------------------------------------------
   * Playlist WebSocket functionality
   */
  /** Connects the playlist websocket to the backend. */
  this.connectPlaylist = function connectPlaylist() {
    logger.debugFunctionCall();
    self.playlistWebSocket.connect(function topicResponseCallback(topicResponse) {
      if (!isEmpty(topicResponse) && !isEmpty(topicResponse.body)) {
        let vlcRcPlaylist = JSON.parse(topicResponse.body);
        self.vlcPlayer.reloadPlaylist(vlcRcPlaylist);
      } else {
        self.vlcPlayer.reloadPlaylist(null);
      }
    });
  }

  /** Reconnects the playlist websocket to the backend. */
  this.reconnectPlaylist = function reconnectPlaylist() {
    logger.debugFunctionCall();
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

        if (!isEmpty(self.vlcPlayer.getVlcRcStatus().information)) {
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
  this.currentPlaylist = [];
  const playSelectedUrl = '/kame-house/api/v1/vlc-rc/players/localhost/commands';

  /** Reload playlist updating the playlist view. */
  this.reload = function reload(playlistArray) {
    //logger.traceFunctionCall();
    //TODO: Look for a more efficient array comparison than stringify
    if (JSON.stringify(self.currentPlaylist) == JSON.stringify(playlistArray)) {
      self.highlightCurrentPlayingItem();
      return;
    }
    self.currentPlaylist = playlistArray;
    // Clear playlist content. 
    $("#playlist-table-body").empty();
    // Add the new playlist items received from the server.
    let $playlistTableBody = $('#playlist-table-body');
    let playlistTableRow;
    if (isEmpty(self.currentPlaylist)) {
      let madaMadaDane = 'まだまだだね';
      playlistTableRow = $('<tr>').append($('<td>').text("No playlist loaded yet or unable to sync. " + madaMadaDane + " :)"));
      $playlistTableBody.append(playlistTableRow);
    } else {
      for (let i = 0; i < self.currentPlaylist.length; i++) {
        let playlistElementButton = $('<button>');
        playlistElementButton.addClass("playlist-table-btn");
        let filename = self.currentPlaylist[i].filename;
        playlistElementButton.data("filename", filename);
        playlistElementButton.text(filename);
        playlistElementButton.click({
          id: self.currentPlaylist[i].id
        }, self.clickEventOnPlaylistRow);
        playlistTableRow = $('<tr id=playlist-table-row-id-' + self.currentPlaylist[i].id + '>').append($('<td>').append(playlistElementButton));
        $playlistTableBody.append(playlistTableRow);
      }
      self.toggleExpandPlaylistFilenames();
      self.highlightCurrentPlayingItem();
    }
  }

  /** Play the clicked element from the playlist. */
  this.clickEventOnPlaylistRow = function clickEventOnPlaylistRow(event) {
    logger.debug("Play playlist id: " + event.data.id);
    let requestBody = {
      name: 'pl_play',
      id: event.data.id
    };
    self.vlcPlayer.getRestClient().post(playSelectedUrl, requestBody);
  }

  /** Highlight currently playing item in the playlist. */
  this.highlightCurrentPlayingItem = function highlightCurrentPlayingItem() {
    let currentPlId = self.vlcPlayer.getVlcRcStatus().currentPlId;
    //logger.trace("currentPlId: " + currentPlId);
    // I can't use self in this case, I need to use this
    let currentPlIdAsRowId = 'playlist-table-row-id-' + currentPlId;
    $('#playlist-table-body tr').each(function () {
      let playlistItemId = $(this).attr('id');
      let playlistEntry = $(this).children().children();
      if (playlistItemId == currentPlIdAsRowId) {
        playlistEntry.addClass("active");
      } else {
        playlistEntry.removeClass("active");
      }
    });
  }

  /** Toggle expand or collapse filenames in the playlist */
  this.toggleExpandPlaylistFilenames = function toggleExpandPlaylistFilenames() {
    logger.debugFunctionCall();
    let isExpandedFilename = true;
    $('#playlist-table-body tr').each(function () {
      let playlistEntry = $(this).children().children();
      let filename = playlistEntry.data("filename");
      let currentText = playlistEntry.text();
      if (currentText == filename) {
        // Currently it's showing the expanded filename. Update to short
        playlistEntry.text(self.getShortFilename(filename));
        isExpandedFilename = false;
      } else {
        // Currently it's showing the short filename. Update to expanded
        playlistEntry.text(filename);
        isExpandedFilename = true;
      }
    });
    self.updateExpandPlaylistFilenamesIcon(isExpandedFilename);
  }

  /** Update the icon to expand or collapse the playlist filenames */
  this.updateExpandPlaylistFilenamesIcon = function updateExpandPlaylistFilenamesIcon(isExpandedFilename) {
    if (isExpandedFilename) {
      $("#toggle-playlist-filenames-img").attr("src", "/kame-house/img/other/double-left-green.png");
    } else {
      $("#toggle-playlist-filenames-img").attr("src", "/kame-house/img/other/double-right-green.png");
    }
  }

  /** Get the last part of the absolute filename */
  this.getShortFilename = function getShortFilename(filename) {
    // Split the filename into an array based on the path separators '/' and '\'
    return filename.split(/[\\/]+/).pop();
  }

  /** Scroll to the current playing element in the playlist. */
  this.scrollToCurrentlyPlaying = function scrollToCurrentlyPlaying() {
    logger.debugFunctionCall();
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
  this.updateView = function updateView() {
    if (!isEmpty(self.vlcPlayer.getVlcRcStatus())) {
      self.highlightCurrentPlayingItem();
    } else {
      self.resetView();
    }
  }

  /** 
   * Reset the playlist view.
   */
  this.resetView = function resetView() {
    self.reload(null);
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
    logger.debugFunctionCall();
    loadingWheelModal.open();
    apiCallTable.get(url,
      function success(responseBody, responseCode, responseDescription) {
        logger.debug("get response: " + JSON.stringify(responseBody));
        loadingWheelModal.close();
      },
      function error(responseBody, responseCode, responseDescription) {
        loadingWheelModal.close(); 
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        if (responseCode == "404") {
          apiCallTable.displayResponseData("Could not connect to VLC player to get the status.", responseCode);
        }
      });
  }

  /** Execute a POST request to the specified url with the specified request body. */
  this.post = function httpPost(url, requestBody) {
    logger.debugFunctionCall();
    setCursorWait();
    apiCallTable.post(url, requestBody,
      function success(responseBody, responseCode, responseDescription) {
        logger.debug("post response: " + JSON.stringify(responseBody));
        self.vlcPlayer.pollVlcRcStatus();
        setCursorDefault();
      }, 
      function error(responseBody, responseCode, responseDescription) {
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        setCursorDefault();
      });
  }

  /** Execute a POST request to the specified url with the specified request url parameters. */
  this.postUrlEncoded = function httpPostUrlEncoded(url, requestParam) {
    logger.debugFunctionCall();
    setCursorWait();
    apiCallTable.postUrlEncoded(url, requestParam,
      function success(responseBody, responseCode, responseDescription) {
        logger.debug("postUrlEncoded response: " + JSON.stringify(responseBody));
        self.vlcPlayer.pollVlcRcStatus();
        setCursorDefault();
      },
      function error(responseBody, responseCode, responseDescription) {
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        setCursorDefault();
      });
  }

  /** Execute a DELETE request to the specified url with the specified request body. */
  this.delete = function httpDelete(url, requestBody) {
    logger.debugFunctionCall();
    setCursorWait();
    apiCallTable.delete(url, requestBody,
      function success(responseBody, responseCode, responseDescription) {
        logger.debug("delete response: " + JSON.stringify(responseBody));
        self.vlcPlayer.pollVlcRcStatus();
        setCursorDefault();
      },
      function error(responseBody, responseCode, responseDescription) {
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        setCursorDefault();
      });
  }
}

/** 
 * Handles the debugger functionality of vlc player.
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
  this.vlcRcStatusHttpUrl = '/kame-house/api/v1/vlc-rc/players/' + vlcPlayer.hostname + '/status';

  /** Toggle debug mode. */
  this.toggleDebugMode = function toggleDebugMode() {
    logger.debug("Toggled debug mode")
    let debugModeDiv = document.getElementById("debug-mode");
    debugModeDiv.classList.toggle("hidden-kh");
  }

  /** 
   * Get VlcRcStatus via http get. It doesn't sync the media player status. 
   * It just updates the debug table to see the current status. 
   */
  this.getVlcRcStatusHttp = function getVlcRcStatusHttp() {
    logger.debugFunctionCall();
    self.vlcPlayer.getRestClient().get(self.vlcRcStatusHttpUrl);
  }
}