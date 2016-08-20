package snorri.animations;

import java.awt.image.BufferedImage;
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

/**
 * stores all the animation frames as Images (PNGS)
 * to convert SWFs to PNGs, use the swfrender utility
 * getSprite() should be used to iterate over frames
 */

public class Animation implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected BufferedImage[] frames;
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
	
	public Animation(BufferedImage image) {
		loadImage(image);
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

		ArrayList<BufferedImage> tempFrames = new ArrayList<>();
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

		this.frames = new BufferedImage[tempFrames.size()];
		int i = 0;
		for (BufferedImage im : tempFrames) {
			this.frames[i] = im;
			i++;
		}

	}

	private void loadImage(BufferedImage image) {
		if (image == null) {
			return;
		}
		frames = new BufferedImage[1];
		frames[0] = image;
	}

	// only save the path to the animation folder/image
	private void writeObject(ObjectOutputStream out) throws IOException {
		
		out.writeObject(getPath() == null ? "..." : getPath());
		
		// for image animations that were not read from a file
		if (path == null) {
			ImageIO.write(frames[0], "png", out);
		}
		
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		
		String read = (String) in.readObject();
		
		if (read.equals("...")) { //signifies "nowhere"
			BufferedImage im = ImageIO.read(in);
			if (im != null) {
				set(new Animation(im));
			} else {
				Main.error("could not load custom frame in animation");
			}
			return;
		}
		
		set(new Animation((String) read));
		
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
	public BufferedImage getSprite() {

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
