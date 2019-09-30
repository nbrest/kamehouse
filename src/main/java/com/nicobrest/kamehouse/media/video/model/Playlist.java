package com.nicobrest.kamehouse.media.video.model;

import com.nicobrest.kamehouse.main.utils.JsonUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;

/**
 * Represents a playlist file.
 * 
 * @author nbrest
 *
 */
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

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Playlist) {
      final Playlist other = (Playlist) obj;
      return new EqualsBuilder().append(name, other.getName()).append(path, other.getPath())
          .append(category, other.getCategory()).isEquals();
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(name).append(path).append(category).toHashCode();
  }
  
  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
