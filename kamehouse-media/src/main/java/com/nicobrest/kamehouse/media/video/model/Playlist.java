package com.nicobrest.kamehouse.media.video.model;

import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a playlist file.
 *
 * @author nbrest
 */
public class Playlist implements Serializable, Comparable<Playlist> {

  private static final long serialVersionUID = 1L;
  private String name;
  private String category;
  private String path;
  @Masked
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

  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public List<String> getFiles() {
    return files;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
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
  public int hashCode() {
    return new HashCodeBuilder().append(name).append(path).append(category).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Playlist) {
      final Playlist other = (Playlist) obj;
      return new EqualsBuilder()
          .append(name, other.getName())
          .append(path, other.getPath())
          .append(category, other.getCategory())
          .isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }

  /**
   * Compares playlists based on its compareTo implementation.
   */
  public static class Comparator implements java.util.Comparator<Playlist>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Playlist playlist1, Playlist playlist2) {
      return playlist1.compareTo(playlist2);
    }
  }
}
