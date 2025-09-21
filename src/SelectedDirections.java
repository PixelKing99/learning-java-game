import java.util.HashMap;

public class SelectedDirections {
	
	
	HashMap<Directions, Boolean> directions = new HashMap<>();
	SelectedDirections() {
		directions.put(Directions.UP, false);
		directions.put(Directions.DOWN, false);
		directions.put(Directions.LEFT, false);
		directions.put(Directions.RIGHT, false);
	}
	public void setTrue(Directions direction) {
		directions.put(direction, true);
	}
	public void setTrue(int horizontal, int vertical) {
		if (Math.abs(horizontal) + Math.abs(vertical) != 1) {
			throw new IllegalArgumentException("one arguement should be 0 and the other +/- 1");
		}
		
		Directions curDirection = null;
		
		switch (vertical) {
			case 1 -> {
				curDirection = Directions.UP;
			}
			case -1 -> {
				curDirection = Directions.DOWN;
			}
		}
		switch (horizontal) {
			case 1 -> {
				curDirection = Directions.RIGHT;
			}
			case -1 -> {
				curDirection = Directions.LEFT;
			}
		}
		
		directions.put(curDirection, true);
	}
	public boolean get(Directions direction) {
		return directions.get(direction);
	}
}