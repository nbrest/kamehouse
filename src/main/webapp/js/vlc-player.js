/**
 * VLC Player page functions.
 * 
 * @author nbrest
 */
var videoPlaylists = [];
var videoPlaylistCategories = [];

var main = function() { 
  populateVideoPlaylistCategories();
};

function executeGet(url) {
  console.debug(getTimestamp() + " : Executing GET on " + url);
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
  console.debug(getTimestamp() + " : Executing POST on " + url);
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
  console.debug(getTimestamp() + " : Executing DELETE on " + url);
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
 * Call main.
 */
$(document).ready(main);