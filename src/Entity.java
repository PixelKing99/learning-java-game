import java.awt.*;

public class Entity {
	
	private class CollisionTiles {
		public boolean[][] collisions = new boolean[2][2];
		public int[][][] collisionIndexes = new int[2][2][2];
		CollisionTiles(boolean[][] collisions, int[][][] collisionIndexes) {
			this.collisions = collisions;
			this.collisionIndexes = collisionIndexes;
		}
	}
	
	
	int x;
	int y;
	int xVel = 0;
	int yVel = 0;
	int imageSize = Game.imageSize;
	
	int xIndex;
	int yIndex;
	
	Graphics g;
	
	Entity(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void updatePosition(Graphics g) {
		xIndex = x / imageSize;
		yIndex = y / imageSize;
		
		this.g = g;
		
		CollisionTiles collisionTiles = findCollisionTiles(xVel, yVel);
		Directions collisions = findCollisionDirection2(collisionTiles, xVel, yVel);
		
		x += xVel;
		y += yVel;
		collide(collisions);
		
	}
	
	public CollisionTiles findCollisionTiles(int offsetX, int offsetY) {
		int xIndex = (x + offsetX) / imageSize;
		int yIndex = (y + offsetY) / imageSize;
		
		boolean overlapRight = (x + offsetX) > xIndex * imageSize;
		boolean overlapDown = (y + offsetY) > yIndex * imageSize;
		
		boolean[][] collisions = {{false, false}, {false, false}};
		int[][][] collisionIndexes = new int[2][2][2];
		
		collisions[0][0] = checkTile(xIndex, yIndex);
		if (collisions[0][0]) {
			collisionIndexes[0][0][0] = xIndex;
			collisionIndexes[0][0][1] = yIndex;
		}
		
		if (overlapRight) {
			collisions[0][1] = checkTile(xIndex + 1, yIndex);
			if (collisions[0][1]) {
				collisionIndexes[0][1][0] = xIndex + 1;
				collisionIndexes[0][1][1] = yIndex;
			}
		}
		if (overlapDown) {
			collisions[1][0] = checkTile(xIndex, yIndex + 1);
			if (collisions[1][0]) {
				collisionIndexes[1][0][0] = xIndex;
				collisionIndexes[1][0][1] = yIndex + 1;
			}
		}
		if (overlapRight && overlapDown) {
			collisions[1][1] = checkTile(xIndex + 1, yIndex + 1);
			if (collisions[1][1]) {
				collisionIndexes[1][1][0] = xIndex + 1;
				collisionIndexes[1][1][1] = yIndex + 1;
			}
		}
		CollisionTiles collisionTiles = new CollisionTiles(collisions, collisionIndexes);
		return collisionTiles;
	}
	
	public boolean checkTile(int xIndex, int yIndex) {
		
		boolean yValid = yIndex < Game.gameMap.length && yIndex >= 0;
		boolean validIndex = yValid && xIndex < Game.gameMap[yIndex].length && xIndex >= 0;
		
		if (validIndex && !Game.gameMap[yIndex][xIndex].equals('0')) {
//			g.setColor(new Color(255, 0,0));
//			g.fillRect(xIndex * imageSize + Game.xDisplayOffset, yIndex * imageSize + Game.yDisplayOffset, imageSize, imageSize);
			
			return true;
			
		} else {
//			g.setColor(new Color(255, 255,255));
//			g.drawRect(xIndex * imageSize + Game.xDisplayOffset, yIndex * imageSize + Game.yDisplayOffset, imageSize, imageSize);
			return false;
		}
	}
	
	public Directions findCollisionDirection2(CollisionTiles collisionsTiles, int offsetX, int offsetY) {
		Directions collisionDirections = new Directions();
		
		g.setColor(new Color(255, 255,255));
		g.setFont(new Font("", 0, 20));
		int newX = x + offsetX;
		int newY = y + offsetY;
		
		if (collisionsTiles.collisions[0][0]) {
			int[] curIndexes = collisionsTiles.collisionIndexes[0][0];
			int xBoundary = (curIndexes[0] + 1) * imageSize;
			int yBoundary = (curIndexes[1] + 1) * imageSize;
			
			if (newX < xBoundary && x >= xBoundary && y < yBoundary) {
				collisionDirections.set('L', true);
//				g.drawString("<", curIndexes[0] * imageSize + Game.xDisplayOffset, curIndexes[1] * imageSize + Game.yDisplayOffset + Game.spacing);
			} else if (newY < yBoundary && y >= yBoundary && x < xBoundary) {
				collisionDirections.set('U', true);
//				g.drawString("^", curIndexes[0] * imageSize + Game.xDisplayOffset, curIndexes[1] * imageSize + Game.yDisplayOffset + Game.spacing);
			} else if (!collisionsTiles.collisions[1][0] && !collisionsTiles.collisions[0][1]) {
				collisionDirections.set('U', true);
//				g.drawString("^", curIndexes[0] * imageSize + Game.xDisplayOffset, curIndexes[1] * imageSize + Game.yDisplayOffset + Game.spacing);
			} else {
//				g.drawString("x", curIndexes[0] * imageSize + Game.xDisplayOffset, curIndexes[1] * imageSize + Game.yDisplayOffset + Game.spacing);
			}
		}
		
		if (collisionsTiles.collisions[0][1]) {
			int[] curIndexes = collisionsTiles.collisionIndexes[0][1];
			int xBoundary = (curIndexes[0] - 1) * imageSize;
			int yBoundary = (curIndexes[1] + 1) * imageSize;
			
			
			if (newX > xBoundary && x <= xBoundary && y < yBoundary) {
				collisionDirections.set('R', true);
//				g.drawString(">", (curIndexes[0] + 1) * imageSize + Game.xDisplayOffset - Game.spacing, curIndexes[1] * imageSize + Game.yDisplayOffset + Game.spacing);
			} else if (newY < yBoundary && y >= yBoundary && x > xBoundary) {
				collisionDirections.set('U', true);
//				g.drawString("^", (curIndexes[0] + 1) * imageSize + Game.xDisplayOffset - Game.spacing, curIndexes[1] * imageSize + Game.yDisplayOffset + Game.spacing);
			} else if (!collisionsTiles.collisions[1][1] && !collisionsTiles.collisions[0][0]) {
				collisionDirections.set('U', true);
//				g.drawString("^", curIndexes[0] * imageSize + Game.xDisplayOffset - Game.spacing, curIndexes[1] * imageSize + Game.yDisplayOffset + Game.spacing);
			} else {
//				g.drawString("x", (curIndexes[0] + 1) * imageSize + Game.xDisplayOffset - Game.spacing, curIndexes[1] * imageSize + Game.yDisplayOffset + Game.spacing);
			}
		}
		
		if (collisionsTiles.collisions[1][0]) {
			int[] curIndexes = collisionsTiles.collisionIndexes[1][0];
			int xBoundary = (curIndexes[0] + 1) * imageSize;
			int yBoundary = (curIndexes[1] - 1) * imageSize;
			
			
			if (newX < xBoundary && x >= xBoundary && y > yBoundary) {
				collisionDirections.set('L', true);
//				g.drawString("<", curIndexes[0] * imageSize + Game.xDisplayOffset, (curIndexes[1] + 1) * imageSize + Game.yDisplayOffset);
			} else if (newY > yBoundary && y <= yBoundary && x < xBoundary) {
				collisionDirections.set('D', true);
//				g.drawString("v", curIndexes[0] * imageSize + Game.xDisplayOffset, (curIndexes[1] + 1) * imageSize + Game.yDisplayOffset);
			} else if (!collisionsTiles.collisions[1][1] && !collisionsTiles.collisions[0][0]) {
				collisionDirections.set('D', true);
//				g.drawString("v", curIndexes[0] * imageSize + Game.xDisplayOffset, (curIndexes[1] + 1) * imageSize + Game.yDisplayOffset);
				
			} else {
//				g.drawString("x", curIndexes[0] * imageSize + Game.xDisplayOffset, (curIndexes[1] + 1) * imageSize + Game.yDisplayOffset);
			}
		}
		
		if (collisionsTiles.collisions[1][1]) {
			int[] curIndexes = collisionsTiles.collisionIndexes[1][1];
			int xBoundary = (curIndexes[0] - 1) * imageSize;
			int yBoundary = (curIndexes[1] - 1) * imageSize;
			
			
			if (newX > xBoundary && x <= xBoundary && y > yBoundary) {
				collisionDirections.set('R', true);
//				g.drawString(">", (curIndexes[0] + 1) * imageSize + Game.xDisplayOffset - Game.spacing, (curIndexes[1] + 1) * imageSize + Game.yDisplayOffset);
			} else if (newY > yBoundary && y <= yBoundary && x > xBoundary) {
				collisionDirections.set('D', true);
//				g.drawString("v", (curIndexes[0] + 1) * imageSize + Game.xDisplayOffset - Game.spacing, (curIndexes[1] + 1) * imageSize + Game.yDisplayOffset);
			} else if (!collisionsTiles.collisions[1][0] && !collisionsTiles.collisions[0][1]) {
				collisionDirections.set('D', true);
//				g.drawString("v", curIndexes[0] * imageSize + Game.xDisplayOffset - Game.spacing, curIndexes[1] * imageSize + Game.yDisplayOffset);
			} else {
//				g.drawString("x", (curIndexes[0] + 1) * imageSize + Game.xDisplayOffset - Game.spacing, (curIndexes[1] + 1) * imageSize + Game.yDisplayOffset);
			}
		}
		
		return collisionDirections;
	}
	
	public void collide(Directions collisions) {
		if (collisions.get('U')) {
			y = (y / imageSize + 1) * imageSize;
			yVel = 0;
		}
		if (collisions.get('D')) {
			y = (y / imageSize) * imageSize;
			yVel = 0;
		}
		if (collisions.get('L')) {
			x = (x / imageSize + 1) * imageSize;
			xVel = 0;
		}
		if (collisions.get('R')) {
			x = (x / imageSize) * imageSize;
			xVel = 0;
		}
	}
}
