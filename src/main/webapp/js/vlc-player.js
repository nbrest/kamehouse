/**
 * VLC Player page functions.
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
var main = function () {
  log("INFO", "Started initializing VLC Player");
  setupWebSocketForVlcStatus();
  updateVolumePercentage(document.getElementById("volume-slider").value);
  populateVideoPlaylistCategories();
  reloadPlaylist();
};

/** ---- General REST request functions --------------------------------------------- **/

/** Execute get on the specified url and display the output in the debug table. */
function doGet(url) {
  log("DEBUG", "Executing GET on " + url);
  var requestTimestamp = getTimestamp();
  displayRequestPayload(requestTimestamp, url, "GET", null, null, null);
  $.get(url)
    .success(function (result) {
      displayRequestPayload(requestTimestamp, url, "GET", null, getTimestamp(), result);
    })
    .error(function (jqXHR, textStatus, errorThrown) {
      log("ERROR", JSON.stringify(jqXHR));
      if (jqXHR.status == "404") {
        displayErrorExecutingRequest("Could not connect to VLC player to get the status.");
      } else {
        displayErrorExecutingRequest();
      }
    });
  setCollapsibleContent();
}

/** Execute a POST request to the specified url with the specified request body. */
function doPost(url, requestBody) {
  log("DEBUG", "Executing POST on " + url + " with requestBody " + JSON.stringify(requestBody));
  var requestTimestamp = getTimestamp();
  displayRequestPayload(requestTimestamp, url, "POST", requestBody, null, null);
  var requestHeaders = getApplicationJsonHeaders();
  $.ajax({
    type: "POST",
    url: url,
    data: JSON.stringify(requestBody),
    headers: requestHeaders,
    success: function (data) {
      log("TRACE", JSON.stringify(data, null, 2));
      getVlcRcStatus();
      displayRequestPayload(requestTimestamp, url, "POST", requestBody, getTimestamp(), data);
    },
    error: function (data) {
      log("ERROR", JSON.stringify(data));
      displayErrorExecutingRequest();
    }
  });
  setCollapsibleContent();
}

/** Execute a POST request to the specified url with the specified request url parameters. */
function doPostUrlEncoded(url, requestParam) {
  log("DEBUG", "Executing POST on " + url + " with requestParam " + JSON.stringify(requestParam));
  var requestTimestamp = getTimestamp();
  displayRequestPayload(requestTimestamp, url, "POST", requestParam, null, null);
  var requestHeaders = getUrlEncodedHeaders();
  $.ajax({
    type: "POST",
    url: url,
    data: requestParam,
    headers: requestHeaders,
    success: function (data) {
      log("TRACE", JSON.stringify(data, null, 2));
      getVlcRcStatus();
      displayRequestPayload(requestTimestamp, url, "POST", requestParam, getTimestamp(), data);
    },
    error: function (data) {
      log("ERROR", JSON.stringify(data));
      displayErrorExecutingRequest();
    }
  });
  setCollapsibleContent();
}

/** Execute a DELETE request to the specified url with the specified request body. */
function doDelete(url, requestBody) {
  log("DEBUG", "Executing DELETE on " + url + " with requestBody " + JSON.stringify(requestBody));
  var requestTimestamp = getTimestamp();
  displayRequestPayload(requestTimestamp, url, "DELETE", requestBody, null, null);
  var requestHeaders = getApplicationJsonHeaders();
  $.ajax({
    type: "DELETE",
    url: url,
    data: requestBody,
    headers: requestHeaders,
    success: function (data) {
      log("TRACE", JSON.stringify(data));
      getVlcRcStatus();
      asyncReloadPlaylist(5000);
      displayRequestPayload(requestTimestamp, url, "DELETE", requestBody, getTimestamp(), data);
    },
    error: function (data) {
      log("ERROR", JSON.stringify(data));
      displayErrorExecutingRequest();
    }
  });
}

/** ---- Calls to rest functions to perform commands ------------------------------- **/

/** Create a vlcrc command with the parameters and execute the request to the server. */
function execVlcRcCommand(url, name) {
  var requestBody = {
    name: name
  };
  doPost(url, requestBody);
}

/** Create a vlcrc command with the parameters and execute the request to the server. */
function execVlcRcCommandWithValue(url, name, val) {
  var requestBody = {
    name: name,
    val: val
  };
  doPost(url, requestBody);
}

/** Reload VLC with the current selected playlist from the dropdowns. */
function loadSelectedPlaylist(url) {
  var playlistSelected = document.getElementById("playlist-dropdown").value;
  log("DEBUG", "Playlist selected: " + playlistSelected);
  var requestParam = "file=" + playlistSelected;
  doPostUrlEncoded(url, requestParam);
  // Wait a few seconds for Vlc Player to restart and reload the playlist.
  asyncReloadPlaylist(5000);
}

/** ---- Populate playlists functions --------------------------------------------- **/

/** Populate playlist categories dropdown. */
function populateVideoPlaylistCategories() {
  let playlistDropdown = $('#playlist-dropdown');
  playlistDropdown.empty();
  playlistDropdown.append('<option selected="true" disabled>Playlist</option>');
  playlistDropdown.prop('selectedIndex', 0);
  let playlistCategoryDropdown = $('#playlist-category-dropdown');
  playlistCategoryDropdown.empty();
  playlistCategoryDropdown.append('<option selected="true" disabled>Playlist Category</option>');
  playlistCategoryDropdown.prop('selectedIndex', 0);

  $.get('/kame-house/api/v1/media/video/playlists')
    .success(function (result) {
      global.videoPlaylists = result;
      setVideoPlaylistCategories(global.videoPlaylists);
      log("TRACE", JSON.stringify(global.videoPlaylists));
      $.each(global.videoPlaylistCategories, function (key, entry) {
        var category = entry;
        var categoryFormatted = category.replace(/\\/g, ' | ').replace(/\//g, ' | ');
        playlistCategoryDropdown.append($('<option></option>').attr('value', entry).text(categoryFormatted));
      });
    })
    .error(function (jqXHR, textStatus, errorThrown) {
      log("ERROR", JSON.stringify(jqXHR));
      displayErrorExecutingRequest();
    });
}

/** Set video playlist categories. */
function setVideoPlaylistCategories(videoPlaylists) {
  global.videoPlaylistCategories = [...new Set(videoPlaylists.map(playlist => playlist.category))];
  log("TRACE", global.videoPlaylistCategories);
}

/** Populate video playlists dropdown. */
function populateVideoPlaylists() {
  var playlistCategoriesList = document.getElementById('playlist-category-dropdown');
  var selectedPlaylistCategory = playlistCategoriesList.options[playlistCategoriesList.selectedIndex].value;
  let playlistDropdown = $('#playlist-dropdown');
  playlistDropdown.empty();
  playlistDropdown.append('<option selected="true" disabled>Playlist</option>');
  playlistDropdown.prop('selectedIndex', 0);
  log("DEBUG", "Selected PlaylistCategory: " + selectedPlaylistCategory);
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
  connectWebSocket();
  pullVlcRcStatusLoop();
}

/** Connect the websocket. */
function connectWebSocket() {
  var socket = new SockJS('/kame-house/api/ws/vlc-player/status');
  global.stompClient = Stomp.over(socket);
  //Disable console messages for stomp. Only enable if I need to debug connection issues.
  global.stompClient.debug = null;
  global.stompClient.connect({}, function (frame) {
    log("DEBUG", 'Connected WebSocket: ' + frame);
    global.isWebSocketConnected = true;
    global.stompClient.subscribe('/topic/vlc-player/status-out', function (vlcRcStatusResponse) {
      updateVlcPlayerStatus(JSON.parse(vlcRcStatusResponse.body));
    });
  });
}

/** Disconnect the websocket. */
function disconnectWebSocket() {
  if (!isEmpty(global.stompClient)) {
    global.stompClient.disconnect();
    global.isWebSocketConnected = false;
  }
  log("DEBUG", "Disconnected WebSocket");
}

/** ---- Update vlc player status functions --------------------------------------------- **/

/** Poll for an updated VlcRcStatus from the server. */
function getVlcRcStatus() {
  // Setting this as trace as it executes every second so if I want to debug other stuff it's noisy.
  log("TRACE", "Requesting vlc-rc status");
  global.stompClient.send("/app/vlc-player/status-in", {});
}

/** Infinite loop to pull VlcRcStatus from the server. */
async function pullVlcRcStatusLoop() {

  // Infinite loop to pull VlcRcStatus every 1 second, switch to vlcRcStatusPullWaitTimeMs seconds 
  // if I'm not playing anything.
  var vlcRcStatusPullWaitTimeMs = 1000;
  let failedCount = 0;
  if (global.isWebSocketConnected) {
    getVlcRcStatus();
  }
  while (global.syncVlcStatus) {
    await sleep(vlcRcStatusPullWaitTimeMs);
    log("TRACE", "pullVlcRcStatusLoop(): vlcRcStatus:" + JSON.stringify(global.vlcRcStatus));
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

/** Update vlc player status based on the VlcRcStatus object. */
function updateVlcPlayerStatus(vlcRcStatusResponse) {
  global.vlcRcStatus = vlcRcStatusResponse;
  log("TRACE", "vlcRcStatusResponse: " + JSON.stringify(global.vlcRcStatus));

  // Update media title.
  var mediaName = getMediaName();
  $("#media-title").text(mediaName.filename);

  // Update media playing time
  if (!isEmpty(global.vlcRcStatus.time)) {
    $("#current-time").text(convertSecondsToHsMsSs(global.vlcRcStatus.time));
    $("#time-slider").val(global.vlcRcStatus.time);

    $("#total-time").text(convertSecondsToHsMsSs(global.vlcRcStatus.length));
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
  $("#current-time").text(convertSecondsToHsMsSs(value));
  execVlcRcCommandWithValue('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'seek', value);
}
/** Update the displayed current time while I'm sliding */
function updateTimeWhileSliding(value) {
  log("TRACE", "Current time: " + value);
  var currentTime = document.getElementById("current-time");
  currentTime.innerHTML = convertSecondsToHsMsSs(value);
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
  log("TRACE", "Current volume value: " + value);
  updateVolumePercentage(value);
  execVlcRcCommandWithValue('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'volume', value);
}

/** Update volume percentage to display with the specified value. */
function updateVolumePercentage(value) {
  let volumePercentaje = Math.floor(value * 200 / 512);
  var currentVolume = document.getElementById("current-volume");
  currentVolume.innerHTML = volumePercentaje + "%";
}

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
  log("DEBUG", "Reloading playlist");
  var requestTimestamp = getTimestamp();
  displayRequestPayload(requestTimestamp, getPlaylistUrl, "GET", null, null, null);
  $.get(getPlaylistUrl)
    .success(function (result) {
      displayPlaylist(result);
      displayRequestPayload(requestTimestamp, getPlaylistUrl, "GET", null, getTimestamp(), result);
    })
    .error(function (jqXHR, textStatus, errorThrown) {
      log("ERROR", JSON.stringify(jqXHR));
      if (jqXHR.status == "404") {
        displayErrorGettingPlaylist("Could not connect to VLC player to get the current playlist.");
      } else {
        displayErrorGettingPlaylist();
      }
    });
}

/** Reload playlist after the specified ms. 
 * Call this function instead of reloadPlaylist() directly
 * if I need to give VLC player to restart so I get the updated playlist. 
 * I will usually need this after a vlc_start. */
async function asyncReloadPlaylist(sleepTime) {
  await sleep(sleepTime);
  reloadPlaylist();
}

/** Display playlist. */
function displayPlaylist(playlistArray) {
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
    log("TRACE", "currentPlId: " + currentPlId);
    $('#playlist-table-body tr').each(function () {
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
  log("DEBUG", "Play playlist id: " + event.data.id);
  var requestBody = {
    name: 'pl_play',
    id: event.data.id
  };
  doPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', requestBody);
}

/** Display error getting playlist from the server. */
function displayErrorGettingPlaylist(errorMessage) {
  displayPlaylist();
  displayErrorExecutingRequest(errorMessage);
}

/** Empty Playlist table body. */
function emptyPlaylistTableBody() {
  $("#playlist-table-body").empty();
}

/** Toggle playlist collapsible active status and show or hide collapsible content. */
// For an example of a collapsible element that expands to full vertical height, 
// check the api-call-output example in the test-apis page. This one expands to a fixed height.
function toggleShowOrHidePlaylistContent() {
  log("DEBUG", "Clicked playlist button");
  var playlistCollapsibleButton = document.getElementById("playlist-collapsible");
  playlistCollapsibleButton.classList.toggle("playlist-collapsible-active");

  var playlistCollapsibleContent = document.getElementById("playlist-collapsible-content");
  playlistCollapsibleContent.classList.toggle("playlist-collapsible-content-active");
}

/** ----- Debug mode functions ----------------------------------------------------------- */

/** Toggle debug mode. */
function toggleDebugMode() {
  log("DEBUG", "Toggled debug mode")
  var debugModeDiv = document.getElementById("debug-mode");
  debugModeDiv.classList.toggle("hidden-kh");
}

/** Call main. */
$(document).ready(main);