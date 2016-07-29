package snorri.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import snorri.main.Main;

public class Audio {

	private static class AudioListener implements LineListener {
		private boolean done = false;

		@Override
		public synchronized void update(LineEvent event) {
			Type eventType = event.getType();
			if (eventType == Type.STOP || eventType == Type.CLOSE) {
				done = true;
				notifyAll();
			}
		}

		public synchronized void waitUntilDone() throws InterruptedException {
			while (!done) {
				wait();
			}
		}

	}

	//private static final AudioListener listener = new AudioListener();

	// private static AudioInputStream loadPath(String path) {
	// try {
	// File f = Main.getPath(path);
	// Main.log(f.exists());
	// return AudioSystem.getAudioInputStream(new BufferedInputStream(new
	// FileInputStream(f)));
	// } catch (UnsupportedAudioFileException | IOException e) {
	// Main.error("could not load audio file " + path);
	// e.printStackTrace();
	// return null;
	// }
	// }

	// TODO store sound effects in clips
	// rather than re-reading them,

	public static synchronized Clip getClip(String path) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(Main.getPath(path)))));
			return clip;
		} catch (Exception e) {
			Main.error("could not load clip " + path);
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static Clip getArrowClip() {
		return getClip("/sound/arrow.wav");
	}

	public static synchronized void playClip(Clip clip) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AudioListener listener = new AudioListener();
					clip.addLineListener(listener);
					clip.start();
					listener.waitUntilDone();
					clip.stop();
					clip.setFramePosition(0);
				} catch (Exception e) {
					Main.error("could not play clip");
					e.printStackTrace();
				}
			}
		}).start();
	}

}
