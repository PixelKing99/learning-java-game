import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class Game extends JPanel {
	
	private final int imageSize = 50;
	
	private final Image lava = new ImageIcon(getClass().getResource("./resources/lava.png")).getImage();
	private final Image pog = new ImageIcon(getClass().getResource("./resources/pog.png")).getImage();
	private final Image skull = new ImageIcon(getClass().getResource("./resources/skull.png")).getImage();
	private final Image grass = new ImageIcon(getClass().getResource("./resources/grass.png")).getImage();
	private final Image zombie = new ImageIcon(getClass().getResource("./resources/zombie.png")).getImage();
	private final Image face = new ImageIcon(getClass().getResource("./resources/face.png")).getImage();
	private final Image[] images = {lava, pog, skull, grass, zombie, face};
	Game() {
		setPreferredSize(new Dimension(500, 500));
		setBackground(new Color(0, 0, 0));
		
//		load();
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < images.length; i++) {
			g.drawImage(images[i], i * imageSize, 0, imageSize, imageSize, null);
		}
	}
	
}
