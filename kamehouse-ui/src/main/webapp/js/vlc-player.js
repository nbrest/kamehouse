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

function VlcPlayerLoader() {
  this.init = init;

  function init() {
    kameHouse.logger.info("Loading vlc player");
    kameHouse.util.dom.load($("#vlc-player-body"), "/kame-house/html-snippets/vlc-player/vlc-player-body.html", () => {
      loadVlcPlayer();
      loadPlaylistBrowser(); 
      kameHouse.util.module.waitForModules(["vlcPlayer", "playlistBrowser"], () => {
        kameHouse.logger.info("Started initializing VLC Player");
        vlcPlayer.init();
        playlistBrowser.init();
        playlistBrowser.populateVideoPlaylistCategories();
      });
      kameHouse.util.module.waitForModules(["kameHouseDebugger"], () => {
        kameHouse.plugin.debugger.renderCustomDebugger("/kame-house/html-snippets/vlc-player/debug-mode-custom.html");
      });
    });
  }

  /**
   * Load the main vlc player object.
   */
  function loadVlcPlayer() {
    kameHouse.util.module.loadWebSocketKameHouse();
    kameHouse.util.fetch.getScript("/kame-house/js/vlc-player/vlc-player.js", () => {
      kameHouse.util.module.waitForModules(["kameHouseDebugger", "kameHouseWebSocket"], () => {
        //TODO get vlcPlayer hostname from some config
        vlcPlayer = new VlcPlayer("localhost");
        kameHouse.util.module.setModuleLoaded("vlcPlayer");
      });
    }); 
  }

  /**
   * Load the playlist browser attached to the vlc player.
   */
  function loadPlaylistBrowser() {
    kameHouse.util.fetch.getScript("/kame-house/js/media/video/playlist-browser.js", () => {
      kameHouse.util.module.waitForModules(["kameHouseDebugger", "vlcPlayer"], () => {
        playlistBrowser = new PlaylistBrowser(vlcPlayer);
        kameHouse.util.module.setModuleLoaded("playlistBrowser");
      });
    });
  }
}

/** Call main. */
$(document).ready(() => {new VlcPlayerLoader().init();});