/*global logger*/
/**
 * VLC Player page functions.
 * 
 * Dependencies: logger, vlcPlayer, playlistBrowser, kameHouseDebugger
 * 
 * @author nbrest
 */

/** ----- Global variables ---------------------------------------------------------------- */
var vlcPlayer;
var playlistBrowser;

function mainVlcPlayer() {
  loadVlcPlayer();
  loadPlaylistBrowser(); 
  moduleUtils.waitForModules(["vlcPlayer", "playlistBrowser"], () => {
    logger.info("Started initializing VLC Player");
    vlcPlayer.init();
    playlistBrowser.init();
    playlistBrowser.populateVideoPlaylistCategories();
  });
  moduleUtils.waitForModules(["kameHouseDebugger"], () => {
    kameHouseDebugger.renderCustomDebugger("/kame-house/html-snippets/vlc-player/debug-mode-custom.html");
  });
};

/**
 * Load the main vlc player object.
 */
function loadVlcPlayer() {
  moduleUtils.loadWebSocketKameHouse();
  fetchUtils.getScript("/kame-house/js/vlc-player/vlc-player.js", () => {
    moduleUtils.waitForModules(["debuggerHttpClient", "webSocketKameHouse"], () => {
      vlcPlayer = new VlcPlayer("localhost");
      moduleUtils.setModuleLoaded("vlcPlayer");
    });
  }); 
}

/**
 * Load the playlist browser attached to the vlc player.
 */
function loadPlaylistBrowser() {
  fetchUtils.getScript("/kame-house/js/media/video/playlist-browser.js", () => {
    moduleUtils.waitForModules(["debuggerHttpClient", "vlcPlayer"], () => {
      playlistBrowser = new PlaylistBrowser(vlcPlayer);
      moduleUtils.setModuleLoaded("playlistBrowser");
    });
  });
}

/** Call main. */
$(document).ready(mainVlcPlayer);