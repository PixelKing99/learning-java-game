package render.gui;

import game.Server;
import game.input.InputState;
import render.Frame;
import render.Panel;

import java.awt.*;

public class MenuFrame extends Frame {
	public final Element menu;
	public final Element background;
	
	public MenuFrame(Graphics g, int width, int height, InputState inputState, Element menu, Element background) {
		super(g, width, height, inputState);
		this.menu = menu;
		this.background = background;
		
		
		background.render(g, 0, 0, width, height, inputState);
		menu.render(g, xDisplayOffset, yDisplayOffset, gameSideLength, gameSideLength, inputState);
		
		drawCursor();
		
		drawHUD();
	}
}
