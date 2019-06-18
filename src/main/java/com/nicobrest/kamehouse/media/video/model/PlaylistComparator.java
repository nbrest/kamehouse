package com.nicobrest.kamehouse.media.video.model;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares playlists based on its compareTo implementation.
 * 
 * @author nbrest
 *
 */
public class PlaylistComparator implements Comparator<Playlist>, Serializable {

  private static final long serialVersionUID = 1L;

  @Override
  public int compare(Playlist playlist1, Playlist playlist2) {
    return playlist1.compareTo(playlist2);
  }

}
