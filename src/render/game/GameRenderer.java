package render.game;

import game.Server;
import game.entities.EntityType;
import game.input.InputState;
import game.input.UserInput;
import render.*;
import render.Panel;
import saves.Tile;

import java.awt.*;
import java.util.HashMap;
import java.util.function.Supplier;

public class GameRenderer implements Renderer<GameFrame> {
	
	private Server server;
	private HashMap<Tile, Image> tileTextures;
	private HashMap<EntityType, Image> entityTextures;
	
	private Runnable goToMenu = Render.getScreenSetter(Screen.MAIN_MENU, false);
	
	public GameRenderer(Server server, HashMap<Tile, Image> tileTextures, HashMap<EntityType, Image> entityTextures) {
		
		this.server = server;
		this.tileTextures = tileTextures;
		this.entityTextures = entityTextures;
		
	}
	
	public void render(Graphics g, int width, int height) {
		InputState inputState = UserInput.getInputState();
		
		GameFrame frame =  new GameFrame(g, width, height, inputState, server, tileTextures, entityTextures);
		
		DebugFrame debug = new DebugFrame(g, width, height, inputState, server, frame);
		
		
		frame.drawMap();
		
		
		frame.drawSelectedTile(); // placed here so it renders under the player
		
		
		frame.drawPlayer();
		
		
		frame.drawSideBars();
		
		
		frame.drawCursor();
		
		
		frame.drawHUD();
		
		
		debug.showCollisionDirections();
//		debug.drawCenteringLines();
//		debug.drawSidebarEdges();
		
		
		if (inputState.escapeKey) {
			goToMenu.run();
		}
		
	}
	
	@Override
	public void updateVisibility(boolean isVisible) {
//		dont really need anything here cause it doesnt have mouseListeners or anything
	}
}
