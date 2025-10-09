package render;

import game.Direction;
import game.entities.Player;
import game.Server;
import game.input.UserInput;

import java.awt.*;

import static game.Server.TILE_SIZE;


public class DebugRenderer {
	private final Graphics g;
	private final Server server;
	private final UserInput userInput;
	private final Panel panel;
	private final Frame frame;
	
	private final Player player;
	private final int width;
	private final int height;
	

	public DebugRenderer(Graphics g, Server server, UserInput userInput, Panel panel, Frame frame) {
		this.g = g;
		this.server = server;
		this.userInput = userInput;
		this.panel = panel;
		this.frame = frame;
		
		this.player = this.server.getPlayer();
		this.width = this.panel.getWidth();
		this.height = this.panel.getHeight();
	}

	
	
	public void showCollisionDirections() {
		g.setColor(new Color(0, 0, 255));
		
		
		if (player.getCollision(Direction.UP)) {
			g.fillRect(width / 2 - frame.imageSize / 6, height / 2 - frame.imageSize / 2, frame.imageSize / 3, frame.imageSize / 3);
		}
		if (player.getCollision(Direction.RIGHT)) {
			g.fillRect(width / 2 + frame.imageSize / 6, height / 2 - frame.imageSize / 6, frame.imageSize / 3, frame.imageSize / 3);
		}
		if (player.getCollision(Direction.DOWN)) {
			g.fillRect(width / 2 - frame.imageSize / 6, height / 2 + frame.imageSize / 6, frame.imageSize / 3, frame.imageSize / 3);
		}
		if (player.getCollision(Direction.LEFT)) {
			g.fillRect(width / 2 - frame.imageSize / 2, height / 2 - frame.imageSize / 6, frame.imageSize / 3, frame.imageSize / 3);
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
		g.drawLine(frame.xDisplayOffset, frame.yDisplayOffset, frame.sidebarWidth, frame.sidebarHeight);
		g.drawLine(width - frame.sidebarWidth, height - frame.sidebarHeight, width - frame.xDisplayOffset, height - frame.yDisplayOffset);
	}
	
	
	
//	outline tile and display index of it
	private void highlightTile(int xIndex, int yIndex, Color color) {
		Point indexPixel = frame.gameCoordToPixel(xIndex * TILE_SIZE, yIndex * TILE_SIZE);
		
		if (frame.isOutsideGameDisplay(indexPixel, frame.imageSize, frame.imageSize)) {
			return;
		}
		
		g.setColor(color);
		
		g.drawRect(indexPixel.x, indexPixel.y, frame.imageSize, frame.imageSize);
		g.drawString(xIndex + ", " + yIndex, indexPixel.x + 5, indexPixel.y + 20);
	}
}
