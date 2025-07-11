import java.awt.*;

public class GameGraphics {
	int playerX;
	int playerY;
	
	int gameSideLength; // the used area of the screen is a square with this length of side
	int imageSize;
	
	int width;
	int height;
	Graphics g;
	
	double scale;
	
	int xDisplayOffset;
	int yDisplayOffset;
	
	GameGraphics(int playerX, int playerY, int width, int height, Graphics g) {
		this.playerX = playerX;
		this.playerY = playerY;
		this.width = width;
		this.height = height;
		this.g = g;
		
		if (width > height) {
			gameSideLength = height;
		} else {
			gameSideLength = width;
		}
		
		xDisplayOffset = width / 2 - gameSideLength / 2;
		yDisplayOffset = height / 2 - gameSideLength / 2;
		
		imageSize = gameSideLength / 10;
		
		scale = (double) imageSize / Game.imageSize;
		
	}
	
	
	public void drawImage(Image image, int x, int y, int sideLength) {
		int newX = (int) Math.round((x + (Game.imageSize * 5) - playerX) * scale) + xDisplayOffset;
		int newY = (int) Math.round((y + (Game.imageSize * 5) - playerY) * scale) + yDisplayOffset;
		int newSideLength = (int) Math.round(sideLength * scale);
		
		if (newX + newSideLength < xDisplayOffset || newX > xDisplayOffset + gameSideLength || newY + newSideLength < yDisplayOffset || newY > yDisplayOffset + gameSideLength) {
			return;
		}
		g.drawImage(image, newX, newY, newSideLength, newSideLength, null);
	}
}
