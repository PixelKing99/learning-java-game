package game;

import game.entities.Player;
import game.input.InputState;
import game.input.UserInput;
import render.Frame;
import render.Hud;
import render.Render;
import render.Screen;
import saves.Map;
import saves.Save;
import util.ConcurrentRateLoop;
import util.DynamicString;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;


public class Server implements Runnable {


//	just copying minecraft ig with 20 ticks per second
	public static final int DEFAULT_TPS = 20;
	public static final int TILE_SIZE = 100; // used for calculations inside the game, not corresponding to a number of pixels


//	make main menu
//	need to add ability to select tiles and then some way to select how to change it
//	add enemies
//	function to lava/add health
//	make saves use a folder - maybe
	
	private Save saveFile;
	private Map gameMap;
	
	private Player player;
	private ConcurrentRateLoop<Server> serverLoop;
	
	
	
	public Server() throws DataFormatException, IOException {
		saveFile = loadSave();
		gameMap = saveFile.get(Map.class);
	
		player = new Player(100, 100);
		Hud.add(3, new DynamicString("x: ").add(() -> {return player.getCoords().x + "";}).add("  y: ").add(() -> {return player.getCoords().y + "";}));
		Hud.add(4, new DynamicString("Δx: ").add(() -> {return player.getVelocity().x + "";}).add("  Δy: ").add(() -> {return player.getVelocity().y + "";}));
		
		
		serverLoop = new ConcurrentRateLoop<>(Server.DEFAULT_TPS, this, "serverThread");
		
		Hud.add(1, new DynamicString("tps: ").add(serverLoop::getDebugData));
	}
	
	
	
	public void pause() {
		serverLoop.pause();
	}
	
	public void resume() {
		serverLoop.resume();
	}
	
	
	
	
	public Save loadSave() throws DataFormatException, IOException {
		return loadSave("default");
	}
	public Save loadSave(String saveName) throws DataFormatException, IOException {
		Save saveFile;
		try {
			saveFile = new Save(saveName);
			
		} catch (FileNotFoundException fnfe) {
			
			saveFile = new Save(saveName, new Map(Map.DEFAULT_MAP));
		}
		
		return saveFile;
	}
	
	
	
	
	@Override
	public void run() {
		tick();
	}
	
	public void tick() {

//		the main tick logic
		InputState inputState = UserInput.getInputState();
		player.tick(inputState.accelerating, gameMap);
		
	}
	
	
	
	
	public static int getIndex(int coord) {
		int index = coord / Server.TILE_SIZE;
		if (coord < 0 && coord != index * Server.TILE_SIZE) {
			index -= 1;
		}
		return index;
	}
	
	
	
	
	public Map getMap() {
		return gameMap;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}