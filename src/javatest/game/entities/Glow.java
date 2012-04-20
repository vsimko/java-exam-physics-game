package javatest.game.entities;

import java.util.TimerTask;

import javatest.game.World;

/**
 * An animated decoration.
 * @author Viliam Simko
 */
public class Glow extends Entity {

	public Glow(World world, int x, int y) {
		super(world);
		setPosition(x, y);
		setEntityAnimation("reached-glow?.png");
	}
	
	@Override
	public void onAnimEnded(TimerTask task) {
		task.cancel();
		world.deleteEntity(this);
	}
}
