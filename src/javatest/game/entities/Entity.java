package javatest.game.entities;

import java.util.TimerTask;

import javatest.game.AnimatedImage;
import javatest.game.World;

/**
 * Base class for all entities in the game world.
 * @author Viliam Simko
 */
abstract public class Entity {

	protected World world;
	protected AnimatedImage anim;
	
	// accessible from other particles
	public double x;
	public double y;
	public double vx;
	public double vy;

	public Entity(World world) {
		this.world = world;
	}
	
	protected void setEntityAnimation(String filenamePattern) {
		setEntityAnimation( new AnimatedImage(filenamePattern) );
	}
	
	/**
	 * Default behavior is that the entity will be animated every 70ms
	 * using the worlds scheduler.
	 * 
	 * @param anim
	 */
	protected void setEntityAnimation(AnimatedImage anim) {
		this.anim = anim;
		world.worldTimer.scheduleAtFixedRate(new TimerTask() {
			private AnimatedImage anim = Entity.this.anim;
			@Override
			public void run() {
				if( ! anim.nextFrame())
					onAnimEnded(this);
			}
		}, 0, 70);
	}
	
	/**
	 * Move entity to the given position.
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Default behavior is that objects will
	 * bounce out of the screen corners.
	 * Feel free to override.
	 */
	public void onUpdatePosition() {
		x += vx;
		y += vy;

		if (x > world.width || x < 0) {
			vx = -vx;
			x += vx;
		}

		if (y > world.height || y < 0) {
			vy = -vy;
			y += vy;
		}
	}
	
	/**
	 * Default behavior is that the current frame is drawn.
	 * Feel free to override.
	 */
	public void onUpdateGraphics() {
		anim.draw(world.gfx, (int)x, (int)y );
	}
	
	/**
	 * Feel free to override.
	 */
	public void onAnimStarted(){}
	
	/**
	 * Default behaviour is a looping animation.
	 * In order to finish the animation, use task.cancel();
	 * Feel free to override.
	 * @param task
	 */
	public void onAnimEnded(TimerTask task){
		anim.resetToFirstFrame();
	}
	
	public boolean isClicked(int clickX, int clickY) {
		int sx = clickX - (int) x;
		int sy = clickY - (int) y;
		
		int r = anim.getClickRadius();
		return sx*sx + sy*sy < r*r;
	}
	
	public int getEntityRadius() {
		return anim.getClickRadius();
	}
}
