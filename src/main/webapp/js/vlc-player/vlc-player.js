/** 
 * VlcPlayer entity.
 * 
 * Dependencies: timeUtils, logger, apiCallTable, websocket
 * 
 * Dependencies in same file: playlist
 * 
 * Call init() after instantiating VlcPlayer to connect the internal websocket
 * and start the sync loops.
 * 
 * @author nbrest
 */
function VlcPlayer(hostname) {
  let self = this;
  this.webSocket = new WebSocketKameHouse();
  this.playlist = new VlcPlayerPlaylist(self);
  this.restClient = new VlcPlayerRestClient(self);
  this.vlcRcStatus = {};
  this.isSyncEnabledValue = true;
  this.isRunningSyncVlcRcStatusLoop = false;
  this.isRunningSyncPlaylistLoop = false;
  this.isRunningKeepAliveWebSocketLoop = false;
  let vlcRcCommandUrl = '/kame-house/api/v1/vlc-rc/players/' + hostname + '/commands';
  let vlcRcStatusHttpUrl = '/kame-house/api/v1/vlc-rc/players/' + hostname + '/status';
  const adminVlcUrl = '/kame-house/api/v1/admin/vlc';
  const webSocketStatusUrl = '/kame-house/api/ws/vlc-player/status';
  const webSocketPollUrl = "/app/vlc-player/status-in";
  const webSocketTopicUrl = '/topic/vlc-player/status-out';

  self.webSocket.setStatusUrl(webSocketStatusUrl);
  self.webSocket.setPollUrl(webSocketPollUrl);
  self.webSocket.setTopicUrl(webSocketTopicUrl);

  /** Init VlcPlayer */
  this.init = function init() {
    logger.debugFunctionCall();
    self.connect();
    self.syncVlcRcStatusLoop();
    self.syncPlaylistLoop();
    self.keepAliveWebSocketLoop();
  }

  /**
   * --------------------------------------------------------------------------
   * Get and Set VlcRcStatus synced from the backend.
   */
  this.getVlcRcStatus = function getVlcRcStatus() {
    //logger.traceFunctionCall();
    return self.vlcRcStatus;
  }

  // vlcRcStatus must never be undefined or null. If no value is passed, set an empty object.
  this.setVlcRcStatus = function setVlcRcStatus(vlcRcStatusResponse) {
    //logger.traceFunctionCall();
    if (!isEmpty(vlcRcStatusResponse)) {
      self.vlcRcStatus = vlcRcStatusResponse;
    } else {
      self.vlcRcStatus = {};
    }
  }

  /**
   * --------------------------------------------------------------------------
   * Get and Set isSyncEnabled, to control wether the app syncs or doesn 't sync
   * the status of the vlc player with the backend.
   * Set to false to stop syncing vlc player through the web socket. Default is true to sync.
   */
  this.isSyncEnabled = function isSyncEnabled() {
    return self.isSyncEnabledValue;
  }

  this.enableSync = function enableSync() {
    self.isSyncEnabledValue = true;
  }

  this.disableSync = function disableSync() {
    self.isSyncEnabledValue = false;
  }

  /**
   * --------------------------------------------------------------------------
   * WebSocket Functionality
   */
  /** Poll for an update of vlcRcStatus through the web socket. */
  this.pollVlcRcStatus = function pollVlcRcStatus() {
    self.webSocket.poll();
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
      self.updateView();
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
   * --------------------------------------------------------------------------
   * Playlist Functionality
   */
  /** Reload the current playlist. */
  this.reloadPlaylist = function reloadPlaylist() {
    self.playlist.reload();
  }

  this.toggleExpandPlaylist = function toggleExpandPlaylist(){
    self.playlist.toggleExpandPlaylist();
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
    self.restClient.post(vlcRcCommandUrl, requestBody);
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

  /** 
   * Get VlcRcStatus via http get for debug. It doesn't sync the media player status. 
   * It just updates the debug table to see the current status. 
   */
  this.getVlcRcStatusForDebug = function getVlcRcStatusForDebug() {
    logger.debugFunctionCall();
    self.restClient.get(vlcRcStatusHttpUrl);
  }

  /** Close vlc player. */
  this.close = function close() {
    logger.debugFunctionCall();
    self.restClient.delete(adminVlcUrl, null);
  }  

  /**
   * --------------------------------------------------------------------------
   * Update view Functionality
   */

  /** Update vlc player view. */
  this.updateView = function updateView() {
    logger.traceFunctionCall();

    // Update media title.
    let mediaName = self.getMediaName();
    $("#media-title").text(mediaName.filename);

    // Update media playing time
    if (!isEmpty(self.vlcRcStatus.time)) {
      $("#current-time").text(timeUtils.convertSecondsToHsMsSs(self.vlcRcStatus.time));
      $("#time-slider").val(self.vlcRcStatus.time);

      $("#total-time").text(timeUtils.convertSecondsToHsMsSs(self.vlcRcStatus.length));
      $("#time-slider").attr('max', self.vlcRcStatus.length);
    }

    // Update volume percentage and slider.
    if (!isEmpty(self.vlcRcStatus.volume)) {
      $("#volume-slider").val(self.vlcRcStatus.volume);
      self.updateVolumePercentage(self.vlcRcStatus.volume);
    }

    // Update media buttons with state
    self.updateMediaButtonsWithState();

    // Highlight current playing item in playlist. 
    self.playlist.highlightCurrentPlayingItem(self.vlcRcStatus.currentPlId);
  }

  /** Get media name from VlcRcStatus. */
  this.getMediaName = function getMediaName() {
    let mediaNameLocal = {};
    mediaNameLocal.filename = "No media loaded";
    mediaNameLocal.title = "No media loaded";
    if (!isEmpty(self.getVlcRcStatus().information)) {
      mediaNameLocal.filename = self.vlcRcStatus.information.meta.filename;
      mediaNameLocal.title = self.vlcRcStatus.information.meta.title;
    }
    return mediaNameLocal;
  }

  /**
   * --------------------------------------------------------------------------
   * Update time Functionality
   */

  /** Set the current time from the slider's value. */
  this.setTimeFromSlider = function setTimeFromSlider(value) {
    $("#current-time").text(timeUtils.convertSecondsToHsMsSs(value));
    self.execVlcRcCommand('seek', value);
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
  /** Set the volume from the slider's value. */
  this.setVolumeFromSlider = function setVolumeFromSlider(value) {
    logger.trace("Current volume value: " + value);
    self.updateVolumePercentage(value);
    self.execVlcRcCommand('volume', value);
  }

  /** Update volume percentage to display with the specified value. */
  this.updateVolumePercentage = function updateVolumePercentage(value) {
    let volumePercentaje = Math.floor(value * 200 / 512);
    let currentVolume = document.getElementById("current-volume");
    currentVolume.innerHTML = volumePercentaje + "%";
  }

  /**
   * --------------------------------------------------------------------------
   * Update media buttons with state Functionality
   */
  /** Update vlc player media buttons that have state, based on vlcRcStatus. */
  this.updateMediaButtonsWithState = function updateMediaButtonsWithState() {
    // Update aspect-ratio 16:9 button
    if (self.vlcRcStatus.aspectRatio == "16:9") {
      self.setMediaButtonPressed('media-btn-aspect-ratio-16-9');
    } else {
      self.setMediaButtonUnpressed('media-btn-aspect-ratio-16-9');
    }

    // Update aspect-ratio 4:3 button
    if (self.vlcRcStatus.aspectRatio == "4:3") {
      self.setMediaButtonPressed('media-btn-aspect-ratio-4-3');
    } else {
      self.setMediaButtonUnpressed('media-btn-aspect-ratio-4-3');
    }

    // Update fullscreen button
    if (self.vlcRcStatus.fullscreen) {
      self.setMediaButtonPressed('media-btn-fullscreen');
    } else {
      self.setMediaButtonUnpressed('media-btn-fullscreen');
    }

    // Update mute button
    if (self.vlcRcStatus.volume == 0) {
      self.setMuteButtonPressed('media-btn-mute');
    } else {
      self.setMuteButtonUnpressed('media-btn-mute');
    }

    // Update repeat 1 button
    if (self.vlcRcStatus.repeat) {
      self.setMediaButtonPressed('media-btn-repeat-1');
    } else {
      self.setMediaButtonUnpressed('media-btn-repeat-1');
    }

    // Update repeat all button
    if (self.vlcRcStatus.loop) {
      self.setMediaButtonPressed('media-btn-repeat');
    } else {
      self.setMediaButtonUnpressed('media-btn-repeat');
    }

    // Update shuffle button
    if (self.vlcRcStatus.random) {
      self.setMediaButtonPressed('media-btn-shuffle');
    } else {
      self.setMediaButtonUnpressed('media-btn-shuffle');
    }

    // Update stop button
    if (self.vlcRcStatus.state == "stopped") {
      self.setMediaButtonPressed('media-btn-stop');
    } else {
      self.setMediaButtonUnpressed('media-btn-stop');
    }
  }

  /** Set media button pressed */
  this.setMediaButtonPressed = function setMediaButtonPressed(mediaButtonId) {
    $('#' + mediaButtonId).removeClass('media-btn-unpressed');
    $('#' + mediaButtonId).addClass('media-btn-pressed');
  }

  /** Set media button unpressed */
  this.setMediaButtonUnpressed = function setMediaButtonUnpressed(mediaButtonId) {
    $('#' + mediaButtonId).removeClass('media-btn-pressed');
    $('#' + mediaButtonId).addClass('media-btn-unpressed');
  }

  /** Set mute button pressed (specific because it has a different size) */
  this.setMuteButtonPressed = function setMuteButtonPressed(mediaButtonId) {
    $('#' + mediaButtonId).removeClass('btn-mute-unpressed');
    $('#' + mediaButtonId).addClass('btn-mute-pressed');
  }

  /** Set mute button unpressed (specific because it has a different size) */
  this.setMuteButtonUnpressed = function setMuteButtonUnpressed(mediaButtonId) {
    $('#' + mediaButtonId).removeClass('btn-mute-pressed');
    $('#' + mediaButtonId).addClass('btn-mute-unpressed');
  }

  /**
   * --------------------------------------------------------------------------
   * Setup infinite loops to sync vlcRcStatus and playlist and keep alive websocket.
   */
  /** 
   * Start infinite loop to pull VlcRcStatus from the server.
   * Break the loop setting isRunningSyncVlcRcStatusLoop to false.
   */
  this.syncVlcRcStatusLoop = async function syncVlcRcStatusLoop() {
    logger.debugFunctionCall();
    if (self.isRunningSyncVlcRcStatusLoop) {
      logger.error("syncVlcRcStatusLoop is already running");
      return;
    }
    self.isRunningSyncVlcRcStatusLoop = true;
    let vlcRcStatusPullWaitTimeMs = 1000;
    let failedCount = 0;
    while (self.isRunningSyncVlcRcStatusLoop) {
      if (self.isSyncEnabled()) {
        logger.trace("InfiniteLoop - vlcRcStatus:" + JSON.stringify(self.getVlcRcStatus()));
        if (self.isConnected()) {
          self.pollVlcRcStatus();
          if (!isEmpty(self.getVlcRcStatus().information)) {
            vlcRcStatusPullWaitTimeMs = 1000;
            failedCount = 0;
            logger.trace("Resetting failedCount and vlcRcStatusPullWaitTimeMs: failedCount:" + failedCount + ". vlcRcStatusPullWaitTimeMs:" + vlcRcStatusPullWaitTimeMs);
          } else {
            failedCount++;
            logger.trace("failedCount: " + failedCount);
            if (failedCount >= 10) {
              logger.trace("failedCount >= 10. Updating vlcRcStatusPullWaitTimeMs. failedCount:" + failedCount);
              vlcRcStatusPullWaitTimeMs = 5000;
            }
          }
        } else {
          vlcRcStatusPullWaitTimeMs = 3000;
          logger.trace("WebSocket is disconnected. Waiting " + vlcRcStatusPullWaitTimeMs + " ms to sync again.");
        }
      } else {
        vlcRcStatusPullWaitTimeMs = 3000;
        logger.trace("VlcPlayer sync is disabled. Waiting " + vlcRcStatusPullWaitTimeMs + " ms to sync again.")
      }
      await sleep(vlcRcStatusPullWaitTimeMs);
    }
  }

  /** 
   * Start infinite loop to sync the current playlist from the server.
   * Break the loop setting isRunningSyncPlaylistLoop to false.
   */
  this.syncPlaylistLoop = async function syncPlaylistLoop() {
    logger.debugFunctionCall();
    if (self.isRunningSyncPlaylistLoop) {
      logger.error("syncPlaylistLoop is already running");
      return;
    }
    self.isRunningSyncPlaylistLoop = true;
    let playlistSyncWaitTimeMs = 7000;
    while (self.isRunningSyncPlaylistLoop) {
      logger.trace("InfiniteLoop - synchronizing playlist:");
      self.reloadPlaylist();
      await sleep(playlistSyncWaitTimeMs);
    }
  }

  /** 
   * Start infinite loop to keep alive the websocket connection.
   * Break the loop setting isRunningSyncPlaylistLoop to false.
   */
  this.keepAliveWebSocketLoop = async function keepAliveWebSocketLoop() {
    logger.debugFunctionCall();
    if (self.isRunningKeepAliveWebSocketLoop) {
      logger.error("keepAliveWebSocketLoop is already running");
      return;
    }
    self.isRunningKeepAliveWebSocketLoop = true;
    let keepAliveWebSocketWaitTimeMs = 12000;
    while (self.isRunningKeepAliveWebSocketLoop) {
      await sleep(keepAliveWebSocketWaitTimeMs);
      if (!self.isConnected()) {
        logger.trace("WebSocket not connected. Reconnecting.");
        self.reconnect();
      } else {
        logger.trace("WebSocket still connected.");
      } 
    }
  }
}

/** 
 * Represents the Playlist component in vlc-player page. 
 * This prototype is meant to be instantiated by VlcPlayer() constructor
 * and added as a property to VlcPlayer.playlist inside that constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this playlist.
 * 
 * Dependencies: logger, apiCallTable
 * 
 * @author nbrest
 */
function VlcPlayerPlaylist(vlcPlayer) {
  let self = this;
  this.vlcPlayer = vlcPlayer;
  this.currentPlaylist = [];
  this.currentPlaylistId = null;

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

  /** Reload playlist after the specified ms. 
   * Call this function instead of reloadPlaylist() directly
   * if I need to give VLC player time to restart so I get the updated playlist. 
   * I will usually need this after a vlc_start. */
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
  this.highlightCurrentPlayingItem = function highlightCurrentPlayingItem(currentPlId) {
    if (!isEmpty(currentPlId)) {
      self.currentPlId = currentPlId;
    }
    logger.trace("currentPlId: " + self.currentPlId);
    // I can't use self in this case, I need to use this
    $('#playlist-table-body tr').each(function () {
      let playlistItemId = $(this).attr('id');
      if (playlistItemId == self.currentPlId) {
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
 * should be a reference to the enclosing VlcPlayer that contains this restClient.
 * 
 * Dependencies: logger, apiCallTable
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