package render.gui;

import game.Server;
import render.Render;
import render.Screen;
import saves.Save;

import java.awt.*;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.zip.DataFormatException;

import static render.Render.getScreenSetter;

public class MenuBuilder {
	
	//		Supplier<Element> menuDefault = () -> new Element().setOutlineColor(new Color(100,0,0));
	private static final Supplier<Element> menuDefault = () -> new Element();
	private static final Supplier<Element> buttonDefault = () -> new Element().setColor(new UiColor(new Color(30, 30, 30, 180), new Color(30, 30, 30, 220)));
	
	private static final Runnable setScreenToGame = getScreenSetter(Screen.GAME);
	
	
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
		
		Partition<Partition> worldsHolder = new Partition<>(3);
		worldsHolder.setWeight(1, 2);
		worldsHolder.set(1,
			new Partition<>(10, () -> {
				Partition p = new Partition(3);
				p.set(1,
					new Element()
						.setColor(new Color(0,0,0,100))
						.setOutlineColor(
							new UiColor(
								new Color(0,0,0,0),
								new Color(255,255,255))
							)
				);
				p.splitHorizontally();
				return p.setWeight(1, 18);
			})
		);
		worldsHolder.<Partition>get(1).splitHorizontally();
		
		mainElement.setWeight(0,3);
		mainElement.set(0, worldsHolder);
		
		String[] worlds = Save.getWorlds();
		for (int i = 0; i < 10 && i < worlds.length; i++) {
			Element curButton = worldsHolder.<Partition<Partition>>get(1).<Partition>get(i).get(1);
			curButton.setMessage(worlds[i]);
			String worldName = worlds[i];
			curButton.addMousePressListener(() -> {
				try {
					Server.start(worldName);
					Render.initializeWorld();
					setScreenToGame.run();
				} catch (DataFormatException e) {
					throw new RuntimeException("the worlds file is incorrectly formatted\n" + e);
				} catch (IOException e) {
					throw new RuntimeException("something went wrong while loading the world\n" + e);
				}
			});
		}
		
		
		
		return new MenuRenderer(mainElement, background);
	}
	
}
