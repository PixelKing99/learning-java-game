package render;

import render.game.GameFrame;
import util.Direction;
import game.entities.Player;
import game.Server;
import game.input.InputState;

import java.awt.*;

import static game.Server.TILE_SIZE;


public class DebugFrame extends Frame {
	public final GameFrame gameFrame;
	
	public final Player player;
	public final int imageSize;
	

	public DebugFrame(Graphics g, int width, int height, InputState inputState, Player player, GameFrame gameFrame) {
		super(g, width, height, inputState);
		this.gameFrame = gameFrame;
		
		this.player = player;
		this.imageSize = this.gameFrame.imageSize;
	}

	
	
//	draws a square on the side of the player in the direction which is colliding
	public void showCollisionDirections() {
		g.setColor(new Color(0, 0, 255));
		
		int centerX = width / 2;
		int centerY = height / 2;
		int indicatorSize = imageSize / 3;
		
		
		if (player.getCollision(Direction.UP)) {
			g.fillRect(centerX - imageSize / 6, centerY - imageSize / 2, indicatorSize, indicatorSize);
		}
		if (player.getCollision(Direction.RIGHT)) {
			g.fillRect(centerX + imageSize / 6, centerY - imageSize / 6, indicatorSize, indicatorSize);
		}
		if (player.getCollision(Direction.DOWN)) {
			g.fillRect(centerX - imageSize / 6, centerY + imageSize / 6, indicatorSize, indicatorSize);
		}
		if (player.getCollision(Direction.LEFT)) {
			g.fillRect(centerX - imageSize / 2, centerY - imageSize / 6, indicatorSize, indicatorSize);
		}
	}
	
	
	
//	just to show the center of the screen and make sure everything is aligned while debugging
	public void drawCenteringLines() {
		g.setColor(new Color(255, 0, 255));
		g.drawLine(0, height / 2, width, height / 2);
		g.drawLine(width / 2, 0, width / 2, height);
	}
	
	

//	debug lines where the edge of the sidebars are
	public void drawSidebarEdges() {
		g.setColor(new Color(255, 0, 0));
		g.drawLine(xDisplayOffset, yDisplayOffset, sidebarWidth, sidebarHeight);
		g.drawLine(width - sidebarWidth, height - sidebarHeight, width - xDisplayOffset, height - yDisplayOffset);
	}
	
	
	
//	outline tile and display index of it
	private void highlightTile(int xIndex, int yIndex, Color color) {
		Point indexPixel = gameFrame.gameCoordToPixel(xIndex * TILE_SIZE, yIndex * TILE_SIZE);
		
		if (gameFrame.isOutsideGameDisplay(indexPixel, imageSize, imageSize)) {
			return;
		}
		
		g.setColor(color);
		
		g.drawRect(indexPixel.x, indexPixel.y, imageSize, imageSize);
		g.drawString(xIndex + ", " + yIndex, indexPixel.x + 5, indexPixel.y + 20);
	}
}
