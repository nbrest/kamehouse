/**
 * VLC Player page functions.
 * 
 * Dependencies: logger, vlcPlayer, playlistSelector
 * 
 * @author nbrest
 */

/** ----- Global variables ---------------------------------------------------------------- */
var vlcPlayer;
var playlistSelector;

/** Main function. */
var main = function() {
  loadPlaylistSelector();
  loadVlcPlayer();
  let loadingModules = ["logger", "vlcPlayer", "playlistSelector"];
  waitForModules(loadingModules, initVlcPlayer);
};

/** Init function to execute after global dependencies are loaded. */
var initVlcPlayer = function() {
  logger.info("Started initializing VLC Player"); 
  playlistSelector.populateVideoPlaylistCategories();
  vlcPlayer.init();
};

function loadPlaylistSelector() {
  $.getScript("/kame-house/js/media/video/playlist-selector.js", function (data, textStatus, jqxhr) {
    let loadingModules = ["logger", "apiCallTable"];
    waitForModules(loadingModules, function initPlaylistSelector() {
      modules.playlistSelector = true;
      playlistSelector = new PlaylistSelector();
    });
  });
}

function loadWebSocketKameHouse() {
  $.getScript("/kame-house/js/utils/websocket-kamehouse.js", function (data, textStatus, jqxhr) {
    let loadingModules = ["logger"];
    waitForModules(loadingModules, function initWebSocket() {
      modules.webSocketKameHouse = true;
    });
  });
}

function loadVlcPlayer() {
  loadWebSocketKameHouse();
  $.getScript("/kame-house/js/vlc-player/vlc-player.js", function (data, textStatus, jqxhr) {
    let loadingModules = ["timeUtils", "logger", "apiCallTable", "webSocketKameHouse"];
    waitForModules(loadingModules, function initVlcPlayerInstance() {
      modules.vlcPlayer = true;
      vlcPlayer = new VlcPlayer("localhost");
    });
  });
}

/** Call main. */
$(document).ready(main);