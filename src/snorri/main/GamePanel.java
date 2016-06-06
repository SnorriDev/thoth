package snorri.main;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

public abstract class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int FRAME_DELTA = 30;

	protected void startAnimation() {

		SwingWorker<Object, Object> sw = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				while (true) {
					onFrame();
					Thread.sleep(FRAME_DELTA);
				}
			}
		};

		sw.execute();
	}

	protected void onFrame() {
		// TODO Auto-generated method stub

	}

	// returns nanosecond-accurate time
	protected long getTimestamp() {
		return System.nanoTime();
	}

}
