package game.input;

import render.Panel;
import render.Render;
import util.Direction;
import game.SelectedDirections;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UserInput implements KeyListener, MouseListener {
	private boolean spacebar = false;
	private boolean escapeKey = false;
	
	private SelectedDirections accelerating = new SelectedDirections();
	
	private Mouse mouse = new Mouse();
	
	private Supplier<Point> mouseCoordGetter;
	
	private List<Consumer<MouseEvent>> mousePressListeners = new LinkedList<>();
	private static Consumer<MouseEvent> mousePressCompletionListener = Render.getClickCompletionListener();
	
	private static UserInput userInput;
	
	private UserInput(Supplier<Point> mouseCoordGetter) {
		this.mouseCoordGetter = mouseCoordGetter;
	}
	
	
//	this is kinda the singleton pattern ig, i think it works here but i kinda used it just cause this is what they were talking about in class
	public static void init(Panel panel) {
		if (userInput != null) {
			throw new IllegalStateException("cannot call init if UserInput is already initialized");
		}
		
		UserInput.userInput = new UserInput(panel::getMousePosition);
		panel.addMouseListener(userInput);
		panel.addKeyListener(userInput);
		
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
//	doesnt do anything with stuff like arrow characters cause nothing is typed
	}
	
	
	
	
	@Override
	public void keyPressed(KeyEvent e) {
//	activates as long as pressing key
//		System.out.println(e.getKeyCode());
		if (e.getKeyCode() == (int) 'W' || e.getKeyCode() == 38) {
			accelerating.setTrue(Direction.UP);
		}
		if (e.getKeyCode() == (int) 'S' || e.getKeyCode() == 40) {
			accelerating.setTrue(Direction.DOWN);
		}
		if (e.getKeyCode() == (int) 'A' || e.getKeyCode() == 37) {
			accelerating.setTrue(Direction.LEFT);
		}
		if (e.getKeyCode() == (int) 'D' || e.getKeyCode() == 39) {
			accelerating.setTrue(Direction.RIGHT);
		}
		if (e.getKeyCode() == 32) { // spacebar
			spacebar = true;
		}
		if (e.getKeyCode() == 27) { // escapeKey
			escapeKey = true;
		}
	}
//	arrow keys
//	up 38
//	left 37
//	down 40
//	right 39
	
	
	
	
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == (int) 'W' || e.getKeyCode() == 38) {
			accelerating.setFalse(Direction.UP);
		}
		if (e.getKeyCode() == (int) 'S' || e.getKeyCode() == 40) {
			accelerating.setFalse(Direction.DOWN);
		}
		if (e.getKeyCode() == (int) 'A' || e.getKeyCode() == 37) {
			accelerating.setFalse(Direction.LEFT);
		}
		if (e.getKeyCode() == (int) 'D' || e.getKeyCode() == 39) {
			accelerating.setFalse(Direction.RIGHT);
		}
		if (e.getKeyCode() == 32) { // spacebar
			spacebar = false;
		}
		if (e.getKeyCode() == 27) { // escapeKey
			escapeKey = false;
		}
	}
	
	
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
//		requires press and release
	}
	
	
	
	
	@Override
	public void mousePressed(MouseEvent e) {
		int button = e.getButton();
		if (button == MouseEvent.BUTTON1) {
			mouse.left = true;
		} else if (button == MouseEvent.BUTTON3) {
			mouse.right = true;
		}
		
		for (Consumer<MouseEvent> a : mousePressListeners) {
			a.accept(e);
		}
//		this exists to check if a button that switches screens is clicked so it doesnt switch screens until all listeners are checked
//		since without this it could register a click on a button thats on the next screen in the same place,
//		if it's listener was after the first one's in the array
		mousePressCompletionListener.accept(e);
	}
	
	
	
	
	@Override
	public void mouseReleased(MouseEvent e) {
		int button = e.getButton();
		if (button == MouseEvent.BUTTON1) {
			mouse.left = false;
		} else if (button == MouseEvent.BUTTON3) {
			mouse.right = false;
		}
	}
	
	
	
	
	@Override
	public void mouseEntered(MouseEvent e) {
//		enter the window
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
//		exit the window
	}
	
	
//	dont really want to use MouseListener interface cause for the most part i only really care about mousePressed
	public static void addMousePressListener(Consumer<MouseEvent> mouseListener) {
		if (userInput == null) {
			throw new IllegalStateException("cannot add MousePressListener as UserInput has not been initialized");
		}
		
		userInput.mousePressListeners.add(mouseListener);
	}
	
	
	public static InputState getInputState() {
		if (userInput == null) {
			throw new IllegalStateException("cannot get InputState as UserInput has not been initialized");
		}
		
		
		userInput.mouse.setMousePos(userInput.mouseCoordGetter.get());
		return new InputState(userInput.accelerating, userInput.spacebar, userInput.escapeKey, userInput.mouse);
	}
	
}
