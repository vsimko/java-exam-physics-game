package javatest.game.types;

import javatest.game.entities.Entity;

/**
 * Interface for entities that can be destroyed e.g. by proximity mines.
 * @author Viliam Simko
 */
public interface IDestroyable {
	
	/**
	 * Notifies the entity that somebody has destroyed it.
	 * @param whoDestroyed
	 */
	void onDestroy(Entity whoDestroyed);
	
	/**
	 * Checks whether the entity has already been destroyed.
	 * @return
	 */
	boolean isDestroyed();
}
