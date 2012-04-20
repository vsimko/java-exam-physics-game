import javatest.game.AnimatedImage;

/**
 * @author Viliam Simko
 */
public class TestAnim {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AnimatedImage anim = new AnimatedImage("reached-glow?.png");
		anim.nextFrame();
	}

}
