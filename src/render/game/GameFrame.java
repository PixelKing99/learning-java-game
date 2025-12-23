package render.game;

import game.entities.Player;
import game.input.InputState;
import game.entities.EntityType;
import render.Frame;
import render.Textures;
import saves.GameMap;
import saves.Tile;

import java.awt.*;
import java.util.HashMap;

import static game.Server.getIndex;
import static game.Server.TILE_SIZE;

public class GameFrame extends Frame {
	public static final int FRAME_SIDELENGTH_IN_BLOCKS = 10;
	
	
	public final GameMap gameMap;
	public final Player player;
	public final Point playerCoord;
	public final Point playerIndex;
	
	
	public final int imageSize;
	public final double scale;
	
	
	
	public GameFrame(Graphics g, int width, int height, InputState inputState, Player player, GameMap gameMap) {
		super(g, width, height, inputState);
		
		this.gameMap = gameMap;
		this.player = player;
		
		this.playerCoord = player.getCoords();
		this.playerIndex = player.getIndexes();
		
		
		
		imageSize = (int) Math.round(gameSideLength / (double) FRAME_SIDELENGTH_IN_BLOCKS);
		
		
		scale = (double) imageSize / TILE_SIZE;
	}
	
	
	
	
	public void drawMap() {
		
		for (int i = 0; i < gameMap.getLength(); i++) {
			for (int j = 0; j < gameMap.getInnerLength(); j++) {
				
				if (gameMap.get(i, j) != Tile.AIR) {
					drawImage(Textures.get(gameMap.get(i, j)), j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE);
				}
			}
		}
	}
	
	
	
	
	public void drawPlayer() {
		drawImage(Textures.get(EntityType.PLAYER), playerCoord, TILE_SIZE);
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
