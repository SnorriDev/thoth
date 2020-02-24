package snorri.windows;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import snorri.entities.Player;
import snorri.main.Debug;
import snorri.main.Main;

public class CutScene extends GamePanel implements MouseListener {

	// TODO(snorri): Add media path arg.
	
	private static final long serialVersionUID = 1L;

	private final String nextWorld;
	private Player player;
	
	public CutScene(String nextWorld, Player player) {
		
		super();
		
		this.nextWorld = nextWorld;
		this.player = player;
		
		add(new JLabel("Cutscene placeholder.."));
		addMouseListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		skip();
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		skip();
	}
	
	public void skip() {
		Debug.logger.info("skipping cutscene before " + nextWorld);
		Main.launchGame(nextWorld, player);
		Debug.logger.info("game launched");
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

}
