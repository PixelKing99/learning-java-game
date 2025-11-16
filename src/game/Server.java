package game;

import game.entities.Player;
import game.input.InputState;
import game.input.UserInput;
import render.Frame;
import render.Hud;
import render.Render;
import render.Screen;
import saves.Map;
import saves.PlayerData;
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
	
	private PlayerData playerData;
	private ConcurrentRateLoop<Server> serverLoop;
	
	private static Server server;
	
	
	
	private Server(String saveName) throws DataFormatException, IOException {
		saveFile = loadSave(saveName);
		
		gameMap = saveFile.get(Map.class);
		
		playerData = saveFile.get(PlayerData.class);
		
		
		serverLoop = new ConcurrentRateLoop<>(Server.DEFAULT_TPS, this, "serverThread");
		
		Hud.add(1, new DynamicString("tps: ").add(serverLoop::getDebugData));
	}
	
	
	public static void start(String saveName) throws DataFormatException, IOException {
		if (server != null) {
			throw new IllegalStateException("server has already been started");
		}
		server = new Server(saveName);
	}
	
	public static void stop() {
		if (server == null) {
			throw new IllegalStateException("the server is not running");
		}
		Hud.remove(1); // removes the tps display from the hud
		server.serverLoop.stopLoop();
		server = null;
	}
	
	
	public static boolean isRunning() {
		return server != null;
	}
	
	
	public static void pause() {
		server.serverLoop.pause();
	}
	
	public static void resume() {
		server.serverLoop.resume();
	}
	
	
	
	private Save loadSave(String saveName) throws DataFormatException, IOException {
		Save saveFile;
		try {
			saveFile = new Save(saveName);
			
		} catch (FileNotFoundException fnfe) {
			
			saveFile = new Save(saveName, new Map(Map.DEFAULT_MAP));
		}
		
		return saveFile;
	}
	
	
	
//	cant actually be run outside Server since the constructor is private and this isnt static
	@Override
	public void run() {
		tick();
	}
	
	private void tick() {
//		the main tick logic

		InputState inputState = UserInput.getInputState();
		
//		the game isnt anywhere near multiplayer but ill just make it like this ig
		for (int i = 0; i < playerData.getPlayerCount(); i++) {
			playerData.getIndex(i).tick(inputState.accelerating, gameMap);
		}
		
	}
	
	
	
	
	public static int getIndex(int coord) {
		int index = coord / Server.TILE_SIZE;
		if (coord < 0 && coord != index * Server.TILE_SIZE) {
			index -= 1;
		}
		return index;
	}
	
	
	
	
	public static Map getMap() {
		return server.gameMap;
	}
	
	
//	this should only really be used called once by the client but i havent actually made a client separate from the server so idrc if i call it multiple times
	public static Player getPlayer(int id) {
		Player player = null;
		
		for (int i = 0; i < server.playerData.getPlayerCount(); i++) {
			if (server.playerData.getIndex(i).ID == id) {
				player = server.playerData.getIndex(i);
				break;
			}
		}
		
		if (player == null) {
			player = new Player(server.gameMap.getSpawn().x, server.gameMap.getSpawn().y, Player.DEFAULT_ID);
		}
		return player;
	}
	
}