package saves;


import util.MergeArray;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;

import static saves.Save.SEPARATOR_BYTE;

public class Map extends Saveable {
	public static final Tile[][] DEFAULT_MAP = new Tile[][]
	{{Tile.WALL, Tile.WALL, Tile.AIR, Tile.WALL, Tile.WALL},
	{Tile.WALL, Tile.AIR, Tile.AIR, Tile.AIR, Tile.WALL},
	{Tile.WALL, Tile.AIR, Tile.AIR, Tile.AIR, Tile.AIR},
	{Tile.WALL, Tile.LAVA, Tile.AIR, Tile.AIR, Tile.WALL},
	{Tile.WALL, Tile.WALL, Tile.WALL, Tile.WALL, Tile.WALL}};
	public static final Point DEFAULT_SPAWN = new Point(200, 200);
	
	
	private static final int MAP_DIMS_BYTES = 2 * 4; // dims = dimensions ie. width and height of the map. each int is 4 bytes and there is 2 numbers stored
	private static final int SPAWN_BYTES = 2 * 4; // x and y for player spawn point
	private static final byte TILE_OFFSET = 16; // the number the tiles start at, offset by 8 just in case i want to add other values that dont represent tiles
	
	private Tile[][] map;
	private Point spawnPoint;
	private byte[] binary;
	
	public Map(Tile[][] map) {
		this.map = map;
		this.spawnPoint = DEFAULT_SPAWN;
	}
	
	public Map(Tile[][] map, Point spawnPoint) {
		this.map = map;
		this.spawnPoint = spawnPoint;
	}
	
	
	
	public Map(byte[] binary) {
		this.binary = binary;
		map = bytesToMap(binary);
	}
	
	
	@Override
	public byte[] getBinary() {
		if (binary != null) {
			return binary;
		}
		binary = mapToBytes(map);
		return binary;
	}
	
	public Tile get(int yIndex, int xIndex) {
		return map[yIndex][xIndex];
	}
	
	public Point getSpawn() {
		return spawnPoint;
	}
	
	
	public int getLength() {
		return map.length;
	}
	
	public int getInnerLength() {
		return map[0].length;
	}
	
	
	private byte[] mapToBytes(Tile[][] map) {
		
		MergeArray<byte[]> data = new MergeArray<>(byte[]::new);

//		stores an integer for the tile width of the map as 4 bytes
		ByteBuffer intBuffer = ByteBuffer.allocate(MAP_DIMS_BYTES + SPAWN_BYTES);
		intBuffer.putInt(map[0].length); // big endian ig
		intBuffer.putInt(map.length);
		
		intBuffer.putInt(spawnPoint.x);
		intBuffer.putInt(spawnPoint.y);
		
		byte[] intSlices = intBuffer.array();
		data.addArray(intSlices);

//		stores each tile from the map as a byte
		for (Tile[] yLevel : map) {
			for (Tile tile : yLevel) {

// the stored number for each tile is gonna start at 16 in case i need to add like characters for like new lines etc then they can be below 16, kinda dumb but idrc
				data.addArray(new byte[]{(byte) (tile.ordinal() + TILE_OFFSET)});
			}

// 			doesnt actually need this since the width is defined at the beginning but its just like a backup ig
			data.addArray(new byte[]{SEPARATOR_BYTE});
		}
		
		return data.getMergedArray();
	}
	
	private Tile[][] bytesToMap(byte[] data) {
		Tile[] tiles = Tile.values();
		
		byte[] intSlices = Arrays.copyOfRange(data, 0, MAP_DIMS_BYTES + SPAWN_BYTES);

// 		i do not understand the specifics of how this formats the data but i do know its big endian
		ByteBuffer intBuffer = ByteBuffer.wrap(intSlices);
		int mapWidth = intBuffer.getInt();
		int mapHeight = intBuffer.getInt();
		
		spawnPoint = new Point();
		spawnPoint.x = intBuffer.getInt();
		spawnPoint.y = intBuffer.getInt();
		
//		check to make sure all of the SEPARATOR_BYTE chars are at the correct spot to prevent reading fked save files and make sure code works
//		should probably make the for loop more readable
		for (int i = MAP_DIMS_BYTES + SPAWN_BYTES + mapWidth; i < data.length; i += mapWidth + 1) {
			if (data[i] != SEPARATOR_BYTE) {
//				was a data format exception but im trying to use the constructor in a Consumer and it was not happy im finna kms java is so annoying
//				theres probably a way to make a consumer that is able to throw an error but idk
				throw new RuntimeException("expected separator byte '" + SEPARATOR_BYTE + "'\ngot '" + data[i] + "'\nat index '" + i + "'");
			}
		}
		
		Tile[][] map = new Tile[mapHeight][mapWidth];
		
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				
				int dataIndex = (mapWidth + 1) * i + j + MAP_DIMS_BYTES + SPAWN_BYTES;
				
				int tileNumber = data[dataIndex] - TILE_OFFSET;
				
				map[i][j] = tiles[tileNumber];
			}
		}
		
		return map;
	}
	
	
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		
		for (Tile[] level : map) {
			stringBuilder.append("\n"); // adds a newline before the first line (out of place) but idrk this is just for development
			
			for (Tile tile : level) {
				stringBuilder.append(tile).append(" ");
			}
		}
		
		return stringBuilder.toString();
	}
	
}
