package saves;

import game.entities.Player;

public class PlayerData extends Saveable {
	
	PlayerData(Player[] players) {
	
	}
	
	PlayerData(byte[] bytes) {
	}
	
	@Override
	public byte[] getBinary() {
		return new byte[0];
	}
}
