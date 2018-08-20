package snorri.overlay;

import javax.swing.ImageIcon;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import snorri.hieroglyphs.Hieroglyphs;
import snorri.parser.Lexicon;

public class VocabTableModel implements TableModel {

	private static enum Columns {
		
		HIEROGLYPH("Hieroglyph", ImageIcon.class),
		TYPED_SPELLING("Typed spelling", String.class),
		PART_OF_SPEECH("Part of speech", String.class),
		MEANING("Meaning", String.class);
		
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
			return Lexicon.lookup(word).getPOS().getSimpleName();
		case MEANING:
			return Lexicon.lookup(word).toString();
		}
		
		return null;
		
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub	
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
