package com.nicobrest.kamehouse.vlcrc.model;

import com.nicobrest.kamehouse.main.utils.JsonUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Playlist item returned by a vlc player.
 * 
 * @author nbrest
 *
 */
public class VlcRcPlaylistItem {

  private int id;
  private String name;
  private String uri;
  private int duration;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).append(name).append(uri).append(duration).toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof VlcRcPlaylistItem) {
      final VlcRcPlaylistItem other = (VlcRcPlaylistItem) obj;
      return new EqualsBuilder().append(id, other.getId()).append(name, other.getName()).append(
          uri, other.getUri()).append(duration, other.getDuration()).isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
