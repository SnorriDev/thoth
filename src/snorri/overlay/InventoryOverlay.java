package snorri.overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import snorri.inventory.FullInventory;
import snorri.inventory.Inventory;
import snorri.keyboard.Key;
import snorri.main.GamePanel;
import snorri.main.GameWindow;

public class InventoryOverlay extends GamePanel implements KeyListener {

	/**
	 * the GUI interface for editing inventory and spells
	 */
	
	private static final long serialVersionUID = 1L;

	private final Inventory inv;
	private final FullInventory fullInv;
	private final GameWindow window;
	
	public InventoryOverlay(GameWindow window, Inventory inventory) {
		
		this.window = window;
		inv = inventory;
		fullInv = inventory.getFullInventory();
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(1000, 618)); //golden ratio
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(new GridBagLayout());
		panel.setBackground(Color.ORANGE);
		setOpaque(false);
		addKeyListener(this);
		
		//filter item panel
		JComponent items = new JPanel(new GridLayout(0, 1));
		items.setPreferredSize(new Dimension(300, 618));
		items.add(new JLabel("An item"));
		items.add(new JLabel("Another item"));
		items.add(new JLabel("A third item"));
				
		c.fill = GridBagConstraints.NORTHWEST;
		c.weightx = 0.25;
		c.gridheight = 2;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(items, c);
		
		//crafting space
		JComponent craftingSpace = new JLabel("Crafting space");
		craftingSpace.setPreferredSize(new Dimension(700, 250));
		
		c.fill = GridBagConstraints.BASELINE;
		c.weightx = 0.75;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		panel.add(craftingSpace, c);
		
		//vocab info space
		JComponent vocabInfo = new JLabel("Vocab info");
		
		c.fill = GridBagConstraints.BASELINE;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 1;
		panel.add(vocabInfo, c);
		
		add(panel);
		
		
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (Key.ESC.isPressed(e)) {
			window.closeInventory();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
