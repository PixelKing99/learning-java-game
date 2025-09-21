import saves.Map;
import saves.Tile;

import java.awt.*;

public class Entity {
	
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
	
	
	int x;
	int y;
	
	private int newX;
	private int newY;
	
	Map map;
	
	int xVel = 0;
	int yVel = 0;
	
	int xIndex;
	int yIndex;
	private int newXIndex;
	private int newYIndex;
	
//	GameGraphics g;
	public SelectedDirections collisionDirections = new SelectedDirections();
	
	Entity(Map map, int x, int y) {
		this.x = x;
		this.y = y;
		this.map = map;
	}
	
	public void updatePosition(boolean spacebar) {
//		this.g = g;
//
//		if (this.g == null) {
//			return;
//		}
		

		xIndex = getIndex(x);
		yIndex = getIndex(y);
		
		newX = x + xVel;
		newY = y + yVel;
		
		newXIndex = getIndex(newX);
		newYIndex = getIndex(newY);
		
		
		CollisionTiles collisionTiles = findCollisionTiles();
		collisionDirections = findCollisionDirections(collisionTiles);
		
		
		x = newX;
		y = newY;
		xIndex = newXIndex;
		yIndex = newYIndex;
		
		collide();
		
	}
	
	private int getIndex(int coord) {
		int index = coord / Game.tileSize;
		if (coord < 0 && coord != index * Game.tileSize) {
			index -= 1;
		}
		return index;
	}
	
//	all of this collision code is ugly and bad af and im finna fix it but dont feel like it rn
	private CollisionTiles findCollisionTiles() {
		
		
		boolean overlapRight = newX > newXIndex * Game.tileSize;
		boolean overlapDown = newY > newYIndex * Game.tileSize;
		
		CollisionTiles collisions = new CollisionTiles();
		
		
		updateCollisions(collisions, 0, 0);
		
		if (overlapRight) {
			updateCollisions(collisions, 0, 1);
		}
		if (overlapDown) {
			updateCollisions(collisions, 1, 0);
		}
		if (overlapRight && overlapDown) {
			updateCollisions(collisions, 1, 1);
		}
		return collisions;
	}
	
	private void updateCollisions(CollisionTiles collisions, int yIndexOffset, int xIndexOffset) {
		
		if (checkTile(newXIndex + xIndexOffset, newYIndex + yIndexOffset)) {
			collisions.setTrue(yIndexOffset, xIndexOffset, newXIndex + xIndexOffset, newYIndex + yIndexOffset);
		}
	}
	
	private boolean checkTile(int xIndex, int yIndex) {
		
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
	
	private void collisionTileToDirection(CollisionTiles collisions, int relativeYIndex, int relativeXIndex, SelectedDirections collisionDirections) {
		
		if (collisions.getBool(relativeYIndex, relativeXIndex)) {
			
			int xDirection = relativeIndexToDirection(relativeXIndex);
			int yDirection = relativeIndexToDirection(relativeYIndex);
			
			Point indexes = collisions.getIndexes(relativeYIndex, relativeXIndex);

//			the direction value adds + or -1 to the index which essentially moves the boundary one tile towards the player because we are treating the player as a single point and not going based off of its nearest corner
			int xBoundary = (indexes.x + xDirection) * Game.tileSize;
			int yBoundary = (indexes.y + yDirection) * Game.tileSize;
			
			
			boolean wasTouchingXBoundary = x == xBoundary;
			boolean wasTouchingYBoundary = y == yBoundary;
			
			boolean isPastXBoundary = newX < xBoundary;
			boolean isPastYBoundary = newY < yBoundary;
			
			boolean wasPastXBoundary = x < xBoundary;
			boolean wasPastYBoundary = y < yBoundary;
			
//			the relativeIndexes should only be 1 or 0 and 0 is what these are initially defined as
			if (relativeYIndex == 1) {
				isPastYBoundary = newY > yBoundary;
				wasPastYBoundary = y > yBoundary;
			}
			
			if (relativeXIndex == 1) {
				isPastXBoundary = newX > xBoundary;
				wasPastXBoundary = x > xBoundary;
			}
			
			
			if (isPastXBoundary && (!wasPastXBoundary || wasTouchingXBoundary) && wasPastYBoundary) {
				// have to invert the horizontal sign because the screen's coordinates increase in the directions v> but it makes the most sense to set Directions.UP and RIGHT be positive (ie. ^>) inside the setTrue method so the vertical already is inverted, but because of the way the checks are set up and stuff we need both to be inverted
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
	
	
	private SelectedDirections findCollisionDirections(CollisionTiles collisions) {
		SelectedDirections collisionDirections = new SelectedDirections();
		
		collisionTileToDirection(collisions, 0, 0, collisionDirections);
		collisionTileToDirection(collisions, 0, 1, collisionDirections);
		collisionTileToDirection(collisions, 1, 0, collisionDirections);
		collisionTileToDirection(collisions, 1, 1, collisionDirections);
		
		return collisionDirections;
	}
	
	public void collide() {
		if (collisionDirections.get(Directions.UP)) {
			y = (yIndex + 1) * Game.tileSize;
			yVel = 0;
		}
		if (collisionDirections.get(Directions.DOWN)) {
			y = yIndex * Game.tileSize;
			yVel = 0;
		}
		if (collisionDirections.get(Directions.LEFT)) {
			x = (xIndex + 1) * Game.tileSize;
			xVel = 0;
		}
		if (collisionDirections.get(Directions.RIGHT)) {
			x = xIndex * Game.tileSize;
			xVel = 0;
		}
	}
}
