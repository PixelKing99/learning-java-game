package game.entities;

import util.Direction;
import game.SelectedDirections;
import saves.GameMap;

import static game.Server.DEFAULT_TPS;
import static game.Server.TILE_SIZE;

public class Player extends Entity {
	public static final int MAX_VELOCITY = TILE_SIZE / 3;
	public static final int ACCELERATION = (int) Math.round(MAX_VELOCITY / ((double) DEFAULT_TPS/2) );
	public static final int DEFAULT_ID = 0;
	public final int ID;
	
	
	
	public Player(int x, int y) {
		super(x, y, MAX_VELOCITY, ACCELERATION);
		this.ID = DEFAULT_ID;
	}
	
	public Player(int x, int y, int id) {
		super(x, y, MAX_VELOCITY, ACCELERATION);
		this.ID = id;
	}
	
	public void tick(SelectedDirections accelDirections, GameMap gameMap) {
		acceleratePlayer(accelDirections);
		updatePosition(gameMap);
	}
	
	
	
	
//	can probably simplify
	private void acceleratePlayer(SelectedDirections accelerationDirections) {
		boolean up = accelerationDirections.get(Direction.UP);
		boolean down = accelerationDirections.get(Direction.DOWN);
		boolean left = accelerationDirections.get(Direction.LEFT);
		boolean right = accelerationDirections.get(Direction.RIGHT);
		
		if (up && !down) {
			if (yVel > 0) {
				this.resetVelocity_Y();
			} else {
				this.increaseSpeed(Direction.UP);
			}
		} else if (down && !up) {
			if (yVel < 0) {
				this.resetVelocity_Y();
			} else {
				this.increaseSpeed(Direction.DOWN);
			}
		} else {
			this.resetVelocity_Y();
		}
		
		
		if (left && !right) {
			if (xVel > 0) {
				this.resetVelocity_X();
			} else {
				this.increaseSpeed(Direction.LEFT);
			}
		} else if (right && !left) {
			if (xVel < 0) {
				this.resetVelocity_X();
			} else {
				this.increaseSpeed(Direction.RIGHT);
			}
		} else {
			this.resetVelocity_X();
		}
	}
}
