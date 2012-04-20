package javatest.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javatest.game.entities.BigRepulsor;
import javatest.game.entities.Entity;
import javatest.game.entities.GoalEntity;
import javatest.game.entities.JointAttractor;
import javatest.game.entities.PlayerStart;
import javatest.game.entities.ProximityMine;

import javax.swing.JFrame;

/**
 * This class is responsible for:
 * - creating the graphical context (window with a background)
 * - generating some entities
 * - scheduling the animation and rendering
 *
 * It also provides important API for handling list of entities.
 * 
 * @author Viliam Simko
 */
public class World {

	final public Graphics gfx;
	final public BufferStrategy gfxbuf;
	final public Timer worldTimer = new Timer(); // used for scheduling
	final public PlayerStart playerStart;
	private JFrame window = new JFrame();
	
	final public int width;
	final public int height;

	private LinkedList<Entity> allEntities = new LinkedList<Entity>();

	private Dimension windowDim = new Dimension();
	private Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

	AnimatedImage backgroundImage = new AnimatedImage("bkg-dark-1024.jpg");
	{
		backgroundImage.setOrigin(0, 0);
		windowDim.setSize( backgroundImage.getDimension() );
	};

	public World() {

		new WorldInput(this, window);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setUndecorated(true);
		window.setSize( windowDim );
		window.setLocation(
				( screenDim.width - windowDim.width)/2,
				(screenDim.height - windowDim.height)/2 );

		window.setVisible(true);
		window.createBufferStrategy(2);
		window.setBackground(Color.BLACK);

		width = window.getWidth();
		height = window.getHeight();

		gfxbuf = window.getBufferStrategy();
		gfx = gfxbuf.getDrawGraphics();
		

		// ==========================================================================
		// generate some objects
		Random rnd = new Random();

		// addEntity(new GoalEntity(this, width/2, height/2, 1), 300);

		 addEntity( new JointAttractor(this, (int)(width * 0.5), (int)(height * 0.2) ));
		 addEntity( new JointAttractor(this, (int)(width * 0.2), (int)(height * 0.7) ));
		 addEntity( new JointAttractor(this, (int)(width * 0.2), (int)(height * 0.8) ));

		// repulsors
		for (int i = 0; i < 3; i++)
			addEntity(new BigRepulsor(this, rnd.nextInt(width), rnd
					.nextInt(height)));
		// goals
		for (int i = 0; i < 5; i++)
			addEntity(new GoalEntity(this, rnd.nextInt(width), rnd
					.nextInt(height), i + 1), i * 300);

		 addEntity( new ProximityMine(this, width/2, 50));
//		 addEntity( new ProximityMine(this, width/2, 50));

		 playerStart = new PlayerStart(this, 100, 100);
		 addEntity(playerStart);
//		 playerStart.createNewPlayer();
		 
		 //		 for (int i = 0; i < 20; i++)
//		 addEntity(new PlayerEntity(this));
		// ==========================================================================

		// use a separate thread for rendering graphics
		worldTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				updatePositions();
			}
		}, 0, 20);

		worldTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				updateGraphics();
			}
		}, 0, 10);
	}

	@Override
	protected void finalize() throws Throwable {
		gfx.dispose();
		super.finalize();
	}

	/**
	 * Adds the entity to the list and notifies the entity.
	 * @param entity
	 */
	final public void addEntity(Entity entity) {
		synchronized (allEntities) {
			allEntities.add(entity);
		}
		entity.onAnimStarted();
	}

	/**
	 * Adds an entity to the list.
	 * The operation can be delayed.
	 * @param entity
	 * @param delay 0=immediately
	 */
	final public void addEntity(final Entity entity, int delay) {
		if(delay < 1) {
			addEntity(entity);
			return;
		}
		
		worldTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				addEntity(entity);
			}
		}, delay);
	}

	/**
	 * Removes the entity from list.
	 * @param entity
	 */
	final public void deleteEntity(Entity entity) {
		synchronized (allEntities) {
			allEntities.remove(entity);
		}
	}

	/**
	 * Redraws the scene by notifying all entities in the list.
	 * This method is called periodically from the scheduler.
	 */
	final public void updateGraphics() {
		backgroundImage.draw(gfx, 0, 0);

		for (Entity entity : getEntities())
			entity.onUpdateGraphics();

		gfxbuf.show();

		// Tell the System to do the Drawing now, otherwise it can take a few
		// extra ms until Drawing is done which looks very jerky
		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * Notifies all entities in the list to compute their new position.
	 * This method is called periodically from the scheduler.
	 */
	final public void updatePositions() {
		for (Entity entity : getEntities())
			entity.onUpdatePosition();
	}

	/**
	 * Creates a snapshot of the list of entities.
	 * Useful for avoiding race conditions.
	 * @return
	 */
	final public Entity[] getEntities() {
		synchronized (allEntities) {
			return (Entity[]) allEntities.toArray(new Entity[0]);
		}
	}
}
