package snorri.main;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

import snorri.world.Vector;

public abstract class GamePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final int FRAME_DELTA = 15; //33 -> 30 FPS (20 -> 50 FPS
	protected static final Color NORMAL_BG = new Color(255, 179, 71);
	protected static final Color SELECTED_BG = new Color(255, 150, 71);
	protected static final Color BORDER = new Color(255, 130, 71);
	
	private boolean stopped = false;

	protected class TextPane extends JTextPane {

		private static final long serialVersionUID = 1L;
		
		public TextPane() {
			setContentType("text/html");
			setEditable(false);
			if (GamePanel.this instanceof KeyListener) {
				addKeyListener((KeyListener) GamePanel.this);
			}
			setBorder(BorderFactory.createLineBorder(BORDER));
			setBackground(NORMAL_BG);
		}
		
	}
	
	public static double getBaseDelta() {
		return FRAME_DELTA / 1000d;
	}
	
	protected void startAnimation() {

		SwingWorker<Object, Object> sw = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				try {
					onStart();
				} catch (Exception e) {
					e.printStackTrace();
				}
				while (!stopped) {
					try {
						onFrame();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Thread.sleep(FRAME_DELTA);
				}
				return null;
			}
		};

		sw.execute();
	}
	
	/**
	 * a general method for making GUI dialogs
	 * @param title
	 * 	the title of the window
	 * @param inputs
	 * 	a dialog map of inputs and textfields;
	 * as a side effect, this map will be updated,
	 * and a pointer to it is return by the method
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
	
	public void stop() {
		stopped = true;
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
