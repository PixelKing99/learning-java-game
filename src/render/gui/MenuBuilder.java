package render.gui;

import render.Screen;
import saves.Save;

import java.awt.*;
import java.util.function.Supplier;

import static render.Render.getScreenSetter;

public class MenuBuilder {
	
	//		Supplier<Element> menuDefault = () -> new Element().setOutlineColor(new Color(100,0,0));
	private static final Supplier<Element> menuDefault = () -> new Element();
	private static final Supplier<Element> buttonDefault = () -> new Element().setColor(new UiColor(new Color(30, 30, 30, 180), new Color(30, 30, 30, 220)));
	
	
//	not exactly the greatest to have to pass the image as a parameter but at least then i can keep this static, should probably make a better solution some time tho
	public static MenuRenderer getMainMenu(Image logo, Image lava) {
//		need to make this a constant inside this class but ill need to do stuff with loading images
		Element background = new Element().setImage(lava, false, 10);
		
		
		Partition mainElement = new Partition(2, menuDefault).splitHorizontally();
		Partition buttonArea = new Partition(3, menuDefault);
		Partition buttonPartition = new Partition(7, menuDefault).splitHorizontally();
		
		Partition logoHolder = new Partition(3);
		logoHolder.setWeight(1, 5).get(1).setImage(logo);
		mainElement.set(0, logoHolder);
		
		buttonPartition.set(0, buttonDefault.get())
		.setMessage("start")
		.addMousePressListener(getScreenSetter(Screen.WORLD_SELECTION));
		
		buttonPartition.set(2, buttonDefault.get())
		.setMessage("options")
		.addMousePressListener(getScreenSetter(Screen.WORLD_SELECTION));
		
		buttonPartition.set(4, buttonDefault.get())
		.setMessage("something else")
		.addMousePressListener(getScreenSetter(Screen.WORLD_SELECTION));
		
		
		buttonArea.set(1, buttonPartition);
		mainElement.set(1, buttonArea);
		
		return new MenuRenderer(mainElement, background);
	}
	
	
	public static MenuRenderer getWorldSelection(Image lava) {
		Element background = new Element().setImage(lava, false, 10);
		
		Partition mainElement = new Partition(2);
		mainElement.splitHorizontally();
		
		Partition<Partition> worldsHolder = new Partition<>(10, () -> {
			Partition p = new Partition(3);
			p.set(1,
				new Element()
					.setColor(new Color(0,0,0,100))
					.setOutlineColor(
						new UiColor(
							new Color(0,0,0,0),
							new Color(255,255,255))
						)
					.addMousePressListener(getScreenSetter(Screen.GAME))
			);
			p.splitHorizontally();
			return p.setWeight(1, 18);
		});
		worldsHolder.splitHorizontally();
		
		mainElement.setWeight(0,3);
		mainElement.set(0, worldsHolder);
		
		String[] worlds = Save.getWorlds();
		for (int i = 0; i < 10 && i < worlds.length; i++) {
			Element curButton = worldsHolder.get(i).get(1);
			curButton.setMessage(worlds[i]);
			String worldName = worlds[i];
			curButton.addMousePressListener(() -> {
				System.out.println("this should load the world " + worldName);
			});
		}
		
		return new MenuRenderer(mainElement, background);
	}
	
}
