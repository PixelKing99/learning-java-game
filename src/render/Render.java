package render;

import game.*;
import game.input.UserInput;
import game.entities.EntityType;
import saves.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;


public class Render {
	private final Image lava = new ImageIcon(getClass().getResource("../resources/lava.png")).getImage();
	private final Image pog = new ImageIcon(getClass().getResource("../resources/pog.png")).getImage();
	private final Image skull = new ImageIcon(getClass().getResource("../resources/skull.png")).getImage();
	private final Image grass = new ImageIcon(getClass().getResource("../resources/grass.png")).getImage();
	private final Image zombie = new ImageIcon(getClass().getResource("../resources/zombie.png")).getImage();
	private final Image face = new ImageIcon(getClass().getResource("../resources/face.png")).getImage();
	
	
	
	private HashMap<Tile, Image> tileTextures = new HashMap<>();
	private HashMap<EntityType, Image> entityTextures = new HashMap<>();
	public static final int DEFAULT_FPS = 60;
	private Server server;
	
	private UserInput userInput;
	
	private Panel panel;
	
	
	
	public Render(Server server, UserInput userInput, Panel panel) {
		this.server = server;
		this.userInput = userInput;
		this.panel = panel;
		
		tileTextures.put(Tile.WALL, pog);
		tileTextures.put(Tile.LAVA, lava);
		
		entityTextures.put(EntityType.PLAYER, face);
		entityTextures.put(EntityType.ZOMBIE, face);

	}
	
	
	
	public void render(Graphics g) {
		Frame frame = new Frame(g, userInput.getInputState(), server.getMap(), tileTextures, entityTextures, server.getPlayer(), panel.getWidth(), panel.getHeight(), panel.getFps.get(), panel.getTps.get());
		DebugRenderer debug = new DebugRenderer(g, server, userInput, panel, frame);
		
		
		
		frame.drawMap();
		
		
		frame.drawSelectedTile(); // placed here so it renders under the player
		
		
		frame.drawPlayer();
		
		
//		frame.drawSideBars();
		
		
		frame.drawCursor();
		
		
		frame.drawHUD();
		
		
		debug.showCollisionDirections();
		debug.drawCenteringLines();
		debug.drawSidebarEdges();
	}
	
}
