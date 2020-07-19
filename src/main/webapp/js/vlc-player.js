/**
 * VLC Player page functions.
 * 
 * Dependencies: logger, vlcPlayer, playlistBrowser
 * 
 * @author nbrest
 */

/** ----- Global variables ---------------------------------------------------------------- */
var vlcPlayer;
var playlistBrowser;

/** Main function. */
var main = () => {
  loadVlcPlayer();
  loadPlaylistBrowser();
  let loadingModules = ["logger", "vlcPlayer", "playlistBrowser"];
  waitForModules(loadingModules, initVlcPlayer);
};

/** Init function to execute after global dependencies are loaded. */
var initVlcPlayer = () => {
  logger.info("Started initializing VLC Player"); 
  playlistBrowser.populateVideoPlaylistCategories();
  vlcPlayer.init();
};

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
      vlcPlayer = new VlcPlayer("localhost");
      modules.vlcPlayer = true;
    });
  });
}

function loadPlaylistBrowser() {
  $.getScript("/kame-house/js/media/video/playlist-browser.js", function (data, textStatus, jqxhr) {
    let loadingModules = ["logger", "apiCallTable", "vlcPlayer"];
    waitForModules(loadingModules, function initPlaylistBrowser() {
      playlistBrowser = new PlaylistBrowser(vlcPlayer);
      modules.playlistBrowser = true;
    });
  });
}

/** Call main. */
$(document).ready(main);