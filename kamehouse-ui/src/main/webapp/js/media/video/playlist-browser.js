/** 
 * Represents the playlist browser component in vlc-player page.
 * It doesn't control the currently active playlist.
 * 
 * Dependencies: tableUtils, logger, debuggerHttpClient
 * 
 * @author nbrest
 */
function PlaylistBrowser(vlcPlayer) {

  this.init = init;
  this.filterPlaylistRows = filterPlaylistRows;
  this.populateVideoPlaylistCategories = populateVideoPlaylistCategories;
  this.populateVideoPlaylists = populateVideoPlaylists;
  this.loadPlaylistContent = loadPlaylistContent;
  this.playSelectedPlaylist = playSelectedPlaylist;

  const mediaVideoAllPlaylistsUrl = '/kame-house-media/api/v1/media/video/playlists';
  const mediaVideoPlaylistUrl = '/kame-house-media/api/v1/media/video/playlist';
  let videoPlaylists = [];
  let videoPlaylistCategories = [];
  let currentPlaylist = null;
  let tbodyAbsolutePaths = null;
  let tbodyFilenames = null;
  let dobleLeftImg = null;
  let dobleRightImg = null;

  /** Init Playlist Browser. */
  function init() {
    logger.debug(arguments.callee.name);
    dobleLeftImg = createDoubleArrowImg("left");
    dobleRightImg = createDoubleArrowImg("right");
    domUtils.replaceWith($("#toggle-playlist-browser-filenames-img"), dobleRightImg);
  }

  /** Create an image object to toggle when expanding/collapsing playlist browser filenames. */
  function createDoubleArrowImg(direction) {
    return domUtils.getImgBtn({
      id: "toggle-playlist-browser-filenames-img",
      src: "/kame-house/img/other/double-" + direction + "-green.png",
      className: "img-btn-kh img-btn-s-kh btn-playlist-controls",
      alt: "Expand/Collapse Filename",
      onClick: () => toggleExpandPlaylistFilenames()
    });
  }

  /** Filter playlist browser rows based on the search string. */
  function filterPlaylistRows() {
    let filterString = document.getElementById("playlist-browser-filter-input").value;
    tableUtils.filterTableRows(filterString, 'playlist-browser-table-body');
  }

  /** Returns the selected playlist from the dropdowns. */
  function getSelectedPlaylist() {
    logger.debug(arguments.callee.name);
    let playlistSelected = document.getElementById("playlist-dropdown").value;
    logger.debug("Playlist selected: " + playlistSelected);
    return playlistSelected;
  }

  /** Populate playlist categories dropdown. */
  function populateVideoPlaylistCategories() {
    logger.debug(arguments.callee.name);
    
    resetPlaylistDropdown();
    resetPlaylistCategoryDropdown();

    debuggerHttpClient.get(mediaVideoAllPlaylistsUrl, 
      (responseBody, responseCode, responseDescription) => {
        videoPlaylists = responseBody;
        videoPlaylistCategories = [...new Set(videoPlaylists.map((playlist) => playlist.category))];
        logger.debug("Playlists: " + JSON.stringify(videoPlaylists));
        logger.debug("Playlist categories: " + videoPlaylistCategories);
        let playlistCategoryDropdown = $('#playlist-category-dropdown');
        $.each(videoPlaylistCategories, function (key, entry) {
          let category = entry;
          let categoryFormatted = category.replace(/\\/g, ' | ').replace(/\//g, ' | ');
          domUtils.append(playlistCategoryDropdown, getPlaylistCategoryOption(entry, categoryFormatted));
        });
      },
      (responseBody, responseCode, responseDescription) => 
        kameHouseDebugger.displayResponseData("Error populating video playlist categories", responseCode)
      );
  }

  /**
   * Reset playlist dropdown view.
   */
  function resetPlaylistDropdown() {
    let playlistDropdown = $('#playlist-dropdown');
    domUtils.empty(playlistDropdown);
    domUtils.append(playlistDropdown, getInitialDropdownOption("Playlist"));
  }

  /**
   * Reset playlist category dropdown view.
   */
  function resetPlaylistCategoryDropdown() {
    let playlistCategoryDropdown = $('#playlist-category-dropdown');
    domUtils.empty(playlistCategoryDropdown);
    domUtils.append(playlistCategoryDropdown, getInitialDropdownOption("Playlist Category"));
  }

  /** Populate video playlists dropdown when a playlist category is selected. */
  function populateVideoPlaylists() {
    logger.debug(arguments.callee.name);
    let playlistCategoriesList = document.getElementById('playlist-category-dropdown');
    let selectedPlaylistCategory = playlistCategoriesList.options[playlistCategoriesList.selectedIndex].value;
    logger.debug("Selected Playlist Category: " + selectedPlaylistCategory);
    resetPlaylistDropdown();
    let playlistDropdown = $('#playlist-dropdown');
    $.each(videoPlaylists, (key, entry) => {
      if (entry.category === selectedPlaylistCategory) {
        let playlistName = entry.name;
        playlistName = playlistName.replace(/.m3u+$/, "");
        domUtils.append(playlistDropdown, getPlaylistOption(entry.path, playlistName));
      }
    });
  }

  /** Load the selected playlist's content in the view */
  function loadPlaylistContent() {
    let playlistFilename = getSelectedPlaylist();
    logger.debug("Getting content for " + playlistFilename);
    let requestParam = "path=" + playlistFilename;
    debuggerHttpClient.getUrlEncoded(mediaVideoPlaylistUrl, requestParam,
      (responseBody, responseCode, responseDescription) => {
        currentPlaylist = responseBody;
        populatePlaylistBrowserTable();
      },
      (responseBody, responseCode, responseDescription) =>
        kameHouseDebugger.displayResponseData("Error getting playlist content", responseCode)
      );
  }

  /** Play selected file in the specified VlcPlayer. */
  function playSelectedPlaylist() {
    logger.debug(arguments.callee.name);
    let playlist = getSelectedPlaylist();
    vlcPlayer.playFile(playlist);
    vlcPlayer.openTab('tab-playlist');
    vlcPlayer.reloadPlaylist();
  }

  /** Populate the playlist table for browsing. */
  function populatePlaylistBrowserTable() {
    logger.trace(arguments.callee.name);
    let $playlistTableBody = $('#playlist-browser-table-body');
    domUtils.empty($playlistTableBody);
    if (isNullOrUndefined(currentPlaylist)) {
      domUtils.append($playlistTableBody, getEmptyPlaylistTr());
    } else {
      tbodyFilenames = getPlaylistBrowserTbody();
      tbodyAbsolutePaths = getPlaylistBrowserTbody();
      for (i = 0; i < currentPlaylist.files.length; i++) {
        let absolutePath = currentPlaylist.files[i];
        let filename = fileUtils.getShortFilename(absolutePath);
        domUtils.append(tbodyFilenames, getPlaylistBrowserTr(filename, absolutePath));
        domUtils.append(tbodyAbsolutePaths, getPlaylistBrowserTr(absolutePath, absolutePath));
      }
      domUtils.replaceWith($playlistTableBody, tbodyFilenames);
    }
    filterPlaylistRows();
  }

  /** Play the clicked element from the playlist. */
  function clickEventOnPlaylistBrowserRow(event) {
    let filename = event.data.filename;
    logger.debug("Play selected playlist browser file : " + filename);
    vlcPlayer.playFile(filename);
    vlcPlayer.openTab('tab-playing');
  }

  /** Toggle expand or collapse filenames in the playlist */
  function toggleExpandPlaylistFilenames() {
    logger.debug(arguments.callee.name);
    let isExpandedFilename = null;
    let filenamesFirstFile = $(tbodyFilenames).children().first().text();
    let currentFirstFile = $('#playlist-browser-table-body tr:first').text();
    let $playlistTable = $('#playlist-browser-table');

    if (currentFirstFile == filenamesFirstFile) {
      // currently displaying filenames, switch to absolute paths 
      if (!isNullOrUndefined(tbodyFilenames)) {
        domUtils.detach(tbodyFilenames);
      }
      domUtils.append($playlistTable, tbodyAbsolutePaths);
      isExpandedFilename = true;
    } else {
      // currently displaying absolute paths, switch to filenames 
      if (!isNullOrUndefined(tbodyAbsolutePaths)) {
        domUtils.detach(tbodyAbsolutePaths);
      }
      domUtils.append($playlistTable, tbodyFilenames);
      isExpandedFilename = false;
    }
    updateExpandPlaylistFilenamesIcon(isExpandedFilename);
    filterPlaylistRows();
  }
  
  /** Update the icon to expand or collapse the playlist filenames */
  function updateExpandPlaylistFilenamesIcon(isExpandedFilename) {
    if (isExpandedFilename) {
      domUtils.replaceWith($("#toggle-playlist-browser-filenames-img"), dobleLeftImg);
    } else {
      domUtils.replaceWith($("#toggle-playlist-browser-filenames-img"), dobleRightImg);
    }
  }

  function getInitialDropdownOption(optionText) {
    return domUtils.getOption({
      disabled: true,
      selected: true
    }, optionText);
  }

  function getPlaylistOption(entry, category) {
    return domUtils.getOption({
      value: entry
    }, category);
  }

  function getPlaylistCategoryOption(path, playlistName) {
    return domUtils.getOption({
      value: path
    }, playlistName);
  }
  
  function getPlaylistBrowserTbody() {
    return domUtils.getTbody({
      id: "playlist-browser-table-body"
    }, null);
  }

  function getEmptyPlaylistTr() {
    return domUtils.getTrTd("No playlist to browse loaded yet or unable to sync. まだまだだね :)");
  }

  function getPlaylistBrowserTr(displayName, filePath) {
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
      click: clickEventOnPlaylistBrowserRow
    });
  }
}