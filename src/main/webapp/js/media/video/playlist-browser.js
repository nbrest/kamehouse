/** 
 * Represents the playlist browser component in vlc-player page.
 * It doesn't control the currently active playlist.
 * 
 * Dependencies: tableUtils, logger, apiCallTable
 * 
 * @author nbrest
 */
function PlaylistBrowser(vlcPlayer) {

  let self = this;
  this.vlcPlayer = vlcPlayer;
  this.videoPlaylists = [];
  this.videoPlaylistCategories = [];
  this.currentPlaylist = null;
  const mediaVideoAllPlaylistsUrl = '/kame-house/api/v1/media/video/playlists';
  const mediaVideoPlaylistUrl = '/kame-house/api/v1/media/video/playlist';
  this.tbodyAbsolutePaths = null;
  this.tbodyFilenames = null;
  this.dobleLeftImg = null;
  this.dobleRightImg = null;

  /** Init Playlist Browser. */
  this.init = function init() {
    logger.debugFunctionCall();
    self.dobleLeftImg = self.createDoubleArrowImg("left");
    self.dobleRightImg = self.createDoubleArrowImg("right");
    $("#toggle-playlist-browser-filenames-img").replaceWith(self.dobleRightImg);
  }

  /** Create an image object to toggle when expanding/collapsing playlist browser filenames. */
  this.createDoubleArrowImg = (direction) => {
    let dobleArrowImg = new Image();
    dobleArrowImg.id = "toggle-playlist-browser-filenames-img";
    dobleArrowImg.src = "/kame-house/img/other/double-" + direction + "-green.png";
    dobleArrowImg.className = "vlc-player-btn-img vlc-player-btn-img-s vlc-player-btn-green btn-playlist-controls";
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
    logger.debugFunctionCall();
    let playlistSelected = document.getElementById("playlist-dropdown").value;
    logger.debug("Playlist selected: " + playlistSelected);
    return playlistSelected;
  }

  /** Populate playlist categories dropdown. */
  this.populateVideoPlaylistCategories = function populateVideoPlaylistCategories() {
    logger.debugFunctionCall();
    let playlistDropdown = $('#playlist-dropdown');
    playlistDropdown.empty();
    playlistDropdown.append('<option selected="true" disabled>Playlist</option>');
    playlistDropdown.prop('selectedIndex', 0);
    let playlistCategoryDropdown = $('#playlist-category-dropdown');
    playlistCategoryDropdown.empty();
    playlistCategoryDropdown.append('<option selected="true" disabled>Playlist Category</option>');
    playlistCategoryDropdown.prop('selectedIndex', 0);
    apiCallTable.get(mediaVideoAllPlaylistsUrl, 
      (responseBody, responseCode, responseDescription) => {
        self.videoPlaylists = responseBody;
        self.videoPlaylistCategories = [...new Set(self.videoPlaylists.map(playlist => playlist.category))];
        logger.trace("Playlists: " + JSON.stringify(self.videoPlaylists));
        logger.trace("Playlist categories: " + self.videoPlaylistCategories);
        $.each(self.videoPlaylistCategories, function (key, entry) {
          let category = entry;
          let categoryFormatted = category.replace(/\\/g, ' | ').replace(/\//g, ' | ');
          playlistCategoryDropdown.append($('<option></option>').attr('value', entry).text(categoryFormatted));
        });
      },
      (responseBody, responseCode, responseDescription) => 
        apiCallTable.displayResponseData("Error populating video playlist categories", responseCode)
      );
  }

  /** Populate video playlists dropdown when a playlist category is selected. */
  this.populateVideoPlaylists = function populateVideoPlaylists() {
    logger.debugFunctionCall();
    let playlistCategoriesList = document.getElementById('playlist-category-dropdown');
    let selectedPlaylistCategory = playlistCategoriesList.options[playlistCategoriesList.selectedIndex].value;
    let playlistDropdown = $('#playlist-dropdown');
    playlistDropdown.empty();
    playlistDropdown.append('<option selected="true" disabled>Playlist</option>');
    playlistDropdown.prop('selectedIndex', 0);
    logger.debug("Selected Playlist Category: " + selectedPlaylistCategory);
    $.each(self.videoPlaylists, (key, entry) => {
      if (entry.category === selectedPlaylistCategory) {
        let playlistName = entry.name;
        playlistName = playlistName.replace(/.m3u+$/, "");
        playlistDropdown.append($('<option></option>').attr('value', entry.path).text(playlistName));
      }
    });
  }

  /** Load the selected playlist's content in the view */
  this.loadPlaylistContent = () => {
    let playlistFilename = self.getSelectedPlaylist();
    logger.debug("Getting content for " + playlistFilename);
    let requestParam = "path=" + playlistFilename;
    apiCallTable.getUrlEncoded(mediaVideoPlaylistUrl, requestParam,
      (responseBody, responseCode, responseDescription) => {
        self.currentPlaylist = responseBody;
        self.populatePlaylistBrowserTable();
      },
      (responseBody, responseCode, responseDescription) =>
        apiCallTable.displayResponseData("Error getting playlist content", responseCode)
      );
  }

  /** Play selected file in the specified VlcPlayer. */
  this.playSelectedPlaylist = function playSelectedPlaylist() {
    logger.debugFunctionCall();
    let playlist = self.getSelectedPlaylist();
    self.vlcPlayer.playFile(playlist);
    self.vlcPlayer.openTab('tab-playlist');
  }

  /** Populate the playlist table for browsing. */
  this.populatePlaylistBrowserTable = function populatePlaylistBrowserTable() {
    logger.traceFunctionCall();
    // Clear playlist browser table content. 
    $("#playlist-browser-table-body").empty();
    // Add the new playlist browser items received from the server.
    let $playlistTableBody = $('#playlist-browser-table-body');    
    if (isNullOrUndefined(self.currentPlaylist)) {
      let playlistTableRow = $('<tr>').append($('<td>').text("No playlist to browse loaded yet or unable to sync. まだまだだね :)"));
      $playlistTableBody.append(playlistTableRow);
    } else {
      self.tbodyFilenames = $('<tbody id="playlist-browser-table-body">');
      self.tbodyAbsolutePaths = $('<tbody id="playlist-browser-table-body">');
      for (let i = 0; i < self.currentPlaylist.files.length; i++) {
        let absolutePath = self.currentPlaylist.files[i];
        let filename = fileUtils.getShortFilename(absolutePath);
        self.tbodyFilenames.append(self.getPlaylistBrowserTableRow(filename, absolutePath));
        self.tbodyAbsolutePaths.append(self.getPlaylistBrowserTableRow(absolutePath, absolutePath));
      }
      $playlistTableBody.replaceWith(self.tbodyFilenames);
    }
    self.filterPlaylistRows();
  }

  /** Create a playlist browser table row */
  this.getPlaylistBrowserTableRow = (displayName, filePath) => {
    let playlistElementButton = $('<button>');
    playlistElementButton.addClass("playlist-browser-table-btn");
    playlistElementButton.text(displayName);
    playlistElementButton.click({
      filename: filePath
    }, self.clickEventOnPlaylistBrowserRow);
    let playlistTableRow = $('<tr>').append($('<td>').append(playlistElementButton));
    return playlistTableRow;
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
    logger.debugFunctionCall();
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
}