package com.nicobrest.kamehouse.ztvrc.model;

import com.nicobrest.kamehouse.commons.dao.Identifiable;
import java.util.List;

/**
 * Interface for a ZTV Player that can be managed and controlled from KameHouse.
 *
 * @author nbrest
 */
public interface ZtvPlayer extends Identifiable {

  /** Sets the hostname of the ZTV Player. */
  public void setHostname(String hostname);

  /** Gets the hostname of the ZTV Player. */
  public String getHostname();

  /** Sets the port of the ZTV Player web API. */
  public void setPort(int port);

  /** Gets the port of the ZTV Player web API. */
  public int getPort();

  /** Sets the username used to connect to the ZTV Player. */
  public void setUsername(String username);

  /** Gets the username used to connect to the ZTV Player. */
  public String getUsername();

  /** Sets the password used to connect to the ZTV Player. */
  public void setPassword(String password);

  /** Gets the password used to connect to the ZTV Player. */
  public String getPassword();

  /** Gets the status information of the ZTV Player. */
  public ZtvRcStatus getZtvRcStatus();

  /** Executes a command in the ZTV Player and returns its status. */
  public ZtvRcStatus execute(ZtvRcCommand command);

  /** Gets the current playlist of the ZTV Player. */
  public List<ZtvRcPlaylistItem> getPlaylist();

  /** Browses the file system of the server running the ZTV Player. */
  public List<ZtvRcFileListItem> browse(String uri);
}
