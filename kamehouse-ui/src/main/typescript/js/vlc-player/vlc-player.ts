/** 
 * VlcPlayer entity.
 * 
 * This class contains the public interface to interact with VlcPlayer. Most of the logic is
 * implemented in the component classes.
 * 
 * Call load() after instantiating VlcPlayer to connect the internal websocket
 * and start the sync loops.
 * 
 * @author nbrest
 */
class VlcPlayer {

  #commandExecutor = null;
  #playlist = null;
  #restClient = null;
  #mainViewUpdater = null;
  #vlcPlayerDebugger = null;
  #synchronizer = null;

  #vlcRcStatus = new VlcRcStatus();
  #hostname = null;

  constructor(hostname) {
    this.#hostname = hostname;
    this.#commandExecutor = new VlcPlayerCommandExecutor(this);
    this.#playlist = new VlcPlayerPlaylist(this);
    this.#restClient = new VlcPlayerRestClient(this);
    this.#mainViewUpdater = new VlcPlayerMainViewUpdater(this);
    this.#vlcPlayerDebugger = new VlcPlayerDebugger(this);
  }

  /** Load VlcPlayer extension. */
  load() {
    kameHouse.logger.info("Started initializing VLC Player", null);
    this.#mainViewUpdater.setStatefulButtons();
    kameHouse.core.loadKameHouseWebSocket();
    this.#loadStateFromCookies();
    this.#playlist.init();
    kameHouse.util.mobile.setMobileEventListeners(() => {this.#stopVlcPlayerLoops()}, () => {this.#restartVlcPlayerLoops()});
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger"], () => {
      kameHouse.util.mobile.exec(
        () => {this.loadStateFromApiRound();},
        () => {
          kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
            this.loadStateFromApiRound();
          });
        }
      );
      kameHouse.plugin.debugger.renderCustomDebugger("/kame-house/html-snippets/vlc-player/vlc-player-debug-mode-custom.html", () => {
        kameHouse.core.configDynamicHtml();
      });
    });
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger", "kameHouseWebSocket"], () => {
      this.#synchronizer = new VlcPlayerSynchronizer(this);
      this.#synchronizer.syncVlcPlayerHttpLoop();
      kameHouse.util.mobile.exec(
        () => {this.#startSynchronizerLoops();},
        () => {
          kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
            // wait for the mobile config to be available before starting the websockets and sync loops
            this.#startSynchronizerLoops();
          });
        }
      );
      kameHouse.util.module.setModuleLoaded("vlcPlayer");
    });
  }

  /**
   * Load the vlc player state and refresh the view from API calls (not through websockets).
   */
  loadStateFromApi(updateCursor) {
    this.getDebugger().getVlcRcStatusFromApi(updateCursor);
    this.getDebugger().getPlaylistFromApi(updateCursor);
  }

  /**
   * Load the player state for a round of several seconds through http api.
   */
  loadStateFromApiRound() {
    for (let i = 0; i < 10; i++) {
      const timeoutMs = i * 2500;
      setTimeout(() => {
        if (this.isSyncEnabled()) {
          this.loadStateFromApi(false);
        }
      }, timeoutMs);
    }
  }
  
  /**
   * Returns true if the sync loops are enabled.
   */
  isSyncEnabled() {
    const vlcStatusSyncCheckbox = document.getElementById("vlc-player-status-sync-checkbox") as HTMLInputElement;
    if (kameHouse.core.isEmpty(vlcStatusSyncCheckbox)) {
      return true;
    }
    return vlcStatusSyncCheckbox.checked;
  }

  /** Get the hostname for this instance of VlcPlayer */
  getHostname() {
    return this.#hostname;
  }

  /** Get internal object to manage the playlist */
  getPlaylist() {
    return this.#playlist;
  }

  /**
   * Open vlc player tab.
   */
  openTab(vlcPlayerTabDivId) {
    // Set kh-vlc-player-current-tab cookie
    kameHouse.util.cookies.setCookie('kh-vlc-player-current-tab', vlcPlayerTabDivId, null);
    // Update tab links
    const vlcPlayerTabLinks = document.getElementsByClassName("vlc-player-tab-link");
    for (const vlcPlayerTabLinkElement of vlcPlayerTabLinks) {
      kameHouse.util.dom.classListRemove(vlcPlayerTabLinkElement, "active");
    }
    const vlcPlayerTabLink = document.getElementById(vlcPlayerTabDivId + '-link');
    kameHouse.util.dom.classListAdd(vlcPlayerTabLink, "active");

    // Update tab content visibility
    const vlcPlayerTabContent = document.getElementsByClassName("vlc-player-tab-content");
    for (const vlcPlayerTabContentElement of vlcPlayerTabContent) {
      kameHouse.util.dom.setDisplay(vlcPlayerTabContentElement, "none");
    }
    const vlcPlayerTabDiv = document.getElementById(vlcPlayerTabDivId);
    kameHouse.util.dom.setDisplay(vlcPlayerTabDiv, "block");

    setTimeout(() => {
      // Asynchronously show or hide playlist and playlist browser content
      const playlistTable = document.getElementById("playlist-table");
      if ("tab-playlist" == vlcPlayerTabDivId) {
        kameHouse.util.dom.setDisplay(playlistTable, "table");
      } else {
        kameHouse.util.dom.setDisplay(playlistTable, "none");
      }

      const playlistBrowserTable = document.getElementById("playlist-browser-table");
      if ("tab-playlist-browser" == vlcPlayerTabDivId) {
        kameHouse.util.dom.setDisplay(playlistBrowserTable, "table");
      } else {
        kameHouse.util.dom.setDisplay(playlistBrowserTable, "none");
      }
    }, 0);
  }

  /**
   * Play the specified file in vlc.
   */
  playFile(fileName) { this.#commandExecutor.playFile(fileName); }

  /**
   * Execute the specified vlc command.
   */
  execVlcRcCommand(name, val) { this.#commandExecutor.execVlcRcCommand(name, val); }

  /**
   * Set the subtitle delay.
   */
  updateSubtitleDelay(increment) {
    let subtitleDelay = this.getVlcRcStatus().subtitleDelay;
    if (!kameHouse.core.isEmpty(subtitleDelay)) {
      subtitleDelay = Number(subtitleDelay) + Number(increment);
    } else {
      subtitleDelay = 0 + Number(increment);
    }
    this.#commandExecutor.execVlcRcCommand('subdelay', subtitleDelay);
  }

  /**
   * Set aspect ratio.
   */
  updateAspectRatio() {
    const aspectRatio = (document.getElementById("aspect-ratio-dropdown") as HTMLSelectElement).value;
    if (!kameHouse.core.isEmpty(aspectRatio)) {
      this.#commandExecutor.execVlcRcCommand('aspectratio', aspectRatio);
    }
  }

  /**
   * Seek through the current playing file.
   */
  seek(value) {
    this.#mainViewUpdater.updateCurrentTimeView(value);
    this.#commandExecutor.execVlcRcCommand('seek', value);
    this.#mainViewUpdater.timeSliderLocked(false);
  }

  /**
   * Update the volume.
   */ 
  setVolume(value) {
    this.#mainViewUpdater.updateVolumeView(value);
    this.#commandExecutor.execVlcRcCommand('volume', value);
    this.#mainViewUpdater.volumeSliderLocked(false);
  }

  /**
   * Close vlc player.
   */
  close() { this.#commandExecutor.close(); }

  /**
   * Get the current vlcRc status.
   */
  getVlcRcStatus() { return this.#vlcRcStatus; }

  /** 
   * Set the VlcRcStatus. vlcRcStatus must never be undefined or null.
   * If no value is passed, set an empty object. Always set vlcRcStatus
   * through this method.
   */
  setVlcRcStatus(vlcRcStatusParam) {
    if (!kameHouse.core.isEmpty(vlcRcStatusParam)) {
      this.#vlcRcStatus = vlcRcStatusParam;
    } else {
      this.#vlcRcStatus = new VlcRcStatus();
    }
  }

  /**
   * Pol vlcrc status from the websocket.
   */
  pollVlcRcStatus() { this.#synchronizer.pollVlcRcStatus(); }

  /**
   * Set updated playlist.
   */
  setUpdatedPlaylist(updatedPlaylist) { this.#playlist.setUpdatedPlaylist(updatedPlaylist); }

  /**
   * Reload playlist.
   */
  reloadPlaylist() { this.#playlist.reload(); }

  /**
   * Render playlist.
   */
  renderPlaylist() { this.#playlist.renderPlaylist(); }

  /**
   * Scroll to currently playing item.
   */
  scrollToCurrentlyPlaying() { this.#playlist.scrollToCurrentlyPlaying(); }

  /**
   * Filter playlist rows.
   */
  filterPlaylistRows() {
    const filterString = (document.getElementById("playlist-filter-input") as HTMLInputElement).value;
    kameHouse.util.table.filterTableRows(filterString, 'playlist-table-body', null, null);
  }

  /**
   * Toggle expand/collapse playlist entries names.
   */
  toggleExpandPlaylistFilenames() { this.#playlist.toggleExpandPlaylistFilenames(); }

  /**
   * --------------------------------------------------------------------------
   * Update view functionality
   */
  /** Calls each internal module that has view logic to update it's view. */
  updateView() {
    this.#mainViewUpdater.updateView();
    this.#playlist.updateView();
  }

  /** Calls each internal module that has view logic to reset it's view. */
  resetView() {
    this.setVlcRcStatus({});
    this.#mainViewUpdater.resetView();
    this.#playlist.resetView();
  }

  /**
   * Update current time view.
   */
  updateCurrentTimeView(value) {
    this.#mainViewUpdater.timeSliderLocked(true);
    this.#mainViewUpdater.updateCurrentTimeView(value);
  }

  /**
   * Update volume view.
   */
  updateVolumeView(value) {
    this.#mainViewUpdater.volumeSliderLocked(true);
    this.#mainViewUpdater.updateVolumeView(value);
  }

  /**
   * Get REST client.
   * Use this getter internally from other components of VlcPlayer. Not externally.
   */
  getRestClient() { return this.#restClient; }

  /**
   * Get vlc player debugger.
   */
  getDebugger() { return this.#vlcPlayerDebugger; }

  /**
   * Unlock screen.
   */
  unlockScreen() {
    const UNLOCK_SCREEN_API_URL = "/kame-house-admin/api/v1/admin/screen/unlock";
    this.getRestClient().post(UNLOCK_SCREEN_API_URL, null, null, () => {}, () => {});
  }

  /**
   * Wake on lan media server.
   */
  wolMediaServer() {
    const requestParam =  {
      server : "media.server"
    };
    const WOL_MEDIA_SERVER_API_URL = "/kame-house-admin/api/v1/admin/power-management/wol";
    this.getRestClient().post(WOL_MEDIA_SERVER_API_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, () => {}, () => {});
  }

  /**
   * Open modal to confirm suspending the server.
   */
  confirmSuspendServer() {
    kameHouse.plugin.modal.basicModal.setHtml(this.#getSuspendServerModalMessage());
    kameHouse.plugin.modal.basicModal.appendHtml(this.#createSuspendButton());
    kameHouse.plugin.modal.basicModal.open();
  }

  /**
   * Single left click.
   */
  mouseSingleClick() {
    const params = {
      mouseButton: "LEFT",
      positionX: 500,
      positionY: 500,
      clickCount: 1
    };
    const WOL_MEDIA_SERVER_API_URL = "/kame-house-admin/api/v1/admin/screen/mouse-click";
    this.getRestClient().post(WOL_MEDIA_SERVER_API_URL, kameHouse.http.getUrlEncodedHeaders(), params, () => {}, () => {});
  }

  /**
   * Trigger a mouse left double click.
   */
  mouseDoubleClick() {
    const requestParam =  {
      mouseButton : "LEFT",
      positionX: 500,
      positionY: 500,
      clickCount: 2
    };
    const MOUSE_CLICK_API_URL = "/kame-house-admin/api/v1/admin/screen/mouse-click";
    this.getRestClient().post(MOUSE_CLICK_API_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, () => {}, () => {});
  }

  /**
   * Send a key press to the server.
   */
  keyPress(key, keyPresses) {
    if (kameHouse.core.isEmpty(keyPresses)) {
      kameHouse.logger.debug("keyPresses not set. Using default value of 1", null);
      keyPresses = 1;
    }
    const requestParam = {
      "key" : key,
      "keyPresses" : keyPresses
    };
    const KEY_PRESS_URL = '/kame-house-admin/api/v1/admin/screen/key-press';
    this.getRestClient().post(KEY_PRESS_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, () => {}, () => {});
  } 

  /**
   * Get suspend server modal message.
   */
  #getSuspendServerModalMessage() {
    const rebootModalMessage = kameHouse.util.dom.getSpan({}, "Are you sure you want to suspend the server? ");
    kameHouse.util.dom.append(rebootModalMessage, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(rebootModalMessage, kameHouse.util.dom.getBr());
    return rebootModalMessage;
  }

  /**
   * Create suspend button.
   */
  #createSuspendButton() {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "img-btn-kh"
      },
      mobileClass: null,
      backgroundImg: "/kame-house/img/pc/shutdown-red.png",
      html: null,
      data: null,
      click: (event, data) => this.#suspendServer()
    });
  }

  /**
   * Suspend the current server.
   */
  #suspendServer() {
    kameHouse.plugin.modal.basicModal.close();
    const requestParam =  {
      delay : 0
    };
    const SUSPEND_SERVER_URL = "/kame-house-admin/api/v1/admin/power-management/suspend";
    this.getRestClient().post(SUSPEND_SERVER_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, 
      () => {
        kameHouse.logger.info("Server suspended", null);
      }, 
      () => {
        kameHouse.logger.error("Error suspending server", null); 
      });
  }

  /**
   * Start synchronization loops.
   */
  #startSynchronizerLoops() {
    kameHouse.logger.info("Started initializing vlc player websockets and sync loops", null);
    this.#synchronizer.initWebSockets();
    this.#synchronizer.connectVlcRcStatus();
    this.#synchronizer.connectPlaylist();
    this.#synchronizer.syncVlcRcStatusLoop();
    this.#synchronizer.syncPlaylistLoop();
    this.#synchronizer.keepAliveWebSocketsLoop();
    this.#synchronizer.syncLoopsHealthCheck();
  }

  /**
   * Stop synchronization loops.
   */
  #stopVlcPlayerLoops() {
    this.#synchronizer.stopVlcPlayerLoops();
  }

  /**
   * Restart synchronization loops.
   */
  #restartVlcPlayerLoops() {
    this.#synchronizer.restartVlcPlayerLoops();
  }

  /**
   * Load the current state from the cookies.
   */
  #loadStateFromCookies() {
    const showPlaylistCheckboxCookie = kameHouse.util.cookies.getCookie('kh-vlc-player-show-playlist-checkbox');
    if (!kameHouse.core.isEmpty(showPlaylistCheckboxCookie) && showPlaylistCheckboxCookie == "false") {
      const showPlaylistCheckbox = document.getElementById("vlc-player-show-playlist-checkbox") as HTMLInputElement;
      showPlaylistCheckbox.checked = false;
    }
    const showPlaylistBrowserCheckboxCookie = kameHouse.util.cookies.getCookie('kh-vlc-player-show-playlist-browser-checkbox');
    if (!kameHouse.core.isEmpty(showPlaylistBrowserCheckboxCookie) && showPlaylistBrowserCheckboxCookie == "false") {
      const showPlaylistBrowserCheckbox = document.getElementById("vlc-player-show-playlist-browser-checkbox") as HTMLInputElement;
      showPlaylistBrowserCheckbox.checked = false;
    }
    let currentTab = kameHouse.util.cookies.getCookie('kh-vlc-player-current-tab');
    if (!currentTab || currentTab == '') {
      currentTab = 'tab-playing';
    }
    this.openTab(currentTab);
  }
} // End VlcPlayer

/** 
 * Handles the execution of vlc commands, such as play, stop, next, close, etc.
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class VlcPlayerCommandExecutor {

  #VLC_PLAYER_PROCESS_CONTROL_URL = '/kame-house-vlcrc/api/v1/vlc-rc/vlc-process';
  
  #vlcPlayer = null;
  #vlcRcCommandUrl = null;

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
    this.#vlcRcCommandUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/commands';
  }

  /** Create a vlcrc command with the parameters and execute the request to the server. */
  execVlcRcCommand(name, val) {
    const requestBody = {
      name: name,
      val: null
    };
    if (!kameHouse.core.isEmpty(val) || val == 0) {
      requestBody.val = val;
    }
    this.#vlcPlayer.getRestClient().post(this.#vlcRcCommandUrl, kameHouse.http.getApplicationJsonHeaders(), requestBody, () => {}, () => {});
  }

  /** Play the selected file (or playlist) into vlc player and reload the current playlist. */
  playFile(fileName) {
    kameHouse.logger.debug("File to play: " + fileName, null);
    const requestParam =  {
      file : fileName
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    this.#vlcPlayer.getRestClient().post(this.#VLC_PLAYER_PROCESS_CONTROL_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, 
      () => {
        this.#vlcPlayer.loadStateFromApiRound();
      }, 
      () => {
        this.#vlcPlayer.loadStateFromApiRound();
      }
    );
  }

  /** Close vlc player. */
  close() {
    this.#vlcPlayer.getRestClient().delete(this.#VLC_PLAYER_PROCESS_CONTROL_URL, null, null, 
      () => {
        this.#vlcPlayer.loadStateFromApiRound();
      }, 
      () => {
        this.#vlcPlayer.loadStateFromApiRound();
      }
    );
  }

} // End VlcPlayerCommandExecutor

/** 
 * Handles the updates to the VlcPlayer main view elements. It consists of
 * title, timer and volume sliders and stateful media buttons.
 * 
 * This prototype is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class VlcPlayerMainViewUpdater {

  #vlcPlayer = null;
  #statefulButtons = [];
  #timeSliderLocked = false;
  #volumeSliderLocked = false;

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
  }

  /** Set time slider locked. */
  timeSliderLocked(value) { this.#timeSliderLocked = value; }

  /** Set volume slider locked. */
  volumeSliderLocked(value) { this.#volumeSliderLocked = value; }

  /** Update vlc player view for main view objects. */
  updateView() {
    if (!kameHouse.core.isEmpty(this.#vlcPlayer.getVlcRcStatus())) {
      this.#updateMediaTitle();
      this.#updateTimeSlider();
      this.#updateVolumeSlider();
      this.#updateSubtitleDelay();
      this.#updateAspectRatioDropdown();
      this.#statefulButtons.forEach((statefulButton) => statefulButton.updateState());
    } else {
      this.resetView();
    }
  }

  /**
   * Set the aspect ratio dropdown view.
   */
  #updateAspectRatioDropdown() {
    const aspectRatio = this.#vlcPlayer.getVlcRcStatus().aspectRatio;
    const aspectRatioDropdown = document.getElementById("aspect-ratio-dropdown") as HTMLSelectElement;
    if (kameHouse.core.isEmpty(aspectRatio) && aspectRatioDropdown.options[0].selected) {
      return;
    }
    for (const option of aspectRatioDropdown.options) {
      if (option.value == aspectRatio && option.selected) {
        return;
      } 
    }
    for (const option of aspectRatioDropdown.options) {
      if (option.value == aspectRatio) {
        option.selected = true;
      } else {
        option.selected = false;
      }
    }
  }

  /** Reset vlc player view for main view objects. */
  resetView() {
    this.#resetMediaTitle();
    this.#resetTimeSlider();
    this.#resetVolumeSlider();
    this.#resetSubtitleDelay();
    this.#resetAspectRatioDropdown();
    this.#statefulButtons.forEach(statefulButton => statefulButton.updateState());
  }

  /**
   * Reset the aspect ratio dropdown view.
   */
  #resetAspectRatioDropdown() {
    const aspectRatioDropdown = document.getElementById("aspect-ratio-dropdown") as HTMLSelectElement;
    aspectRatioDropdown.selectedIndex = 0;
  }

  /** Update the displayed current time. */
  updateCurrentTimeView(value) {
    const currentTime = document.getElementById("current-time");
    kameHouse.util.dom.setHtml(currentTime, kameHouse.util.time.convertSecondsToHsMsSs(value));
    kameHouse.util.dom.setValueById("time-slider", value);
  }

  /** Update volume percentage to display with the specified value. */
  updateVolumeView(value) {
    kameHouse.util.dom.setValueById("volume-slider", value);
    const volumePercentaje = Math.floor(value * 200 / 512);
    const currentVolume = document.getElementById("current-volume");
    kameHouse.util.dom.setHtml(currentVolume, volumePercentaje + "%");
  }

  /**
   * Set stateful buttons state.
   */
  setStatefulButtons() {
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-fullscreen', "fullscreen", true, null));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-repeat-1', "repeat", true, null));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-repeat', "loop", true, null));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-shuffle', "random", true, null));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-stop', "state", "stopped", null));
    this.#statefulButtons.push(new StatefulMediaButton(this.#vlcPlayer, 'media-btn-mute', "volume", 0, 'btn-mute'));
  }

  /** Update the media title. */
  #updateMediaTitle() {
    const mediaName = {
      filename: "No media loaded",
      title: "No media loaded"
    };
    if (!kameHouse.core.isEmpty(this.#vlcPlayer.getVlcRcStatus().information)) {
      mediaName.filename = this.#vlcPlayer.getVlcRcStatus().information.meta.filename;
      mediaName.title = this.#vlcPlayer.getVlcRcStatus().information.meta.title;
    }
    kameHouse.util.dom.setHtmlById("media-title", mediaName.filename);
  }

  /** Reset the media title. */
  #resetMediaTitle() {
    const mediaName = {
      filename: "No media loaded",
      title: "No media loaded"
    };
    kameHouse.util.dom.setHtmlById("media-title", mediaName.filename);
  }

  /** Update subtitle delay. */
  #updateSubtitleDelay() {
    let subtitleDelay = this.#vlcPlayer.getVlcRcStatus().subtitleDelay;
    if (kameHouse.core.isEmpty(subtitleDelay)) {
      subtitleDelay = "0";
    }
    kameHouse.util.dom.setHtmlById("subtitle-delay-value", String(subtitleDelay));
  }

  /** Reset subtitle delay. */
  #resetSubtitleDelay() {
    kameHouse.util.dom.setHtmlById("subtitle-delay-value", "0");
  }

  /**
   * --------------------------------------------------------------------------
   * Update time Functionality
   */
  /** Update media time slider from VlcRcStatus and resets view when there's no input. */
  #updateTimeSlider() {
    if (!this.#timeSliderLocked) {
      if (!kameHouse.core.isEmpty(this.#vlcPlayer.getVlcRcStatus().time)) {
        this.updateCurrentTimeView(this.#vlcPlayer.getVlcRcStatus().time);
        this.#updateTotalTimeView(this.#vlcPlayer.getVlcRcStatus().length);
      } else {
        this.#resetTimeSlider();
      }
    }
  }

  /** Reset time slider. */
  #resetTimeSlider() {
    kameHouse.util.dom.setHtmlById("current-time", "--:--:--");
    kameHouse.util.dom.setValueById("time-slider", 500);
    kameHouse.util.dom.setHtmlById("total-time", "--:--:--");
    kameHouse.util.dom.setAttributeById("time-slider",'max', 1000);
  }

  /** Update the displayed total time. */
  #updateTotalTimeView(value) {
    kameHouse.util.dom.setHtmlById("total-time", kameHouse.util.time.convertSecondsToHsMsSs(value));
    kameHouse.util.dom.setAttributeById("time-slider",'max', value);
  }

  /**
   * --------------------------------------------------------------------------
   * Update volume Functionality
   */
  /** Update volume slider from VlcRcStatus. */
  #updateVolumeSlider() {
    if (!this.#volumeSliderLocked) {
      const volume = this.#vlcPlayer.getVlcRcStatus().volume;
      if (!kameHouse.core.isEmpty(volume) || volume == 0) {
        this.updateVolumeView(volume);
      } else {
        this.#resetVolumeSlider();
      }
    }
  }

  /** Reset volume slider. */
  #resetVolumeSlider() { this.updateVolumeView(256); }

} // End VlcPlayerMainViewUpdater

/** 
 * Represents a media button that has state (pressed/unpressed).
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class StatefulMediaButton {

  #DEFAULT_BTN_PREFIX_CLASS = 'media-btn';

  #vlcPlayer = null;
  #id = null;
  #pressedField = null;
  #pressedCondition = null;
  #buttonPrefixClass = null;

  constructor(vlcPlayer, id, pressedField, pressedCondition, btnPrefixClass) {
    this.#vlcPlayer = vlcPlayer;
    this.#id = id;
    this.#pressedField = pressedField;
    this.#pressedCondition = pressedCondition;
    if (kameHouse.core.isEmpty(btnPrefixClass)) {
      this.#buttonPrefixClass = this.#DEFAULT_BTN_PREFIX_CLASS;
    } else {
      this.#buttonPrefixClass = btnPrefixClass;
    }
  }

  /** Update the state of the button (pressed/unpressed) */
  updateState() {
    if (this.#isPressed()) {
      this.#setMediaButtonPressed();
    } else {
      this.#setMediaButtonUnpressed();
    }
  }  

  /** Determines if the button is pressed or unpressed. */
  #isPressed() { return this.#vlcPlayer.getVlcRcStatus()[this.#pressedField] == this.#pressedCondition; }

  /** Set media button pressed */
  #setMediaButtonPressed() {
    kameHouse.util.dom.classListRemoveById(this.#id, this.#buttonPrefixClass + '-unpressed');
    kameHouse.util.dom.classListAddById(this.#id, this.#buttonPrefixClass + '-pressed');
  }

  /** Set media button unpressed */
  #setMediaButtonUnpressed() {
    kameHouse.util.dom.classListRemoveById(this.#id, this.#buttonPrefixClass + '-pressed');
    kameHouse.util.dom.classListAddById(this.#id, this.#buttonPrefixClass + '-unpressed');
  }
} // End StatefulMediaButton

/** 
 * Manages the websocket connection, synchronization and keep alive loops. 
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class VlcPlayerSynchronizer {

  #vlcPlayer = null;
  #vlcRcStatusWebSocket = null;
  #playlistWebSocket = null;
  #syncLoopsConfig = {
    isRunningSyncVlcRcStatusLoop : false,
    isRunningSyncPlaylistLoop : false,
    isRunningKeepAliveWebSocketLoop : false,
    isRunningSyncVlcPlayerHttpLoop : false,
    vlcRcStatusLoopCount : 0,
    vlcPlaylistLoopCount : 0,    
    keepAliveWebSocketLoopCount : 0,
    syncVlcPlayerHttpLoopCount : 0
  };

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
    this.#vlcRcStatusWebSocket = new KameHouseWebSocket();
    this.#playlistWebSocket = new KameHouseWebSocket();
  }

  /**
   * Initialize web sockets.
   */
  initWebSockets() {
    const vlcRcStatusWebSocketStatusUrl = '/kame-house-vlcrc/api/ws/vlc-player/status';
    const vlcRcStatusWebSocketPollUrl = "/app/vlc-player/status-in";
    const vlcRcStatusWebSocketTopicUrl = '/topic/vlc-player/status-out';
    this.#vlcRcStatusWebSocket.statusUrl(vlcRcStatusWebSocketStatusUrl);
    this.#vlcRcStatusWebSocket.pollUrl(vlcRcStatusWebSocketPollUrl);
    this.#vlcRcStatusWebSocket.topicUrl(vlcRcStatusWebSocketTopicUrl);

    const playlistWebSocketStatusUrl = '/kame-house-vlcrc/api/ws/vlc-player/playlist';
    const playlistWebSocketPollUrl = "/app/vlc-player/playlist-in";
    const playlistWebSocketTopicUrl = '/topic/vlc-player/playlist-out';
    this.#playlistWebSocket.statusUrl(playlistWebSocketStatusUrl);
    this.#playlistWebSocket.pollUrl(playlistWebSocketPollUrl);
    this.#playlistWebSocket.topicUrl(playlistWebSocketTopicUrl);
  }

  /** Poll for an update of vlcRcStatus through the web socket. */
  pollVlcRcStatus() { this.#vlcRcStatusWebSocket.poll(); }

  /** Connects the websocket to the backend. */
  connectVlcRcStatus() {
    this.#vlcRcStatusWebSocket.connect((topicResponse) => {
      if (!kameHouse.core.isEmpty(topicResponse) && !kameHouse.core.isEmpty(topicResponse.body)) {
        this.#vlcPlayer.setVlcRcStatus(kameHouse.json.parse(topicResponse.body));
      } else {
        this.#vlcPlayer.setVlcRcStatus({});
      }
    });
  }

  /** Connects the playlist websocket to the backend. */
  connectPlaylist() {
    this.#playlistWebSocket.connect((topicResponse) => {
      if (!kameHouse.core.isEmpty(topicResponse) && !kameHouse.core.isEmpty(topicResponse.body)) {
        this.#vlcPlayer.setUpdatedPlaylist(kameHouse.json.parse(topicResponse.body));
      } else {
        this.#vlcPlayer.setUpdatedPlaylist(null);
      }
    });
  }

  /** 
   * Start infinite loop to pull VlcRcStatus from the server.
   * Break the loop setting isRunningSyncVlcRcStatusLoop to false.
   */
  syncVlcRcStatusLoop() {
    const config = {
      vlcRcStatusPullWaitTimeMs : 1000
    };
    this.#syncLoopExecution(config, "syncVlcRcStatusLoop", async (config) => {await this.#syncVlcRcStatusLoopRun(config)}, "isRunningSyncVlcRcStatusLoop", "vlcRcStatusLoopCount");
  }

  /** 
   * Start infinite loop to sync the current playlist from the server.
   * Break the loop setting isRunningSyncPlaylistLoop to false.
   */
  syncPlaylistLoop() {
    const config = {
      playlistLoopWaitTimeMs : 5000
    };
    this.#syncLoopExecution(config, "syncPlaylistLoop", async (config) => {await this.#syncPlaylistLoopRun(config)}, "isRunningSyncPlaylistLoop", "vlcPlaylistLoopCount");
  }

  /** 
   * Start infinite loop to keep alive the websocket connections.
   * Break the loop setting isRunningKeepAliveWebSocketLoop to false.
   */
  keepAliveWebSocketsLoop() {
    const config = {
      keepAliveLoopWaitMs : 5000,
      syncLoopStartDelayMs : 15000
    };
    this.#syncLoopExecution(config, "keepAliveWebSocketsLoop", async (config) => {await this.#keepAliveWebSocketsLoopRun(config)}, "isRunningKeepAliveWebSocketLoop", "keepAliveWebSocketLoopCount");
  }

  /** 
   * Start infinite loop to sync falling back to http calls when the websockets are disconnected.
   * Break the loop setting isRunningSyncVlcPlayerHttpLoop to false.
   */
  syncVlcPlayerHttpLoop() {
    const config = {
      syncVlcPlayerHttpWaitMs : 7000
    };
    this.#syncLoopExecution(config, "syncVlcPlayerHttpLoop", async (config) => {await this.#syncVlcPlayerHttpLoopRun(config)}, "isRunningSyncVlcPlayerHttpLoop", "syncVlcPlayerHttpLoopCount");
  }

  /**
   * Stop all sync loops.
   */
  stopVlcPlayerLoops() {
    const message = "KameHouse sent to background. Stopping sync loops and disconnecting websockets";
    kameHouse.logger.info(message, kameHouse.logger.getGreenText(message));
    this.#syncLoopsConfig.isRunningSyncVlcRcStatusLoop = false;
    this.#syncLoopsConfig.isRunningSyncPlaylistLoop = false;
    this.#syncLoopsConfig.isRunningKeepAliveWebSocketLoop = false;
    this.#syncLoopsConfig.isRunningSyncVlcPlayerHttpLoop = false;
    this.#vlcRcStatusWebSocket.disconnect();
    this.#playlistWebSocket.disconnect(); 
  }

  /**
   * Restart all sync loops.
   */
  restartVlcPlayerLoops() {
    const message = "KameHouse sent to foreground. Restarting sync loops and reconnecting websockets";
    kameHouse.logger.info(message, kameHouse.logger.getCyanText(message));
    this.#vlcPlayer.loadStateFromApiRound();
    this.#vlcRcStatusWebSocket.disconnect();
    this.#playlistWebSocket.disconnect();
    this.#restartSyncVlcPlayerHttpLoop(this.#getRestartLoopConfig());
    this.#restartSyncVlcRcStatusLoop(this.#getRestartLoopConfig());
    this.#restartSyncPlaylistLoop(this.#getRestartLoopConfig());
    this.#restartKeepAliveWebSocketsLoop(this.#getRestartLoopConfig());
  }

  /**
   * Run periodic health checks on all sync loops.
   */
  async syncLoopsHealthCheck() {
    const PERIODIC_HEALTH_CHECK_WAIT_MS = 15000;
    setTimeout(async () => {
      let continueLoop = true;
      let printLoopStatusCount = 0;
      while (continueLoop) {
        if (this.#vlcPlayer.isSyncEnabled()) {
          printLoopStatusCount++;
          if (printLoopStatusCount >= 4) {
            this.#printLoopStatus();
            printLoopStatusCount = 0;
          }
          this.#executeSyncLoopsHealthCheck();
        }
        await kameHouse.core.sleep(PERIODIC_HEALTH_CHECK_WAIT_MS);
        if (PERIODIC_HEALTH_CHECK_WAIT_MS < -10000) { // fix sonar bug
          continueLoop = false;
        }
      }
    }, 0);
  }  

  /**
   * --------------------------------------------------------------------------
   * VlcRcStatus WebSocket functionality
   */
  /** Reconnects the VlcRcStatus websocket to the backend. */
  #reconnectVlcRcStatus() {
    this.#vlcRcStatusWebSocket.disconnect();
    this.connectVlcRcStatus();
  }

  /**
   * --------------------------------------------------------------------------
   * Playlist WebSocket functionality
   */
  /** Reconnects the playlist websocket to the backend. */
  #reconnectPlaylist() {
    this.#playlistWebSocket.disconnect();
    this.connectPlaylist();
  }

  /**
   * --------------------------------------------------------------------------
   * Sychronization loops
   */

  /**
   * General logic shared by all sync loops.
   */
  async #syncLoopExecution(config, loopName, loopRunFunction, isLoopRunningName, loopCountName) {
    setTimeout(async () => {
      kameHouse.logger.info("Started " + loopName, null);
      if (this.#syncLoopsConfig[isLoopRunningName] || this.#syncLoopsConfig[loopCountName] > 1) {
        const message = loopName + " is already running";
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        return;
      }
      this.#syncLoopsConfig[isLoopRunningName] = true;
      this.#syncLoopsConfig[loopCountName]++;
      if (config.syncLoopStartDelayMs) {
        kameHouse.logger.info("Started " + loopName + " with initial delay of " + config.syncLoopStartDelayMs + " ms", null);
        await kameHouse.core.sleep(config.syncLoopStartDelayMs);
      }
      while (this.#syncLoopsConfig[isLoopRunningName]) {
        if (this.#vlcPlayer.isSyncEnabled()) {
          await loopRunFunction(config);
        } else {
          await kameHouse.core.sleep(3000);
        }
        if (this.#syncLoopsConfig[loopCountName] > 1) {
          kameHouse.logger.info(loopName + ": Running multiple " + loopName + ", exiting this loop", null);
          break;
        }
      }
      this.#syncLoopsConfig[loopCountName]--;
      kameHouse.logger.info("Finished " + loopName, null);
    }, 0);
  }

  /**
   * VlcRcStatus loop run.
   */
  async #syncVlcRcStatusLoopRun(config) {
    kameHouse.logger.trace("syncVlcRcStatusLoop - vlcRcStatus: " + kameHouse.json.stringify(this.#vlcPlayer.getVlcRcStatus(), null, null), null);
    this.#setVlcRcStatusPullWaitTimeMs(config);
    this.#updateViewSyncVlcRcStatusLoop();
    await kameHouse.core.sleep(config.vlcRcStatusPullWaitTimeMs);
    if (config.vlcRcStatusPullWaitTimeMs < -10000) { // fix sonar bug
      this.#syncLoopsConfig.isRunningSyncVlcRcStatusLoop = false;
    }
  }

  /**
   * Set VlcRcStatus pull wait time.
   */
  #setVlcRcStatusPullWaitTimeMs(config) {
    const VLC_STATUS_CONNECTED_PLAYING_MS = 1000;
    const VLC_STATUS_CONNECTED_NOT_PLAYING_MS = 5000;
    const VLC_STATUS_DISCONNECTED_MS = 5000;
    if (!this.#vlcRcStatusWebSocket.isConnected()) {
      config.vlcRcStatusPullWaitTimeMs = VLC_STATUS_DISCONNECTED_MS;
      return;
    }
    if (kameHouse.core.isEmpty(this.#vlcPlayer.getVlcRcStatus().information)) {
      config.vlcRcStatusPullWaitTimeMs = VLC_STATUS_CONNECTED_NOT_PLAYING_MS;
      return;
    }
    config.vlcRcStatusPullWaitTimeMs = VLC_STATUS_CONNECTED_PLAYING_MS;
  }

  /**
   * Update vlc player view.
   */
  #updateViewSyncVlcRcStatusLoop() {
    if (this.#vlcRcStatusWebSocket.isConnected()) {
      // poll VlcRcStatus from the websocket.
      this.#vlcRcStatusWebSocket.poll();
      this.#vlcPlayer.updateView();
    }
  }

  /**
   * Playlist loop run.
   */
  async #syncPlaylistLoopRun(config) {
    kameHouse.logger.trace("syncPlaylistLoop", null);
    if (this.#playlistWebSocket.isConnected()) {
      // poll playlist from the websocket.
      this.#playlistWebSocket.poll();
      this.#vlcPlayer.reloadPlaylist();
    }
    await kameHouse.core.sleep(config.playlistLoopWaitTimeMs);
    if (config.playlistLoopWaitTimeMs < -10000) { // fix sonar bug
      this.#syncLoopsConfig.isRunningSyncPlaylistLoop = false;
    }
  }

  /**
   * Keep alive websockets loop run.
   */
  async #keepAliveWebSocketsLoopRun(config) {
    kameHouse.logger.trace("keepAliveWebSocketsLoop", null);
    if (!this.#vlcRcStatusWebSocket.isConnected()) {
      kameHouse.logger.trace("keepAliveWebSocketsLoop: VlcRcStatus webSocket not connected. Reconnecting...", null);
      this.#reconnectVlcRcStatus();
    }
    if (!this.#playlistWebSocket.isConnected()) {
      kameHouse.logger.trace("keepAliveWebSocketsLoop: Playlist webSocket not connected. Reconnecting...", null);
      this.#reconnectPlaylist();
    }
    await kameHouse.core.sleep(config.keepAliveLoopWaitMs);
    if (config.keepAliveLoopWaitMs < -10000) { // fix sonar bug
      this.#syncLoopsConfig.isRunningKeepAliveWebSocketLoop = false;
    }
  }

  /**
   * Http fallback sync loop run.
   */
  async #syncVlcPlayerHttpLoopRun(config) {
    this.#setSyncVlcPlayerHttpWaitMs(config);
    this.#loadStateFromApiSyncVlcPlayerHttpLoop();
    await kameHouse.core.sleep(config.syncVlcPlayerHttpWaitMs);
    if (config.syncVlcPlayerHttpWaitMs < -10000) { // fix sonar bug
      this.#syncLoopsConfig.isRunningSyncVlcPlayerHttpLoop = false;
    }
  }

  /**
   * Set http fallback sync loop wait.
   */
  #setSyncVlcPlayerHttpWaitMs(config) {
    const WEB_SOCKETS_CONNECTED_WAIT_MS = 7000;
    const WEB_SOCKETS_DISCONNECTED_WAIT_MS = 2000;
    if (!this.#vlcRcStatusWebSocket.isConnected() || !this.#playlistWebSocket.isConnected()) {
      config.syncVlcPlayerHttpWaitMs = WEB_SOCKETS_DISCONNECTED_WAIT_MS;
      return;
    }
    config.syncVlcPlayerHttpWaitMs = WEB_SOCKETS_CONNECTED_WAIT_MS;
  }

  /**
   * Load vlc player state from http api.
   */
  #loadStateFromApiSyncVlcPlayerHttpLoop() {
    if (!this.#vlcRcStatusWebSocket.isConnected() || !this.#playlistWebSocket.isConnected()) {
      kameHouse.logger.debug("syncVlcPlayerHttpLoop: Websockets disconnected, synchronizing vlc player through http requests", null);
      this.#vlcPlayer.loadStateFromApi(false);
    } else {
      kameHouse.logger.trace("syncVlcPlayerHttpLoop: Websockets connected. Skipping synchronization through http requests", null);
    }
  }

  /**
   * Print all sync loops statuses.
   */
  #printLoopStatus() {
    const separator = "---------------------------------------------";
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
    const loopsStatus = "Sync loops status:";
    kameHouse.logger.trace(loopsStatus, kameHouse.logger.getYellowText(loopsStatus));
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
    kameHouse.logger.trace("isRunningSyncVlcRcStatusLoop: " + this.#syncLoopsConfig.isRunningSyncVlcRcStatusLoop, null);
    kameHouse.logger.trace("isRunningSyncPlaylistLoop: " + this.#syncLoopsConfig.isRunningSyncPlaylistLoop, null);
    kameHouse.logger.trace("isRunningKeepAliveWebSocketLoop: " + this.#syncLoopsConfig.isRunningKeepAliveWebSocketLoop, null);
    kameHouse.logger.trace("isRunningSyncVlcPlayerHttpLoop: " + this.#syncLoopsConfig.isRunningSyncVlcPlayerHttpLoop, null);
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
    kameHouse.logger.trace("vlcRcStatusLoopCount: " + this.#syncLoopsConfig.vlcRcStatusLoopCount, null);
    kameHouse.logger.trace("vlcPlaylistLoopCount: " + this.#syncLoopsConfig.vlcPlaylistLoopCount, null);
    kameHouse.logger.trace("keepAliveWebSocketLoopCount: " + this.#syncLoopsConfig.keepAliveWebSocketLoopCount, null);
    kameHouse.logger.trace("syncVlcPlayerHttpLoopCount: " + this.#syncLoopsConfig.syncVlcPlayerHttpLoopCount, null);        
    kameHouse.logger.trace(separator, kameHouse.logger.getRedText(separator));
  }

  /**
   * Execute health checks on all sync loops.
   */
  #executeSyncLoopsHealthCheck() {
    kameHouse.logger.trace("Checking state of sync loops", null);
    if (this.#syncLoopsConfig.vlcRcStatusLoopCount <= 0) {
      this.#restartSyncVlcRcStatusLoop(this.#getRestartLoopConfig());
    }
    if (this.#syncLoopsConfig.vlcPlaylistLoopCount <= 0) {
      this.#restartSyncPlaylistLoop(this.#getRestartLoopConfig());
    }
    if (this.#syncLoopsConfig.keepAliveWebSocketLoopCount <= 0) {
      this.#restartKeepAliveWebSocketsLoop(this.#getRestartLoopConfig());
    }
    if (this.#syncLoopsConfig.syncVlcPlayerHttpLoopCount <= 0) {
      this.#restartSyncVlcPlayerHttpLoop(this.#getRestartLoopConfig());
    }
  }

  /**
   * Get restart loop default config.
   */
  #getRestartLoopConfig() {
    const MAX_RETRIES = 30;
    const RESTART_LOOPS_WAIT_MS = 1000;
    return {
      maxRetries : MAX_RETRIES,
      restartLoopWaitMs : RESTART_LOOPS_WAIT_MS,
      restartLoopDelayMs : 7000
    };
  }

  /**
   * Shared logic between all restart loop executions.
   */
  #restartSyncLoopExecution(config, loopName, restartLoopFunction, loopCountName) {
    setTimeout(async () => {
      kameHouse.logger.info("Restarting " + loopName, null);
      let retriesLeft = config.maxRetries;
      let startLoop = true;
      while (this.#syncLoopsConfig[loopCountName] > 0) {
        retriesLeft--;
        kameHouse.logger.trace("Waiting for " + loopName + " to finish before restarting", null);
        await kameHouse.core.sleep(config.restartLoopWaitMs);
        if (retriesLeft <= 0) {
          kameHouse.logger.debug("Too many attempts to restart " + loopName + ". It seems to be running already. Skipping restart", null);
          startLoop = false;
          break;
        }
        if (config.maxRetries < -10000) { // fix sonar bug
          this.#syncLoopsConfig[loopCountName] = 0;
        }
      }
      if (startLoop) {
        restartLoopFunction();
      }
    }, config.restartLoopDelayMs);
  }

  /**
   * Restart vlcRcStatus loop.
   */
  #restartSyncVlcRcStatusLoop(config) {
    const firstRestartConfig = this.#getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 5000;
    this.#restartSyncLoopExecution(firstRestartConfig, "vlcRcStatusLoop", () => {this.#vlcRcStatusRestartFunction()}, "vlcRcStatusLoopCount");
    this.#restartSyncLoopExecution(config, "vlcRcStatusLoop", () => {this.#vlcRcStatusRestartFunction()}, "vlcRcStatusLoopCount");
  }

  /**
   * VlcRcStatus loop restart function.
   */
  #vlcRcStatusRestartFunction() {
    this.#reconnectVlcRcStatus();
    this.syncVlcRcStatusLoop();
  }

  /**
   * Restart playlist loop.
   */
  #restartSyncPlaylistLoop(config) {
    const firstRestartConfig = this.#getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 5000;
    this.#restartSyncLoopExecution(firstRestartConfig, "vlcPlaylistLoop", () => {this.#playlistRestartFunction()}, "vlcPlaylistLoopCount");
    this.#restartSyncLoopExecution(config, "vlcPlaylistLoop", () => {this.#playlistRestartFunction()}, "vlcPlaylistLoopCount");
  }

  /**
   * Playlist loop restart function.
   */
  #playlistRestartFunction() {
    this.#reconnectPlaylist();
    this.syncPlaylistLoop();
  }

  /**
   * Restart keep alive websockets loop.
   */
  #restartKeepAliveWebSocketsLoop(config) {
    const firstRestartConfig = this.#getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 5000;
    this.#restartSyncLoopExecution(firstRestartConfig, "keepAliveWebSocketLoop", () => {this.keepAliveWebSocketsLoop()}, "keepAliveWebSocketLoopCount");
    this.#restartSyncLoopExecution(config, "keepAliveWebSocketLoop", () => {this.keepAliveWebSocketsLoop()}, "keepAliveWebSocketLoopCount");
  }
  
  /**
   * Restart http fallback sync loop.
   */
  #restartSyncVlcPlayerHttpLoop(config) {
    const firstRestartConfig = this.#getRestartLoopConfig();
    firstRestartConfig.restartLoopDelayMs = 2000;
    this.#restartSyncLoopExecution(firstRestartConfig, "syncVlcPlayerHttpLoop", () => {this.syncVlcPlayerHttpLoop()}, "syncVlcPlayerHttpLoopCount");
    config.restartLoopDelayMs = 9000;
    this.#restartSyncLoopExecution(config, "syncVlcPlayerHttpLoop", () => {this.syncVlcPlayerHttpLoop()}, "syncVlcPlayerHttpLoopCount");
  }

} // End VlcPlayerSynchronizer

/** 
 * Represents the Playlist component in vlc-player page. 
 * It also handles the updates to the view of the playlist.
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class VlcPlayerPlaylist {

  #vlcPlayer = null;
  #playSelectedUrl = null;
  #dobleLeftButton = null;
  #dobleRightButton = null;

  #currentPlaylist = null;
  #updatedPlaylist = null;
  #tbodyAbsolutePaths = null;
  #tbodyFilenames = null;
  #tbodyHiddenPlaylist = null;
  #isPlaylistShown = true;

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
    this.#playSelectedUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/commands';
    this.#dobleLeftButton = this.#createDoubleArrowButton("left");
    this.#dobleRightButton = this.#createDoubleArrowButton("right");
  }

  /** Init Playlist. */
  init() {
    kameHouse.util.dom.replaceWithById("toggle-playlist-filenames-btn", this.#dobleRightButton);
  }

  /** Set updated playlist: Temporary storage for the playlist I receive from the websocket */
  setUpdatedPlaylist(updatedPlaylistParam) { 
    this.#updatedPlaylist = updatedPlaylistParam; 
  }  

  /** Reload playlist updating the playlist view. */
  reload() {
    if (!this.#isPlaylistUpdated(this.#currentPlaylist, this.#updatedPlaylist)) {
      // Playlist content not updated, just update currently playing element and return
      this.#highlightCurrentPlayingItem();
      return;
    }
    this.#currentPlaylist = this.#updatedPlaylist;
    // Clear the playlist
    const playlistTableBody = document.getElementById('playlist-table-body');
    kameHouse.util.dom.empty(playlistTableBody);
    if (this.#isEmptyPlaylist()) {
      kameHouse.util.dom.append(playlistTableBody, this.#getEmptyPlaylistTr());
      this.#updatePlaylistSize();
      return;
    }
    // Add the new playlist items received from the server.
    this.#initInternalPlaylists();
    this.#rebuildInternalPlaylists();
    this.renderPlaylist();
  }

  /**
   * Render playlist.
   */
  renderPlaylist() {
    const playlistTableBody = document.getElementById('playlist-table-body');
    if (!kameHouse.core.isEmpty(playlistTableBody)) {
      kameHouse.util.dom.detach(playlistTableBody);
    }
    const playlistTable = document.getElementById('playlist-table');
    if (this.#showPlaylist()) {
      kameHouse.logger.info("Show playlist content", null);
      this.#isPlaylistShown = true;
      kameHouse.util.cookies.setCookie("kh-vlc-player-show-playlist-checkbox", "true", null);
      kameHouse.util.dom.append(playlistTable, this.#tbodyFilenames);
      this.#highlightCurrentPlayingItem();
      this.#vlcPlayer.filterPlaylistRows();
    } else {
      kameHouse.logger.info("Hide playlist content", null);
      this.#isPlaylistShown = false;
      kameHouse.util.cookies.setCookie("kh-vlc-player-show-playlist-checkbox", "false", null);
      kameHouse.util.dom.append(playlistTable, this.#tbodyHiddenPlaylist);
    }
    this.#updatePlaylistSize();
  }

  /** Scroll to the current playing element in the playlist. */
  scrollToCurrentlyPlaying() {
    const currentPlId = this.#vlcPlayer.getVlcRcStatus().currentPlId;
    const currentPlayingRowId = 'playlist-table-row-id-' + currentPlId;
    const currentPlayingRow = document.getElementById(currentPlayingRowId);
    kameHouse.logger.debug("Scroll to " + currentPlayingRowId, null);
    if (!kameHouse.core.isEmpty(currentPlayingRow)) {
      const playlistTableWrapper = document.getElementById('playlist-table-wrapper');
      kameHouse.core.scrollTop(playlistTableWrapper, 0);
      const scrollToOffset = kameHouse.core.offset(currentPlayingRow).top - kameHouse.core.offset(playlistTableWrapper).top;
      kameHouse.core.scrollTop(playlistTableWrapper, scrollToOffset);
    }
  }

  /** 
   * Update the playlist view. Add all the functionality that needs to happen 
   * to update the view of the playlist when vlcRcStatus changes  
   */
  updateView() {
    if (!kameHouse.core.isEmpty(this.#vlcPlayer.getVlcRcStatus())) {
      this.#highlightCurrentPlayingItem();
    } else {
      this.resetView();
    }
  }  

  /** 
   * Reset the playlist view.
   */
  resetView() {
    this.#updatedPlaylist = null;
    this.reload();
  }

  /**
   * Init internal playlists properties.
   */
  #initInternalPlaylists() {
    this.#tbodyFilenames = this.#getPlaylistTbody();
    this.#tbodyAbsolutePaths = this.#getPlaylistTbody();
    this.#tbodyHiddenPlaylist = this.#getPlaylistTbody();
    kameHouse.util.dom.append(this.#tbodyHiddenPlaylist, this.#getHiddenPlaylistTr());
  }

  /**
   * Build the playlist with the items received from the backend
   */
  #rebuildInternalPlaylists() {
    for (const currentPlaylistElement of this.#currentPlaylist) {
      const absolutePath = currentPlaylistElement.filename;
      const filename = kameHouse.util.file.getShortFilename(absolutePath);
      const playlistElementId = currentPlaylistElement.id;
      kameHouse.util.dom.append(this.#tbodyFilenames, this.#getPlaylistTr(filename, playlistElementId));
      kameHouse.util.dom.append(this.#tbodyAbsolutePaths, this.#getPlaylistTr(absolutePath, playlistElementId));
    }
  }

  /**
   * Returns true if the playlist should be rendered.
   */
  #showPlaylist() {
    const showPlaylistCheckbox = document.getElementById("vlc-player-show-playlist-checkbox") as HTMLInputElement;
    if (kameHouse.core.isEmpty(showPlaylistCheckbox)) {
      return true;
    }
    return showPlaylistCheckbox.checked;
  }

  /**
   * Update the playlist size view.
   */
  #updatePlaylistSize() {
    if (!this.#isEmptyPlaylist()) {
      kameHouse.util.dom.setHtmlById("playlist-size-val", this.#currentPlaylist.length);
      kameHouse.util.dom.classListRemoveById("playlist-size-wrapper", "hidden-kh");
    } else {
      kameHouse.util.dom.setHtmlById("playlist-size-val", "0");
      kameHouse.util.dom.classListAddById("playlist-size-wrapper", "hidden-kh");
    }
  }

  /**
   * Check if current playlist is empty.
   */
  #isEmptyPlaylist() {
    return kameHouse.core.isEmpty(this.#currentPlaylist) || kameHouse.core.isEmpty(this.#currentPlaylist.length) || this.#currentPlaylist.length <= 0;
  }

  /** Create a button to toggle when expanding/collapsing playlist browser filenames. */
  #createDoubleArrowButton(direction) {
    return kameHouse.util.dom.getButton({
      attr: {
        id: "toggle-playlist-filenames-btn",
        class: "img-btn-kh img-btn-s-kh btn-playlist-controls va-m-kh",
      },
      mobileClass: "img-btn-kh-mobile",
      backgroundImg: "/kame-house/img/other/double-" + direction + "-green.png",
      html: null,
      data: null,
      click: (event, data) => this.#toggleExpandPlaylistFilenames()
    });
  }

  /** Compares two playlists. Returns true if they are different or empty. Expects 2 vlc playlist arrays */
  #isPlaylistUpdated(currentPls, updatedPls) {
    const MAX_COMPARISONS = 30;
    // For empty playlists, return true, so it updates the UI
    if (kameHouse.core.isEmpty(currentPls) || kameHouse.core.isEmpty(updatedPls)) {
      return true;
    }
    // If the sizes don't match, it's updated
    if (currentPls.length != updatedPls.length) {
      return true;
    }
    // If the sizes match, compare playlists elements in the specified increment. 
    // Don't check all filenames to avoid doing too many comparisons in very large playlists
    let step = 0;
    if (currentPls.length <= MAX_COMPARISONS) {
      step = 1;
    } else if ((currentPls.length > MAX_COMPARISONS) && (currentPls.length <= MAX_COMPARISONS * 2)) {
      step = 2;
    }
    if (step == 0) {
      step = Math.round(currentPls.length / MAX_COMPARISONS);
    }
    for (let i = 0; i < currentPls.length; i = i + step) {
      if (currentPls[i].filename != updatedPls[i].filename) {
        return true;
      }
    }
    // Playlist is not updated
    return false;
  }

  /** Play the clicked element from the playlist. */
  #clickEventOnPlaylistRow(event, data) {
    kameHouse.logger.debug("Play playlist id: " + data.id, null);
    const requestBody = {
      name: 'pl_play',
      id: data.id
    };
    this.#vlcPlayer.getRestClient().post(this.#playSelectedUrl, kameHouse.http.getApplicationJsonHeaders(), requestBody, 
      () => {}, 
      () => {});
  }

  /** Highlight currently playing item in the playlist. */
  #highlightCurrentPlayingItem() {
    const currentPlId = this.#vlcPlayer.getVlcRcStatus().currentPlId;
    const currentPlIdAsRowId = 'playlist-table-row-id-' + currentPlId;
    document.querySelectorAll('#playlist-table-body tr td button').forEach((element) => {
      kameHouse.util.dom.classListRemove(element, "active");
    });
    const currentPlaylistElement = document.querySelector("#" + currentPlIdAsRowId + " td button");
    if (currentPlaylistElement) {
      kameHouse.util.dom.classListAdd(currentPlaylistElement, "active");
      if (!kameHouse.core.isEmpty(this.#currentPlaylist) && this.#currentPlaylist.length == 1) {
        kameHouse.util.dom.setHtmlById("playlist-current-position-val", "1");
      } else {
        // In playlists with more than one element, currentPlId starts at 4, so need -3
        kameHouse.util.dom.setHtmlById("playlist-current-position-val", currentPlId - 3);
      }
    } else {
      kameHouse.util.dom.setHtmlById("playlist-current-position-val", "0");
    }
  }

  /** Toggle expand or collapse filenames in the playlist */
  #toggleExpandPlaylistFilenames() {
    if (kameHouse.core.isEmpty(this.#tbodyFilenames) || kameHouse.core.isEmpty(this.#tbodyFilenames.firstElementChild)) {
      return;
    }
    if (!this.#isPlaylistShown) {
      return;
    }
    const filenamesFirstFile = this.#tbodyFilenames.firstElementChild.textContent;
    const currentFirstFile = document.getElementById('playlist-table-body').firstElementChild.textContent;
    const playlistTable = document.getElementById('playlist-table');
    let isExpandedFilename = null;

    if (currentFirstFile == filenamesFirstFile) {
      // currently displaying filenames, switch to absolute paths 
      if (!kameHouse.core.isEmpty(this.#tbodyFilenames)) {
        kameHouse.util.dom.detach(this.#tbodyFilenames);
      }
      kameHouse.util.dom.append(playlistTable, this.#tbodyAbsolutePaths);
      isExpandedFilename = true;
    } else {
      // currently displaying absolute paths, switch to filenames 
      if (!kameHouse.core.isEmpty(this.#tbodyAbsolutePaths)) {
        kameHouse.util.dom.detach(this.#tbodyAbsolutePaths);
      }
      kameHouse.util.dom.append(playlistTable, this.#tbodyFilenames);
      isExpandedFilename = false;
    }
    this.#highlightCurrentPlayingItem();
    this.#updateExpandPlaylistFilenamesIcon(isExpandedFilename);
    this.#vlcPlayer.filterPlaylistRows();
  }

  /** Update the icon to expand or collapse the playlist filenames */
  #updateExpandPlaylistFilenamesIcon(isExpandedFilename) {
    if (isExpandedFilename) {
      kameHouse.util.dom.replaceWithById("toggle-playlist-filenames-btn", this.#dobleLeftButton);
    } else {
      kameHouse.util.dom.replaceWithById("toggle-playlist-filenames-btn", this.#dobleRightButton);
    }
  }

  /**
   * Get empty playlist table row.
   */
  #getEmptyPlaylistTr() {
    const madaMadaDane = 'まだまだだね';
    return kameHouse.util.dom.getTrTd("No playlist loaded. " + madaMadaDane);
  }

  /**
   * Get hidden playlist table row.
   */
  #getHiddenPlaylistTr() {
    const row = kameHouse.util.dom.getTrTd("Playlist content is hidden");
    kameHouse.util.dom.classListAdd(row, "vlc-player-playlist-hidden");
    return row;
  }

  /**
   * Get playlist table body.
   */
  #getPlaylistTbody() {
    return kameHouse.util.dom.getTbody({
      id: "playlist-table-body"
    }, null);
  }

  /**
   * Get playlist table row.
   */
  #getPlaylistTr(displayName, playlistElementId) {
    return kameHouse.util.dom.getTr({
      id: "playlist-table-row-id-" + playlistElementId
    }, kameHouse.util.dom.getTd({}, this.#getPlaylistTrBtn(displayName, playlistElementId)));
  }

  /**
   * Get playlist table row button.
   */
  #getPlaylistTrBtn(displayName, playlistElementId) {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "playlist-table-btn",
      },
      mobileClass: null,
      backgroundImg: null,
      html: displayName,
      data: {
        id: playlistElementId
      },
      click: (event, data) => {this.#clickEventOnPlaylistRow(event, data)}
    });
  }
} // End VlcPlayerPlaylist

/** 
 * Represents an internal rest client for the VlcPlayer to split functionality. 
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor
 * and added as a property to VlcPlayer.restClient inside that constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains this prototype.
 * 
 * @author nbrest
 */
class VlcPlayerRestClient {

  #vlcPlayer = null;

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
  }

  /** Execute GET on the specified url and display the output in the debug table. */
  get(url, requestHeaders, requestBody, updateCursor, successCallback, errorCallback) {
    if (updateCursor) {
      kameHouse.util.cursor.setCursorWait();
    }
    const config = kameHouse.http.getConfig();
    config.timeout = 10;
    kameHouse.plugin.debugger.http.get(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.util.cursor.setCursorDefault();
        if (!kameHouse.core.isEmpty(successCallback)) {
          successCallback(responseBody, responseCode, responseDescription, responseHeaders);
        } else {
          this.#apiCallSuccessDefault(responseBody);
        }
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.util.cursor.setCursorDefault();
        if (!kameHouse.core.isEmpty(errorCallback)) {
          errorCallback(responseBody, responseCode, responseDescription, responseHeaders);
        } else {
          this.#apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders);
          if (responseCode == "404") {
            kameHouse.logger.error("Could not connect to VLC player to get the status.", null);
          }
        }
      });
  }

  /** Execute a POST request to the specified url with the specified request body. */
  post(url, requestHeaders, requestBody, successCallback, errorCallback) {
    kameHouse.util.cursor.setCursorWait();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.util.cursor.setCursorDefault();
        this.#apiCallSuccessDefault(responseBody);
        if (kameHouse.core.isFunction(successCallback)) {
          successCallback();
        }
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.util.cursor.setCursorDefault();
        this.#apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders);
        if (kameHouse.core.isFunction(errorCallback)) {
          errorCallback();
        }
      }
    );
  }

  /** Execute a DELETE request to the specified url with the specified request body. */
  delete(url, requestHeaders, requestBody, successCallback, errorCallback) {
    kameHouse.util.cursor.setCursorWait();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, url, requestHeaders, requestBody,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.util.cursor.setCursorDefault();
        this.#apiCallSuccessDefault(responseBody)
        if (kameHouse.core.isFunction(successCallback)) {
          successCallback();
        }
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.util.cursor.setCursorDefault();
        this.#apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders);
        if (kameHouse.core.isFunction(errorCallback)) {
          errorCallback();
        }
      }
    );
  }

  /** Default actions for succesful api responses */
  #apiCallSuccessDefault(responseBody) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    this.#vlcPlayer.pollVlcRcStatus();
  }

  /** Default actions for error api responses */
  #apiCallErrorDefault(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    // Don't display api errors for not found or service not available errors or cordova mock
    if (responseCode != 404 && responseCode != 503 && responseCode != 999 && responseCode > 300) {
      kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    }
  }
} // End VlcPlayerRestClient

/** 
 * Handles the debugger functionality of vlc player in the debugger's custom area.
 * 
 * This class is meant to be instantiated by VlcPlayer() constructor.
 * It's not meant to be used standalone. The vlcPlayer parameter to the constructor
 * should be a reference to the enclosing VlcPlayer that contains prototype.
 * 
 * @author nbrest
 */
class VlcPlayerDebugger {

  #vlcPlayer = null;
  #vlcRcStatusApiUrl = null;
  #playlistApiUrl = null;

  constructor(vlcPlayer) {
    this.#vlcPlayer = vlcPlayer;
    this.#vlcRcStatusApiUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/status';
    this.#playlistApiUrl = '/kame-house-vlcrc/api/v1/vlc-rc/players/' + vlcPlayer.getHostname() + '/playlist';
  }

  /** Get the vlcRcStatus from an http api call instead of from the websocket. */
  getVlcRcStatusFromApi(updateCursor) { 
    this.#vlcPlayer.getRestClient().get(this.#vlcRcStatusApiUrl, null, null, updateCursor, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#getVlcRcStatusApiSuccessCallback(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#getVlcRcStatusApiErrorCallback(responseBody, responseCode, responseDescription, responseHeaders)}); 
  }

  /** Get the playlist from an http api call instead of from the websocket. */
  getPlaylistFromApi(updateCursor) { 
    this.#vlcPlayer.getRestClient().get(this.#playlistApiUrl, null, null, updateCursor, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#getPlaylistApiSuccessCallback(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#getPlaylistApiErrorCallback(responseBody, responseCode, responseDescription, responseHeaders)}); 
  }

  /** Update the main player view. */
  #getVlcRcStatusApiSuccessCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    this.#vlcPlayer.setVlcRcStatus(responseBody);
    this.#vlcPlayer.updateView();
  }

  /** Handle api error on get vlcrc status. */
  #getVlcRcStatusApiErrorCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.logger.error("Unable to get vlcRcStatus from an API call. This can happen if vlc player process isn't running or a timeout. ResponseCode: " + responseCode, null);
  }

  /** Update the playlist view. */
  #getPlaylistApiSuccessCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    this.#vlcPlayer.getPlaylist().setUpdatedPlaylist(responseBody);
    this.#vlcPlayer.getPlaylist().reload();
  }

  /** Handle api error on get playlist. */
  #getPlaylistApiErrorCallback(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.logger.error("Unable to get the playlist from an API call. This can happen if vlc player process isn't running or a timeout. ResponseCode: " + responseCode, null);
  }

} // End VlcPlayerDebugger

/**
 * VlcRcStatus.
 */
class VlcRcStatus {

  fullscreen: boolean;
  repeat: boolean;
  subtitleDelay: number;
  aspectRatio: string;
  audioDelay: number;
  apiVersion: number;
  currentPlId: number;
  time: number;
  volume: number;
  length: number;
  random: boolean;
  rate: number;
  state: string;
  loop: boolean;
  position: number;
  version: string;
  equalizer: any;
  audioFilters: any;
  stats: any;
  videoEffects: any;
  information: any;

} // VlcRcStatus

/**
 * Information.
 */
class Information {

  chapter: string;
  chapters: string[];
  title: string;
  titles: string[];
  audio: any;
  meta: Meta;
  subtitle: any;
  video: any;

} // Information

/**
 * Meta.
 */
class Meta {
  
  name: string;
  filename: string;
  title: string;
  artist: string;
  setting: string;
  software: string;
  artworkUrl: string;

} // Meta

kameHouse.ready(() => {kameHouse.addExtension("vlcPlayer", new VlcPlayer("localhost"))});