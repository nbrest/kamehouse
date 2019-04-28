package com.nicobrest.kamehouse.media.video.model;

import java.util.Comparator;

public class PlaylistComparator implements Comparator<Playlist> {

  @Override
  public int compare(Playlist playlist1, Playlist playlist2) {
    return playlist1.compareTo(playlist2);
  }

}
