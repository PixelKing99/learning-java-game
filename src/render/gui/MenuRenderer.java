package render.gui;

import game.input.UserInput;
import render.Renderer;
import render.Screen;
import render.Texture;

import java.awt.*;
import java.util.function.Supplier;

import static render.Render.getScreenSetter;

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
	
    public static final Element DEFAULT_BACKGROUND = new Element().setTexture(Texture.LAVA, false, 10);

    //		Supplier<Element> menuDefault = () -> new Element().setOutlineColor(new Color(100,0,0));
    protected static final Supplier<Element> menuDefault = () -> new Element();
    protected static final Supplier<Element> buttonDefault = () -> new Element().setColor(new UiColor(new Color(30, 30, 30, 180), new Color(30, 30, 30, 220)));

    protected static final Runnable setScreenToGame = getScreenSetter(Screen.GAME);

	protected Element menu;
	protected Element background = DEFAULT_BACKGROUND;


//    could use these if i wanted to define like a simple menu without having to make a class for it but i dont need to rn

//	public MenuRenderer(Element menu, Element background) {
//		this.menu = menu;
//		this.background = background;
//	}
//
//	public MenuRenderer(Element menu) {
//		this.menu = menu;
//	}


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
