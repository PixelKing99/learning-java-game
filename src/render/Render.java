package render;

import game.*;
import game.input.UserInput;
import game.entities.EntityType;
import render.game.GameFrame;
import render.game.GameRenderer;
import render.gui.*;
import saves.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

//	how should i do rendering stuff
//		could have every potential distinct screen have its own renderer class (although ig all menus share one)
//			eg. to render game with inventory the game renderer would run and then the inventory one
//				idk not really good but otherwise they are both in the same class which could get cluttered





public class Render {

//	should probably make a separate class for all the image loading stuff so its easier to give a class access to an image
	private final Image lava = new ImageIcon(getClass().getResource("/resources/lava.png")).getImage();
	private final Image pog = new ImageIcon(getClass().getResource("/resources/pog.png")).getImage();
	private final Image skull = new ImageIcon(getClass().getResource("/resources/skull.png")).getImage();
	private final Image grass = new ImageIcon(getClass().getResource("/resources/grass.png")).getImage();
	private final Image zombie = new ImageIcon(getClass().getResource("/resources/zombie.png")).getImage();
	private final Image face = new ImageIcon(getClass().getResource("/resources/face.png")).getImage();
	private final Image logo = new ImageIcon(getClass().getResource("/resources/logo.png")).getImage();
	
	
	private HashMap<Tile, Image> tileTextures = new HashMap<>();
	private HashMap<EntityType, Image> entityTextures = new HashMap<>();
	public static final int DEFAULT_FPS = 60;
	private static Server server;
	
	
	private static Screen currentScreen = Screen.MAIN_MENU;
	private HashMap<Screen, Renderer<? extends Frame>> screenToRenderer = new HashMap<>();
	
	
	private Renderer<MenuFrame> mainMenuRenderer;
	private Renderer<GameFrame> gameRenderer;
	
	
	
	public Render(Server server) {
		Render.server = server;
		
		tileTextures.put(Tile.WALL, pog);
		tileTextures.put(Tile.LAVA, lava);
		
		entityTextures.put(EntityType.PLAYER, face);
		entityTextures.put(EntityType.ZOMBIE, face);


		mainMenuRenderer = new MenuRenderer(MenuBuilder.getMainMenu(logo), MenuBuilder.getMainMenuBackgound(lava));
		
		gameRenderer = new GameRenderer(server, tileTextures, entityTextures);
		
		
		screenToRenderer.put(Screen.MAIN_MENU, mainMenuRenderer);
		screenToRenderer.put(Screen.GAME, gameRenderer);
	}
	
	
	
	public void render(Graphics g, int width, int height) {
		screenToRenderer.get(currentScreen).render(g, width, height);
	}
	
	
	public static Runnable getScreenSetter(Screen screen) {
		return () -> {
			if (screen.equals(Render.currentScreen)) {
				throw new IllegalArgumentException("must change screen\ngiven: " + screen + "\ncurrent: " + Render.currentScreen);
			}
			Render.currentScreen = screen;
			
			
			if (screen.equals(Screen.GAME)) {
				server.resume();
			} else {
				server.pause();
			}
		};
	}
}
