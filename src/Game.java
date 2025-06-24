import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;


public class Game extends JPanel implements ActionListener, KeyListener {
	
	public static final char[] directions = {'U', 'D', 'L', 'R'};
	
	public static final int xDisplayOffset = 0;
	public static final int yDisplayOffset = 0;
	
	public static final int spacing = 20;
	public static final int imageSize = 100;
	public static final int maxVel = imageSize - 1;
	public final static int accel = maxVel / 40;

	private final Image lava = new ImageIcon(getClass().getResource("./resources/lava.png")).getImage();
	private final Image pog = new ImageIcon(getClass().getResource("./resources/pog.png")).getImage();
	private final Image skull = new ImageIcon(getClass().getResource("./resources/skull.png")).getImage();
	private final Image grass = new ImageIcon(getClass().getResource("./resources/grass.png")).getImage();
	private final Image zombie = new ImageIcon(getClass().getResource("./resources/zombie.png")).getImage();
	private final Image face = new ImageIcon(getClass().getResource("./resources/face.png")).getImage();
	private final Image[] images = {lava, pog, skull, grass, zombie, face};
	
	boolean up;
	boolean down;
	boolean left;
	boolean right;
	public static boolean pause;
	
	public static final Character[][] gameMap = {{'0','0','0','0','0','0','0'},{'1','1','1','1','1','1','1'},
						{'1','0','0','0','0','0','1'},
						{'1','0','0','0','0','0','1'},
						{'1','0','0','0','0','0','1'},
						{'1','0','0','0','0','0','1'},
						{'1','2','0','0','0','0','1'},
						{'1','1','1','1','1','1','1'}};
	HashMap<Character, Image> tileImages = new HashMap<>();


	Timer gameLoop;
	Entity player;
	Game() {
		tileImages.put('1', pog);
		tileImages.put('2', lava);
		
	setPreferredSize(new Dimension(700, 700));
		setBackground(new Color(0, 0, 0));
		addKeyListener(this);
		setFocusable(true);

//		load();
		gameLoop = new Timer(17, this);
		
		player = new Entity(100, 100);
		
		gameLoop.start();

	}

//	public void load() {}


//	gets called automatically
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (int i = 0; i < gameMap.length; i++) {
			for (int j = 0; j < gameMap[i].length; j++) {
				if (!gameMap[i][j].equals('0')) {
					g.drawImage(tileImages.get(gameMap[i][j]), j * imageSize + xDisplayOffset, i * imageSize + yDisplayOffset, imageSize, imageSize, null);
				}
			}
		}
		
//		how should i order this stuff
//		find velocity
//		find collision direction and maximum coordinate
//			find colliding block(s)
//			find colliding side of block
//				add each velocity individually and see which makes it overlap
//				if neither do then select the one with the smaller velocity
//				return the directions that overlap and the coordinate which it cant go past
//		update position
		
		
		accelerateUser();
		player.updatePosition(g);
		int kys = 500;
		g.setColor(new Color(255, 255,255));
		g.drawString(Integer.toString(player.xVel) + ", " + Integer.toString(player.yVel), 3, kys);
		g.drawString(Integer.toString(player.xIndex) + ", " + Integer.toString(player.yIndex), 3, kys + spacing);
		g.drawString(Integer.toString(player.xIndex * imageSize) + ", " + Integer.toString(player.yIndex * imageSize), 3, kys + (spacing * 2));
		g.drawString(Integer.toString(player.x) + ", " + Integer.toString(player.y), 3, kys + (spacing * 3));
		g.drawImage(face, player.x + xDisplayOffset, player.y + yDisplayOffset, imageSize, imageSize, null);
	}

	public void accelerateUser() {
		if (up || down) {
			if (up) {
				if (player.yVel > 0) {
					player.yVel = 0;
				} else if (player.yVel - accel > -maxVel) {
					player.yVel -= accel;
				} else {
					player.yVel = -maxVel;
				}
			}
			if (down) {
				if (player.yVel < 0) {
					player.yVel = 0;
				} else if (player.yVel + accel < maxVel) {
					player.yVel += accel;
				} else {
					player.yVel = maxVel;
				}
			}
		} else {
			player.yVel = 0;
		}

		if (left || right) {
			if (left) {
				if (player.xVel > 0) {
					player.xVel = 0;
				} else if (player.xVel - accel > -maxVel){
					player.xVel -= accel;
				} else {
					player.xVel = -maxVel;
				}
			}
			if (right) {
				if (player.xVel < 0) {
					player.xVel = 0;
				} else if (player.xVel + accel < maxVel){
					player.xVel += accel;
				} else {
					player.xVel = maxVel;
				}
			}
		} else {
			player.xVel = 0;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final int width = getWidth();
		final int height = getHeight();
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
//	doesnt do anything with stuff like arrow characters cause nothing is typed
	}

	@Override
	public void keyPressed(KeyEvent e) {
//	activates as long as pressing key
//		System.out.println(e.getKeyCode());
		if (e.getKeyCode() == 87) {
			up = true;
		}
		if (e.getKeyCode() == 83) {
			down = true;
		}
		if (e.getKeyCode() == 65) {
			left = true;
		}
		if (e.getKeyCode() == 68) {
			right = true;
		}
		if (e.getKeyCode() == 32) { // space
			pause = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 87) { // w
			up = false;
		}
		if (e.getKeyCode() == 83) { // s
			down = false;
		}
		if (e.getKeyCode() == 65) { // a
			left = false;
		}
		if (e.getKeyCode() == 68) { // d
			right = false;
		}
		if (e.getKeyCode() == 32) { // space
			pause = false;
		}
	}
}

//d=68
//s=83
//a=65
//w=87