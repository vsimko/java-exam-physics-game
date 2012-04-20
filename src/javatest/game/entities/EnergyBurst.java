package javatest.game.entities;

import java.util.TimerTask;

import javatest.game.World;

/**
 * An animated decoration.
 * @author Viliam Simko
 */
public class EnergyBurst extends Entity {
	Entity target;
	public EnergyBurst(World world, double x, double y, Entity target, double bias) {
		super(world);

		setEntityAnimation("exhaust-blue?.png");
		setPosition(x, y);
		this.target = target;
		
		
		double fx = target.x - x;
		double fy = target.y - y;
		double invr = 1/Math.sqrt(fx*fx + fy*fy);
		vx = - bias * fy * invr * 2;
		vy =   bias * fx * invr * 2;
	}

	@Override
	public void onAnimEnded(TimerTask task) {
		task.cancel();
		world.deleteEntity(this);
	}

	@Override
	public void onUpdatePosition() {
		double fx = target.x - x;
		double fy = target.y - y;
		double invr = 1/Math.sqrt(fx*fx + fy*fy);
		
		vx += fx * invr;
		vy += fy * invr;
		super.onUpdatePosition();
	}
}
