import java.awt.*;

public class GameGraphics {
	int playerX;
	int playerY;
	
	int gameSideLength; // the used area of the screen is a square with this length of side
	int imageSize;
	int frameSideLengthInBlocks = 10; // the side-length of the frame measured in blocks
	
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
		
		xDisplayOffset = (int) Math.round(width / 2.0 - gameSideLength / 2.0);
		yDisplayOffset = (int) Math.round(height / 2.0 - gameSideLength / 2.0);
		
		imageSize = (int) Math.round(gameSideLength / (double) frameSideLengthInBlocks);
		
		scale = (double) imageSize / Game.tileSize;
		
	}
	
	
	public void drawImage(Image image, int x, int y, int sideLength) {
		Point pixel = gameCoordToPixel(new Point(x, y));
		
		int newSideLength = (int) Math.round(sideLength * scale);
		
		if (pixel.x + newSideLength < xDisplayOffset || pixel.x > xDisplayOffset + gameSideLength || pixel.y + newSideLength < yDisplayOffset || pixel.y > yDisplayOffset + gameSideLength) {
			return;
		}
		g.drawImage(image, pixel.x, pixel.y, newSideLength, newSideLength, null);
	}
	
	public Point gameCoordToPixel(Point coord) {
		Point pixel = new Point();
		pixel.x = (int) Math.round((coord.x + ((double) (Game.tileSize * (frameSideLengthInBlocks - 1)) / 2) - playerX) * scale) + xDisplayOffset;
		pixel.y = (int) Math.round((coord.y + ((double) (Game.tileSize * (frameSideLengthInBlocks - 1)) / 2) - playerY) * scale) + yDisplayOffset;
		
		return pixel;
	}
	
	public Point pixelToGameCoord(Point pixel) {
		Point gameCoord = new Point();
		
		gameCoord.x = (int) Math.round((pixel.x - xDisplayOffset) / scale) + playerX - (Game.tileSize * (frameSideLengthInBlocks - 1) / 2);
		gameCoord.y = (int) Math.round((pixel.y - yDisplayOffset) / scale) + playerY - (Game.tileSize * (frameSideLengthInBlocks - 1) / 2);
		
		return gameCoord;
	}
	
	public void drawCursor(Point mouse) {
//		when mouse is outside of the window getMousePosition returns null
		if (mouse == null) {
			return;
		}
		
		if (Game.leftButtonDown) {
			g.setColor(new Color(255, 0, 0));
		} else if (Game.rightButtonDown) {
			g.setColor(new Color(0, 255, 0));
		} else {
			g.setColor(new Color(255, 255,255));
		}
		
		g.fillRect(mouse.x - gameSideLength / 100, mouse.y - gameSideLength / 100, gameSideLength / 50, gameSideLength / 50);
	}
	
	public void drawSelectedTile(Point mouse) {
		if (mouse == null) {
			return;
		}
		
		
		boolean insideFrameX = mouse.x >= xDisplayOffset && mouse.x <= xDisplayOffset + gameSideLength;
		boolean insideFrameY = mouse.y >= yDisplayOffset && mouse.y <= yDisplayOffset + gameSideLength;
		if (!insideFrameX || !insideFrameY) {
			return;
		}
		
		Graphics2D g2d = (Graphics2D) g;
		
		if (Game.leftButtonDown) {
			g2d.setStroke(new BasicStroke(3));
			g.setColor(new Color(255, 0,0));
		} else if (Game.rightButtonDown) {
			g2d.setStroke(new BasicStroke(3));
			g.setColor(new Color(0, 255, 0));
		} else {
			g.setColor(new Color(255, 255,255));
		}
		
		Point gameCoord = pixelToGameCoord(new Point(mouse.x, mouse.y));
		
		int hoveredXIndex = gameCoord.x / Game.tileSize;
		int hoveredYIndex = gameCoord.y / Game.tileSize;
//		have to do this since 0.5 and -0.5 round to 0, so it essentially makes negative numbers round up
		if (gameCoord.x < 0) {
			hoveredXIndex -= 1;
		}
		if (gameCoord.y < 0) {
			hoveredYIndex -= 1;
		}
		
		Point hoveredIndex = new Point(hoveredXIndex, hoveredYIndex);
		
		Point pixel = gameCoordToPixel(new Point(hoveredIndex.x * Game.tileSize, hoveredIndex.y * Game.tileSize));
		g.drawRect(pixel.x, pixel.y, imageSize, imageSize);
		
		g2d.setStroke(new BasicStroke(1));
	}
	
	public void highlightTile(int xIndex, int yIndex, Color color) {
		Point indexPos = new Point(xIndex * Game.tileSize, yIndex * Game.tileSize);
		Point indexPixel = gameCoordToPixel(indexPos);
		
		boolean insideFrameX = indexPixel.x + imageSize >= xDisplayOffset && indexPixel.x <= xDisplayOffset + gameSideLength;
		boolean insideFrameY = indexPixel.y + imageSize >= yDisplayOffset && indexPixel.y <= yDisplayOffset + gameSideLength;
		if (!insideFrameX || !insideFrameY) {
			return;
		}
		
		g.setColor(color);
		
		g.drawRect(indexPixel.x, indexPixel.y, imageSize, imageSize);
		g.drawString(xIndex + ", " + yIndex, indexPixel.x + 5, indexPixel.y + 20);
	}
}
