package snorri.overlay;

import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import snorri.grammar.DefaultLexicon;
import snorri.grammar.Lexicon;
import snorri.hieroglyphs.Hieroglyphs;

public class VocabTableModel implements TableModel {

	private static enum Columns {

		HIEROGLYPH("Hieroglyph", ImageIcon.class), TYPED_SPELLING("Typed spelling", String.class),
		PART_OF_SPEECH("Part of speech", String.class), MEANING("Meaning", String.class);

		private final String name;
		private final Class<?> type;

		Columns(String name, Class<?> type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public Class<?> getType() {
			return type;
		}

	}

	private String[] data;

	public VocabTableModel(Lexicon lexicon) {
		refresh(lexicon);
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return Columns.values().length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return Columns.values()[columnIndex].getName();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Columns.values()[columnIndex].getType();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
//		if (rowIndex == 0) {
//			return Columns.values()[columnIndex].getName();
//		}
		String word = data[rowIndex];
		switch (Columns.values()[columnIndex]) {
		case HIEROGLYPH:
			return Hieroglyphs.getSmallIcon(word);
		case TYPED_SPELLING:
			return word;
		case PART_OF_SPEECH:
			return DefaultLexicon.lookup(word).stream().map(def -> def.getPartOfSpeech()).collect(Collectors.toList())
					.toString();
		case MEANING:
			return DefaultLexicon.lookup(word).stream().map(def -> def.getEnglish()).collect(Collectors.toList());
		}

		return null;

	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}

	public void refresh(Lexicon lexicon) {
		data = new String[lexicon.size()];
		int i = 0;
		for (String word : lexicon) {
			data[i] = word;
			i++;
		}
	}

}
