package snorri.animations;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.imageio.ImageIO;

import snorri.main.Main;

public class Animation implements Serializable {

	/**
	 * stores all the animation frames as Images (PNGS)
	 * to convert SWFs to PNGs, use the swfrender utility
	 * getSprite() should be used to iterate over frames
	 */
	
	//TODO load all animations statically at the beginning and copy them

	private static final long serialVersionUID = 1L;
	
	//TODO: move these into respective classes?
	public static final Animation EXPLOSION = new Animation("/textures/animations/explosion");
	public static final Animation MUMMY_IDLE = new Animation("/textures/animations/mummy/idle");
	public static final Animation SPARKLE = new Animation("/textures/animations/sparkle");
	public static final Animation UNIT_IDLE = new Animation("/textures/animations/unit/idle");

	protected Image[] frames;
	private int currentFrame = 0;
	private boolean hasCycled = false;
	private String path;

	/**
	 * load an animation from a path (not package name)
	 * @param path
	 * @throws URISyntaxException
	 */

	public Animation(String str) {
		if (str.endsWith(".png")) {
			loadImage(Main.getImage(str));
		} else {
			loadFolder(Main.getFile(str));
		}
		path = str;
	}

	public Animation(Animation other) {
		set(other);
	}

	private void loadFolder(File folder) {

		if (!folder.isDirectory()) {
			return;
		}

		File[] frames = folder.listFiles();
		Arrays.sort(frames, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				return f1.getName().compareTo(f2.getName());
			}
		});

		ArrayList<Image> tempFrames = new ArrayList<Image>();
		for (int i = 0; i < frames.length; i++) {

			if (!frames[i].getName().endsWith(".png")) {
				continue;
			}
			
			try {
				tempFrames.add(ImageIO.read(frames[i]));
			} catch (IOException e) {
				Main.error("animation frame " + frames[i].getName() + " could not be loaded");
			}
		}

		this.frames = new Image[tempFrames.size()];
		int i = 0;
		for (Image im : tempFrames) {
			this.frames[i] = im;
			i++;
		}

	}

	private void loadImage(Image image) {
		if (image == null) {
			return;
		}
		frames = new Image[1];
		frames[0] = image;
	}

	// only save the path to the animation folder/image
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(getPath());
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		set(new Animation((String) in.readObject()));
	}
	
	/**
	 * set the animation back to the initial frame
	 */
	public void restart() {
		currentFrame = 0;
	}

	private void set(Animation animation) {
		path = animation.path;
		frames = animation.frames;
		hasCycled = animation.hasCycled;
		currentFrame = animation.currentFrame;
	}

	public String getPath() {
		return path;
	}

	/**
	 * updates the frame counter to move to the next frame
	 * 
	 * @return the current image
	 */
	public Image getSprite() {

		if (currentFrame == frames.length) {
			currentFrame = 0;
			hasCycled = true;
		}

		return frames[currentFrame++];

	}

	/**
	 * @return whether the animation loop has been completed at least once
	 */
	public boolean hasCycled() {
		return hasCycled;
	}
	
	@Override
	public String toString() {
		return "Animation{len: " + frames.length + ", cur: " + currentFrame + "}";
	}

}
