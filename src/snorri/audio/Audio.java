package snorri.audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import snorri.main.Debug;
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

	public static synchronized Clip getClip(String path) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(Main.getFile(path)))));
			return clip;
		} catch (Exception e) {
			Debug.error(e);
			return null;
		}
		
	}
	
	public static void loopClip(Clip clip) {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public static void loopClip(ClipWrapper wrapper) {
		loopClip(wrapper.getClip());
	}
	
	public static void playClip(Clip clip) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
					AudioListener listener = new AudioListener();
					clip.setFramePosition(0);
					clip.start();
					listener.waitUntilDone();
					clip.removeLineListener(listener);
					clip.stop();
					clip.setFramePosition(0);
				} catch (Exception e) {
					Debug.error(e);
				}
			}
		}).start();
	}
	
	public static void playClip(ClipWrapper wrapper) {
		playClip(wrapper.getClip());
	}

}
