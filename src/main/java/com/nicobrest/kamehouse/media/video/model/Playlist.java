package com.nicobrest.kamehouse.media.video.model;

import java.util.List;

public class Playlist implements Comparable<Playlist> {

  private String name;
  private String category;
  private String path;
  private List<String> files;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public List<String> getFiles() {
    return files;
  }

  public void setFiles(List<String> files) {
    this.files = files;
  }

  @Override
  public int compareTo(Playlist otherPlaylist) {
    if (this.path == null) {
      return -1;
    }
    if (otherPlaylist.getPath() == null) {
      return 1;
    }
    return this.path.compareTo(otherPlaylist.getPath());
  }
}
