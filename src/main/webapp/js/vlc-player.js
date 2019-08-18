/**
 * VLC Player page functions.
 * 
 * @author nbrest
 */

/** Global variables. */
var videoPlaylists = [];
var videoPlaylistCategories = [];

var stompClient = null;
var vlcRcStatus = {};
var isWebSocketConnected = false; 
var isPlaying = true;

var currentPlaylist = [];

/** Main function. */
var main = function() { 
  setupWebSocketForVlcStatus();
  updateVolumePercentage(document.getElementById("volume-slider").value);
  populateVideoPlaylistCategories();
  reloadPlaylist();
};

/** ---- General REST request functions --------------------------------------------- **/

/** Reload VLC with the current selected playlist from the dropdowns. */
function executeAdminVlcPostWithSelectedPlaylist(url, command) {
  var playlistSelected = document.getElementById("playlist-dropdown").value;
  //console.debug("playlistSelected " + playlistSelected);
  var requestBody = {
    command: command,
    file: playlistSelected
  };
  executePost(url, requestBody);
}

/** Create a vlcrc command with the parameters and execute the request to the server. */
function executeVlcRcCommandPost(url, name) {
  var requestBody = {
    name: name
  };
  executePost(url, requestBody);
}

/** Create a vlcrc command with the parameters and execute the request to the server. */
function executeVlcRcCommandPost(url, name, val) {
  var requestBody = {
    name: name,
    val: val
  };
  executePost(url, requestBody);
}

/** Execute a POST request to the specified url with the specified request body. */
function executePost(url, requestBody) {
  //console.debug(getTimestamp() + " : Executing POST on " + url);
  requestHeaders = getCsrfRequestHeadersObject();
  $.ajax({
    type: "POST",
    url: url,
    data: JSON.stringify(requestBody),
    headers: requestHeaders,
    success: function(data) {
      //console.debug(JSON.stringify(data));
      //console.debug(JSON.stringify(data, null, 2));
      getVlcRcStatus(); 
      if ((!isEmpty(requestBody.command) && requestBody.command == 'vlc_start') || 
          (!isEmpty(requestBody.name) && requestBody.name == 'pl_stop')) {
        // If command is vlc_start or pl_stop I'm restarting vlc or stopping it. 
        // Relad playlist (after a few seconds to give it time to restart vlc).
        asyncReloadPlaylist(5000);
      } 
      displayRequestPayload(data, url, "POST", requestBody);
    },
    error: function(data) {
      console.error(JSON.stringify(data));
      displayErrorExecutingRequest(); 
    }
    });
  setCollapsibleContent();
}

/** Execute a DELETE request to the specified url with the specified request body. */
function executeDelete(url, requestBody) {
  //console.debug(getTimestamp() + " : Executing DELETE on " + url);
  requestHeaders = getCsrfRequestHeadersObject();
  $.ajax({
    type: "DELETE",
    url: url,
    data: requestBody,
    headers: requestHeaders,
    success: function(data) {
      //console.debug(JSON.stringify(data));  
      getVlcRcStatus();
      asyncReloadPlaylist(5000);
      displayRequestPayload(data, url, "DELETE", requestBody);
    },
    error: function(data) {
      console.error(JSON.stringify(data));
      displayErrorExecutingRequest(); 
    }
  }); 
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
    .success(function(result) { 
      videoPlaylists = result;
      setVideoPlaylistCategories(videoPlaylists);
      //console.debug(JSON.stringify(videoPlaylists));
      $.each(videoPlaylistCategories, function (key, entry) {
        var category = entry;
        var categoryFormatted = category.replace(/\\/g, ' | ').replace(/\//g, ' | ');
        playlistCategoryDropdown.append($('<option></option>').attr('value', entry).text(categoryFormatted));
      });
    })
    .error(function(jqXHR, textStatus, errorThrown) {
      console.error(JSON.stringify(jqXHR));
      displayErrorExecutingRequest();
    }); 
}

/** Set video playlist categories. */
function setVideoPlaylistCategories(videoPlaylists) {
  videoPlaylistCategories = [...new Set(videoPlaylists.map(playlist => playlist.category))];
  //console.debug(videoPlaylistCategories);  
}

/** Populate video playlists dropdown. */
function populateVideoPlaylists() {
  var playlistCategoriesList = document.getElementById('playlist-category-dropdown');
  var selectedPlaylistCategory = playlistCategoriesList.options[playlistCategoriesList.selectedIndex].value;
  let playlistDropdown = $('#playlist-dropdown');
  playlistDropdown.empty();
  playlistDropdown.append('<option selected="true" disabled>Playlist</option>');
  playlistDropdown.prop('selectedIndex', 0);
  //console.debug("selectedPlaylistCategory " + selectedPlaylistCategory);
  $.each(videoPlaylists, function (key, entry) {
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
  stompClient = Stomp.over(socket);
  //Disable console messages for stomp. Only enable to debug.
  stompClient.debug = null;
  stompClient.connect({}, function (frame) { 
      //console.log('Connected WebSocket: ' + frame);
      isWebSocketConnected = true;
      stompClient.subscribe('/topic/vlc-player/status-out', function (vlcRcStatusResponse) {
        updateVlcPlayerStatus(JSON.parse(vlcRcStatusResponse.body));
      });
  });
}

/** Disconnect the websocket. */
function disconnectWebSocket() {
    if (!isEmpty(stompClient)) {
        stompClient.disconnect();
        isWebSocketConnected = false;
    }
    console.log("Disconnected WebSocket");
}

/** ---- Update vlc player status functions --------------------------------------------- **/

/** Poll for an updated VlcRcStatus from the server. */
function getVlcRcStatus() {
  //console.log("Requesting vlc-rc status");
  stompClient.send("/app/vlc-player/status-in", {});
}

/** Infinite loop to pull VlcRcStatus from the server. */
async function pullVlcRcStatusLoop() {
  
  // Infinite loop to pull VlcRcStatus every 1 second, switch to XX seconds if I'm not playing anything.
  var vlcRcStatusPullWaitTimeMs = 1000;
  let failedCount = 0;
  // TODO: Make the client side contain a status of when vlc player is actually running on the server and only pull when it's running.
  if (isWebSocketConnected) {
    getVlcRcStatus();
  } 
  for ( ; ; ) { 
    await sleep(vlcRcStatusPullWaitTimeMs); 
    if (isWebSocketConnected && isPlaying) {
      getVlcRcStatus();
    }  
    if (!isEmpty(vlcRcStatus.information)) {
      //isPlaying = true;
      vlcRcStatusPullWaitTimeMs = 1000;
      failedCount = 0;
    } else {
      //isPlaying = false;
      failedCount++;
      if (failedCount >= 10) {
        vlcRcStatusPullWaitTimeMs = 15000;
      }
    } 
  }
}

/** Update vlc player status based on the VlcRcStatus object. */
function updateVlcPlayerStatus(vlcRcStatusResponse) {
  vlcRcStatus = vlcRcStatusResponse;
  //console.log("vlcRcStatusResponse: " + JSON.stringify(vlcRcStatus));
  
  // Update media title.
  mediaName = getMediaName(); 
  $("#media-title").text(mediaName.filename);
  
  // Update media playing time
  if (!isEmpty(vlcRcStatus.time)) { 
    $("#current-time").text(convertSecondsToHsMsSs(vlcRcStatus.time));
    $("#time-slider").val(vlcRcStatus.time);
    
    $("#total-time").text(convertSecondsToHsMsSs(vlcRcStatus.length)); 
    $("#time-slider").attr('max', vlcRcStatus.length);
  } 
  
  // Update volume percentage and slider.
  if (!isEmpty(vlcRcStatus.volume)) {
    $("#volume-slider").val(vlcRcStatus.volume);
    updateVolumePercentage(vlcRcStatus.volume);
  } 
}

/** Set the current time from the slider's value. */
function setTimeFromSlider(value) {
  $("#current-time").text(convertSecondsToHsMsSs(value)); 
  executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'seek', value);
}
/** Update the displayed current time while I'm sliding */
function updateTimeWhileSliding(value) {
  console.log("current time: " + value);
  $("#current-time").text(convertSecondsToHsMsSs(value));  
}

/** Get media name from VlcRcStatus. */
function getMediaName() {
  var mediaName = {};
  mediaName.filename = "No media loaded";
  mediaName.title = "No media loaded";
  if (!isEmpty(vlcRcStatus.information)) {
      vlcRcStatus.information.category.forEach(function (category) {
        if (!isEmpty(category.filename)) {
          mediaName.filename = category.filename;
          mediaName.title = category.title;
        }
    });
  }  
  return mediaName;
}

/** Set the volume from the slider's value. */
function setVolumeFromSlider(value) {
  //console.log("Current volume value " + value); 
  updateVolumePercentage(value);
	executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'volume', value);
}

/** Update volume percentage to display with the specified value. */
function updateVolumePercentage(value) {
  let volumePercentaje = Math.floor(value * 200/512);
  var currentVolume = document.getElementById("current-volume"); 
  currentVolume.innerHTML = volumePercentaje + "%";
}

/** ----- Display playlist functions ----------------------------------------------------------- **/

/** Reload current playlist from server. */
function reloadPlaylist() {
  var getPlaylistUrl = '/kame-house/api/v1/vlc-rc/players/localhost/playlist';
  //console.debug(getTimestamp() + " : Reloading playlist");
  $.get(getPlaylistUrl)
    .success(function(result) {  
      displayPlaylist(result);
      displayRequestPayload(result, getPlaylistUrl, "GET", null);
    })
    .error(function(jqXHR, textStatus, errorThrown) {
      console.error(JSON.stringify(jqXHR));
      displayErrorGettingPlaylist();
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
  currentPlaylist = playlistArray;
  // Clear playlist content, if it has.
  emptyPlaylistTableBody();
  // Add the new playlist items received from the server.
  var $playlistTableBody = $('#playlist-table-body'); 
  if (isEmptyArray(currentPlaylist)) {
    var playlistTableRow = $('<tr>').append($('<td>').text("No playlist loaded yet. Mada mada dane :)"));
    $playlistTableBody.append(playlistTableRow);
  } else {
    for (var i = 0; i < currentPlaylist.length ; i++) {
      var playlistElementButton = $('<button>');
      playlistElementButton.addClass("btn btn-outline-danger btn-borderless btn-playlist");
      playlistElementButton.text(currentPlaylist[i].name);
      playlistElementButton.click({id: currentPlaylist[i].id}, clickEventOnPlaylistRow);
      var playlistTableRow = $('<tr>').append($('<td>').append(playlistElementButton));
      $playlistTableBody.append(playlistTableRow);
    } 
  } 
}

/** Play the clicked element from the playlist. */
function clickEventOnPlaylistRow(event) {
  //console.log("Play playlist id: " + event.data.id);
  var requestBody = {
    name: 'pl_play',
    id: event.data.id
  };
  executePost('/kame-house/api/v1/vlc-rc/players/localhost/commands', requestBody);
}

/** Display error getting playlist from the server. */
function displayErrorGettingPlaylist() {
  emptyPlaylistTableBody();
  var $playlistTableBody = $('#playlist-table-body'); 
  var $errorTableRow = $("<tr>").append($('<td>').text(getTimestamp() +
  " : Error getting playlist from the server. Please check server logs."));
  $playlistTableBody.append($errorTableRow);
  console.error(getTimestamp() + " : Error getting playlist from the server. Please check server logs.");
}

/** Empty Playlist table body. */
function emptyPlaylistTableBody() {
  $("#playlist-table-body").empty();  
}

/** Toggle playlist collapsible active status and show or hide collapsible content. */
// For an example of a collapsible element that expands to full vertical height, 
// check the api-call-output example in the test-apis page. This one expands to a fixed height.
function toggleShowOrHidePlaylistContent() {
  //console.log(getTimestamp() + " clicked playlist button");
  var playlistCollapsibleButton = document.getElementById("playlist-collapsible");
  playlistCollapsibleButton.classList.toggle("playlist-collapsible-active");
  
  var playlistCollapsibleContent = document.getElementById("playlist-collapsible-content");
  playlistCollapsibleContent.classList.toggle("playlist-collapsible-content-active");
} 

/** ----- Debug mode functions ----------------------------------------------------------- */

/** Toggle debug mode. */
function toggleDebugMode() { 
  //console.log("Toggled debug mode.")
  var debugModeDiv = document.getElementById("debug-mode");
  debugModeDiv.classList.toggle("hidden-kh");
} 

/** Call main. */
$(document).ready(main);