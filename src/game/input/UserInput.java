package game.input;

import game.Direction;
import game.SelectedDirections;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Supplier;

public class UserInput implements KeyListener, MouseListener {
	private boolean spacebar = false;
	
	private SelectedDirections accelerating = new SelectedDirections();
	
	private Mouse mouse = new Mouse();
	
	private Supplier<Point> mouseCoordGetter;
	
	public UserInput(Supplier<Point> mouseCoordGetter) {
		this.mouseCoordGetter = mouseCoordGetter;
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
	}
	
	
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
//		requires press and release
	}
	
	
	
	
	@Override
	public void mousePressed(MouseEvent e) {
		int button = e.getButton();
		if (button == 1) {
			mouse.left = true;
		} else if (button == 3) {
			mouse.right = true;
		}
	}
	
	
	
	
	@Override
	public void mouseReleased(MouseEvent e) {
		int button = e.getButton();
		if (button == 1) {
			mouse.left = false;
		} else if (button == 3) {
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
	
	
	
	
	public InputState getInputState() {
		mouse.setMousePos(mouseCoordGetter.get());
		return new InputState(accelerating, spacebar, mouse);
	}
	
}
