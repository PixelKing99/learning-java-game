import saves.Map;
import saves.Saves;
import saves.Tile;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import javax.swing.*;



public class Game extends JPanel implements ActionListener, KeyListener, MouseListener {
	
	public static final int spacing = 20;
	public static int tileSize = 100; // used for calculations inside the game, not corrosponding to a number of pixels
	public static int maxVel = tileSize * 9 / 10;
	public static int accel = maxVel / 40;

	private final Image lava = new ImageIcon(getClass().getResource("./resources/lava.png")).getImage();
	private final Image pog = new ImageIcon(getClass().getResource("./resources/pog.png")).getImage();
	private final Image skull = new ImageIcon(getClass().getResource("./resources/skull.png")).getImage();
	private final Image grass = new ImageIcon(getClass().getResource("./resources/grass.png")).getImage();
	private final Image zombie = new ImageIcon(getClass().getResource("./resources/zombie.png")).getImage();
	public final Image face = new ImageIcon(getClass().getResource("./resources/face.png")).getImage();
	
	boolean up;
	boolean down;
	boolean left;
	boolean right;
	boolean spacebar;
	
	
	boolean cursor = true;
	boolean selectedTile = true;
	
	// mouse buttons
	static boolean leftButtonDown = false;
	static boolean rightButtonDown = false;


//	need to add ability to select tiles and then some way to select how to change it
//	add enemies
//	function to lava
	
	private static Saves saveFile;
	private static Map gameMap;
	
	HashMap<Tile, Image> tileImages = new HashMap<>();


	static Timer gameLoop;
	Entity player;
	
	static boolean slowMode = false;
	private boolean prevSpacebar = false;
	
	
	
	Game() throws DataFormatException, IOException {
		tileImages.put(Tile.WALL, pog);
		tileImages.put(Tile.LAVA, lava);
		
		setPreferredSize(new Dimension(700, 700));
		setBackground(new Color(0, 0, 0));
		addKeyListener(this);
		addMouseListener(this);
		setFocusable(true);
		
		saveFile = loadSave();
		gameMap = saveFile.getMap();
		
//		1000ms / 60fps so that it is about 60 fps
		gameLoop = new Timer((int) Math.round(1000 / 60.0), this);
		
		player = new Entity(gameMap, 100, 100);
		
		gameLoop.start();

	}

	public Saves loadSave() throws DataFormatException, IOException {
		Saves saveFile;
		try {
			saveFile = new Saves("default");
			
		} catch (FileNotFoundException fnfe) {
			
			saveFile = new Saves("default", new Map(Map.DEFAULT_MAP));
		}
		
		return saveFile;
	}


//	gets called automatically
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		
		accelerateUser();
		player.updatePosition(spacebar);
		
		GameGraphics curFrame = new GameGraphics(player.x, player.y, getWidth(), getHeight(), g);
		
		for (int i = 0; i < gameMap.getLength(); i++) {
			for (int j = 0; j < gameMap.getInnerLength(); j++) {
				
				if (gameMap.get(i, j) != Tile.AIR) {
					curFrame.drawImage(tileImages.get(gameMap.get(i,j)), j * tileSize, i * tileSize, tileSize);
				}
			}
		}
		
		Point mouse = this.getMousePosition();

		if (selectedTile) {
			curFrame.drawSelectedTile(mouse);
		}

		
//		just to debug collisions, should probably make some sort of dedicated debug renderer cause i keep on deleting debug stuff and then recoding it
		g.setColor(new Color(0, 0,255));
		curFrame.drawImage(face, player.x, player.y, tileSize);
		if (player.collisionDirections.get(Directions.UP)) {
			g.fillRect(curFrame.width / 2 - curFrame.imageSize / 6, curFrame.height /2 - curFrame.imageSize/2, curFrame.imageSize/3, curFrame.imageSize/3);
		}
		if (player.collisionDirections.get(Directions.RIGHT)) {
			g.fillRect(curFrame.width / 2 + curFrame.imageSize / 6, curFrame.height /2 - curFrame.imageSize/6, curFrame.imageSize/3, curFrame.imageSize/3);
		}
		if (player.collisionDirections.get(Directions.DOWN)) {
			g.fillRect(curFrame.width / 2 - curFrame.imageSize / 6, curFrame.height /2 + curFrame.imageSize/6, curFrame.imageSize/3, curFrame.imageSize/3);
		}
		if (player.collisionDirections.get(Directions.LEFT)) {
			g.fillRect(curFrame.width / 2 - curFrame.imageSize / 2, curFrame.height /2 - curFrame.imageSize/6, curFrame.imageSize/3, curFrame.imageSize/3);
		}
		
		
		
		int sidebarWidth;
		int sidebarHeight;
		
		if (curFrame.width > curFrame.height) {
			sidebarWidth = curFrame.xDisplayOffset;
			sidebarHeight = curFrame.height;
		} else {
			sidebarWidth = curFrame.width;
			sidebarHeight = curFrame.yDisplayOffset;
		}

//		g.setColor(new Color(20, 20,20));
//		g.fillRect(0, 0, sidebarWidth, sidebarHeight);
//		g.fillRect(curFrame.width - sidebarWidth, curFrame.height - sidebarHeight, sidebarWidth, sidebarHeight);

//		debug lines where the edge of the sidebars are
		g.setColor(new Color(255, 0,0));
		g.drawLine(curFrame.xDisplayOffset, curFrame.yDisplayOffset, sidebarWidth, sidebarHeight);
		g.drawLine(curFrame.width - sidebarWidth, curFrame.height - sidebarHeight, curFrame.width - curFrame.xDisplayOffset, curFrame.height - curFrame.yDisplayOffset);
		
		if (cursor) {
			curFrame.drawCursor(mouse);
		}
		
//		just to show the center of the screen and make sure everything is aligned while debugging
		g.setColor(new Color(255, 0,255));
		g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
		g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
		
		
		g.setColor(new Color(255, 255,255));
		int textOffset = curFrame.height/2;
		g.drawString(Integer.toString(player.xVel) + ", " + Integer.toString(player.yVel), 3, textOffset);
		g.drawString(Integer.toString(player.xIndex) + ", " + Integer.toString(player.yIndex), 3, textOffset + spacing);
		g.drawString(Integer.toString(player.xIndex * tileSize) + ", " + Integer.toString(player.yIndex * tileSize), 3, textOffset + (spacing * 2));
		g.drawString(Integer.toString(player.x) + ", " + Integer.toString(player.y), 3, textOffset + (spacing * 3));
		
		
		
		if (spacebar && !prevSpacebar) {
			if (slowMode) {
				gameLoop.setDelay((int) Math.round(1000 / 1.0));
				slowMode = false;
			} else {
				gameLoop.setDelay((int) Math.round(1000 / 60.0));
				slowMode = true;
			}
		}
		
		prevSpacebar = spacebar;
	}

//	this can definitely be improved, some of this is definitely redundant
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
		if (e.getKeyCode() == (int) 'W') {
			up = true;
		}
		if (e.getKeyCode() == (int) 'S') {
			down = true;
		}
		if (e.getKeyCode() == (int) 'A') {
			left = true;
		}
		if (e.getKeyCode() == (int) 'D') {
			right = true;
		}
		if (e.getKeyCode() == 32) { // spacebar
			spacebar = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == (int) 'W') {
			up = false;
		}
		if (e.getKeyCode() == (int) 'S') {
			down = false;
		}
		if (e.getKeyCode() == (int) 'A') {
			left = false;
		}
		if (e.getKeyCode() == (int) 'D') {
			right = false;
		}
		if (e.getKeyCode() == 32) { // spacebar
			spacebar = false;
		}
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
//		requires press and release
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		int button = e.getButton();
		if (button == 1) {
			leftButtonDown = true;
		} else if (button == 3) {
			rightButtonDown = true;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		int button = e.getButton();
		if (button == 1) {
			leftButtonDown = false;
		} else if (button == 3) {
			rightButtonDown = false;
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
//		enter the window
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
//		exit the window
	}
}

//d=68
//s=83
//a=65
//w=87