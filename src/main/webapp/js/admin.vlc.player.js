/**
 * Admin VLC Player test page functions.
 * 
 * @author nbrest
 */
var videoPlaylists = [];
var videoPlaylistCategories = [];

function setCollapsibleContent() {
  var coll = document.getElementsByClassName("collapsible");
  var i;

  for (i = 0; i < coll.length; i++) {
    coll[i].addEventListener("click", function() {
      this.classList.toggle("active");
      var content = this.nextElementSibling;
      if (content.style.maxHeight){
        content.style.maxHeight = null;
      } else {
        content.style.maxHeight = content.scrollHeight + "px";
      } 
    });
  }  
}

var main = function() { 
  displayRequestPayload(null, null, null, null);
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
  $.ajax({
    type: "POST",
    url: url,
    data: requestBody,
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
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
  $.ajax({
    type: "DELETE",
    url: url,
    data: requestBody,
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
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

/**
 * Display api call output.
 */
function displayRequestPayload(apiResponsePayload, url, requestType, requestBody) {
  emptyApiCallOutputDiv();
  //console.debug(apiResponsePayload);
  var $apiCallOutput = $("#api-call-output");
  var $apiCallOutputTable = $('<table class="table table-bordered table-responsive">');
  // Request Type row.
  var $requestTypeRow = $("<tr>");
  $requestTypeRow.append($('<td>').text("Request Type"));
  $requestTypeRow.append($('<td>').text(requestType));
  $apiCallOutputTable.append($requestTypeRow);
  // Url row.
  var $urlRow = $("<tr>");
  $urlRow.append($('<td>').text("Url"));
  $urlRow.append($('<td>').text(url));
  $apiCallOutputTable.append($urlRow);
  // Request Body row.
  var $requestBodyRow = $("<tr>");
  $requestBodyRow.append($('<td>').text("Request Body"));
  $requestBodyRow.append($('<td>').text(JSON.stringify(requestBody, null, 2)));
  $apiCallOutputTable.append($requestBodyRow);
  // Time row.
  var $timeRow = $("<tr>");
  $timeRow.append($('<td>').text("Time"));
  $timeRow.append($('<td>').text(getTimestamp()));
  $apiCallOutputTable.append($timeRow);
  $apiCallOutput.append($apiCallOutputTable);
  // Output payload.
  var $outputPayloadButton = $('<button class="collapsible">');
  $outputPayloadButton.text("Output Payload");
  $outputPayloadContent = $('<div class="content">');
  $outputPayloadContent.append($('<pre style="color:white;">').text(JSON.stringify(apiResponsePayload, null, 2)));
  $apiCallOutput.append($outputPayloadButton);
  $apiCallOutput.append($outputPayloadContent);
  setCollapsibleContent();
}

/**
 * Display error executing the request.
 */
function displayErrorExecutingRequest() {
  emptyApiCallOutputDiv();
  var $apiCallOutput = $("#api-call-output");
  var $errorTable = $('<table class="table table-bordered table-responsive table-ehcache">');
  var $errorTableRow = $("<tr>");
  $errorTableRow.append($('<td>').text(getTimestamp() +
    " : Error executing api request. Please check server logs."));
  $errorTable.append($errorTableRow);
  $apiCallOutput.append($errorTable);
  console.error(getTimestamp() + " : Error executing api request. Please check server logs.");
}

function populateVideoPlaylistCategories() {
  let playlistDropdown = $('#playlist-dropdown');
  playlistDropdown.empty();
  playlistDropdown.append('<option selected="true" disabled>Choose a playlist</option>');
  playlistDropdown.prop('selectedIndex', 0);
  let playlistCategoryDropdown = $('#playlist-category-dropdown');
  playlistCategoryDropdown.empty();
  playlistCategoryDropdown.append('<option selected="true" disabled>Choose a playlist category</option>');
  playlistCategoryDropdown.prop('selectedIndex', 0);

  $.get('/kame-house/api/v1/media/video/playlists')
    .success(function(result) { 
      videoPlaylists = result;
      getVideoPlaylistCategories(videoPlaylists);
      //console.debug(JSON.stringify(videoPlaylists));
      $.each(videoPlaylistCategories, function (key, entry) {
        var category = entry;
        var categoryFormatted = category.replace(/\\/g, ' \\ ').replace(/\//g, ' / ');
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
  playlistDropdown.append('<option selected="true" disabled>Choose a playlist</option>');
  playlistDropdown.prop('selectedIndex', 0);
  //console.debug(JSON.stringify(videoPlaylists));
  console.debug("selectedPlaylistCategory " + selectedPlaylistCategory);
  $.each(videoPlaylists, function (key, entry) {
    if (entry.category === selectedPlaylistCategory) { 
      playlistDropdown.append($('<option></option>').attr('value', entry.path).text(entry.name));
    }
  });
}

/**
 * Empty api call output div.
 */
function emptyApiCallOutputDiv() {
  var $apiCallOutput = $("#api-call-output");
  $apiCallOutput.empty();
}

/**
 * Get timestamp.
 */
function getTimestamp() {
  return new Date().toISOString().replace("T", " ").slice(0, 19);
}

/**
 * Get CSRF token.
 */
function getCsrfToken() {
  var token = $("meta[name='_csrf']").attr("content");
  return token;
}

/**
 * Get CSRF header.
 */
function getCsrfHeader() {
  var header = $("meta[name='_csrf_header']").attr("content");
  return header;
}

/**
 * Call main.
 */
$(document).ready(main);