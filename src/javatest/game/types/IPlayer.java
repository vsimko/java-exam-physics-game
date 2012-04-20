package javatest.game.types;

import javatest.game.entities.Entity;
import javatest.game.entities.GoalEntity;

/**
 * All types of players must implement this interface.
 * @author Viliam Simko
 */
public interface IPlayer {
	
	/**
	 * Notifies the player that the given goal has been reached.
	 * @param goal
	 */
	void goalReached(GoalEntity goal);

	/**
	 * Computes a vector which would accelerate the player towards a given
	 * target entity
	 * 
	 * @param target
	 */
	void followEntity(Entity target);

	/**
	 * Computes an evasive maneuver for player.
	 */
	void evadeEntity(Entity target);
}
