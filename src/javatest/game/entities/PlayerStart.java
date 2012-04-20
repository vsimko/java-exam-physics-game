package javatest.game.entities;

import java.util.TimerTask;

import javatest.game.AnimatedImage;
import javatest.game.World;

public class PlayerStart extends Entity {

	private boolean isActivated = false;
	static final private AnimatedImage activatedAnim = new AnimatedImage("playerstart-active.png");
	
	public PlayerStart(World world, int x, int y) {
		super(world);
		setEntityAnimation("playerstart.png");
		setPosition(x, y);
	}
	
	@Override
	public void onUpdatePosition() {
		// do nothing
	}
	
	@Override
	public void onUpdateGraphics() {
		super.onUpdateGraphics();
		if(isActivated) {
			activatedAnim.draw(world.gfx, (int)x, (int)y);
		}
	}
	
	final public PlayerEntity createNewPlayer() {
		
		// deactivate after few milliseconds
		world.worldTimer.schedule(new TimerTask() {
			private int countdown = 10;
			public void run() {
				isActivated = ! isActivated;
				countdown--;
				if(countdown < 0) {
					cancel();
					isActivated = false;
				}
			}
		}, 0, 300);
		
		PlayerEntity player = new PlayerEntity(world, (int)x, (int)y);
		world.addEntity(player);
		return player;
	}
}
