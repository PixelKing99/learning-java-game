package saves;

import game.entities.Player;
import util.MergeArray;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;

import static saves.Save.SEPARATOR_BYTE;

public class PlayerData extends Saveable {
	
	private static final int PLAYER_DATA_BYTES = 4 * 3; // id, x, y
	private byte[] bytes;
	private Player[] players;
	
	
	public PlayerData(Player[] players) {
		this.players = players;
	}
	
	public PlayerData(byte[] bytes) throws DataFormatException {
		this.bytes = bytes;
		this.players = bytesToPlayers(bytes);
	}
	
	@Override
	public byte[] getBinary() {
		if (bytes != null) {
			return bytes;
		}
		bytes = playersToBytes(players);
		return bytes;
	}
	
	
	private byte[] playersToBytes(Player[] players) {
		
		
		MergeArray<byte[]> data = new MergeArray<>(byte[]::new);
		
		data.addArray(Save.intToBytes(players.length));
//		stores each tile from the map as a byte
		for (Player player : players) {
			
			ByteBuffer buffer = ByteBuffer.allocate(PLAYER_DATA_BYTES);
			buffer.putInt(player.ID);
			buffer.putInt(player.getCoords().x);
			buffer.putInt(player.getCoords().y);
			data.addArray(buffer.array());
			
			data.addArray(new byte[]{SEPARATOR_BYTE});
		}
		
		return data.getMergedArray();
	}
	
	private Player[] bytesToPlayers(byte[] bytes) throws DataFormatException {
		
		byte[] intSlices = Arrays.copyOfRange(bytes, 0, 4);

		ByteBuffer intBuffer = ByteBuffer.wrap(intSlices);
		int playerCount = intBuffer.getInt();
		Player[] players = new Player[playerCount];

//		check to make sure all of the SEPARATOR_BYTE chars are at the correct spot to prevent reading fked save files and make sure code works
//		should probably make the for loop more readable
		for (int i = 4 + PLAYER_DATA_BYTES; i < bytes.length; i += PLAYER_DATA_BYTES + 1) {
			if (bytes[i] != SEPARATOR_BYTE) {
				throw new DataFormatException("expected '" + SEPARATOR_BYTE + "'\ngot '" + bytes[i] + "'\nat index '" + i + "'");
			}
		}
		
		
		
		for (int i = 0; i < playerCount; i++) {
			
			int currentPlayerDataIndex = i * PLAYER_DATA_BYTES + i + 4; // +i is to account for SEPARATOR_BYTEs
			
			intSlices = Arrays.copyOfRange(bytes, currentPlayerDataIndex, currentPlayerDataIndex + PLAYER_DATA_BYTES);
			
			intBuffer = ByteBuffer.wrap(intSlices);
			int id = intBuffer.getInt();
			int x = intBuffer.getInt();
			int y = intBuffer.getInt();
			
			players[i] = new Player(x, y, id);
		}
		
		return players;
	}
	
	public int getPlayerCount() {
		return players.length;
	}
	
	public Player getIndex(int i) {
		return players[i];
	}
}
