package snorri.audio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.sound.sampled.Clip;

public class ClipWrapper implements Serializable {

	/**
	 * Serializable class for saving audio.
	 */
	private static final long serialVersionUID = 1L;
	private String path;
	private Clip clip;
	
	public ClipWrapper(String path) {
		set(path);
	}
	
	public Clip getClip() {
		return clip;
	}
	
	// only save the path to the animation folder/image
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(path);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		set((String) in.readObject());
	}
	
	private void set(String path) {
		this.path = path;
		clip = Audio.getClip(path);
	}
	
}
