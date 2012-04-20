package javatest.game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.ImageIcon;

import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * Supports loading images from the "images" package located in the JAR.
 * The animation is constructed from images matching the given filename pattern.
 * You can use this API also to load a single image from a JAR.
 * 
 * Example:
 * 
 *   AnimatedImage anim = new AnimatedImage("frame?.png");
 *   AnimatedImage singleImage = new AnimatedImage("img.png");
 *   
 * @author Viliam Simko
 */
public class AnimatedImage {

	static final private String PKGIMAGES = "/images";
	static final private Hashtable<String, Image[]> loadedFrames = new Hashtable<String, Image[]>();

	/**
	 * Loads individual images matching the filename pattern as frames of an animation.
	 * @param filenamePattern e.g. "glow?.png"
	 * @return array containing the frames
	 */
	synchronized static private Image[] loadAnimation(
			final String filenamePattern) {

		Image[] frames = loadedFrames.get(filenamePattern);

		if (frames == null) {

			WildcardFileFilter filter = new WildcardFileFilter(filenamePattern);

			URL appRootUrl = AnimatedImage.class.getResource(PKGIMAGES);
			
			try {
				String urlProtocol = appRootUrl.getProtocol();

				String[] files;
				
				if( "file".equals(urlProtocol) ) {
					files = new File(appRootUrl.toURI()).list(filter);
				} else if( "jar".equals(urlProtocol)) {
//					System.out.println("Trying to load from JAR");
					JarFile jar;
					JarURLConnection jarcon = (JarURLConnection) appRootUrl.openConnection();
					jar = jarcon.getJarFile();
//					System.out.println(jar.getName());
					
					LinkedList<String> acceptedFiles = new LinkedList<String>();
					for (Enumeration<JarEntry> i = jar.entries(); i.hasMoreElements();) {
				        JarEntry je = i.nextElement();
				        String name = "/" + je.getName();
				        if(name.startsWith(PKGIMAGES)) {
					        name = name.substring( PKGIMAGES.length() + 1 );
					        if(filter.accept(null, name)) {
//						        System.out.println("Accepted:" + name);
					        	acceptedFiles.add(name);
					        }
				        }
				    }
					files = acceptedFiles.toArray( new String[]{} );
				} else {
					throw new URISyntaxException( appRootUrl.toString(), "Unsupported protocol");
				}
				
				Arrays.sort(files);

				frames = new Image[files.length];
				for (int i = 0; i < files.length; i++) {
					String filename = files[i];
					URL imgUrl = AnimatedImage.class.getResource(PKGIMAGES
							+ "/" + filename);
//					System.out.println(imgUrl);
					ImageIcon icon = new ImageIcon(imgUrl);
					frames[i] = icon.getImage();
				}
				loadedFrames.put(filenamePattern, frames);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return frames;
	}

	private Image[] imageFrames;
	private int currentFrame;

	private int originX;
	private int originY;

	/**
	 * Loads images as frames and sets the origin to the center.
	 * @param filenamePattern
	 */
	public AnimatedImage(String filenamePattern) {
		imageFrames = loadAnimation(filenamePattern);

		resetToFirstFrame();
		setOrigin(
			getCurrentImage().getWidth(null) / 2,
			getCurrentImage().getHeight(null) / 2 );
	}

	/**
	 * Advances the index to the next frame.
	 * (Does not draw the image)
	 * @return index of the current frame (0.. animation length)
	 */
	final public boolean nextFrame() {
		currentFrame++;
		return currentFrame < imageFrames.length;
	}
	
	/**
	 * Rewinds the animation to the first frame.
	 */
	final public void resetToFirstFrame() {
		currentFrame = 0;
	}

	/**
	 * Draws the current frame of the animation to the given coordinates.
	 * @param gfx where to draw the image (graphics context)
	 * @param x
	 * @param y
	 */
	final public void draw(Graphics gfx, int x, int y) {
		gfx.drawImage(getCurrentImage(), x - originX, y - originY, null);
	}

	/**
	 * You might want to adjust the x,y coordinates for your image.
	 * 
	 * Example: Use center of the image as the origin.
	 * 
	 *   myanim.setOrigin(
	 *     myanim.getWidth(null) / 2,
	 *     myanim.getHeight(null) / 2 )
	 *     
	 * @param x
	 * @param y
	 */
	final public void setOrigin(int x, int y) {
		originX = x;
		originY = y;
	}
		
	/**
	 * A simple heuristics to estimate radius of an image.
	 * This works for round images.
	 * @return estimated radius in pixels
	 */
	final public int getClickRadius() {
		Image curImg = getCurrentImage();
		return (int) (( curImg.getWidth(null) + curImg.getHeight(null) ) * 0.2);
	}
	
	/**
	 * @return the Dimension object containing width and height
	 */
	final public Dimension getDimension() {
		Image curImg = getCurrentImage();
		return new Dimension(curImg.getWidth(null), curImg.getHeight(null));
	}
	
	private Image getCurrentImage() {
		if (currentFrame >= imageFrames.length)
			return imageFrames[imageFrames.length - 1];

		return imageFrames[currentFrame];
	}
}
