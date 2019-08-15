/**
 * VLC Player page functions.
 * 
 * @author nbrest
 */
var videoPlaylists = [];
var videoPlaylistCategories = [];

var main = function() { 
  populateVideoPlaylistCategories();
  setupWebSocketForVlcStatus();
};

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
      getVideoPlaylistCategories(videoPlaylists);
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

function getVideoPlaylistCategories(videoPlaylists) {
  videoPlaylistCategories = [...new Set(videoPlaylists.map(playlist => playlist.category))];
  //console.debug(videoPlaylistCategories);  
}

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

/**
 * Websockets functionality to poll vlc player status.
 * */
var stompClient = null;
var vlcRcStatus = {};
var isWebSocketConnected = false; 
var isPlaying = true;
function setupWebSocketForVlcStatus() {
  connectWebSocket();
  pullVlcRcStatusLoop();
}

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
 
function updateVlcPlayerStatus(vlcRcStatusResponse) {
  vlcRcStatus = vlcRcStatusResponse;
  //console.log("vlcRcStatusResponse: " + JSON.stringify(vlcRcStatus));
  mediaName = getMediaName(); 
  mediaTime = getMediaTime();
  $("#media-title").text(mediaName.filename );
  $("#current-time").text(convertSecondsToHsMsSs(mediaTime.currentTime));
  $("#total-time").text(convertSecondsToHsMsSs(mediaTime.totalTime));
}

function disconnectWebSocket() {
    if (stompClient !== null) {
        stompClient.disconnect();
        isWebSocketConnected = false;
    }
    console.log("Disconnected WebSocket");
}

function getVlcRcStatus() {
  //console.log("Requesting vlc-rc status");
  stompClient.send("/app/vlc-player/status-in", {});
}

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

function getMediaTime() {
  var mediaTime = {};
  mediaTime.currentTime = vlcRcStatus.time;
  mediaTime.totalTime = vlcRcStatus.length;
  return mediaTime;
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

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

var volumeSlider = document.getElementById("volume-slider");
var currentVolume = document.getElementById("current-volume");
currentVolume.innerHTML = volumeSlider.value;

volumeSlider.oninput = function() {
	currentVolume.innerHTML = this.value;
}

/**
 * Call main.
 */
$(document).ready(main);