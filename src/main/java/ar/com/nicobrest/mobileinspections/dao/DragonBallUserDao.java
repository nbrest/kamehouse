package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import java.util.List;

/**
 * @since v0.02
 * @author nbrest
 *
 *      DragonBallUserDao interface
 */
public interface DragonBallUserDao {

  public DragonBallUser getDragonBallUser(String username);

  public List<DragonBallUser> getAllDragonBallUsers();

}
