/** 
 * Represents the playlist browser component in vlc-player page.
 * It doesn't control the currently active playlist.
 * 
 * Dependencies: kameHouse.util.table. logger, kameHouseDebugger
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
  const dobleLeftImg = createDoubleArrowImg("left");
  const dobleRightImg = createDoubleArrowImg("right");

  let videoPlaylists = [];
  let videoPlaylistCategories = [];
  let currentPlaylist = null;
  let tbodyAbsolutePaths = null;
  let tbodyFilenames = null;

  /** Init Playlist Browser. */
  function init() {
    kameHouse.util.dom.replaceWith($("#toggle-playlist-browser-filenames-img"), dobleRightImg);
  }

  /** Create an image object to toggle when expanding/collapsing playlist browser filenames. */
  function createDoubleArrowImg(direction) {
    return kameHouse.util.dom.getImgBtn({
      id: "toggle-playlist-browser-filenames-img",
      src: "/kame-house/img/other/double-" + direction + "-green.png",
      className: "img-btn-kh img-btn-s-kh btn-playlist-controls",
      alt: "Expand/Collapse Filename",
      onClick: () => toggleExpandPlaylistFilenames()
    });
  }

  /** Filter playlist browser rows based on the search string. */
  function filterPlaylistRows() {
    const filterString = document.getElementById("playlist-browser-filter-input").value;
    kameHouse.util.table.filterTableRows(filterString, 'playlist-browser-table-body');
  }

  /** Returns the selected playlist from the dropdowns. */
  function getSelectedPlaylist() {
    const playlistSelected = document.getElementById("playlist-dropdown").value;
    kameHouse.logger.debug("Playlist selected: " + playlistSelected);
    return playlistSelected;
  }

  /** Populate playlist categories dropdown. */
  function populateVideoPlaylistCategories() {
    
    resetPlaylistDropdown();
    resetPlaylistCategoryDropdown();

    kameHouse.plugin.debugger.http.get(mediaVideoAllPlaylistsUrl, null, null, 
      (responseBody, responseCode, responseDescription) => {
        videoPlaylists = responseBody;
        videoPlaylistCategories = [...new Set(videoPlaylists.map((playlist) => playlist.category))];
        kameHouse.logger.debug("Playlists: " + JSON.stringify(videoPlaylists));
        kameHouse.logger.debug("Playlist categories: " + videoPlaylistCategories);
        const playlistCategoryDropdown = $('#playlist-category-dropdown');
        $.each(videoPlaylistCategories, (key, entry) => {
          const category = entry;
          const categoryFormatted = category.replace(/\\/g, ' | ').replace(/\//g, ' | ');
          kameHouse.util.dom.append(playlistCategoryDropdown, getPlaylistCategoryOption(entry, categoryFormatted));
        });
      },
      (responseBody, responseCode, responseDescription) => 
        kameHouse.plugin.debugger.displayResponseData("Error populating video playlist categories", responseCode)
      );
  }

  /**
   * Reset playlist dropdown view.
   */
  function resetPlaylistDropdown() {
    const playlistDropdown = $('#playlist-dropdown');
    kameHouse.util.dom.empty(playlistDropdown);
    kameHouse.util.dom.append(playlistDropdown, getInitialDropdownOption("Playlist"));
  }

  /**
   * Reset playlist category dropdown view.
   */
  function resetPlaylistCategoryDropdown() {
    const playlistCategoryDropdown = $('#playlist-category-dropdown');
    kameHouse.util.dom.empty(playlistCategoryDropdown);
    kameHouse.util.dom.append(playlistCategoryDropdown, getInitialDropdownOption("Playlist Category"));
  }

  /** Populate video playlists dropdown when a playlist category is selected. */
  function populateVideoPlaylists() {
    const playlistCategoriesList = document.getElementById('playlist-category-dropdown');
    const selectedPlaylistCategory = playlistCategoriesList.options[playlistCategoriesList.selectedIndex].value;
    kameHouse.logger.debug("Selected Playlist Category: " + selectedPlaylistCategory);
    resetPlaylistDropdown();
    const playlistDropdown = $('#playlist-dropdown');
    $.each(videoPlaylists, (key, entry) => {
      if (entry.category === selectedPlaylistCategory) {
        const playlistName = entry.name.replace(/.m3u+$/, "");
        kameHouse.util.dom.append(playlistDropdown, getPlaylistOption(entry.path, playlistName));
      }
    });
  }

  /** Load the selected playlist's content in the view */
  function loadPlaylistContent() {
    const playlistFilename = getSelectedPlaylist();
    kameHouse.logger.debug("Getting content for " + playlistFilename);
    const requestParam = {
      "path" : playlistFilename
    }
    kameHouse.plugin.debugger.http.get(mediaVideoPlaylistUrl, kameHouse.http.getUrlEncodedHeaders(), requestParam,
      (responseBody, responseCode, responseDescription) => {
        currentPlaylist = responseBody;
        populatePlaylistBrowserTable();
      },
      (responseBody, responseCode, responseDescription) =>
        kameHouse.plugin.debugger.displayResponseData("Error getting playlist content", responseCode)
      );
  }

  /** Play selected file in the specified VlcPlayer. */
  function playSelectedPlaylist() {
    const playlist = getSelectedPlaylist();
    vlcPlayer.playFile(playlist);
    vlcPlayer.openTab('tab-playlist');
    vlcPlayer.reloadPlaylist();
  }

  /** Populate the playlist table for browsing. */
  function populatePlaylistBrowserTable() {
    const $playlistTableBody = $('#playlist-browser-table-body');
    kameHouse.util.dom.empty($playlistTableBody);
    if (kameHouse.core.isEmpty(currentPlaylist)) {
      kameHouse.util.dom.append($playlistTableBody, getEmptyPlaylistTr());
    } else {
      tbodyFilenames = getPlaylistBrowserTbody();
      tbodyAbsolutePaths = getPlaylistBrowserTbody();
      for (const file of currentPlaylist.files) {
        const absolutePath = file;
        const filename = kameHouse.util.file.getShortFilename(absolutePath);
        kameHouse.util.dom.append(tbodyFilenames, getPlaylistBrowserTr(filename, absolutePath));
        kameHouse.util.dom.append(tbodyAbsolutePaths, getPlaylistBrowserTr(absolutePath, absolutePath));
      }
      kameHouse.util.dom.replaceWith($playlistTableBody, tbodyFilenames);
    }
    filterPlaylistRows();
  }

  /** Play the clicked element from the playlist. */
  function clickEventOnPlaylistBrowserRow(event) {
    const filename = event.data.filename;
    kameHouse.logger.debug("Play selected playlist browser file : " + filename);
    vlcPlayer.playFile(filename);
    vlcPlayer.openTab('tab-playing');
  }

  /** Toggle expand or collapse filenames in the playlist */
  function toggleExpandPlaylistFilenames() {
    let isExpandedFilename = null;
    const filenamesFirstFile = $(tbodyFilenames).children().first().text();
    const currentFirstFile = $('#playlist-browser-table-body tr:first').text();
    const $playlistTable = $('#playlist-browser-table');

    if (currentFirstFile == filenamesFirstFile) {
      // currently displaying filenames, switch to absolute paths 
      if (!kameHouse.core.isEmpty(tbodyFilenames)) {
        kameHouse.util.dom.detach(tbodyFilenames);
      }
      kameHouse.util.dom.append($playlistTable, tbodyAbsolutePaths);
      isExpandedFilename = true;
    } else {
      // currently displaying absolute paths, switch to filenames 
      if (!kameHouse.core.isEmpty(tbodyAbsolutePaths)) {
        kameHouse.util.dom.detach(tbodyAbsolutePaths);
      }
      kameHouse.util.dom.append($playlistTable, tbodyFilenames);
      isExpandedFilename = false;
    }
    updateExpandPlaylistFilenamesIcon(isExpandedFilename);
    filterPlaylistRows();
  }
  
  /** Update the icon to expand or collapse the playlist filenames */
  function updateExpandPlaylistFilenamesIcon(isExpandedFilename) {
    if (isExpandedFilename) {
      kameHouse.util.dom.replaceWith($("#toggle-playlist-browser-filenames-img"), dobleLeftImg);
    } else {
      kameHouse.util.dom.replaceWith($("#toggle-playlist-browser-filenames-img"), dobleRightImg);
    }
  }

  function getInitialDropdownOption(optionText) {
    return kameHouse.util.dom.getOption({
      disabled: true,
      selected: true
    }, optionText);
  }

  function getPlaylistOption(entry, category) {
    return kameHouse.util.dom.getOption({
      value: entry
    }, category);
  }

  function getPlaylistCategoryOption(path, playlistName) {
    return kameHouse.util.dom.getOption({
      value: path
    }, playlistName);
  }
  
  function getPlaylistBrowserTbody() {
    return kameHouse.util.dom.getTbody({
      id: "playlist-browser-table-body"
    }, null);
  }

  function getEmptyPlaylistTr() {
    return kameHouse.util.dom.getTrTd("No playlist to browse loaded yet or unable to sync. まだまだだね :)");
  }

  function getPlaylistBrowserTr(displayName, filePath) {
    return kameHouse.util.dom.getTrTd(getPlaylistBrowserTrButton(displayName, filePath));
  }

  function getPlaylistBrowserTrButton(displayName, filePath) {
    return kameHouse.util.dom.getButton({
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