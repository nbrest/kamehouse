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

  /** Filter playlist browser rows based on the search string. */
  this.filterPlaylistRows = (filterString) => tableUtils.filterTableRows(filterString, 'playlist-browser-table-body');

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
    self.tbodyFilenames = $('<tbody id="playlist-browser-table-body">');
    self.tbodyAbsolutePaths = $('<tbody id="playlist-browser-table-body">');

    if (isNullOrUndefined(self.currentPlaylist)) {
      playlistTableRow = $('<tr>').append($('<td>').text("No playlist to browse loaded yet or unable to sync. まだまだだね :)"));
      $playlistTableBody.append(playlistTableRow);
    } else {
      for (let i = 0; i < self.currentPlaylist.files.length; i++) {
        let absolutePath = self.currentPlaylist.files[i];
        let filename = fileUtils.getShortFilename(absolutePath);

        // Create the in-memory tbody of filenames to toggle with absolute paths
        let playlistElementButtonFilenames = $('<button>');
        playlistElementButtonFilenames.addClass("playlist-browser-table-btn");
        playlistElementButtonFilenames.text(filename);
        playlistElementButtonFilenames.click({
          filename: absolutePath
        }, self.clickEventOnPlaylistBrowserRow);
        let playlistTableRowFilenames = $('<tr id=playlist-browser-entry-' + [i] + '>').append($('<td>').append(playlistElementButtonFilenames));
        self.tbodyFilenames.append(playlistTableRowFilenames);

        // Create the in-memory tbody of absolute paths to toggle with filenames
        let playlistElementButtonAbsolutePaths = $('<button>');
        playlistElementButtonAbsolutePaths.addClass("playlist-browser-table-btn");  
        playlistElementButtonAbsolutePaths.text(absolutePath);
        playlistElementButtonAbsolutePaths.click({
          filename: absolutePath
        }, self.clickEventOnPlaylistBrowserRow);
        let playlistTableRowAbsolutePaths = $('<tr id=playlist-browser-entry-' + [i] + '>').append($('<td>').append(playlistElementButtonAbsolutePaths));
        self.tbodyAbsolutePaths.append(playlistTableRowAbsolutePaths);
      }
      $playlistTableBody.replaceWith(self.tbodyFilenames);
    }
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
    let isExpandedFilename = true;
    let filenamesFirstFile = $(self.tbodyFilenames).children().first().text();
    let currentFirstFile = $('#playlist-browser-table-body tr:first').text();
    let $playlistTable = $('#playlist-browser-table');

    if (currentFirstFile == filenamesFirstFile) {
      // currently displaying filenames, switch to absolute paths 
      self.tbodyFilenames.detach();
      $playlistTable.append(self.tbodyAbsolutePaths);
      isExpandedFilename = true;
    } else {
      // currently displaying absolute paths, switch to filenames 
      self.tbodyAbsolutePaths.detach();  
      $playlistTable.append(self.tbodyFilenames);
      isExpandedFilename = false;
    }
    self.updateExpandPlaylistFilenamesIcon(isExpandedFilename);
  }

  /** Update the icon to expand or collapse the playlist filenames */
  this.updateExpandPlaylistFilenamesIcon = (isExpandedFilename) => {
    if (isExpandedFilename) {
      $("#toggle-playlist-browser-filenames-img").attr("src", "/kame-house/img/other/double-left-green.png");
    } else {
      $("#toggle-playlist-browser-filenames-img").attr("src", "/kame-house/img/other/double-right-green.png");
    }
  }
}