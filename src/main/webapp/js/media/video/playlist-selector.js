/** 
 * DONE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * Represents the playlist selector component in vlc-player page.
 * It doesn't control the currently active playlist.
 * 
 * Dependencies: logger, apiCallTable
 * 
 * @author nbrest
 */
function PlaylistSelector() {

  let self = this;
  this.videoPlaylists = [];
  this.videoPlaylistCategories = [];
  const mediaVideoPlaylistsUrl = '/kame-house/api/v1/media/video/playlists';

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
    apiCallTable.get(mediaVideoPlaylistsUrl,
      function (responseBody, responseCode, responseDescription) {
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
      function (responseBody, responseCode, responseDescription) {
        apiCallTable.displayResponseData("Error populating video playlist categories", responseCode);
      });
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
    $.each(self.videoPlaylists, function (key, entry) {
      if (entry.category === selectedPlaylistCategory) {
        let playlistName = entry.name;
        playlistName = playlistName.replace(/.m3u+$/, "");
        playlistDropdown.append($('<option></option>').attr('value', entry.path).text(playlistName));
      }
    });
  }

  /** Play selected file in the specified VlcPlayer. */
  this.playSelectedPlaylist = function playSelectedPlaylist(vlcPlayer) {
    logger.debugFunctionCall();
    let playlist = self.getSelectedPlaylist();
    vlcPlayer.playFile(playlist);
  }
}
