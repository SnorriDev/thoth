package snorri.overlay;

import javax.swing.ImageIcon;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import snorri.hieroglyphs.Hieroglyphs;
import snorri.inventory.VocabDrop;

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
	
	private VocabDrop[] data;
	
	public VocabTableModel(VocabDrop[] c) {
		data = c;
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
		VocabDrop word = data[rowIndex];
		switch (Columns.values()[columnIndex]) {
		case HIEROGLYPH:
			return Hieroglyphs.getSmallIcon(word.getOrthography());
		case TYPED_SPELLING:
			return word.getOrthography();
		case PART_OF_SPEECH:
			return word.getMeaning().getPOS().getSimpleName();
		case MEANING:
			return word.getMeaning().toString();
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
	
	public void refresh(VocabDrop[] data) {
		this.data = data;
	}

}
