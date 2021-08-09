/** 
 * Represents the playlist browser component in vlc-player page.
 * It doesn't control the currently active playlist.
 * 
 * Dependencies: tableUtils, logger, debuggerHttpClient
 * 
 * @author nbrest
 */
function PlaylistBrowser(vlcPlayer) {

  let self = this;
  this.vlcPlayer = vlcPlayer;
  this.videoPlaylists = [];
  this.videoPlaylistCategories = [];
  this.currentPlaylist = null;
  const mediaVideoAllPlaylistsUrl = '/kame-house-media/api/v1/media/video/playlists';
  const mediaVideoPlaylistUrl = '/kame-house-media/api/v1/media/video/playlist';
  this.tbodyAbsolutePaths = null;
  this.tbodyFilenames = null;
  this.dobleLeftImg = null;
  this.dobleRightImg = null;

  /** Init Playlist Browser. */
  this.init = function init() {
    logger.debug(arguments.callee.name);
    self.dobleLeftImg = self.createDoubleArrowImg("left");
    self.dobleRightImg = self.createDoubleArrowImg("right");
    $("#toggle-playlist-browser-filenames-img").replaceWith(self.dobleRightImg);
  }

  /** Create an image object to toggle when expanding/collapsing playlist browser filenames. */
  this.createDoubleArrowImg = (direction) => {
    let dobleArrowImg = new Image();
    dobleArrowImg.id = "toggle-playlist-browser-filenames-img";
    dobleArrowImg.src = "/kame-house/img/other/double-" + direction + "-green.png";
    dobleArrowImg.className = "img-btn-kh img-btn-s-kh btn-playlist-controls";
    dobleArrowImg.alt = "Expand/Collapse Filename";
    dobleArrowImg.title = "Expand/Collapse Filename";
    dobleArrowImg.onclick = () => self.toggleExpandPlaylistFilenames();
    return dobleArrowImg;
  }

  /** Filter playlist browser rows based on the search string. */
  this.filterPlaylistRows = () => {
    let filterString = document.getElementById("playlist-browser-filter-input").value;
    tableUtils.filterTableRows(filterString, 'playlist-browser-table-body');
  }

  /** Returns the selected playlist from the dropdowns. */
  this.getSelectedPlaylist = function getSelectedPlaylist() {
    logger.debug(arguments.callee.name);
    let playlistSelected = document.getElementById("playlist-dropdown").value;
    logger.debug("Playlist selected: " + playlistSelected);
    return playlistSelected;
  }

  /** Populate playlist categories dropdown. */
  this.populateVideoPlaylistCategories = function populateVideoPlaylistCategories() {
    logger.debug(arguments.callee.name);
    
    self.resetPlaylistDropdown();
    self.resetPlaylistCategoryDropdown();

    debuggerHttpClient.get(mediaVideoAllPlaylistsUrl, 
      (responseBody, responseCode, responseDescription) => {
        self.videoPlaylists = responseBody;
        self.videoPlaylistCategories = [...new Set(self.videoPlaylists.map(playlist => playlist.category))];
        logger.debug("Playlists: " + JSON.stringify(self.videoPlaylists));
        logger.debug("Playlist categories: " + self.videoPlaylistCategories);
        let playlistCategoryDropdown = $('#playlist-category-dropdown');
        $.each(self.videoPlaylistCategories, function (key, entry) {
          let category = entry;
          let categoryFormatted = category.replace(/\\/g, ' | ').replace(/\//g, ' | ');
          playlistCategoryDropdown.append(self.getPlaylistCategoryOption(entry, categoryFormatted));
        });
      },
      (responseBody, responseCode, responseDescription) => 
        kameHouseDebugger.displayResponseData("Error populating video playlist categories", responseCode)
      );
  }

  /**
   * Reset playlist dropdown view.
   */
  this.resetPlaylistDropdown = () => {
    let playlistDropdown = $('#playlist-dropdown');
    domUtils.empty(playlistDropdown);
    playlistDropdown.append(self.getInitialDropdownOption("Playlist"));
  }

  /**
   * Reset playlist category dropdown view.
   */
  this.resetPlaylistCategoryDropdown = () => {
    let playlistCategoryDropdown = $('#playlist-category-dropdown');
    domUtils.empty(playlistCategoryDropdown);
    playlistCategoryDropdown.append(self.getInitialDropdownOption("Playlist Category"));
  }

  /** Populate video playlists dropdown when a playlist category is selected. */
  this.populateVideoPlaylists = function populateVideoPlaylists() {
    logger.debug(arguments.callee.name);
    let playlistCategoriesList = document.getElementById('playlist-category-dropdown');
    let selectedPlaylistCategory = playlistCategoriesList.options[playlistCategoriesList.selectedIndex].value;
    logger.debug("Selected Playlist Category: " + selectedPlaylistCategory);
    self.resetPlaylistDropdown();
    let playlistDropdown = $('#playlist-dropdown');
    $.each(self.videoPlaylists, (key, entry) => {
      if (entry.category === selectedPlaylistCategory) {
        let playlistName = entry.name;
        playlistName = playlistName.replace(/.m3u+$/, "");
        playlistDropdown.append(self.getPlaylistOption(entry.path, playlistName));
      }
    });
  }

  /** Load the selected playlist's content in the view */
  this.loadPlaylistContent = () => {
    let playlistFilename = self.getSelectedPlaylist();
    logger.debug("Getting content for " + playlistFilename);
    let requestParam = "path=" + playlistFilename;
    debuggerHttpClient.getUrlEncoded(mediaVideoPlaylistUrl, requestParam,
      (responseBody, responseCode, responseDescription) => {
        self.currentPlaylist = responseBody;
        self.populatePlaylistBrowserTable();
      },
      (responseBody, responseCode, responseDescription) =>
        kameHouseDebugger.displayResponseData("Error getting playlist content", responseCode)
      );
  }

  /** Play selected file in the specified VlcPlayer. */
  this.playSelectedPlaylist = function playSelectedPlaylist() {
    logger.debug(arguments.callee.name);
    let playlist = self.getSelectedPlaylist();
    self.vlcPlayer.playFile(playlist);
    self.vlcPlayer.openTab('tab-playlist');
    self.vlcPlayer.reloadPlaylist();
  }

  /** Populate the playlist table for browsing. */
  this.populatePlaylistBrowserTable = function populatePlaylistBrowserTable() {
    logger.trace(arguments.callee.name);
    let $playlistTableBody = $('#playlist-browser-table-body');
    domUtils.empty($playlistTableBody);
    if (isNullOrUndefined(self.currentPlaylist)) {
      $playlistTableBody.append(self.getEmptyPlaylistTr());
    } else {
      self.tbodyFilenames = self.getPlaylistBrowserTbody();
      self.tbodyAbsolutePaths = self.getPlaylistBrowserTbody();
      for (let i = 0; i < self.currentPlaylist.files.length; i++) {
        let absolutePath = self.currentPlaylist.files[i];
        let filename = fileUtils.getShortFilename(absolutePath);
        self.tbodyFilenames.append(self.getPlaylistBrowserTr(filename, absolutePath));
        self.tbodyAbsolutePaths.append(self.getPlaylistBrowserTr(absolutePath, absolutePath));
      }
      $playlistTableBody.replaceWith(self.tbodyFilenames);
    }
    self.filterPlaylistRows();
  }

  /** Play the clicked element from the playlist. */
  this.clickEventOnPlaylistBrowserRow = (event) => {
    let filename = event.data.filename;
    logger.debug("Play selected playlist browser file : " + filename);
    self.vlcPlayer.playFile(filename);
    self.vlcPlayer.openTab('tab-playing');
  }

  /** Toggle expand or collapse filenames in the playlist */
  this.toggleExpandPlaylistFilenames = function toggleExpandPlaylistFilenames() {
    logger.debug(arguments.callee.name);
    let isExpandedFilename = null;
    let filenamesFirstFile = $(self.tbodyFilenames).children().first().text();
    let currentFirstFile = $('#playlist-browser-table-body tr:first').text();
    let $playlistTable = $('#playlist-browser-table');

    if (currentFirstFile == filenamesFirstFile) {
      // currently displaying filenames, switch to absolute paths 
      if (!isNullOrUndefined(self.tbodyFilenames)) {
        self.tbodyFilenames.detach();
      }
      $playlistTable.append(self.tbodyAbsolutePaths);
      isExpandedFilename = true;
    } else {
      // currently displaying absolute paths, switch to filenames 
      if (!isNullOrUndefined(self.tbodyAbsolutePaths)) {
        self.tbodyAbsolutePaths.detach();
      }
      $playlistTable.append(self.tbodyFilenames);
      isExpandedFilename = false;
    }
    self.updateExpandPlaylistFilenamesIcon(isExpandedFilename);
    self.filterPlaylistRows();
  }
  
  /** Update the icon to expand or collapse the playlist filenames */
  this.updateExpandPlaylistFilenamesIcon = (isExpandedFilename) => {
    if (isExpandedFilename) {
      $("#toggle-playlist-browser-filenames-img").replaceWith(self.dobleLeftImg);
    } else {
      $("#toggle-playlist-browser-filenames-img").replaceWith(self.dobleRightImg);
    }
  }

  this.getInitialDropdownOption = (optionText) => {
    return domUtils.getOption({
      disabled: true,
      selected: true
    }, optionText);
  }

  this.getPlaylistOption = (entry, category) => {
    return domUtils.getOption({
      value: entry
    }, category);
  }

  this.getPlaylistCategoryOption = (path, playlistName) => {
    return domUtils.getOption({
      value: path
    }, playlistName);
  }
  
  this.getPlaylistBrowserTbody = () => {
    return domUtils.getTbody({
      id: "playlist-browser-table-body"
    }, null);
  }

  this.getEmptyPlaylistTr = () => {
    return domUtils.getTrTd("No playlist to browse loaded yet or unable to sync. まだまだだね :)");
  }

  this.getPlaylistBrowserTr = (displayName, filePath) => {
    return domUtils.getTrTd(getPlaylistBrowserTrButton(displayName, filePath));
  }

  function getPlaylistBrowserTrButton(displayName, filePath) {
    return domUtils.getButton({
      attr: {
        class: "playlist-browser-table-btn",
      },
      html: displayName,
      clickData: {
        filename: filePath
      },
      click: self.clickEventOnPlaylistBrowserRow
    });
  }
}