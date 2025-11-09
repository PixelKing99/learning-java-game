package render.gui;

import render.Screen;

import java.awt.*;
import java.util.function.Supplier;

import static render.Render.getScreenSetter;

public class MenuBuilder {
	
	//		Supplier<Element> menuDefault = () -> new Element().setOutlineColor(new Color(100,0,0));
	private static final Supplier<Element> menuDefault = () -> new Element();
	private static final Supplier<Element> buttonDefault = () -> new Element().setColor(new UiColor(new Color(30, 30, 30, 180), new Color(30, 30, 30, 220)));
	
	
//	not exactly the greatest to have to pass the image as a parameter but at least then i can keep this static, should probably make a better solution some time tho
	public static Element getMainMenuBackgound(Image lava) {
		return new Element().setImage(lava, false, 10);
	}
	
	public static Element getMainMenu(Image logo) {
		Partition mainElement = new Partition(2, menuDefault).splitHorizontally();
		Partition buttonArea = new Partition(3, menuDefault);
		Partition buttonPartition = new Partition(7, menuDefault).splitHorizontally();
		
		Partition logoHolder = new Partition(3);
		logoHolder.setWeight(1, 5).get(1).setImage(logo);
		mainElement.set(0, logoHolder);
		
		buttonPartition.set(0, buttonDefault.get())
		.setMessage("start")
		.addActionListener(getScreenSetter(Screen.GAME));
		
		buttonPartition.set(2, buttonDefault.get())
		.setMessage("options")
		.addActionListener(getScreenSetter(Screen.GAME));
		
		buttonPartition.set(4, buttonDefault.get())
		.setMessage("something else")
		.addActionListener(getScreenSetter(Screen.GAME));
		
		
		buttonArea.set(1, buttonPartition);
		mainElement.set(1, buttonArea);
		
		return mainElement;
	}
	
}
