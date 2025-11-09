package render;

import game.Server;
import game.input.InputState;
import game.input.Mouse;
import util.DynamicString;

import java.awt.*;
import java.util.ArrayList;

public abstract class Frame {
	
	public final Graphics g;
	public final InputState inputState;
	
	
	
	public final Mouse mouse;
	public final Color mouseColor;
	
	
	public final int width;
	public final int height;
	public final int sidebarWidth;
	public final int sidebarHeight;
	
	public final int gameSideLength; // the used area of the screen is a square with this length of side
	public final int xDisplayOffset;
	public final int yDisplayOffset;
	
	
	
	
	public Frame(Graphics g, int width, int height, InputState inputState) {
		this.g = g;
		this.inputState = inputState;
		
		
		this.width = width;
		this.height = height;
		this.mouse = inputState.mouse;
		
		
		
		
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
		
		
		
		if (mouse.left && !mouse.right) {
			mouseColor = new Color(255, 122, 0);
		} else if (mouse.right && !mouse.left) {
			mouseColor = new Color(0, 122, 255);
		} else if (mouse.right && mouse.left) {
			mouseColor = new Color(122, 61, 122);
		} else {
			mouseColor = new Color(255, 255,255);
		}
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
	
	
	
	
	public void drawHUD() {
		Hud.render(g);
	}
}

