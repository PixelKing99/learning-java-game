package render;

import game.*;
import game.entities.EntityType;
import render.game.GameRenderer;
import render.gui.*;
import saves.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.function.Consumer;

//	how should i do rendering stuff
//		could have every potential distinct screen have its own renderer class (although ig all menus share one)
//			eg. to render game with inventory the game renderer would run and then the inventory one
//				idk not really good but otherwise they are both in the same class which could get cluttered





public class Render {

	public static final int DEFAULT_FPS = 60;
	
	
	private static Screen currentScreen = Screen.MAIN_MENU;
	private static Screen screenToSet;
	private static final HashMap<Screen, Renderer<? extends Frame>> screenToRenderer = new HashMap<>();


	private static MenuRenderer mainMenuRenderer;
	private static MenuRenderer worldSelectionRenderer;
	private static GameRenderer gameRenderer;
	
	
	
	public Render() {
	}
	
	public void initializeRenderers() {

		mainMenuRenderer = new MainMenu();
		
		worldSelectionRenderer = new WorldSelection();
		
		
		screenToRenderer.put(Screen.MAIN_MENU, mainMenuRenderer);
		screenToRenderer.put(Screen.WORLD_SELECTION, worldSelectionRenderer);
		
		
		screenToRenderer.get(currentScreen).updateVisibility(true);
	}
	
	public static void initializeWorld() {
		gameRenderer = new GameRenderer();
		screenToRenderer.put(Screen.GAME, gameRenderer);
	}
	
	
	
	public void render(Graphics g, int width, int height) {
		screenToRenderer.get(currentScreen).render(g, width, height);
	}
	
	
	
//	these screen setting methods are so complicated to make sure that the screen is only set once per click event
	public static Runnable getScreenSetter(Screen screen) {
		return getScreenSetter(screen, true);
	}
	
	public static Runnable getScreenSetter(Screen screen, boolean requiresClick) {
		return () -> {
			if (requiresClick) {
				screenToSet = screen;
			} else {
				setScreenToRender(screen);
			}
		};
	}
	
	/**	should only be used if there is no interaction with UI involved with changing screens
	 * @param screen
	 */
	public static void setScreenToRender(Screen screen) {
		if (screen.equals(Render.currentScreen)) {
			throw new IllegalArgumentException("must change screen\ngiven: " + screen + "\ncurrent: " + Render.currentScreen);
		}
		screenToRenderer.get(currentScreen).updateVisibility(false);
		Render.currentScreen = screen;
		screenToRenderer.get(currentScreen).updateVisibility(true);
		
		
		if (screen.equals(Screen.GAME)) {
			Server.resume();
		} else if (Server.isRunning()) {
			Server.pause();
		}
	}
	
	public static Consumer<MouseEvent> getClickCompletionListener() {
		return (MouseEvent e) -> {
			if (screenToSet != null) {
				setScreenToRender(screenToSet);
				screenToSet = null;
			}
		};
	}
}
