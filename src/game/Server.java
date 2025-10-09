package game;

import game.entities.Player;
import game.input.InputState;
import game.input.UserInput;
import saves.Map;
import saves.Saves;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;


public class Server implements Runnable {


//	just copying minecraft ig with 20 ticks per second
	public static final int DEFAULT_TPS = 20;
	public static final int TILE_SIZE = 100; // used for calculations inside the game, not corresponding to a number of pixels
	


//	make saves use a folder
//	need to add ability to select tiles and then some way to select how to change it
//	add enemies
//	function to lava
	
	private Saves saveFile;
	private Map gameMap;
	
	private Player player;
	private UserInput userInput;
	
	
	
	
	public Server() throws DataFormatException, IOException {
		
		saveFile = loadSave();
		gameMap = saveFile.getMap();
	
		player = new Player(100, 100);
		
	}
	
	public void initializeUserInput(UserInput userInput) {
		this.userInput = userInput;
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
	
	
	
	
	public void tick() {

//		the main tick logic
		InputState inputState = userInput.getInputState();
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
	
	
	@Override
	public void run() {
		tick();
	}
}