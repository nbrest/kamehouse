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

/** Main function. */
var main = function() { 
  updateVolumePercentage(document.getElementById("volume-slider").value);
  populateVideoPlaylistCategories();
  setupWebSocketForVlcStatus();
};

/** ---- General REST request functions --------------------------------------------- **/
function executeGet(url) {
  //console.debug(getTimestamp() + " : Executing GET on " + url);
  //console.debug(url);
  $.get(url)
    .success(function(result) { 
      displayRequestPayload(result, url, "GET", null);
    })
    .error(function(jqXHR, textStatus, errorThrown) {
      console.error(JSON.stringify(jqXHR));
      displayErrorExecutingRequest();
    });
  setCollapsibleContent();
}

function executeAdminVlcPostWithSelectedPlaylist(url, command) {
  var playlistSelected = document.getElementById("playlist-dropdown").value;
  //console.debug("playlistSelected " + playlistSelected);
  var requestBody = JSON.stringify({
    command: command,
    file: playlistSelected
  });
  executePost(url, requestBody);
}

function executeAdminVlcPost(url, command, file) {
  var requestBody = JSON.stringify({
    command: command,
    file: file
  });
  executePost(url, requestBody);
}

function executeVlcRcCommandPost(url, name) {
  var requestBody = JSON.stringify({
    name: name
  });
  executePost(url, requestBody);
}

function executeVlcRcCommandPost(url, name, val) {
  var requestBody = JSON.stringify({
    name: name,
    val: val
  });
  executePost(url, requestBody);
}

function executeAdminShutdownPost(url, command, time) {
  var requestBody = JSON.stringify({
    command: command,
    time: time
  });
  executePost(url, requestBody);
}

function executePost(url, requestBody) {
  //console.debug(getTimestamp() + " : Executing POST on " + url);
  requestHeaders = getCsrfRequestHeadersObject();
  $.ajax({
    type: "POST",
    url: url,
    data: requestBody,
    headers: requestHeaders,
    success: function(data) {
      //console.debug(JSON.stringify(data));
      //console.debug(JSON.stringify(data, null, 2));  
      displayRequestPayload(data, url, "POST", requestBody);
    },
    error: function(data) {
      console.error(JSON.stringify(data));
      displayErrorExecutingRequest(); 
    }
    });
  setCollapsibleContent();
}

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
    if (stompClient !== null) {
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
  
  // Infinite loop to pull VlcRcStatus every 1 second, switch to 60 seconds if I'm not playing anything.
  var sleepTime = 1000;
  let failedCount = 0;
  // TODO: Make the client side contain a status of when vlc player is actually running on the server and only pull when it's running.
  for ( ; ; ) { 
    if (isWebSocketConnected && isPlaying) {
      getVlcRcStatus();
    }  
    if (vlcRcStatus.information != null && vlcRcStatus.information != undefined) {
      sleepTime = 1000;
      failedCount = 0;
      //isPlaying = true;
    } else {
      failedCount++;
      if (failedCount >= 10) {
        sleepTime = 60000;
      }
      //isPlaying = false;
    } 
    await sleep(sleepTime); 
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
  if (vlcRcStatus.time != null && vlcRcStatus.time != undefined) { 
    $("#current-time").text(convertSecondsToHsMsSs(vlcRcStatus.time));
    $("#time-slider").val(vlcRcStatus.time);
    
    $("#total-time").text(convertSecondsToHsMsSs(vlcRcStatus.length)); 
    $("#time-slider").attr('max', vlcRcStatus.length);
  } 
  
  // Update volume percentage and slider.
  if (vlcRcStatus.volume != null && vlcRcStatus.volume != undefined) {
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
  if (vlcRcStatus.information != null && vlcRcStatus.information.category != null) {
      vlcRcStatus.information.category.forEach(function (category) {
        if (category.filename != undefined && category.filename != null) {
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
 
/**
 * Call main.
 */
$(document).ready(main);