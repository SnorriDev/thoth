package snorri.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import snorri.world.Vector;

public abstract class GamePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final int FRAME_DELTA = 15; // 33 -> 30 FPS (20 -> 50 FPS
	public static final int MARGIN = 20;

	private static final Color BACKGROUND_COLOR = new Color(255, 242, 197);
	private static final Color BUTTON_COLOR = new Color(55, 135, 206);

	private boolean stopped = false;

	protected GamePanel() {
		setBackground(BACKGROUND_COLOR);
	}

	public static double getBaseDelta() {
		return FRAME_DELTA / 1000d;
	}

	protected void startAnimation() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				onStart();
				try {
					while (!stopped) {
						onFrame();
						Thread.sleep(FRAME_DELTA);
					}
				} catch (InterruptedException e) {
					Main.error("thread interrupted");
				}
			}
		});
		
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				Main.error("unhandled exception");
				e.printStackTrace();
			}
		});
		thread.start();

	}

	/**
	 * a general method for making GUI dialogs
	 * 
	 * @param title
	 *            the title of the window
	 * @param inputs
	 *            a dialog map of inputs and textfields; as a side effect, this
	 *            map will be updated, and a pointer to it is return by the
	 *            method
	 * @return a pointer to the modified DialogMap
	 */
	protected DialogMap dialog(String title, DialogMap inputs) {
		JPanel panel = new JPanel(new GridLayout(0, 2));
		for (String key : inputs.keySet()) {
			panel.add(new JLabel(key));
			panel.add(inputs.get(key));
		}
		int option = JOptionPane.showConfirmDialog(null, panel, title, JOptionPane.PLAIN_MESSAGE);
		if (option == JOptionPane.CLOSED_OPTION) {
			return null;
		}
		return inputs;
	}

	public void onClose() {
		stopped = true;
	}

	protected JButton createButton(String text) {
		JButton button = new JButton(text);
		button.setOpaque(true);
		button.setFont(button.getFont().deriveFont(Font.BOLD));
		button.setBackground(BUTTON_COLOR);
		button.setSelected(false);
		button.addActionListener(this);
		return button;
	}

	public Vector getDimensions() {
		return new Vector(getBounds());
	}

	public Vector getCenter() {
		return getDimensions().divide(2);
	}

	protected void onStart() {
	}

	protected void onFrame() {
	}

	/**
	 * @return nanosecond-accurate time
	 */
	protected long getTimestamp() {
		return System.nanoTime();
	}

}
