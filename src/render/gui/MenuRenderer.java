package render.gui;

import game.input.UserInput;
import render.Renderer;

import java.awt.*;

public class MenuRenderer implements Renderer<MenuFrame> {
	
//	menu holds an element
//	when the menu is rendered it is passed a xOffset, yOffset, width, and height.
//	the width and height are passed to children and used for calculations, and the offset is added to all final results
//		elements store a text, an image, and a background color, maybe a border color
//		elements default size is as much as is provided, can be set to have a max value (no min maybe since that would mean it overrides the parent?)
//		to render its passed an x and y and a width and height and then it renders itself
//		maybe there should be a render previous state so that it will skip doing any calculations and just use the previous width/coordinates
//		by default text aligns to center but can be top/bottom, and left/right
//			partition is an element
//			partitions are either horizontal of vertical
//			a partition is divided into a set number of sections
//			each section stores an element but they can be invisible
//			each section has a weight, by default is 1, the weight determines how large the section is relative to the others
//			should be able to be told if nothing has changed and then use stored previous section coordinates
//
//			button is an element that can have a highlight color and onclick has a callback function
//			needs to be passed the mouse coords ig
	
	
	private Element menu;
	private Element background;
	
	public MenuRenderer(Element menu, Element background) {
		this.menu = menu;
		this.background = background;
	}
	
	@Override
	public void render(Graphics g, int width, int height) {
		new MenuFrame(g, width, height, UserInput.getInputState(), menu, background);
	}
	
	@Override
	public void updateVisibility(boolean isVisible) {
		menu.updateVisibility(isVisible);
		background.updateVisibility(isVisible);
	}
}
