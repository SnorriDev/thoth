package snorri.main;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import snorri.entities.Player;

public class CutScene extends GamePanel implements MouseListener {

	//TODO add media path arg.
	
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
		Debug.log("skipping cutscene before " + nextWorld);
		Main.launchGame(nextWorld, player);
		Debug.log("game launched");
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
