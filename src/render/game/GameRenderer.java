package render.game;

import game.Server;
import game.entities.EntityType;
import game.entities.Player;
import game.input.InputState;
import game.input.UserInput;
import render.*;
import render.Panel;
import saves.Map;
import saves.Tile;
import util.DynamicString;

import java.awt.*;
import java.util.HashMap;
import java.util.function.Supplier;

public class GameRenderer implements Renderer<GameFrame> {
	
	private HashMap<Tile, Image> tileTextures;
	private HashMap<EntityType, Image> entityTextures;
	private Player player = Server.getPlayer(Player.DEFAULT_ID);
	private Map map = Server.getMap();
	
	
	private Runnable goToMenu = () -> {
		Render.setScreenToRender(Screen.MAIN_MENU);
		Server.stop();
		Hud.remove(3);
		Hud.remove(4);
	};
	
	public GameRenderer(HashMap<Tile, Image> tileTextures, HashMap<EntityType, Image> entityTextures) {
		
		this.tileTextures = tileTextures;
		this.entityTextures = entityTextures;
		
		
		Hud.add(3, new DynamicString("x: ").add(() -> {return player.getCoords().x + "";}).add("  y: ").add(() -> {return player.getCoords().y + "";}));
		Hud.add(4, new DynamicString("Δx: ").add(() -> {return player.getVelocity().x + "";}).add("  Δy: ").add(() -> {return player.getVelocity().y + "";}));
	}
	
	public void render(Graphics g, int width, int height) {
		InputState inputState = UserInput.getInputState();
		
		GameFrame frame =  new GameFrame(g, width, height, inputState, player, map, tileTextures, entityTextures);
		
		DebugFrame debug = new DebugFrame(g, width, height, inputState,player , frame);
		
		
		frame.drawMap();
		
		
		frame.drawSelectedTile(); // placed here so it renders under the player
		
		
		frame.drawPlayer();
		
		
		frame.drawSideBars();
		
		
		frame.drawCursor();
		
		
		frame.drawHUD();
		
		
		debug.showCollisionDirections();
//		debug.drawCenteringLines();
//		debug.drawSidebarEdges();
		
//		this stuff prolly needs to be somewhere else in like a client class or smthn cause its not really related to rendering
		if (inputState.escapeKey) {
			goToMenu.run();
		}
		
	}
	
	@Override
	public void updateVisibility(boolean isVisible) {
//		dont really need anything here cause it doesnt have mouseListeners or anything
	}
}
