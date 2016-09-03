package snorri.audio;

import javax.sound.sampled.Clip;

public enum Music {

	MAIN_THEME("/sound/music/mainTheme.wav");
	
	private final Clip clip;
	
	Music(String path) {
		this.clip = Audio.getClip(path);
	}
	
	public void play() {
		Audio.loopClip(clip);
	}

	public void stop() {
		clip.stop();
		clip.setFramePosition(0);
	}
	
}
