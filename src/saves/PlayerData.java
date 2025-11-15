package saves;

import game.entities.Player;
import util.MergeArray;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static saves.Save.SEPARATOR_BYTE;

public class PlayerData extends Saveable {
	
	private static final int PLAYER_DATA_BYTES = 4 * 3; // id, x, y
	private byte[] bytes;
	private Player[] players;
	
	
	PlayerData(Player[] players) {
		this.players = players;
	}
	
	PlayerData(byte[] bytes) {
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
	
	private Player[] bytesToPlayers(byte[] bytes) {
		
		byte[] intSlices = Arrays.copyOfRange(bytes, 0, 4);

		ByteBuffer intBuffer = ByteBuffer.wrap(intSlices);
		int playerCount = intBuffer.getInt();
		Player[] players = new Player[playerCount];

//		check to make sure all of the NEXT_LAYER chars are at the correct spot to prevent reading fked save files and make sure code works
//		should probably make the for loop more readable
		for (int i = 4 + PLAYER_DATA_BYTES; i < bytes.length; i += PLAYER_DATA_BYTES + 1) {
			if (bytes[i] != SEPARATOR_BYTE) {
//				was a data format exception but im trying to use the constructor in a Consumer and it was not happy im finna kms java is so annoying
//				theres probably a way to make a consumer that is able to throw an error but idk
				throw new RuntimeException("expected '" + SEPARATOR_BYTE + "'\ngot '" + bytes[i] + "'\nat index '" + i + "'");
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
