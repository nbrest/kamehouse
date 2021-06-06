package com.nicobrest.kamehouse.vlcrc.model;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * File list item returned by a vlc player when browsing for files.
 * 
 * @author nbrest
 *
 */
public class VlcRcFileListItem implements Serializable {

  private static final long serialVersionUID = 1L;
  private String type;
  private String name;
  private String path;
  private String uri;
  private int size;
  private int accessTime;
  private int creationTime;
  private int modificationTime;
  private int uid;
  private int gid;
  private int mode;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getAccessTime() {
    return accessTime;
  }

  public void setAccessTime(int accessTime) {
    this.accessTime = accessTime;
  }

  public int getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(int creationTime) {
    this.creationTime = creationTime;
  }

  public int getModificationTime() {
    return modificationTime;
  }

  public void setModificationTime(int modificationTime) {
    this.modificationTime = modificationTime;
  }

  public int getUid() {
    return uid;
  }

  public void setUid(int uid) {
    this.uid = uid;
  }

  public int getGid() {
    return gid;
  }

  public void setGid(int gid) {
    this.gid = gid;
  }

  public int getMode() {
    return mode;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(type).append(name).append(path).append(uri).append(size)
        .toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof VlcRcFileListItem) {
      final VlcRcFileListItem other = (VlcRcFileListItem) obj;
      return new EqualsBuilder().append(type, other.getType()).append(name, other.getName())
          .append(path, other.getPath()).append(uri, other.getUri()).append(size, other.getSize())
          .isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
