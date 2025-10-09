package render;

import game.entities.Player;
import game.input.InputState;
import game.input.Mouse;
import game.entities.EntityType;
import saves.Map;
import saves.Tile;

import java.awt.*;
import java.util.HashMap;

import static game.Server.getIndex;
import static game.Server.TILE_SIZE;

public class Frame {
	public static final int FRAME_SIDELENGTH_IN_BLOCKS = 10;
	
	public final int gameSideLength; // the used area of the screen is a square with this length of side
	public final int imageSize;
	public final double scale;
	
	public final int width;
	public final int height;
	public final int xDisplayOffset;
	public final int yDisplayOffset;
	
	public final Graphics g;
	public final InputState inputState;
	public final Map map;
	public final Player player;
	public final Point playerCoord;
	public final Point playerIndex;
	public final Mouse mouse;
	public final Color mouseColor;
	public final HashMap<Tile, Image> tileTextures;
	public final HashMap<EntityType, Image> entityTextures;
	public final String fps;
	public final String tps;
	public final int sidebarWidth;
	public final int sidebarHeight;
	
	
	
	public Frame(Graphics g, InputState inputState, Map map, HashMap<Tile, Image> tileTextures, HashMap<EntityType, Image> entityTextures, Player player, int width, int height, String fps, String tps) {
		this.g = g;
		this.inputState = inputState;
		this.map = map;
		this.tileTextures = tileTextures;
		this.entityTextures = entityTextures;
		this.player = player;
		this.width = width;
		this.height = height;
		this.fps = fps;
		this.tps = tps;
		
		this.mouse = inputState.mouse;
		this.playerCoord = player.getCoords();
		this.playerIndex = player.getIndexes();
		
		
		
		
		if (width > height) {
			gameSideLength = height;
		} else {
			gameSideLength = width;
		}
		
		xDisplayOffset = (int) Math.round(width / 2.0 - gameSideLength / 2.0);
		yDisplayOffset = (int) Math.round(height / 2.0 - gameSideLength / 2.0);
		
		if (width > height) {
			sidebarWidth = xDisplayOffset;
			sidebarHeight = height;
		} else {
			sidebarWidth = width;
			sidebarHeight = yDisplayOffset;
		}
		
		
		
		imageSize = (int) Math.round(gameSideLength / (double) FRAME_SIDELENGTH_IN_BLOCKS);
		
		
		
		if (mouse.left) {
			mouseColor = new Color(255, 0, 0);
		} else if (mouse.right) {
			mouseColor = new Color(0, 255, 0);
		} else {
			mouseColor = new Color(255, 255,255);
		}
		
		
		
		scale = (double) imageSize / TILE_SIZE;
	}
	
	
	
	
	public void drawMap() {
		
		for (int i = 0; i < map.getLength(); i++) {
			for (int j = 0; j < map.getInnerLength(); j++) {
				
				if (map.get(i, j) != Tile.AIR) {
					drawImage(tileTextures.get(map.get(i, j)), j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE);
				}
			}
		}
	}
	
	
	
	
	public void drawPlayer() {
		drawImage(entityTextures.get(EntityType.PLAYER), playerCoord, TILE_SIZE);
	}
	
	
	public void drawSideBars() {
		g.setColor(new Color(20, 20,20));
		g.fillRect(0, 0, sidebarWidth, sidebarHeight);
		g.fillRect(width - sidebarWidth, height - sidebarHeight, sidebarWidth, sidebarHeight);
	}
	
	
	
	
	public void drawCursor() {
		
		if (mouse.mouseOutsideWindow) {
			return;
		}
		
		g.setColor(mouseColor);
		
		g.fillRect(mouse.x - gameSideLength / 100, mouse.y - gameSideLength / 100, gameSideLength / 50, gameSideLength / 50);
	}
	
	
	
	
//	draws an outline around the tile the cursor is hovering over
	public void drawSelectedTile() {
		if (mouse.mouseOutsideWindow) {
			return;
		}
		
		if (isOutsideGameDisplay(mouse.x, mouse.y, 0,0)) {
			return;
		}
		
		
		
		Graphics2D g2d = (Graphics2D) g;
		if (mouse.left || mouse.right) {
			g2d.setStroke(new BasicStroke(3));
		}
		
		
//		finds the coordinates of the tile the cursor is in
		Point gameCoord = pixelToGameCoord(mouse.x, mouse.y);
		Point tilePixel = gameCoordToPixel(getIndex(gameCoord.x) * TILE_SIZE, getIndex(gameCoord.y) * TILE_SIZE);
		
		
		g.setColor(mouseColor);
		g.drawRect(tilePixel.x, tilePixel.y, imageSize, imageSize);
		
		g2d.setStroke(new BasicStroke(1));
	}
	
	
	
	
	public void drawHUD() {
		String[] text = {
		"fps: " + fps,
		"tps: " + tps,
		player.getVelocity().x + ", " + player.getVelocity().y,
		playerIndex.x + ", " + playerIndex.y,
		playerCoord.x + ", " + playerCoord.y
		};
		
		
		int spacing = 20;
		int offset = spacing;
		
		
		g.setColor(new Color(255, 255, 255));
		for (String message : text) {
			g.drawString(message, 3, offset);
			offset += spacing;
		}
		
	}
	
	
	
	
	public boolean isOutsideGameDisplay(Point point, int width, int height) {return isOutsideGameDisplay(point.x, point.y, width, height);}
	public boolean isOutsideGameDisplay(int x, int y, int width, int height) {
		boolean insideX = x + width >= xDisplayOffset && x <= xDisplayOffset + gameSideLength;
		boolean insideY = y + height >= yDisplayOffset && y <= yDisplayOffset + gameSideLength;
		
		return !insideX || !insideY;
	}
	
	
	
//	basically a wrapper for g.drawImage but it takes a gameCoord and converts it to a pixel coord before drawing
	private void drawImage(Image image, Point point, int sideLength) {drawImage(image, point.x, point.y, sideLength);}
	private void drawImage(Image image, int x, int y, int sideLength) {
		Point pixel = gameCoordToPixel(x, y);
		
		int newSideLength = (int) Math.round(sideLength * scale);
		
		if (isOutsideGameDisplay(pixel, newSideLength, newSideLength)) {
			return;
		}
		g.drawImage(image, pixel.x, pixel.y, newSideLength, newSideLength, null);
	}
	
	
	
//	ugly af math
//	basically scales it and makes the coordinate relative to the players coordinate (so the player is always in the center) and stuff
	public Point gameCoordToPixel(int x, int y) {return gameCoordToPixel(new Point(x,y));}
	public Point gameCoordToPixel(Point coord) {
		Point pixel = new Point();
		pixel.x = (int) Math.round((coord.x + ((double) (TILE_SIZE * (FRAME_SIDELENGTH_IN_BLOCKS - 1)) / 2) - playerCoord.x) * scale) + xDisplayOffset;
		pixel.y = (int) Math.round((coord.y + ((double) (TILE_SIZE * (FRAME_SIDELENGTH_IN_BLOCKS - 1)) / 2) - playerCoord.y) * scale) + yDisplayOffset;
		
		return pixel;
	}
	
	
	
	public Point pixelToGameCoord(int x, int y) {return pixelToGameCoord(new Point(x,y));}
	public Point pixelToGameCoord(Point pixel) {
		Point gameCoord = new Point();
		
		gameCoord.x = (int) Math.round((pixel.x - xDisplayOffset) / scale) + playerCoord.x - (TILE_SIZE * (FRAME_SIDELENGTH_IN_BLOCKS - 1) / 2);
		gameCoord.y = (int) Math.round((pixel.y - yDisplayOffset) / scale) + playerCoord.y - (TILE_SIZE * (FRAME_SIDELENGTH_IN_BLOCKS - 1) / 2);
		
		return gameCoord;
	}
}
