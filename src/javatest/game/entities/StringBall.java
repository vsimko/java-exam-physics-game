package javatest.game.entities;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Random;

import javatest.game.World;

/**
 * An animated decoration.
 * @author Viliam Simko
 */
public class StringBall extends Entity {

	final static Image img = Toolkit.getDefaultToolkit().createImage("ball.gif");

	public StringBall(World world) {
		super(world);
		
		Random rnd = new Random();
		x = rnd.nextInt(world.width);
		y = rnd.nextInt(world.height);
	}
	
	@Override
	public void onUpdatePosition() {
	}

	@Override
	public void onUpdateGraphics() {
		world.gfx.drawImage(img, (int)x, (int)y, null);
	}

}
