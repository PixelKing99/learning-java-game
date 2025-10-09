package game.input;

import game.SelectedDirections;

import java.awt.*;

public class InputState {
	public final SelectedDirections accelerating;
	public final boolean spacebar;
	public final Mouse mouse;
	
	InputState(SelectedDirections accelerating, boolean spacebar, Mouse mouse) {
		this.accelerating = accelerating;
		this.spacebar = spacebar;
		this.mouse = mouse;
	}
}
