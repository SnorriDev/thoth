package snorri.animations;

import java.awt.Image;
import java.io.File;

import snorri.main.Main;

public class Animation {

	/**
	 * stores all the animation frames as Images (PNGS)
	 * to convert SWFs to PNGs, use swfrender
	 * can cycle through frames sequentially by calling getSprite()
	 */
	
	private Image[] frames;
	private int currentFrame = 0;
	
	public Animation(File folder) {
		
		if (! folder.isDirectory()) {
			return;
		}
		
		File[] frames = folder.listFiles();
		this.frames = new Image[frames.length];
		
		for (int i = 0; i < frames.length; i++) {
			this.frames[i] = Main.getImageResource(frames[i].getPath());
		}
		
	}
	
	/**
	 * updates the frame counter to move to the next frame
	 * @return the current image
	 */
	public Image getSprite() {
		
		if (currentFrame == frames.length) {
			currentFrame = 0;
		}
		
		return frames[currentFrame++];
		
	}
	
}
