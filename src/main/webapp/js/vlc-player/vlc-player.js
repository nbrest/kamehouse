/** 
 * VlcPlayer entity.
 * 
 * Dependencies: timeUtils, logger, apiCallTable, websocket
 * 
 * Dependencies in same file: VlcPlayerPlaylist, VlcPlayerRestClient, VlcPlayerSynchronizer, VlcPlayerViewUpdater
 * 
 * Call init() after instantiating VlcPlayer to connect the internal websocket
 * and start the sync loops.
 * 
 * @author nbrest
 */
function VlcPlayer(hostname) {
  let self = this;
  const adminVlcUrl = '/kame-house/api/v1/admin/vlc';
  this.vlcRcCommandUrl = '/kame-house/api/v1/vlc-rc/players/' + hostname + '/commands';
  this.vlcRcStatusHttpUrl = '/kame-house/api/v1/vlc-rc/players/' + hostname + '/status';
  this.playlist = new VlcPlayerPlaylist(self);
  this.restClient = new VlcPlayerRestClient(self);
  this.synchronizer = new VlcPlayerSynchronizer(self);
  this.viewUpdater = new VlcPlayerViewUpdater(self);
  this.debugger = new VlcPlayerDebugger(self);
  this.vlcRcStatus = {};

  /** Init VlcPlayer */
  this.init = function init() {
    logger.debugFunctionCall();
    self.synchronizer.connect();
    self.synchronizer.syncVlcRcStatusLoop();
    self.synchronizer.syncPlaylistLoop();
    self.synchronizer.keepAliveWebSocketLoop();
  }

  /**
   * --------------------------------------------------------------------------
   * Public interface to execute VlcPlayer commands
   */
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
    self.restClient.post(self.vlcRcCommandUrl, requestBody);
  }

  /** Play the selected file (or playlist) into vlc player and reload the current playlist. */
  this.playFile = function playFile(fileName) {
    logger.debugFunctionCall();
    logger.debug("File to play: " + fileName);
    let requestParam = "file=" + fileName; 
    self.restClient.postUrlEncoded(adminVlcUrl, requestParam);
    // Wait a few seconds for Vlc Player to restart and reload the playlist.
    self.playlist.asyncReload(5000);
  }

  /** Close vlc player. */
  this.close = function close() {
    logger.debugFunctionCall();
    self.restClient.delete(adminVlcUrl, null);
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
   * --------------------------------------------------------------------------
   * Playlist functionality
   */
  this.reloadPlaylist = function reloadPlaylist() {
    self.playlist.reload();
  }

  this.toggleExpandPlaylist = function toggleExpandPlaylist(){
    self.playlist.toggleExpandPlaylist();
  }

  this.highlightCurrentPlayingItem = function highlightCurrentPlayingItem() {
    self.playlist.highlightCurrentPlayingItem();
  }

  /**
   * --------------------------------------------------------------------------
   * Update view functionality
   */
  this.updateView = function updateView() {
    self.viewUpdater.update();
  }
 
  this.setTimeFromSlider = function setTimeFromSlider(value) {
    self.viewUpdater.setTimeFromSlider(value);
  } 

  this.updateTimeWhileSliding = function updateTimeWhileSliding(value) {
    self.viewUpdater.updateTimeWhileSliding(value);
  } 

  this.setVolumeFromSlider = function setVolumeFromSlider(value) {
    self.viewUpdater.setVolumeFromSlider(value);
  }

  this.updateVolumePercentage = function updateVolumePercentage(value) {
    self.viewUpdater.updateVolumePercentage(value);
  }

  /**
   * --------------------------------------------------------------------------
   * Debugger functionality
   */
  this.toggleDebugMode = function toggleDebugMode() {
    self.debugger.toggleDebugMode();
  }

  this.getVlcRcStatusHttp = function getVlcRcStatusHttp() {
    self.debugger.getVlcRcStatusHttp();
  }
}

/** 
 * Handles the updates to the VlcPlayer view. Except the playlist view,
 * which is handled by VlcPlayerPlaylist.
 * This prototype is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
function VlcPlayerViewUpdater(vlcPlayer) {
  let self = this;
  this.vlcPlayer = vlcPlayer; 
  this.statefulButtons = [];

  function setStatefulButtons() {
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-aspect-ratio-4-3', "aspectRatio", "4:3"));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-aspect-ratio-16-9', "aspectRatio", "16:9"));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-fullscreen', "fullscreen", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-repeat-1', "repeat", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-repeat', "loop", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-shuffle', "random", true));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-stop', "state", "stopped"));
    self.statefulButtons.push(new StatefulMediaButton(vlcPlayer, 'media-btn-mute', "volume", 0, 'btn-mute'));
  }
  setStatefulButtons();

  /** Update vlc player view. */
  this.update = function update() {
    logger.traceFunctionCall();
    self.updateMediaTitle();
    self.updateTimeSlider();
    self.updateVolumeSlider();
    self.statefulButtons.forEach(statefulButton => statefulButton.updateState());
    self.vlcPlayer.highlightCurrentPlayingItem();
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

  /**
   * --------------------------------------------------------------------------
   * Update time Functionality
   */
  /** Update media time slider from VlcRcStatus. */
  this.updateTimeSlider = function updateTimeSlider() {
    if (!isEmpty(self.vlcPlayer.getVlcRcStatus().time)) {
      $("#current-time").text(timeUtils.convertSecondsToHsMsSs(self.vlcPlayer.getVlcRcStatus().time));
      $("#time-slider").val(self.vlcPlayer.getVlcRcStatus().time);

      $("#total-time").text(timeUtils.convertSecondsToHsMsSs(self.vlcPlayer.getVlcRcStatus().length));
      $("#time-slider").attr('max', self.vlcPlayer.getVlcRcStatus().length);
    }
  }

  /** Set the current time from the slider's value. */
  this.setTimeFromSlider = function setTimeFromSlider(value) {
    $("#current-time").text(timeUtils.convertSecondsToHsMsSs(value));
    self.vlcPlayer.execVlcRcCommand('seek', value);
  }

  /** Update the displayed current time while I'm sliding */
  this.updateTimeWhileSliding = function updateTimeWhileSliding(value) {
    logger.trace("Current time: " + value);
    let currentTime = document.getElementById("current-time");
    currentTime.innerHTML = timeUtils.convertSecondsToHsMsSs(value);
  }

  /**
   * --------------------------------------------------------------------------
   * Update volume Functionality
   */
  /** Update volume slider from VlcRcStatus. */
  this.updateVolumeSlider = function updateVolumeSlider() {
    if (!isEmpty(self.vlcPlayer.getVlcRcStatus().volume)) {
      $("#volume-slider").val(self.vlcPlayer.getVlcRcStatus().volume);
      self.updateVolumePercentage(self.vlcPlayer.getVlcRcStatus().volume);
    }
  }

  /** Set the volume from the slider's value. */
  this.setVolumeFromSlider = function setVolumeFromSlider(value) {
    logger.trace("Current volume value: " + value);
    self.updateVolumePercentage(value);
    self.vlcPlayer.execVlcRcCommand('volume', value);
  }

  /** Update volume percentage to display with the specified value. */
  this.updateVolumePercentage = function updateVolumePercentage(value) {
    let volumePercentaje = Math.floor(value * 200 / 512);
    let currentVolume = document.getElementById("current-volume");
    currentVolume.innerHTML = volumePercentaje + "%";
  }
}

/** 
 * Represents a media button that has state (pressed/unpressed).
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
 * This prototype is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
function VlcPlayerSynchronizer(vlcPlayer) {
  let self = this;
  this.vlcPlayer = vlcPlayer;
  this.webSocket = new WebSocketKameHouse();
  this.isRunningSyncVlcRcStatusLoop = false;
  this.isRunningSyncPlaylistLoop = false;
  this.isRunningKeepAliveWebSocketLoop = false;

  function setWebSocket() {
    const webSocketStatusUrl = '/kame-house/api/ws/vlc-player/status';
    const webSocketPollUrl = "/app/vlc-player/status-in";
    const webSocketTopicUrl = '/topic/vlc-player/status-out';
    self.webSocket.setStatusUrl(webSocketStatusUrl);
    self.webSocket.setPollUrl(webSocketPollUrl);
    self.webSocket.setTopicUrl(webSocketTopicUrl);
  }
  setWebSocket();

  /** Poll for an update of vlcRcStatus through the web socket. */
  this.pollVlcRcStatus = function pollVlcRcStatus() {
    self.webSocket.poll();
  }

  /** 
   * Set the VlcRcStatus. vlcRcStatus must never be undefined or null.
   * If no value is passed, set an empty object. 
   */
  this.setVlcRcStatus = function setVlcRcStatus(vlcRcStatusResponse) {
    if (!isEmpty(vlcRcStatusResponse)) {
      self.vlcPlayer.vlcRcStatus = vlcRcStatusResponse;
    } else {
      self.vlcPlayer.vlcRcStatus = {};
    }
  }

  /** Returns true if the websocket is connected. */
  this.isConnected = function isConnected() {
    return self.webSocket.isConnected();
  }

  /** Connects the websocket to the backend. */
  this.connect = function connect() {
    logger.debugFunctionCall();
    self.webSocket.connect(function topicResponseCallback(topicResponse) {
      if (!isEmpty(topicResponse) && !isEmpty(topicResponse.body)) {
        self.setVlcRcStatus(JSON.parse(topicResponse.body));
      } else {
        self.setVlcRcStatus({});
      }
      self.vlcPlayer.updateView();
    });
  }

  /** Disconnects the websocket from the backend. */
  this.disconnect = function disconnect() {
    self.webSocket.disconnect();
  }

  /** Reconnects the websocket to the backend. */
  this.reconnect = function reconnect() {
    self.disconnect();
    self.connect();
  }

  /** 
   * Start infinite loop to pull VlcRcStatus from the server.
   * Break the loop setting isRunningSyncVlcRcStatusLoop to false.
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
      logger.trace("InfiniteLoop - vlcRcStatus:" + JSON.stringify(self.vlcPlayer.getVlcRcStatus()));
      if (self.isConnected()) {
        self.webSocket.poll();
        if (!isEmpty(self.vlcPlayer.getVlcRcStatus().information)) {
          vlcRcStatusPullWaitTimeMs = 1000;
          failedCount = 0;
          logger.trace("Resetting failedCount and vlcRcStatusPullWaitTimeMs: failedCount:" + failedCount + ". vlcRcStatusPullWaitTimeMs:" + vlcRcStatusPullWaitTimeMs);
        } else {
          failedCount++;
          logger.trace("syncVlcRcStatusLoop failedCount: " + failedCount);
          if (failedCount >= 10) {
            logger.trace("syncVlcRcStatusLoop failedCount >= 10. Updating vlcRcStatusPullWaitTimeMs. failedCount:" + failedCount);
            vlcRcStatusPullWaitTimeMs = 5000;
          }
        }
      } else {
        vlcRcStatusPullWaitTimeMs = 3000;
        logger.trace("WebSocket is disconnected. Waiting " + vlcRcStatusPullWaitTimeMs + " ms to sync again.");
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
    let playlistSyncWaitTimeMs = 7000;
    while (self.isRunningSyncPlaylistLoop) {
      logger.trace("InfiniteLoop - synchronizing playlist:");
      self.vlcPlayer.reloadPlaylist();
      await sleep(playlistSyncWaitTimeMs);
    }
    logger.info("Finished syncPlaylistLoop");
  }

  /** 
   * Start infinite loop to keep alive the websocket connection.
   * Break the loop setting isRunningSyncPlaylistLoop to false.
   */
  this.keepAliveWebSocketLoop = async function keepAliveWebSocketLoop() {
    logger.info("Started keepAliveWebSocketLoop");
    if (self.isRunningKeepAliveWebSocketLoop) {
      logger.error("keepAliveWebSocketLoop is already running");
      return;
    }
    self.isRunningKeepAliveWebSocketLoop = true;
    let keepAliveWebSocketWaitTimeMs = 5000;
    while (self.isRunningKeepAliveWebSocketLoop) {
      await sleep(keepAliveWebSocketWaitTimeMs);
      if (!self.isConnected()) {
        logger.trace("WebSocket not connected. Reconnecting.");
        self.reconnect();
      }
    }
    logger.info("Finished keepAliveWebSocketLoop");
  }
}

/** 
 * Represents the Playlist component in vlc-player page. 
 * It also handles the updates to the view of the playlist.
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

  const getPlaylistUrl = '/kame-house/api/v1/vlc-rc/players/localhost/playlist';
  const playSelectedUrl = '/kame-house/api/v1/vlc-rc/players/localhost/commands';

  /** Reload current playlist from server. */
  this.reload = function reload() {
    logger.trace("Reloading playlist");
    apiCallTable.get(getPlaylistUrl,
      function success(responseBody, responseCode, responseDescription) {
        let vlcRcPlaylist = responseBody;
        self.display(vlcRcPlaylist);
      },
      function error(responseBody, responseCode, responseDescription) {
        self.display();
        if (responseCode == "404") {
          apiCallTable.displayResponseData("Could not connect to VLC player to get the current playlist.", responseCode);
        }
      });
  }

  /** 
   * Reload playlist after the specified ms. 
   * Call this function instead of reloadPlaylist() directly
   * if I need to give VLC player time to restart so I get the updated playlist. 
   * I will usually need this after a vlc_start. 
   */
  this.asyncReload = async function asyncReload(sleepTime) {
    logger.debugFunctionCall();
    await sleep(sleepTime);
    self.reload();
  }

  /** Display playlist. */
  this.display = function display(playlistArray) {
    logger.traceFunctionCall();
    self.currentPlaylist = playlistArray;
    // Clear playlist content, if it has. 
    $("#playlist-table-body").empty();
    // Add the new playlist items received from the server.
    let $playlistTableBody = $('#playlist-table-body');
    let playlistTableRow;
    if (isEmpty(self.currentPlaylist)) {
      playlistTableRow = $('<tr>').append($('<td>').text("No playlist loaded yet. Mada mada dane :)"));
      $playlistTableBody.append(playlistTableRow);
    } else {
      for (let i = 0; i < self.currentPlaylist.length; i++) {
        let playlistElementButton = $('<button>');
        playlistElementButton.addClass("btn btn-outline-danger btn-borderless btn-playlist");
        playlistElementButton.text(self.currentPlaylist[i].name);
        playlistElementButton.click({
          id: self.currentPlaylist[i].id
        }, self.clickEventOnPlaylistRow);
        playlistTableRow = $('<tr id=' + self.currentPlaylist[i].id + '>').append($('<td>').append(playlistElementButton));
        $playlistTableBody.append(playlistTableRow);
      }
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
    self.vlcPlayer.restClient.post(playSelectedUrl, requestBody);
  }

  /** Highlight currently playing item in the playlist. */
  this.highlightCurrentPlayingItem = function highlightCurrentPlayingItem() {
    let currentPlId = self.vlcPlayer.getVlcRcStatus().currentPlId;
    logger.trace("currentPlId: " + currentPlId);
    // I can't use self in this case, I need to use this
    $('#playlist-table-body tr').each(function () {
      let playlistItemId = $(this).attr('id');
      if (playlistItemId == currentPlId) {
        $(this).children().children().addClass("playlist-table-element-playing");
      } else {
        $(this).children().children().removeClass("playlist-table-element-playing");
      }
    });
  }

  /** Toggle playlist collapsible active status and show or hide collapsible content. */
  // For an example of a collapsible element that expands to full vertical height, 
  // check the api-call-table example in the test-apis page. This one expands to a fixed height.
  this.toggleExpandPlaylist = function toggleExpandPlaylist() {
    logger.debug("Clicked playlist button");
    let playlistCollapsibleButton = document.getElementById("playlist-collapsible");
    playlistCollapsibleButton.classList.toggle("playlist-collapsible-active");

    let playlistCollapsibleContent = document.getElementById("playlist-collapsible-content");
    playlistCollapsibleContent.classList.toggle("playlist-collapsible-content-active");
  }
}

/** 
 * Represents an internal rest client for the VlcPlayer to split functionality. 
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

  /** Execute get on the specified url and display the output in the debug table. */
  this.get = function httpGet(url) {
    logger.debugFunctionCall();
    apiCallTable.get(url,
      function success(responseBody, responseCode, responseDescription) {
        logger.debug("get response: " + JSON.stringify(responseBody));
      },
      function error(responseBody, responseCode, responseDescription) {
        if (responseCode == "404") {
          apiCallTable.displayResponseData("Could not connect to VLC player to get the status.", responseCode);
        }
      });
  }

  /** Execute a POST request to the specified url with the specified request body. */
  this.post = function httpPost(url, requestBody) {
    logger.debugFunctionCall();
    apiCallTable.post(url, requestBody,
      function success(responseBody, responseCode, responseDescription) {
        logger.debug("post response: " + JSON.stringify(responseBody));
        self.vlcPlayer.pollVlcRcStatus();
      }, null);
  }

  /** Execute a POST request to the specified url with the specified request url parameters. */
  this.postUrlEncoded = function httpPostUrlEncoded(url, requestParam) {
    logger.debugFunctionCall();
    apiCallTable.postUrlEncoded(url, requestParam,
      function success(responseBody, responseCode, responseDescription) {
        logger.debug("postUrlEncoded response: " + JSON.stringify(responseBody));
        self.vlcPlayer.pollVlcRcStatus();
      }, null);
  }

  /** Execute a DELETE request to the specified url with the specified request body. */
  this.delete = function httpDelete(url, requestBody) {
    logger.debugFunctionCall();
    apiCallTable.delete(url, requestBody,
      function success(responseBody, responseCode, responseDescription) {
        logger.debug("delete response: " + JSON.stringify(responseBody));
        self.vlcPlayer.pollVlcRcStatus();
      }, null);
  }
}

/** 
 * Handles the debugger functionality of vlc player.
 * This prototype is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains prototype.
 * 
 * @author nbrest
 */
function VlcPlayerDebugger(vlcPlayer) {
  let self = this;
  this.vlcPlayer = vlcPlayer;
  this.vlcRcStatusHttpUrl = vlcPlayer.vlcRcStatusHttpUrl;

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
    self.vlcPlayer.restClient.get(self.vlcRcStatusHttpUrl);
  }
}