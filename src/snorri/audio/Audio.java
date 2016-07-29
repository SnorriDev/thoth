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

	public static final File ARROW = Main.getPath("/sound/arrow.wav");

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

	public static synchronized void playSound(File f) {
		class AudioListener implements LineListener {
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
		AudioListener listener = new AudioListener();
		new Thread(new Runnable() {
			public void run() {
				Clip clip = null;
				try {
					clip = AudioSystem.getClip();
					clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(f))));
					clip.start();
					listener.waitUntilDone();
				} catch (Exception e) {
					Main.error("could not play audio file");
					e.printStackTrace();
				} finally {
					if (clip != null)
						clip.close();
				}
			}
		}).start();
	}

}
