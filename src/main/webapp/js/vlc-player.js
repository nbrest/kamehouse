/**
 * VLC Player page functions.
 * 
 * Dependencies: timeUtils, logger, apiCallTable.
 * 
 * @author nbrest
 */

/** ----- Global variables ---------------------------------------------------------------- */
global.vlcRcStatus = {};

global.videoPlaylists = [];
global.videoPlaylistCategories = [];
global.currentPlaylist = [];

global.stompClient = null;
global.isWebSocketConnected = false;
global.syncVlcStatus = true;

/** Main function. */
var main = function() {
  var loadingModules = ["timeUtils", "logger", "apiCallTable"];
  waitForModules(loadingModules, initVlcPlayer);
};

/** Init function to execute after global dependencies are loaded. */
var initVlcPlayer = function() {
  logger.info("Started initializing VLC Player");
  setupWebSocketForVlcStatus();
  updateVolumePercentage(document.getElementById("volume-slider").value);
  populateVideoPlaylistCategories();
  reloadPlaylist();
};

/** ---- General REST request functions --------------------------------------------- **/

/** Execute get on the specified url and display the output in the debug table. */
function doGet(url) {
  logger.debugFunctionCall();
  apiCallTable.get(url, null,
    function (responseBody, responseCode, responseDescription) {
      if (responseCode == "404") {
        apiCallTable.displayResponseData("Could not connect to VLC player to get the status.", responseCode);
      }
    });
}

/** Execute a POST request to the specified url with the specified request body. */
function doPost(url, requestBody) {
  logger.debugFunctionCall();
  apiCallTable.post(url, requestBody,
    function success(responseBody, responseCode, responseDescription) {
      logger.trace(JSON.stringify(responseBody, null, 2));
      getVlcRcStatus();
    }, null);
}

/** Execute a POST request to the specified url with the specified request url parameters. */
function doPostUrlEncoded(url, requestParam) {
  logger.debugFunctionCall();
  apiCallTable.postUrlEncoded(url, requestParam, 
    function success(responseBody, responseCode, responseDescription) {
      logger.trace(JSON.stringify(responseBody, null, 2));
      getVlcRcStatus();
    }, null);
}

/** Execute a DELETE request to the specified url with the specified request body. */
function doDelete(url, requestBody) {
  logger.debugFunctionCall();
  apiCallTable.delete(url, requestBody,
    function success(responseBody, responseCode, responseDescription) {
      logger.trace(JSON.stringify(responseBody));
      getVlcRcStatus();
      asyncReloadPlaylist(5000);
    }, null);
}

/** ---- Calls to rest functions to perform commands ------------------------------- **/

/** Create a vlcrc command with the parameters and execute the request to the server. */
function execVlcRcCommand(url, name) {
  logger.debugFunctionCall();
  var requestBody = {
    name: name
  };
  doPost(url, requestBody);
}

/** Create a vlcrc command with the parameters and execute the request to the server. */
function execVlcRcCommandWithValue(url, name, val) {
  logger.debugFunctionCall();
  var requestBody = {
    name: name,
    val: val
  };
  doPost(url, requestBody);
}

/** Reload VLC with the current selected playlist from the dropdowns. */
function loadSelectedPlaylist(url) {
  logger.debugFunctionCall();
  var playlistSelected = document.getElementById("playlist-dropdown").value;
  logger.debug("Playlist selected: " + playlistSelected);
  var requestParam = "file=" + playlistSelected;
  doPostUrlEncoded(url, requestParam);
  // Wait a few seconds for Vlc Player to restart and reload the playlist.
  asyncReloadPlaylist(5000);
}

/** ---- Populate playlists functions --------------------------------------------- **/

/** Populate playlist categories dropdown. */
function populateVideoPlaylistCategories() {
  logger.debugFunctionCall();
  let playlistDropdown = $('#playlist-dropdown');
  playlistDropdown.empty();
  playlistDropdown.append('<option selected="true" disabled>Playlist</option>');
  playlistDropdown.prop('selectedIndex', 0);
  let playlistCategoryDropdown = $('#playlist-category-dropdown');
  playlistCategoryDropdown.empty();
  playlistCategoryDropdown.append('<option selected="true" disabled>Playlist Category</option>');
  playlistCategoryDropdown.prop('selectedIndex', 0);
  var url = '/kame-house/api/v1/media/video/playlists';
  apiCallTable.get(url,
    function (responseBody, responseCode, responseDescription) {
      global.videoPlaylists = responseBody;
      setVideoPlaylistCategories(global.videoPlaylists);
      logger.trace("playlists: " + JSON.stringify(global.videoPlaylists));
      $.each(global.videoPlaylistCategories, function (key, entry) {
        var category = entry;
        var categoryFormatted = category.replace(/\\/g, ' | ').replace(/\//g, ' | ');
        playlistCategoryDropdown.append($('<option></option>').attr('value', entry).text(categoryFormatted));
      });
    },
    function (responseBody, responseCode, responseDescription) { 
      apiCallTable.displayResponseData("Error populating video playlist categories", responseCode);
    });
}

/** Set video playlist categories. */
function setVideoPlaylistCategories(videoPlaylists) {
  global.videoPlaylistCategories = [...new Set(videoPlaylists.map(playlist => playlist.category))];
  logger.trace("Playlist categories: " + global.videoPlaylistCategories);
}

/** Populate video playlists dropdown. */
function populateVideoPlaylists() {
  logger.debugFunctionCall();
  var playlistCategoriesList = document.getElementById('playlist-category-dropdown');
  var selectedPlaylistCategory = playlistCategoriesList.options[playlistCategoriesList.selectedIndex].value;
  let playlistDropdown = $('#playlist-dropdown');
  playlistDropdown.empty();
  playlistDropdown.append('<option selected="true" disabled>Playlist</option>');
  playlistDropdown.prop('selectedIndex', 0);
  logger.debug("Selected PlaylistCategory: " + selectedPlaylistCategory);
  $.each(global.videoPlaylists, function (key, entry) {
    if (entry.category === selectedPlaylistCategory) {
      var playlistName = entry.name;
      playlistName = playlistName.replace(/.m3u+$/, "");
      playlistDropdown.append($('<option></option>').attr('value', entry.path).text(playlistName));
    }
  });
}

/** ---- Websockets functions --------------------------------------------- **/

/** Setup the websocket for pulling VlcRcStatus. */
function setupWebSocketForVlcStatus() {
  logger.debugFunctionCall();
  connectWebSocket();
  startPullVlcRcStatusLoop();
}

/** Connect the websocket. */
function connectWebSocket() {
  logger.debugFunctionCall();
  var socket = new SockJS('/kame-house/api/ws/vlc-player/status');
  global.stompClient = Stomp.over(socket);
  //Disable console messages for stomp. Only enable if I need to debug connection issues.
  global.stompClient.debug = null;
  global.stompClient.connect({}, function (frame) {
    logger.debug('Connected WebSocket: ' + frame);
    global.isWebSocketConnected = true;
    global.stompClient.subscribe('/topic/vlc-player/status-out', function (vlcRcStatusResponse) {
      updateVlcPlayerStatus(JSON.parse(vlcRcStatusResponse.body));
    });
  });
}

/** Disconnect the websocket. */
function disconnectWebSocket() {
  logger.debugFunctionCall();
  if (!isEmpty(global.stompClient)) {
    global.stompClient.disconnect();
    global.isWebSocketConnected = false;
  }
}

/** ---- Update vlc player status functions --------------------------------------------- **/

/** Poll for an updated VlcRcStatus from the server. */
function getVlcRcStatus() {
  // Setting this as trace as it executes every second so if I want to debug other stuff it's noisy.
  logger.traceFunctionCall();
  global.stompClient.send("/app/vlc-player/status-in", {});
}

/** Start infinite loop to pull VlcRcStatus from the server. */
async function startPullVlcRcStatusLoop() {
  logger.debugFunctionCall();
  // Infinite loop to pull VlcRcStatus every 1 second, switch to vlcRcStatusPullWaitTimeMs seconds 
  // if I'm not playing anything.
  global.infiniteVlcRcStatusLoop = true;
  var vlcRcStatusPullWaitTimeMs = 1000;
  let failedCount = 0;
  if (global.isWebSocketConnected) {
    getVlcRcStatus();
  }
  while (global.infiniteVlcRcStatusLoop) {
    await sleep(vlcRcStatusPullWaitTimeMs);
    if (global.syncVlcStatus) {
      logger.trace("pullVlcRcStatusLoop(): vlcRcStatus:" + JSON.stringify(global.vlcRcStatus));
      if (global.isWebSocketConnected) {
        getVlcRcStatus();
      }
      if (!isEmpty(global.vlcRcStatus.information)) {
        vlcRcStatusPullWaitTimeMs = 1000;
        failedCount = 0;
      } else {
        failedCount++;
        if (failedCount >= 10) {
          vlcRcStatusPullWaitTimeMs = 4000;
        }
      }
    }
  }
}

/** Update vlc player status based on the VlcRcStatus object. */
function updateVlcPlayerStatus(vlcRcStatusResponse) {
  logger.traceFunctionCall();
  global.vlcRcStatus = vlcRcStatusResponse;

  // Update media title.
  var mediaName = getMediaName();
  $("#media-title").text(mediaName.filename);

  // Update media playing time
  if (!isEmpty(global.vlcRcStatus.time)) {
    $("#current-time").text(timeUtils.convertSecondsToHsMsSs(global.vlcRcStatus.time));
    $("#time-slider").val(global.vlcRcStatus.time);

    $("#total-time").text(timeUtils.convertSecondsToHsMsSs(global.vlcRcStatus.length));
    $("#time-slider").attr('max', global.vlcRcStatus.length);
  }

  // Update volume percentage and slider.
  if (!isEmpty(global.vlcRcStatus.volume)) {
    $("#volume-slider").val(global.vlcRcStatus.volume);
    updateVolumePercentage(global.vlcRcStatus.volume);
  }

  // Update media buttons with state
  updateMediaButtonsWithState();

  highlightCurrentPlayingItemInPlaylist(global.vlcRcStatus.currentPlId);
}

/** Set the current time from the slider's value. */
function setTimeFromSlider(value) {
  $("#current-time").text(timeUtils.convertSecondsToHsMsSs(value));
  execVlcRcCommandWithValue('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'seek', value);
}
/** Update the displayed current time while I'm sliding */
function updateTimeWhileSliding(value) {
  logger.trace("Current time: " + value);
  var currentTime = document.getElementById("current-time");
  currentTime.innerHTML = timeUtils.convertSecondsToHsMsSs(value);
}

/** Get media name from VlcRcStatus. */
function getMediaName() {
  var mediaNameLocal = {};
  mediaNameLocal.filename = "No media loaded";
  mediaNameLocal.title = "No media loaded";
  if (!isEmpty(global.vlcRcStatus.information)) {
    mediaNameLocal.filename = global.vlcRcStatus.information.meta.filename;
    mediaNameLocal.title = global.vlcRcStatus.information.meta.title;
  }
  return mediaNameLocal;
}

/** Set the volume from the slider's value. */
function setVolumeFromSlider(value) {
  logger.trace("Current volume value: " + value);
  updateVolumePercentage(value);
  execVlcRcCommandWithValue('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'volume', value);
}

/** Update volume percentage to display with the specified value. */
function updateVolumePercentage(value) {
  let volumePercentaje = Math.floor(value * 200 / 512);
  var currentVolume = document.getElementById("current-volume");
  currentVolume.innerHTML = volumePercentaje + "%";
}

/** Update vlc player media buttons that have state, based on vlcRcStatus. */
function updateMediaButtonsWithState() {
  // Update aspect-ratio 16:9 button
  if (global.vlcRcStatus.aspectRatio == "16:9") {
    setMediaButtonPressed('media-btn-aspect-ratio-16-9');
  } else {
    setMediaButtonUnpressed('media-btn-aspect-ratio-16-9');
  }

  // Update aspect-ratio 4:3 button
  if (global.vlcRcStatus.aspectRatio == "4:3") {
    setMediaButtonPressed('media-btn-aspect-ratio-4-3');
  } else {
    setMediaButtonUnpressed('media-btn-aspect-ratio-4-3');
  }

  // Update fullscreen button
  if (global.vlcRcStatus.fullscreen) {
    setMediaButtonPressed('media-btn-fullscreen');
  } else {
    setMediaButtonUnpressed('media-btn-fullscreen');
  }

  // Update mute button
  if (global.vlcRcStatus.volume == 0) {
    setMuteButtonPressed('media-btn-mute');
  } else {
    setMuteButtonUnpressed('media-btn-mute');
  }

  // Update repeat 1 button
  if (global.vlcRcStatus.repeat) {
    setMediaButtonPressed('media-btn-repeat-1');
  } else {
    setMediaButtonUnpressed('media-btn-repeat-1');
  }

  // Update repeat all button
  if (global.vlcRcStatus.loop) {
    setMediaButtonPressed('media-btn-repeat');
  } else {
    setMediaButtonUnpressed('media-btn-repeat');
  }

  // Update shuffle button
  if (global.vlcRcStatus.random) {
    setMediaButtonPressed('media-btn-shuffle');
  } else {
    setMediaButtonUnpressed('media-btn-shuffle');
  }

  // Update stop button
  if (global.vlcRcStatus.state == "stopped") {
    setMediaButtonPressed('media-btn-stop');
  } else {
    setMediaButtonUnpressed('media-btn-stop');
  }
}

/** Set media button pressed */
function setMediaButtonPressed(mediaButtonId) {
  $('#' + mediaButtonId).removeClass('media-btn-unpressed');
  $('#' + mediaButtonId).addClass('media-btn-pressed');
}

/** Set media button unpressed */
function setMediaButtonUnpressed(mediaButtonId) {
  $('#' + mediaButtonId).removeClass('media-btn-pressed');
  $('#' + mediaButtonId).addClass('media-btn-unpressed');
}

/** Set mute button pressed (specific because it has a different size) */
function setMuteButtonPressed(mediaButtonId) {
  $('#' + mediaButtonId).removeClass('btn-mute-unpressed');
  $('#' + mediaButtonId).addClass('btn-mute-pressed');
}

/** Set mute button unpressed (specific because it has a different size) */
function setMuteButtonUnpressed(mediaButtonId) {
  $('#' + mediaButtonId).removeClass('btn-mute-pressed');
  $('#' + mediaButtonId).addClass('btn-mute-unpressed');
}

/** ----- Display playlist functions ----------------------------------------------------------- **/

/** Reload current playlist from server. */
function reloadPlaylist() {
  var getPlaylistUrl = '/kame-house/api/v1/vlc-rc/players/localhost/playlist';
  logger.debug("Reloading playlist");
  apiCallTable.get(getPlaylistUrl,
    function (responseBody, responseCode, responseDescription) {
      displayPlaylist(responseBody);
      logger.debug("playlist: " + JSON.stringify(responseBody));
    },
    function (responseBody, responseCode, responseDescription) { 
      displayPlaylist();
      if (responseCode == "404") {
        apiCallTable.displayResponseData("Could not connect to VLC player to get the current playlist.", responseCode);
      }
    }
  );
}

/** Reload playlist after the specified ms. 
 * Call this function instead of reloadPlaylist() directly
 * if I need to give VLC player time to restart so I get the updated playlist. 
 * I will usually need this after a vlc_start. */
async function asyncReloadPlaylist(sleepTime) {
  logger.debugFunctionCall();
  await sleep(sleepTime);
  reloadPlaylist();
}

/** Display playlist. */
function displayPlaylist(playlistArray) {
  logger.debugFunctionCall();
  global.currentPlaylist = playlistArray;
  // Clear playlist content, if it has.
  emptyPlaylistTableBody();
  // Add the new playlist items received from the server.
  var $playlistTableBody = $('#playlist-table-body');
  var playlistTableRow;
  if (isEmpty(global.currentPlaylist)) {
    playlistTableRow = $('<tr>').append($('<td>').text("No playlist loaded yet. Mada mada dane :)"));
    $playlistTableBody.append(playlistTableRow);
  } else {
    for (var i = 0; i < global.currentPlaylist.length; i++) {
      var playlistElementButton = $('<button>');
      playlistElementButton.addClass("btn btn-outline-danger btn-borderless btn-playlist");
      playlistElementButton.text(global.currentPlaylist[i].name);
      playlistElementButton.click({
        id: global.currentPlaylist[i].id
      }, clickEventOnPlaylistRow);
      playlistTableRow = $('<tr id=' + global.currentPlaylist[i].id + '>').append($('<td>').append(playlistElementButton));
      $playlistTableBody.append(playlistTableRow);
    }
    highlightCurrentPlayingItemInPlaylist(global.vlcRcStatus.currentPlId);
  }
}

/** Highlight currently playing item in the playlist. Only do the update if the playlist is not collapsed. */
function highlightCurrentPlayingItemInPlaylist(currentPlId) {
  var isPlaylistCurrentlyVisible = $('#playlist-collapsible').hasClass("playlist-collapsible-active");
  if (isPlaylistCurrentlyVisible) {
    logger.trace("currentPlId: " + currentPlId);
    $('#playlist-table-body tr').each(function() {
      var playlistItemId = $(this).attr('id');
      if (playlistItemId == currentPlId) {
        $(this).children().children().addClass("playlist-table-element-playing");
      } else {
        $(this).children().children().removeClass("playlist-table-element-playing");
      }
    });
  }
}

/** Play the clicked element from the playlist. */
function clickEventOnPlaylistRow(event) {
  logger.debug("Play playlist id: " + event.data.id);
  var requestBody = {
    name: 'pl_play',
    id: event.data.id
  };
  doPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', requestBody);
}

/** Empty Playlist table body. */
function emptyPlaylistTableBody() {
  $("#playlist-table-body").empty();
}

/** Toggle playlist collapsible active status and show or hide collapsible content. */
// For an example of a collapsible element that expands to full vertical height, 
// check the api-call-table example in the test-apis page. This one expands to a fixed height.
function toggleShowOrHidePlaylistContent() {
  logger.debug("Clicked playlist button");
  var playlistCollapsibleButton = document.getElementById("playlist-collapsible");
  playlistCollapsibleButton.classList.toggle("playlist-collapsible-active");

  var playlistCollapsibleContent = document.getElementById("playlist-collapsible-content");
  playlistCollapsibleContent.classList.toggle("playlist-collapsible-content-active");
}

/** ----- Debug mode functions ----------------------------------------------------------- */

/** Toggle debug mode. */
function toggleDebugMode() {
  logger.debug("Toggled debug mode")
  var debugModeDiv = document.getElementById("debug-mode");
  debugModeDiv.classList.toggle("hidden-kh");
}

/** Call main. */
$(document).ready(main);