/** 
 * Represents the playlist browser component in vlc-player page.
 * It doesn't control the currently active playlist.
 * 
 * @author nbrest
 */
class PlaylistBrowser {

  #mediaVideoAllPlaylistsUrl = '/kame-house-media/api/v1/media/video/playlists';
  #mediaVideoPlaylistUrl = '/kame-house-media/api/v1/media/video/playlist';

  #dobleLeftButton = null;
  #dobleRightButton = null;
  #videoPlaylists = [];
  #videoPlaylistCategories = [];
  #currentPlaylist = null;
  #tbodyAbsolutePaths = null;
  #tbodyFilenames = null;
  #tbodyHiddenPlaylist = null;
  #isPlaylistShown = true;

  constructor() {
    this.#dobleLeftButton = this.#createDoubleArrowButton("left");
    this.#dobleRightButton = this.#createDoubleArrowButton("right");
  }

  /** Load Playlist Browser extension. */
  load() {
    kameHouse.logger.info("Started initializing playlist browser", null);
    kameHouse.util.dom.replaceWithById("toggle-playlist-browser-filenames-btn", this.#dobleRightButton);
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger"], () => {
      this.populateVideoPlaylistCategories();
      kameHouse.util.module.setModuleLoaded("playlistBrowser");
    });
  }

  /** Filter playlist browser rows based on the search string. */
  filterPlaylistRows() {
    const filterString = (document.getElementById("playlist-browser-filter-input") as HTMLInputElement).value;
    kameHouse.util.table.filterTableRows(filterString, 'playlist-browser-table-body', null, null);
  }

  /** Populate playlist categories dropdown. */
  populateVideoPlaylistCategories() {
    
    this.#resetPlaylistDropdown();
    this.#resetPlaylistCategoryDropdown();

    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#mediaVideoAllPlaylistsUrl, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        this.#videoPlaylists = responseBody;
        this.#videoPlaylistCategories = [...new Set(this.#videoPlaylists.map((playlist) => playlist.category))];
        kameHouse.logger.debug("Playlists: " + kameHouse.json.stringify(this.#videoPlaylists, null, null), null);
        kameHouse.logger.debug("Playlist categories: " + this.#videoPlaylistCategories, null);
        const playlistCategoryDropdown = document.getElementById('playlist-category-dropdown');
        this.#videoPlaylistCategories.forEach((category) => {
          const categoryFormatted = category.replace(/\\/g, ' | ')
                                            .replace(/\//g, ' | ')
                                            .replace(/_/g, ' ');
          kameHouse.util.dom.append(playlistCategoryDropdown, this.#getPlaylistCategoryOption(category, this.#capitalizeAllWords(categoryFormatted)));
        });
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.error("Error populating video playlist categories", null);
      });
  }

  /** Populate video playlists dropdown when a playlist category is selected. */
  populateVideoPlaylists() {
    const playlistCategoriesList = document.getElementById('playlist-category-dropdown') as HTMLSelectElement;
    const selectedPlaylistCategory = playlistCategoriesList.options[playlistCategoriesList.selectedIndex].value;
    kameHouse.logger.debug("Selected Playlist Category: " + selectedPlaylistCategory, null);
    this.#resetPlaylistDropdown();
    const playlistDropdown = document.getElementById('playlist-dropdown');
    this.#videoPlaylists.forEach((entry) => {
      if (entry.category === selectedPlaylistCategory) {
        const playlistName = entry.name.replace(/.m3u+$/, "")
                                       .replace(/_/g, " ");
        kameHouse.util.dom.append(playlistDropdown, this.#getPlaylistOption(entry.path, this.#capitalizeAllWords(playlistName)));
      }
    });
  }

  /** Load the selected playlist's content in the view */
  loadPlaylistContent() {
    const playlistFilename = this.#getSelectedPlaylist();
    kameHouse.logger.debug("Getting content for " + playlistFilename, null);
    const requestParam = {
      "path" : playlistFilename
    };
    const config = kameHouse.http.getConfig();
    kameHouse.util.cursor.setCursorWait();
    kameHouse.plugin.debugger.http.get(config, this.#mediaVideoPlaylistUrl, kameHouse.http.getUrlEncodedHeaders(), requestParam,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        this.#currentPlaylist = responseBody;
        this.#populatePlaylistBrowserTable();
        kameHouse.util.cursor.setCursorDefault();
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.error("Error getting playlist content", null);
        kameHouse.util.cursor.setCursorDefault();
      });
  }

  /** Play selected file in the VlcPlayer */
  playSelectedPlaylist() {
    const playlist = this.#getSelectedPlaylist();
    kameHouse.extension.vlcPlayer.playFile(playlist);
    kameHouse.extension.vlcPlayer.openTab('tab-playing');
    kameHouse.extension.vlcPlayer.reloadPlaylist();
  }

  /**
   * Render playlist.
   */
  renderPlaylist() {
    const playlistBrowserTableBody = document.getElementById('playlist-browser-table-body');     
    if (!kameHouse.core.isEmpty(playlistBrowserTableBody)) {
      kameHouse.util.dom.detach(playlistBrowserTableBody);
    }
    const playlistBrowserTable = document.getElementById('playlist-browser-table');
    if (this.#showPlaylist()) {
      kameHouse.logger.info("Show playlist browser content", null);
      this.#isPlaylistShown = true;
      kameHouse.util.cookies.setCookie("kh-vlc-player-show-playlist-browser-checkbox", "true", null);
      kameHouse.util.dom.append(playlistBrowserTable, this.#tbodyFilenames);
      kameHouse.util.dom.classListAddById('playlist-browser-hidden-section', "hidden-kh");
      kameHouse.util.dom.classListRemoveById('playlist-browser-controls', "hidden-kh");
      kameHouse.util.dom.classListRemoveById('playlist-browser-table-wrapper', "hidden-kh");
      this.filterPlaylistRows();
    } else {
      kameHouse.logger.info("Hide playlist browser content", null);
      this.#isPlaylistShown = false;
      kameHouse.util.cookies.setCookie("kh-vlc-player-show-playlist-browser-checkbox", "false", null);
      kameHouse.util.dom.append(playlistBrowserTable, this.#tbodyHiddenPlaylist);
      kameHouse.util.dom.classListRemoveById('playlist-browser-hidden-section', "hidden-kh");
      kameHouse.util.dom.classListAddById('playlist-browser-controls', "hidden-kh");
      kameHouse.util.dom.classListAddById('playlist-browser-table-wrapper', "hidden-kh");
    }
    this.#updatePlaylistBrowserSize();
  }

  /** Create a button object to toggle when expanding/collapsing playlist browser filenames. */
  #createDoubleArrowButton(direction) {
    return kameHouse.util.dom.getButton({
      attr: {
        id: "toggle-playlist-browser-filenames-btn",
        class: "img-btn-kh img-btn-s-kh btn-playlist-controls va-m-kh",
      },
      mobileClass: "img-btn-kh-mobile",
      backgroundImg: "/kame-house/img/other/double-" + direction + "-green.png",
      html: null,
      data: null,
      click: (event, data) => this.#toggleExpandPlaylistFilenames()
    });
  }

  /** Returns the selected playlist from the dropdowns. */
  #getSelectedPlaylist() {
    const playlistSelected = (document.getElementById("playlist-dropdown") as HTMLSelectElement).value;
    kameHouse.logger.debug("Playlist selected: " + playlistSelected, null);
    return playlistSelected;
  }

  /**
   * Convert all words to upper case.
   */
  #capitalizeAllWords(string) {
    if (kameHouse.core.isEmpty(string)) {
      return string;
    }
    const arr = string.split(" ");
    for (let i = 0; i < arr.length; i++) {
        arr[i] = arr[i].charAt(0).toUpperCase() + arr[i].slice(1);
    }
    return arr.join(" ");
  }

  /**
   * Reset playlist dropdown view.
   */
  #resetPlaylistDropdown() {
    const playlistDropdown = document.getElementById('playlist-dropdown');
    kameHouse.util.dom.empty(playlistDropdown);
    kameHouse.util.dom.append(playlistDropdown, this.#getInitialDropdownOption("Playlist"));
  }

  /**
   * Reset playlist category dropdown view.
   */
  #resetPlaylistCategoryDropdown() {
    const playlistCategoryDropdown = document.getElementById('playlist-category-dropdown');
    kameHouse.util.dom.empty(playlistCategoryDropdown);
    kameHouse.util.dom.append(playlistCategoryDropdown, this.#getInitialDropdownOption("Playlist Category"));
  }

  /** Populate the playlist table for browsing. */
  #populatePlaylistBrowserTable() {
    const playlistTableBody = document.getElementById('playlist-browser-table-body');
    kameHouse.util.dom.empty(playlistTableBody);
    if (kameHouse.core.isEmpty(this.#currentPlaylist)) {
      kameHouse.util.dom.append(playlistTableBody, this.#getEmptyPlaylistTr());
      this.#updatePlaylistBrowserSize();
      return;
    }
    this.#initInternalPlaylists();
    this.#rebuildInternalPlaylists();
    this.renderPlaylist();
  }

  /**
   * Init internal playlists properties.
   */
  #initInternalPlaylists() {
    this.#tbodyFilenames = this.#getPlaylistBrowserTbody();
    this.#tbodyAbsolutePaths = this.#getPlaylistBrowserTbody();
    this.#tbodyHiddenPlaylist = this.#getPlaylistBrowserTbody();
  }

  /**
   * Build the playlist with the items received from the backend
   */
  #rebuildInternalPlaylists() {
    for (const file of this.#currentPlaylist.files) {
      const absolutePath = file;
      const filename = kameHouse.util.file.getShortFilename(absolutePath);
      kameHouse.util.dom.append(this.#tbodyFilenames, this.#getPlaylistBrowserTr(filename, absolutePath));
      kameHouse.util.dom.append(this.#tbodyAbsolutePaths, this.#getPlaylistBrowserTr(absolutePath, absolutePath));
    }
  }
  
  /**
   * Returns true if the playlist should be rendered.
   */
  #showPlaylist() {
    const showPlaylistBrowserCheckbox = document.getElementById("vlc-player-show-playlist-browser-checkbox") as HTMLInputElement;
    if (kameHouse.core.isEmpty(showPlaylistBrowserCheckbox)) {
      return true;
    }
    return showPlaylistBrowserCheckbox.checked;
  }

  /**
   * Update the playlist browser size view.
   */
  #updatePlaylistBrowserSize() {
    kameHouse.logger.trace("Updating playlist browser size", null);
    if (!this.#isEmptyPlaylist()) {
      kameHouse.util.dom.setHtmlById("playlist-browser-size", this.#currentPlaylist.files.length);
      kameHouse.util.dom.classListRemoveById("playlist-browser-size-wrapper", "hidden-kh");
    } else {
      kameHouse.util.dom.setHtmlById("playlist-browser-size", "0");
      kameHouse.util.dom.classListAddById("playlist-browser-size-wrapper", "hidden-kh");
    }
  }

  /**
   * Check if current playlist to browse is empty.
   */
  #isEmptyPlaylist() {
    return kameHouse.core.isEmpty(this.#currentPlaylist) || kameHouse.core.isEmpty(this.#currentPlaylist.files) || kameHouse.core.isEmpty(this.#currentPlaylist.files.length) || this.#currentPlaylist.files.length <= 0;
  }

  /** Play the clicked element from the playlist. */
  #clickEventOnPlaylistBrowserRow(event, data) {
    const filename = data.filename;
    kameHouse.logger.info("Play selected playlist browser file : " + filename, null);
    kameHouse.extension.vlcPlayer.playFile(filename);
    kameHouse.extension.vlcPlayer.openTab('tab-playing');
  }

  /** Toggle expand or collapse filenames in the playlist */
  #toggleExpandPlaylistFilenames() {
    if (kameHouse.core.isEmpty(this.#tbodyFilenames) || kameHouse.core.isEmpty(this.#tbodyFilenames.firstElementChild)) {
      return;
    }
    if (!this.#isPlaylistShown) {
      return;
    }
    let isExpandedFilename = null;
    const filenamesFirstFile = this.#tbodyFilenames.firstElementChild.textContent;
    const currentFirstFile = document.getElementById('playlist-browser-table-body').firstElementChild.textContent;
    const playlistTable = document.getElementById('playlist-browser-table');

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
    this.#updateExpandPlaylistFilenamesIcon(isExpandedFilename);
    this.filterPlaylistRows();
  }
  
  /** Update the icon to expand or collapse the playlist filenames */
  #updateExpandPlaylistFilenamesIcon(isExpandedFilename) {
    if (isExpandedFilename) {
      kameHouse.util.dom.replaceWithById("toggle-playlist-browser-filenames-btn", this.#dobleLeftButton);
    } else {
      kameHouse.util.dom.replaceWithById("toggle-playlist-browser-filenames-btn", this.#dobleRightButton);
    }
  }

  /**
   * Get initial dropdown option.
   */
  #getInitialDropdownOption(optionText) {
    return kameHouse.util.dom.getOption({
      disabled: true,
      selected: true
    }, optionText);
  }

  /**
   * Get playlist option.
   */
  #getPlaylistOption(entry, category) {
    return kameHouse.util.dom.getOption({
      value: entry
    }, category);
  }

  /**
   * Get playlist category option.
   */
  #getPlaylistCategoryOption(path, playlistName) {
    return kameHouse.util.dom.getOption({
      value: path
    }, playlistName);
  }
  
  /**
   * Get playlist browser table body.
   */
  #getPlaylistBrowserTbody() {
    return kameHouse.util.dom.getTbody({
      id: "playlist-browser-table-body"
    }, null);
  }

  /**
   * Get empty playlist table row.
   */
  #getEmptyPlaylistTr() {
    return kameHouse.util.dom.getTrTd("No playlist to browse loaded yet or unable to sync. まだ まだ だね :)");
  }

  /**
   * Get playlist browser table row.
   */
  #getPlaylistBrowserTr(displayName, filePath) {
    return kameHouse.util.dom.getTrTd(this.#getPlaylistBrowserTrButton(displayName, filePath));
  }

  /**
   * Get playlist browser table row button.
   */
  #getPlaylistBrowserTrButton(displayName, filePath) {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "playlist-browser-table-btn",
      },
      mobileClass: null,
      backgroundImg: null,
      html: displayName,
      data: {
        filename: filePath
      },
      click: (event, data) => this.#clickEventOnPlaylistBrowserRow(event, data)
    });
  }
}

kameHouse.ready(() => {kameHouse.addExtension("playlistBrowser", new PlaylistBrowser())});