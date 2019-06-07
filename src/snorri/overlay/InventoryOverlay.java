package snorri.overlay;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import snorri.events.CastEvent.Caster;
import snorri.hieroglyphs.Hieroglyphs;
import snorri.inventory.DropContainer;
import snorri.inventory.Droppable;
import snorri.inventory.Item;
import snorri.keyboard.Key;
import snorri.main.Debug;
import snorri.main.DialogMap;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.nonterminals.Sentence;
import snorri.parser.Grammar;
import snorri.triggers.Trigger.TriggerType;

/** The GUI interface for editing inventory and spells.
 * 
 * @author lambdaviking
 */
public class InventoryOverlay extends Overlay implements MouseListener, ListSelectionListener, DocumentListener, FocusListener, DropContainer<Droppable> {
		
	private static final long serialVersionUID = 1L;
	private static final Image BACKGROUND = Main.getImage("/textures/hud/inventory.png");
	protected static final Color SELECTED_BG = new Color(120, 96, 115);
	protected static final Color BORDER = new Color(115, 93, 109);
	
	private static final int INVENTORY_WIDTH = 757;
	private static final int INVENTORY_HEIGHT = 468;
	private static final double LEFT_PANEL_WIDTH_MULTIPLIER = 0.2352;
	private static final int PADDING = 16;
	private static final int LEFT_PANEL_WIDTH = (int) (INVENTORY_WIDTH * LEFT_PANEL_WIDTH_MULTIPLIER); // 178
	private static final int LEFT_PANEL_LABEL_WIDTH = PADDING - 2 * LEFT_PANEL_WIDTH;
	private static final int LEFT_PANEL_ITEM_HEIGHT = 30;
	private static final double CRAFTING_SPACE_HEIGHT_MULTIPLIER = 0.3676;
	private static final int CRAFTING_SPACE_WIDTH = INVENTORY_WIDTH - LEFT_PANEL_WIDTH; // 579
	private static final int CRAFTING_SPACE_HEIGHT = (int) (INVENTORY_HEIGHT * CRAFTING_SPACE_HEIGHT_MULTIPLIER); // 172
	private static final double TEXT_BOX_HEIGHT_MULTIPLIER = 0.55;
	private static final int TEXT_BOX_WIDTH = CRAFTING_SPACE_WIDTH - 2 * PADDING; // 547
	private static final int TEXT_BOX_HEIGHT = (int) (CRAFTING_SPACE_HEIGHT * TEXT_BOX_HEIGHT_MULTIPLIER);// 94
	private static final String ENCHANT_BUTTON_NAME = "CAST";
	
	private final Caster caster;
	
	private final JPanel craftingSpace;
	private final JPanel inputPanel;
	private final JTable vocabBox;
	private final JButton enchantButton;
	private final JEditorPane field;
	
	private final JList<Item> list;
	private final ListModel<Item> model;
		
	private boolean editMode;
	private List<String> spellsEnchanted;
	
	private class ItemCellRenderer implements ListCellRenderer<Item> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Item> list, Item item, int index, boolean isSelected, boolean cellHasFocus) {
			String text = item.toString();
			JLabel label = new JLabel(text, item.getIcon(), JLabel.LEFT);
			label.setPreferredSize(new Dimension(LEFT_PANEL_LABEL_WIDTH, LEFT_PANEL_ITEM_HEIGHT));
			label.setFont(label.getFont());
			if (isSelected) {
				label.setBorder(getThinBorder());
			}
			label.setBackground(SELECTED_BG);
			label.setOpaque(isSelected);
			return label;
		}
	}
	
	private class ItemSelectionModel extends DefaultListSelectionModel {

		private static final long serialVersionUID = 1L;

		private final int i;
		
		public ItemSelectionModel(int i) {
			super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			super.setSelectionInterval(i, i);
			this.i = i;
		}
		
		@Override
	    public boolean isSelectedIndex(int j) {
			if (editMode) {
				return super.isSelectedIndex(j);
				
			}
			return i == j;
	    }
				
	}
	
	public InventoryOverlay(FocusedWindow<?> focusedWindow, Caster caster, boolean editMode, int initialSelection) {
		
		super(focusedWindow);
		this.caster = caster;
		this.editMode = editMode;
		
		JPanel panel = new JPanel(new GridBagLayout()) {
			private static final long serialVersionUID = 1L;
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(BACKGROUND, 0, 0, null);
			}
		};
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(INVENTORY_WIDTH, INVENTORY_HEIGHT)); // inventory panel, everything must fit within this box
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(new GridBagLayout());
		setOpaque(false);
		
		spellsEnchanted = new ArrayList<>();
		
		// filter item panel
		model = caster.getInventory().getItemModel();
		list = new JList<Item>(model);
		list.setSelectionModel(new ItemSelectionModel(initialSelection));
		list.setCellRenderer(new ItemCellRenderer());
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(this);
		list.addKeyListener(this);
		list.setOpaque(false);
				
		// scroll on item panel
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, INVENTORY_HEIGHT));
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(emptyBorder());
		
		c.fill = GridBagConstraints.NORTHWEST;
		c.weightx = 0.25;
		c.gridheight = 2;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(scrollPane, c);
		
		// crafting space
		craftingSpace = new JPanel();
		craftingSpace.setLayout(new BoxLayout(craftingSpace, BoxLayout.Y_AXIS));
		craftingSpace.setPreferredSize(new Dimension(CRAFTING_SPACE_WIDTH, CRAFTING_SPACE_HEIGHT));
		craftingSpace.setOpaque(false);
		craftingSpace.addKeyListener(this);
		craftingSpace.setBorder(emptyBorder(4, 0));
		
		// the panel to show/hide
		inputPanel = new JPanel();
		inputPanel.setOpaque(false);
//		inputPanel.setVisible(false);
//		inputPanel.setFocusable(false);
		inputPanel.addKeyListener(this);
		craftingSpace.add(inputPanel);
		
		field = new JEditorPane();
		field.setEditorKit(getHTMLEditorKit());
		field.setPreferredSize(new Dimension(TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT));
		field.setBackground(SELECTED_BG);
		field.setBorder(getThinBorder());
		field.getDocument().addDocumentListener(this);
		field.addKeyListener(this);
		field.addFocusListener(this);
		inputPanel.add(field);
		
		// Add KeyListeners for text box to the full field, so you don't have to select it.
//		for (KeyListener listener : field.getKeyListeners()) {
//			addKeyListener(listener);
//		}
		
		enchantButton = createButton(ENCHANT_BUTTON_NAME, SELECTED_BG);
		enchantButton.setEnabled(false);
		enchantButton.addKeyListener(this);
		enchantButton.setFocusable(false);
		inputPanel.add(enchantButton);
		
		c.fill = GridBagConstraints.CENTER;
		c.weightx = 0.75;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		panel.add(craftingSpace, c);
		
		// vocab info space
		JComponent vocabInfo = new JPanel();
		vocabInfo.setOpaque(false);
		vocabInfo.setLayout(new GridLayout(0, 1));
		
		vocabBox = new JTable(new VocabTableModel(caster.getLexicon()));
		vocabBox.setOpaque(false);
		vocabBox.setRowHeight(30);
		vocabBox.setBackground(SELECTED_BG);
		vocabBox.setFocusable(false);
		vocabBox.setCellSelectionEnabled(false);
		
		vocabInfo.add(vocabBox);
		if (editMode) {
			JComponent buttons = new JPanel();
			buttons.setOpaque(false);
			JComponent button = createButton("ADD");
			button.addKeyListener(this);
			button.setFocusable(false);
			buttons.add(button);
			button = createButton("DELETE");
			button.addKeyListener(this);
			button.setFocusable(false);
			buttons.add(button);
			vocabInfo.add(buttons);
		}
		
		scrollPane = new JScrollPane(vocabInfo);
		scrollPane.setPreferredSize(new Dimension(CRAFTING_SPACE_WIDTH, 250)); // originally 750,414
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(emptyBorder(4, 4));
		
		c.fill = GridBagConstraints.BASELINE;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 1;
		panel.add(scrollPane, c);
		
		add(panel);
		
		setGlyphs();
		
	}
	
	public String getTagless() {
		return field.getText().replaceAll("<[^>]+>", "").replaceAll("\\s\\s", " ").trim(); // crude html strip
	}
	
	public List<String> getWords() {
		return Grammar.getWords(getTagless());
	}
	
	public String extractSpell() {
		return String.join(" ", Grammar.getWords(field.getText()));
	}
	
	@Override
	public boolean add(Droppable d) {
		if (caster.add(d)) {
			((VocabTableModel) vocabBox.getModel()).refresh(caster.getLexicon());	
			// TODO(#51): Update items as well.
			return true;
		}
		return false;
	}
	
	@Override
	public boolean remove(Droppable d, boolean specific) {
		if (caster.remove(d, specific)) {
			((VocabTableModel) vocabBox.getModel()).refresh(caster.getLexicon());
			// TODO(#51): Update items as well.
			return true;
		}
		return false;
	}
	
	private Item getItem() {
		return list.getSelectedValue();
	}
	
	/** Enchant the selected item with the contents of the spell window. */
	private void enchantIfWellFormed() {
		Item item = getItem();
		String rawSpell = getTagless();
		Sentence spell = Grammar.parseSentence(rawSpell);
		if (item == null || spell == null) {
			return;
		}
		getItem().setSpell(spell);
		spellsEnchanted.add(rawSpell);
		setGlyphs();
		if (!editMode) {
			window.unpause();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals(ENCHANT_BUTTON_NAME) && getItem() != null) {
			enchantIfWellFormed();
		} else if (e.getActionCommand().equals("ADD")) {	
			DialogMap inputs = new DialogMap();
			inputs.put("Droppable", "Enter word or item here");
			dialog("Enter droppable to add to inventory", inputs);			
			add(Droppable.fromString(inputs.getText("Droppable")));
		} else if (e.getActionCommand().equals("DELETE")) {
			DialogMap inputs = new DialogMap();
			inputs.put("Word", "Enter here...");
			dialog("Enter word to remove from inventory", inputs);			
			remove(Droppable.fromString(inputs.getText("Word")), false);
		}
				
	}
	
	private void setGlyphs() {
		if (getItem() == null || getItem().getSpell() == null) {
			return;
		}
		field.setText(Hieroglyphs.transliterate(getItem().getSpell().getOrthography()));
	}
	
	private void checkParse(DocumentEvent e) {
		String text = getTagless();
		if (Debug.allHieroglyphsUnlocked() || editMode) {
			enchantButton.setEnabled(Grammar.isValidSentence(Grammar.parseString(text)));
		}
		else {
			enchantButton.setEnabled(caster.getLexicon().contains(Grammar.getWords(text)) && Grammar.isValidSentence(Grammar.parseString(text)));
		}
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		checkParse(e);
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		if (getItem() == null || getItem().getSpell() == null) {
			field.setText("");
			return;
		}
		if (e.getComponent() instanceof JEditorPane) {
			field.setText(getItem().getSpell().getOrthography());
		}
	}
	
	@Override
	public void removeUpdate(DocumentEvent e) {
		checkParse(e);
	}
	
	@Override
	public void changedUpdate(DocumentEvent e) {
		checkParse(e);
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		if (e.getComponent() instanceof JEditorPane) {
			setGlyphs();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void stopBackgroundThread() {
		super.stopBackgroundThread();
		for (String spell : spellsEnchanted) {
			TriggerType.ENCHANT.activate(spell);
		}
	}
	
	private Border getThinBorder() {
		return BorderFactory.createLineBorder(BORDER);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (Key.ENTER.isPressed(e)) {
			enchantIfWellFormed();
		}
	}
	
}
