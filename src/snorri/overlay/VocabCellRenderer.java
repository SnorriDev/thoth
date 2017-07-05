package snorri.overlay;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import snorri.hieroglyphs.Hieroglyphs;

public class VocabCellRenderer implements TableCellRenderer {

	public static class Hieroglyph extends VocabCellRenderer {
		
		@Override
		public JLabel getLabel(Object value) {
			return new JLabel(Hieroglyphs.getIcon(value.toString()));
		}
		
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel label = getLabel(value);
		label.setBackground(InventoryOverlay.SELECTED_BG);
		return label;
	}
	
	public JLabel getLabel(Object value) {
		return new JLabel(value.toString());
	}
	
}
