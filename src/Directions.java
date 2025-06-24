import java.util.HashMap;

public class Directions {
	HashMap<Character, Boolean> directions = new HashMap<>();
	Directions() {
		directions.put('U', false);
		directions.put('D', false);
		directions.put('L', false);
		directions.put('R', false);
	}
	public void set(char direction, boolean value) {
		directions.put(direction, value);
	}
	public boolean get(char direction) {
		return directions.get(direction);
	}
}