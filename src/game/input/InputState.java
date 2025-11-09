package game.input;

import game.SelectedDirections;

import java.awt.*;

public class InputState {
	public final SelectedDirections accelerating;
	public final boolean spacebar;
	public final boolean escapeKey;
	public final Mouse mouse;
	
	public InputState(SelectedDirections accelerating, boolean spacebar, boolean escapeKey, Mouse mouse) {
		this.accelerating = accelerating;
		this.spacebar = spacebar;
		this.escapeKey = escapeKey;
		this.mouse = mouse;
	}
}
