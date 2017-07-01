package snorri.overlay;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import snorri.hieroglyphs.Hieroglyphs;
import snorri.inventory.Armor;
import snorri.inventory.Droppable;
import snorri.inventory.FullInventory;
import snorri.inventory.Inventory;
import snorri.inventory.Item;
import snorri.inventory.Orb;
import snorri.inventory.Papyrus;
import snorri.inventory.VocabDrop;
import snorri.inventory.Weapon;
import snorri.keyboard.Key;
import snorri.main.Debug;
import snorri.main.DialogMap;
import snorri.main.FocusedWindow;
import snorri.main.Main;
import snorri.main.Util;
import snorri.parser.Grammar;
import snorri.triggers.Trigger.TriggerType;

public class InventoryOverlay extends Overlay implements MouseListener, ListSelectionListener, DocumentListener, FocusListener {
	
	/** the GUI interface for editing inventory and spells */
	
	private static final long serialVersionUID = 1L;
	private static final Image BACKGROUND = Main.getImage("/textures/hud/inventory.png");
	protected static final Color SELECTED_BG = new Color(120, 96, 115);
	protected static final Color BORDER = new Color(115, 93, 109);
	
	private static final int INVENTORY_WIDTH = 757;
	private static final int INVENTORY_HEIGHT = 468;
	private static final double LEFT_PANEL_WIDTH_MULTIPLIER = 0.2352;
	private static final int PADDING = 16;
	private static final int LEFT_PANEL_WIDTH = (int) (INVENTORY_WIDTH * LEFT_PANEL_WIDTH_MULTIPLIER); // 178
	private static final int LEFT_PANEL_HEIGHT = INVENTORY_HEIGHT;
	private static final int LEFT_PANEL_LABEL_WIDTH = PADDING - 2 * LEFT_PANEL_WIDTH;
	private static final int LEFT_PANEL_ITEM_HEIGHT = 30;
	private static final double CRAFTING_SPACE_HEIGHT_MULTIPLIER = 0.3676;
	private static final int CRAFTING_SPACE_WIDTH = INVENTORY_WIDTH - LEFT_PANEL_WIDTH; // 579
	private static final int CRAFTING_SPACE_HEIGHT = (int) (INVENTORY_HEIGHT * CRAFTING_SPACE_HEIGHT_MULTIPLIER); // 172
	private static final double TEXT_BOX_HEIGHT_MULTIPLIER = 0.55;
	private static final int TEXT_BOX_WIDTH = CRAFTING_SPACE_WIDTH - 2 * PADDING; // 547
	private static final int TEXT_BOX_HEIGHT = (int) (CRAFTING_SPACE_HEIGHT * TEXT_BOX_HEIGHT_MULTIPLIER);// 94
	//private static final int VOCAB_BOX_WIDTH = CRAFTING_SPACE_WIDTH - 4*PADDING;
	//private static final int VOCAB_BOX_HEIGHT = INVENTORY_HEIGHT-CRAFTING_SPACE_HEIGHT - 6*PADDING;
	private static final int HIEROGLYPH_SIZE = 18;
	private static final int HIEROGLYPH_TRANSLATION_FONT_SIZE = 12;
	
	private final Inventory inv;
	private final FullInventory fullInv;
	
	private final JList<Item> list;
	private final JPanel craftingSpace;
	private final JPanel inputPanel;
	private final JLabel nullInputText;
	private final JTable vocabBox;
	private final JButton enchantButton;
	private final JEditorPane field;
	
	private final SortedListModel<Item> model;
	private final VocabTableModel vocabModel;
	
	private boolean editMode;
	private List<String> spellsEnchanted;
	
	private class ItemCellRenderer implements ListCellRenderer<Item> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Item> list, Item item, int index, boolean isSelected, boolean cellHasFocus) {
			Key k = inv.getKey(item);
			String text = item.toString() + (k == null ? "" : (" (" + k.getChar() + ")"));
			JLabel label = new JLabel(text, item.getType().getIcon(), JLabel.LEFT);
			label.setPreferredSize(new Dimension(LEFT_PANEL_LABEL_WIDTH, LEFT_PANEL_ITEM_HEIGHT));
			label.setFont(label.getFont().deriveFont(inv.getIndex(item) == Integer.MAX_VALUE ? Font.PLAIN : Font.BOLD));
			if (isSelected) {
				label.setBorder(getThinBorder());
			}
			label.setBackground(SELECTED_BG);
			label.setOpaque(isSelected);
			return label;
		}
	}
	
	public InventoryOverlay(FocusedWindow<?> focusedWindow, Inventory inventory) {
		this(focusedWindow, inventory, false);
	}
	
	public InventoryOverlay(FocusedWindow<?> focusedWindow, Inventory inventory, boolean editMode) {
		
		super(focusedWindow);
		inv = inventory;
		fullInv = inventory.getFullInventory();
		
		this.editMode = editMode;
		Droppable.setInventoryForComparison(inv);
		
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
		model = fullInv.getItemModel();
		list = new JList<Item>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(this);
		list.addKeyListener(this);
		list.setOpaque(false);
		list.setCellRenderer(new ItemCellRenderer());
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, LEFT_PANEL_HEIGHT)); //Left Panel Size
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
		inputPanel.setVisible(false);
		inputPanel.setFocusable(false);
		craftingSpace.add(inputPanel);
		
		// the text which appears when nothing is selected
		nullInputText = new JLabel("Select an item to edit its spell...");
		nullInputText.setOpaque(false);
		craftingSpace.add(nullInputText);
		
		field = new JEditorPane();
		field.setEditorKit(getHTMLEditorKit());
		field.setPreferredSize(new Dimension(TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT));
		field.setBackground(SELECTED_BG);
		field.setBorder(getThinBorder());
		field.getDocument().addDocumentListener(this);
		field.addKeyListener(this);
		field.addFocusListener(this);
		inputPanel.add(field);
		
		enchantButton = createButton("ENCHANT");
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
		
		vocabModel = new VocabTableModel(fullInv.getVocab());
		vocabBox = new JTable(vocabModel);
		vocabBox.setOpaque(false);
		vocabBox.setRowHeight(30);
		vocabBox.setBackground(SELECTED_BG);
		vocabBox.setFocusable(false);
		vocabBox.setCellSelectionEnabled(false);
		
		vocabInfo.add(vocabBox);
		if (editMode) {
			JComponent buttons = new JPanel();
			buttons.setOpaque(false);
			buttons.add(createButton("ADD"));
			buttons.add(createButton("DELETE"));
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
		
		if (model.getSize() > 0) {
			list.setSelectedIndex(0);
		}
		
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
	
	private void redrawVocab() {
		// TODO update vocab
	}
	
	private void addWordPanel(VocabDrop drop) {
		
		JPanel wordPanel = new JPanel();
		wordPanel.setBackground(SELECTED_BG);
		wordPanel.setBorder(getThinBorder());
		
		JLabel icon;
		if (Hieroglyphs.getIcon(drop.getOrthography()) == null) {
			icon = new JLabel();
		}
		else {
			icon = new JLabel(Util.resize(Hieroglyphs.getIcon(drop.getOrthography()), 0, HIEROGLYPH_SIZE));
		}
		JLabel orth = new JLabel(drop.getOrthography());
		orth.setFont(new Font(orth.getFont().getName(), Font.BOLD, HIEROGLYPH_TRANSLATION_FONT_SIZE));
		JLabel pos = new JLabel(drop.getMeaning().getPOS().getSimpleName());
		pos.setFont(new Font(pos.getFont().getName(), Font.ITALIC, HIEROGLYPH_TRANSLATION_FONT_SIZE));
		String d = drop.getMeaning().toString();
		JLabel desc = new JLabel(d == null ? "unknown" : d);
		desc.setFont(new Font(pos.getFont().getName(), Font.PLAIN, HIEROGLYPH_TRANSLATION_FONT_SIZE));
		wordPanel.add(icon);
		wordPanel.add(orth);
		wordPanel.add(pos);
		wordPanel.add(desc);
		
//		vocabModel.put(drop.getOrthography(), wordPanel);
//		vocabBox.add(wordPanel);
		
	}
	
	private boolean add(Droppable d) {
		
		if (d == null) {
			Debug.error("adding null item to inventory");
		}
		
		if (!inv.add(d)) {
			return false;
		}
		
		if (d instanceof Item) {
			model.addElement((Item) d);
		}
		if (d instanceof VocabDrop) {
			addWordPanel((VocabDrop) d);
			vocabBox.revalidate();
		}
		
		return true;
		
	}
	
	private boolean delete(Droppable d, boolean specific) {
		
		if (!inv.remove(d, specific)) {
			return false;
		}
		
		if (d instanceof Item) {
			model.removeElement((Item) d);
		}
		if (d instanceof VocabDrop) {
//			vocabBox.remove(vocabModel.get(((VocabDrop) d).getOrthography()));
//			vocabModel.remove(((VocabDrop) d).getOrthography());
			vocabBox.revalidate();
		}
		
		return true;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals("ENCHANT") && list.getSelectedValue() != null) {
			String rawSpell = getTagless();
			list.getSelectedValue().setSpell(Grammar.parseSentence(rawSpell));
			spellsEnchanted.add(rawSpell);
			setGlyphs();
		}
		if (e.getActionCommand().equals("ADD")) {
			
			DialogMap inputs = new DialogMap();
			inputs.put("Droppable", "Enter word or item here");
			dialog("Enter droppable to add to inventory", inputs);
			
			add(Droppable.fromString(inputs.getText("Droppable")));
			redrawVocab();
		}
		if (e.getActionCommand().equals("DELETE")) {
			
			DialogMap inputs = new DialogMap();
			inputs.put("Word", "Enter here...");
			dialog("Enter word to remove from inventory", inputs);
			
			delete(Droppable.fromString(inputs.getText("Word")), false);
			redrawVocab();
		}
		
		list.requestFocus();
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		super.keyPressed(e);
		
		if (list.getSelectedValue() instanceof Papyrus) {
			for (int i = 0; i < Inventory.PAPYRUS_KEYS.length; i++) {
				if (Inventory.PAPYRUS_KEYS[i].isPressed(e)) {
					inv.setPapyrus(i, (Papyrus) list.getSelectedValue());
					model.redraw();
				}
			}
		}
		
		if (Key.SPACE.isPressed(e)) {
			
			if (list.getSelectedValue() instanceof Weapon) {
				inv.setWeapon((Weapon) list.getSelectedValue());
				model.redraw();
			}
			
			if (list.getSelectedValue() instanceof Armor) {
				inv.setArmor((Armor) list.getSelectedValue());
				model.redraw();
			}
			
			if (list.getSelectedValue() instanceof Orb) {
				inv.setOrb((Orb) list.getSelectedValue());
				model.redraw();
			}
			
		}
		
		if (e.getSource() == field || list.getSelectedValue() == null || !editMode) {
			return;
		}
		
		if (Key.DELETE.isPressed(e)) {
			delete(list.getSelectedValue(), true);
			model.redraw();
		}
		
	}
	
	/** This method is called when someone selects an item */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		nullInputText.setVisible(false);
		inputPanel.setVisible(true);
		setGlyphs();
		craftingSpace.revalidate();
	}
	
	private void setGlyphs() {
		
		if (list.getSelectedValue() == null) {
			return;
		}
		
		if (list.getSelectedValue().getSpell() == null) {
			field.setText("<p>enter spell here...</p>");
		}
		else {
			field.setText(Hieroglyphs.transliterate(list.getSelectedValue().getSpell().getOrthography()));
		}
	}
	
	private void checkParse(DocumentEvent e) {
		String text = getTagless();
		if (Debug.ALL_HIEROGLYPHS_UNLOCKED) {
			enchantButton.setEnabled(Grammar.isValidSentence(Grammar.parseString(text)));
		}
		else {
			enchantButton.setEnabled(fullInv.knowsWords(Grammar.getWords(text)) && Grammar.isValidSentence(Grammar.parseString(text)));
		}
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		checkParse(e);
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		if (list.getSelectedValue() == null || list.getSelectedValue().getSpell() == null) {
			field.setText("");
			return;
		}
		field.setText(list.getSelectedValue().getSpell().getOrthography());
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
	
}
