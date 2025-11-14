package saves;


import util.MergeArray;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;

public class Map extends Saveable {
	public static final Tile[][] DEFAULT_MAP = new Tile[][]
	{{Tile.WALL, Tile.WALL, Tile.AIR, Tile.WALL, Tile.WALL},
	{Tile.WALL, Tile.AIR, Tile.AIR, Tile.AIR, Tile.WALL},
	{Tile.WALL, Tile.AIR, Tile.AIR, Tile.AIR, Tile.AIR},
	{Tile.WALL, Tile.LAVA, Tile.AIR, Tile.AIR, Tile.WALL},
	{Tile.WALL, Tile.WALL, Tile.WALL, Tile.WALL, Tile.WALL}};
	
	
	private static final int MAP_DIMS_BYTES = 2 * 4; // dims = dimensions ie. width and height of the map. each int is 4 bytes and there is 2 numbers stored
	private static final byte NEXT_LAYER = 0; // represents the next array in the list of arrays that make up the map
	private static final byte TILE_OFFSET = 16; // the number the tiles start at, offset by 8 just in case i want to add other values that dont represent tiles
	
	private Tile[][] map;
	private byte[] binary;
	
	public Map(Tile[][] map) {
		this.map = map;
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
	
	public int getLength() {
		return map.length;
	}
	
	public int getInnerLength() {
		return map[0].length;
	}
	
	
	private byte[] mapToBytes(Tile[][] map) {
		
		MergeArray<byte[]> data = new MergeArray<>(byte[]::new);

//		stores an integer for the tile width of the map as 4 bytes
		ByteBuffer intBuffer = ByteBuffer.allocate(MAP_DIMS_BYTES);
		intBuffer.putInt(map[0].length); // big endian ig
		intBuffer.putInt(map.length);
		
		byte[] intSlices = intBuffer.array();
		data.addArray(intSlices);

//		stores each tile from the map as a byte
		for (Tile[] yLevel : map) {
			for (Tile tile : yLevel) {

// the stored number for each tile is gonna start at 16 in case i need to add like characters for like new lines etc then they can be below 16, kinda dumb but idrc
				data.addArray(new byte[]{(byte) (tile.ordinal() + TILE_OFFSET)});
			}

// 			doesnt actually need this since the width is defined at the beginning but its just like a backup ig
			data.addArray(new byte[]{NEXT_LAYER});
		}
		
		return data.getMergedArray();
	}
	
	private Tile[][] bytesToMap(byte[] data) {
		Tile[] tiles = Tile.values();
		
		byte[] intSlices = Arrays.copyOfRange(data, 0, MAP_DIMS_BYTES);

// 		i do not understand the specifics of how this formats the data but i do know its big endian
		ByteBuffer intBuffer = ByteBuffer.wrap(intSlices);
		int mapWidth = intBuffer.getInt();
		int mapHeight = intBuffer.getInt();
		
//		check to make sure all of the NEXT_LAYER chars are at the correct spot to prevent reading fked save files and make sure code works
//		should probably make the for loop more readable
		for (int i = MAP_DIMS_BYTES + mapWidth; i < data.length; i += mapWidth + 1) {
			if (data[i] != NEXT_LAYER) {
//				was a data format exception but im trying to use the constructor in a Consumer and it was not happy im finna kms java is so annoying
//				theres probably a way to make a consumer that is able to throw an error but idk
				throw new RuntimeException("expected '" + NEXT_LAYER + "'\ngot '" + data[i] + "'\nat index '" + i + "'");
			}
		}
		
		Tile[][] map = new Tile[mapHeight][mapWidth];
		
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				
				int dataIndex = (mapWidth + 1) * i + j + MAP_DIMS_BYTES;
				
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
