package javatest.game.entities;

import java.util.TimerTask;

import javatest.game.World;

/**
 * An animated decoration.
 * @author Viliam Simko
 */
public class Exhaust extends Entity {

	public Exhaust(World world, double x, double y, double vx, double vy) {
		super(world);
		
		setEntityAnimation("exhaust?.png");
		
		setPosition(x, y);
		this.vx = vx;
		this.vy = vy;
	}

	@Override
	public void onAnimEnded(TimerTask task) {
		task.cancel();
		world.deleteEntity(this);
	}
}
