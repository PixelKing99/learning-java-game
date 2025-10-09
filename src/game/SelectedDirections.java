package game;

import java.util.HashMap;

public class SelectedDirections {
	
	
	HashMap<Direction, Boolean> directions = new HashMap<>();
	public SelectedDirections() {
		directions.put(Direction.UP, false);
		directions.put(Direction.DOWN, false);
		directions.put(Direction.LEFT, false);
		directions.put(Direction.RIGHT, false);
	}
	public void setTrue(Direction direction) {
		directions.put(direction, true);
	}
	public void setFalse(Direction direction) {
		directions.put(direction, false);
	}
	public void setTrue(int horizontal, int vertical) {
		directions.put(intsToDirection(horizontal, vertical), true);
	}
	
	private static Direction intsToDirection(int horizontal, int vertical) {
		
		if (Math.abs(horizontal) + Math.abs(vertical) != 1) {
			throw new IllegalArgumentException("one arguement should be 0 and the other +/- 1");
		}
		
		switch (vertical) {
			case 1 -> {
				return Direction.UP;
			}
			case -1 -> {
				return Direction.DOWN;
			}
		}
		switch (horizontal) {
			case 1 -> {
				return Direction.RIGHT;
			}
			// default just to make the ide happy
			default -> {
				return Direction.LEFT;
			}
		}
	}
	
	public boolean get(Direction direction) {
		return directions.get(direction);
	}
	public boolean get(int horizontal, int vertical) {
		return directions.get(intsToDirection(horizontal, vertical));
	}
}