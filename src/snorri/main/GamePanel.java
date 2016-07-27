package snorri.main;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import snorri.world.Vector;

public abstract class GamePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final int FRAME_DELTA = 15; //33 -> 30 FPS (20 -> 50 FPS

	public static double getBaseDelta() {
		return FRAME_DELTA / 1000d;
	}
	
	protected void startAnimation() {

		SwingWorker<Object, Object> sw = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				while (true) {
					try {
						onFrame();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Thread.sleep(FRAME_DELTA);
				}
			}
		};

		sw.execute();
	}

	protected JButton createButton(String text) {
		JButton button = new JButton(text);
		button.setSelected(false);
		button.addActionListener(this);
		return button;
	}
	
	protected JButton createButton(String text, Vector size) {
		JButton button = createButton(text);
		button.setFont(Main.getCustomFont(20f));
		button.setSize(size.getX(), size.getY());
		return button;
	}

	public Vector getDimensions() {
		return new Vector(getBounds());
	}

	public Vector getCenter() {
		return getDimensions().divide(2);
	}

	protected void onFrame() {

	}

	// returns nanosecond-accurate time
	protected long getTimestamp() {
		return System.nanoTime();
	}

}
