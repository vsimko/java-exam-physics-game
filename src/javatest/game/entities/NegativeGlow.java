package javatest.game.entities;

import java.util.TimerTask;

import javatest.game.World;

/**
 * An animated decoration.
 * @author Viliam Simko
 */
public class NegativeGlow extends Entity {

	public NegativeGlow(World world, int x, int y) {
		super(world);
		setPosition(x, y);
		setEntityAnimation("negative-glow?.png");
	}
	
	@Override
	public void onAnimEnded(TimerTask task) {
		task.cancel();
		world.deleteEntity(this);
	}
}
