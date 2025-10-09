package game.entities;

import game.Direction;
import game.SelectedDirections;
import game.Server;
import saves.Map;
import saves.Tile;

import java.awt.*;

import static game.Server.getIndex;

public abstract class Entity {
	
	private static class CollisionTiles {
		private boolean[][] collisions = {{false, false}, {false, false}};
		private int[][][] collisionIndexes = new int[2][2][2];
		
		public void setTrue(int i, int j, int xIndex, int yIndex) {
			collisions[i][j] = true;
			collisionIndexes[i][j][0] = xIndex;
			collisionIndexes[i][j][1] = yIndex;
		}
		
		public boolean getBool(int i, int j) {
			return collisions[i][j];
		}
		
		public Point getIndexes(int i, int j) {
			return new Point(collisionIndexes[i][j][0], collisionIndexes[i][j][1]);
		}
	}
	
	
	private int x;
	private int y;
	
	private int newX;
	private int newY;
	
	
	protected int xVel = 0;
	protected int yVel = 0;
	
	private int xIndex;
	private int yIndex;
	private int newXIndex;
	private int newYIndex;
	
	private int maxSpeed;
	private int accel;
	private SelectedDirections collisionDirections;
	
	
	Entity(int x, int y, int maxSpeed, int accel) {
		this.x = x;
		this.y = y;
		this.maxSpeed = maxSpeed;
		this.accel = accel;
	}
	
	protected void updatePosition(Map map) {

		xIndex = getIndex(x);
		yIndex = getIndex(y);
		
		newX = x + xVel;
		newY = y + yVel;
		
		newXIndex = getIndex(newX);
		newYIndex = getIndex(newY);
		
		collisionDirections = new SelectedDirections();
		CollisionTiles collisionTiles = findCollisionTiles(map);
		findCollisionDirections(collisionTiles);
		
		
		x = newX;
		y = newY;
		xIndex = newXIndex;
		yIndex = newYIndex;
		
		collide();
		
	}
	
	
	
	private CollisionTiles findCollisionTiles(Map map) {
		
		
		boolean overlapRight = newX > newXIndex * Server.TILE_SIZE;
		boolean overlapDown = newY > newYIndex * Server.TILE_SIZE;
		
		CollisionTiles collisions = new CollisionTiles();
		
		
		updateCollisions(map, collisions, 0, 0);
		
		if (overlapRight) {
			updateCollisions(map, collisions, 0, 1);
		}
		if (overlapDown) {
			updateCollisions(map, collisions, 1, 0);
		}
		if (overlapRight && overlapDown) {
			updateCollisions(map, collisions, 1, 1);
		}
		return collisions;
	}
	
	
	
	
	private void updateCollisions(Map map, CollisionTiles collisions, int yIndexOffset, int xIndexOffset) {
		
		if (checkTile(map, newXIndex + xIndexOffset, newYIndex + yIndexOffset)) {
			collisions.setTrue(yIndexOffset, xIndexOffset, newXIndex + xIndexOffset, newYIndex + yIndexOffset);
		}
	}
	
	
	
	
	private boolean checkTile(Map map, int xIndex, int yIndex) {
		
		boolean yValid = yIndex < map.getLength() && yIndex >= 0;
		boolean validIndex = yValid && xIndex < map.getInnerLength() && xIndex >= 0;
		
		return validIndex && map.get(yIndex, xIndex) == Tile.WALL;
	}
	
	
	
	
	private int switch1And0(int oneOrZero) {
		switch (oneOrZero) {
			case 1 -> {
				return 0;
			}
			case 0 -> {
				return 1;
			}
		}
		throw new IllegalArgumentException("only accepts 1 or 0");
	}
	
	
	
	
//	direction represented as +/-1, relative to the players position
//	does this:
//	0,0 | 1,0		+ + | - +
//	----+----  -->	----+----
//	0,1 | 1,1		+ - | - -
//	idk, this explanation is not good, its just works ok -_-
	
	private int relativeIndexToDirection(int indexOffset) {
		switch (indexOffset) {
			case 1 -> {
				return -1;
			}
			case 0 -> {
				return 1;
			}
		}
		throw new IllegalArgumentException("only accepts 1 or 0");
		
	}
	
	private void collisionTileToDirection(CollisionTiles collisions, int relativeYIndex, int relativeXIndex) {
		
		if (collisions.getBool(relativeYIndex, relativeXIndex)) {
			
			int xDirection = relativeIndexToDirection(relativeXIndex);
			int yDirection = relativeIndexToDirection(relativeYIndex);
			
			Point indexes = collisions.getIndexes(relativeYIndex, relativeXIndex);

//			the direction value adds + or -1 to the index which essentially moves the boundary one tile towards the player
//			this is because we are treating the player as a single point and not going based off of its nearest corner, ngl maybe i should do it the other way but it works so like...
			int xBoundary = (indexes.x + xDirection) * Server.TILE_SIZE;
			int yBoundary = (indexes.y + yDirection) * Server.TILE_SIZE;
			
			
			boolean wasTouchingXBoundary = x == xBoundary;
			boolean wasTouchingYBoundary = y == yBoundary;
			
			boolean isPastXBoundary = newX < xBoundary;
			boolean isPastYBoundary = newY < yBoundary;
			
			boolean wasPastXBoundary = x < xBoundary;
			boolean wasPastYBoundary = y < yBoundary;
			
//			the relativeIndexes should only be 1 or 0 and the values for if they are 0 is what they are initially defined as
			if (relativeYIndex == 1) {
				isPastYBoundary = newY > yBoundary;
				wasPastYBoundary = y > yBoundary;
			}
			
			if (relativeXIndex == 1) {
				isPastXBoundary = newX > xBoundary;
				wasPastXBoundary = x > xBoundary;
			}
			
			
			if (isPastXBoundary && (!wasPastXBoundary || wasTouchingXBoundary) && wasPastYBoundary) {
				// have to invert the horizontal sign because the screen's coordinates increase in the directions v> but it makes the most sense to set game.Direction.UP and RIGHT be positive (ie. ^>) inside the setTrue method so the vertical already is inverted, but because of the way the checks are set up and stuff we need both to be inverted
				collisionDirections.setTrue(-xDirection, 0);
				
			} else if (isPastYBoundary && (!wasPastYBoundary || wasTouchingYBoundary) && wasPastXBoundary) {
				collisionDirections.setTrue(0, yDirection);

//			this is the case where it is colliding with a tile with no neighbors and it collides from both directions so it could be interpreted as either direction
//			the 'switch1And0' is to check to see if we are colliding with either of the tiles beside the one being checked
			} else if (!collisions.getBool(switch1And0(relativeYIndex), relativeXIndex) && !collisions.getBool(relativeYIndex, switch1And0(relativeXIndex))) {
				collisionDirections.setTrue(0, yDirection); // -xDirection would also work here, its arbitrary
			}
		}
		
	}
	
	
	private void findCollisionDirections(CollisionTiles collisions) {
		
		collisionTileToDirection(collisions, 0, 0);
		collisionTileToDirection(collisions, 0, 1);
		collisionTileToDirection(collisions, 1, 0);
		collisionTileToDirection(collisions, 1, 1);
	}
	
	public void collide() {
		if (collisionDirections.get(Direction.UP)) {
			y = (yIndex + 1) * Server.TILE_SIZE;
			yVel = 0;
		}
		if (collisionDirections.get(Direction.DOWN)) {
			y = yIndex * Server.TILE_SIZE;
			yVel = 0;
		}
		if (collisionDirections.get(Direction.LEFT)) {
			x = (xIndex + 1) * Server.TILE_SIZE;
			xVel = 0;
		}
		if (collisionDirections.get(Direction.RIGHT)) {
			x = xIndex * Server.TILE_SIZE;
			xVel = 0;
		}
	}
	
	public Point getCoords() {
		return new Point(x, y);
	}
	
	public Point getVelocity() {
		return new Point(xVel, yVel);
	}
	
	public Point getIndexes() {
		return new Point(xIndex, yIndex);
	}
	
	public void increaseSpeed(Direction direction) {
		switch (direction) {
			case UP -> {
				yVel -= accel;
				if (yVel < -maxSpeed) {
					yVel = -maxSpeed;
				}
			}
			case DOWN -> {
				yVel += accel;
				if (yVel > maxSpeed) {
					yVel = maxSpeed;
				}
			}
			case LEFT -> {
				xVel -= accel;
				if (xVel < -maxSpeed) {
					xVel = -maxSpeed;
				}
			}
			case RIGHT -> {
				xVel += accel;
				if (xVel > maxSpeed) {
					xVel = maxSpeed;
				}
			}
			default -> {
				throw new IllegalArgumentException("invalid direction: " + direction);
			}
		}
	}
	
	
//	reset functions are named weirdly cause i created an annoying bug by getting them mixed up
	public void resetVelocity_X() {
		xVel = 0;
	}
	
	public void resetVelocity_Y() {
		yVel = 0;
	}
	
	public boolean getCollision(Direction direction) {
		return collisionDirections.get(direction);
	}
	
}
