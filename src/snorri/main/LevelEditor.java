package snorri.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LevelEditor extends GamePanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	public LevelEditor() {
		Main.log(Main.getFileDialog("Hello world"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	}
	
	//TOBY do stuff here!
}
