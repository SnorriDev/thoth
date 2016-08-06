package snorri.overlay;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import snorri.hieroglyphs.Hieroglyphs;
import snorri.inventory.Droppable;
import snorri.inventory.FullInventory;
import snorri.inventory.Inventory;
import snorri.inventory.Item;
import snorri.inventory.VocabDrop;
import snorri.keyboard.Key;
import snorri.main.Debug;
import snorri.main.DialogMap;
import snorri.main.FocusedWindow;
import snorri.nonterminals.NonTerminal;
import snorri.parser.Grammar;

public class InventoryOverlay extends Overlay implements MouseListener, ListSelectionListener, DocumentListener, FocusListener {

	/**
	 * the GUI interface for editing inventory and spells
	 */
	
	private static final long serialVersionUID = 1L;

	private final Inventory inv;
	private final FullInventory fullInv;
		
	private final JList<Item> list;
	private final JPanel craftingSpace;
	private final JPanel inputPanel;
	private final JPanel vocabBox;
	private final JButton enchantButton;
	private final JEditorPane field;
	
	private final DefaultListModel<Item> model;
	private final Map<String, JComponent> vocabModel;
		
	private static class ItemCellRenderer implements ListCellRenderer<Item> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Item> list, Item item, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel label = new JLabel(item.toString(), item.getType().getIcon(), JLabel.LEFT);
			label.setPreferredSize(new Dimension(290, 35));
			label.setBackground(isSelected ? SELECTED_BG : NORMAL_BG);
			label.setOpaque(true);
			return label;
		}
	}
	
	public InventoryOverlay(FocusedWindow focusedWindow, Inventory inventory) {
		this(focusedWindow, inventory, false);
	}
	
	public InventoryOverlay(FocusedWindow focusedWindow, Inventory inventory, boolean editMode) {
		
		super(focusedWindow);
		inv = inventory;
		fullInv = inventory.getFullInventory();
				
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(1000, 618)); //golden ratio
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(new GridBagLayout());
		panel.setBackground(NORMAL_BG);
		setOpaque(false);
		
		//filter item panel
		model = fullInv.getItemModel();
		list = new JList<Item>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setBackground(NORMAL_BG);
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(this);
		list.addKeyListener(this);
		list.setCellRenderer(new ItemCellRenderer());
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(300, 618));
		scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 5));
				
		c.fill = GridBagConstraints.NORTHWEST;
		c.weightx = 0.25;
		c.gridheight = 2;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(scrollPane, c);
		
		//crafting space
		craftingSpace = new JPanel();
		craftingSpace.setLayout(new BoxLayout(craftingSpace, BoxLayout.Y_AXIS));
		craftingSpace.setPreferredSize(new Dimension(700, 250));
		craftingSpace.addKeyListener(this);
		craftingSpace.setBackground(NORMAL_BG);
		craftingSpace.setBorder(BorderFactory.createLineBorder(BORDER, 5));
		
		//the panel to show/hide
		inputPanel = new JPanel();
		inputPanel.setOpaque(false);
		inputPanel.setVisible(false);
		craftingSpace.add(inputPanel);
		
		field = new JEditorPane();
		field.setContentType("text/html");
		field.setPreferredSize(new Dimension(650, 100));
		field.setBorder(BorderFactory.createLineBorder(BORDER));
		field.setBackground(SELECTED_BG);
		field.getDocument().addDocumentListener(this);
		field.addKeyListener(this);
		field.addFocusListener(this);
		inputPanel.add(field);
				
		enchantButton = createButton("Enchant");
		enchantButton.setEnabled(false);
		enchantButton.addKeyListener(this);
		inputPanel.add(enchantButton);
		
		c.fill = GridBagConstraints.CENTER;
		c.weightx = 0.75;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		panel.add(craftingSpace, c);
		
		//vocab info space
		JComponent vocabInfo = new JPanel();
		vocabInfo.setPreferredSize(new Dimension(700, 368));
		vocabInfo.setBackground(NORMAL_BG);
		vocabInfo.setBorder(BorderFactory.createLineBorder(BORDER, 5));
		vocabInfo.setLayout(new GridLayout(0, 1));
		
		vocabBox = new JPanel();
		vocabModel = new HashMap<>();
		vocabBox.setOpaque(false);
		
		for (VocabDrop drop : fullInv.getVocab()) {
			addWordPanel(drop);
		}
		
		vocabInfo.add(vocabBox);
		if (editMode) {
			JComponent buttons = new JPanel();
			buttons.setOpaque(false);
			buttons.add(createButton("Add"));
			buttons.add(createButton("Delete"));
			vocabInfo.add(buttons);
		}
		
		c.fill = GridBagConstraints.BASELINE;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 1;
		panel.add(vocabInfo, c);
		
		add(panel);
				
	}
		
	public String getTagless() {
		return field.getText()
				.replaceAll("<[^>]+>", "").replaceAll("\\s\\s", " ").trim(); //crude html strip
	}
	
	public List<String> getWords() {
		return Grammar.getWords(getTagless());
	}
	
	public String extractSpell() {
		return String.join(" ", Grammar.getWords(field.getText()));
	}
	
	private void redrawVocab() {
		//TODO update vocab
	}
	
	private void addWordPanel(VocabDrop drop) {
		
		JPanel wordPanel = new JPanel();
		wordPanel.setBackground(SELECTED_BG);
		
		JLabel icon = new JLabel(Hieroglyphs.getIcon(drop.getOrthography()));
		JLabel orth = new JLabel(drop.getOrthography());
		orth.setFont(new Font(orth.getFont().getName(), Font.BOLD, 16));
		JLabel pos = new JLabel(drop.getMeaning().getPOS().getSimpleName());
		pos.setFont(new Font(pos.getFont().getName(), Font.ITALIC, 12));
		String d = drop.getMeaning().getShortDesc();
		JLabel desc = new JLabel(d == null ? "unknown" : d);
		desc.setFont(new Font(pos.getFont().getName(), Font.PLAIN, 14));
		wordPanel.add(icon);
		wordPanel.add(orth);
		wordPanel.add(pos);
		wordPanel.add(desc);
		
		vocabModel.put(drop.getOrthography(), wordPanel);
		vocabBox.add(wordPanel);
		
	}
	
	private void add(Droppable d) {
		fullInv.add(d);
		if (d instanceof Item) {
			model.addElement((Item) d);
		}
		if (d instanceof VocabDrop) {
			addWordPanel((VocabDrop) d);
			vocabBox.revalidate();
		}
	}
	
	private void delete(Droppable d) {
		inv.remove(d);
		if (d instanceof Item) {
			model.removeElement((Item) d);
		}
		if (d instanceof VocabDrop) {
			vocabBox.remove(vocabModel.get(((VocabDrop) d).getOrthography()));
			vocabModel.remove(((VocabDrop) d).getOrthography());
			vocabBox.revalidate();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Enchant")) {
			list.getSelectedValue().setSpell(Grammar.parseString(getTagless()));
			setGlyphs();
		}
		if (e.getActionCommand().equals("Add")) {
			
			DialogMap inputs = new DialogMap();
			inputs.put("Droppable", "Enter word or item here");
			dialog("Enter droppable to add to inventory", inputs);
			
			add(Droppable.fromString(inputs.getText("Droppable")));
			redrawVocab();
		}
		if (e.getActionCommand().equals("Delete")) {
			
			DialogMap inputs = new DialogMap();
			inputs.put("Word", "Enter here...");
			dialog("Enter word to remove from inventory", inputs);
			
			delete(Droppable.fromString(inputs.getText("Word")));
			redrawVocab();
		}
		
		list.requestFocus();
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		super.keyPressed(e);
		
		if (Key.DELETE.isPressed(e) && list.getSelectedValue() != null) {
			delete(list.getSelectedValue());
		}
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		inputPanel.setVisible(true);
		setGlyphs();
		craftingSpace.revalidate();
	}
	
	private void setGlyphs() {
		if (list.getSelectedValue().getSpell() == null) {
			field.setText("<p>enter spell here...</p>");
		} else {
			field.setText(Hieroglyphs.transliterate(list.getSelectedValue().getSpell().getOrthography()));
		}
	}
	
	private void checkParse(DocumentEvent e) {
		String text = getTagless();
		if (Debug.ALL_HIEROGLYPHS_UNLOCKED) {
			enchantButton.setEnabled(Grammar.parseString(text) instanceof NonTerminal);
		} else {
			enchantButton.setEnabled(Grammar.parseString(text) instanceof NonTerminal && fullInv.knowsWords(Grammar.getWords(text)));
	
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		checkParse(e);
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (list.getSelectedValue().getSpell() == null) {
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

}
