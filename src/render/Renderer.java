package render;

import game.input.InputState;
import game.input.UserInput;

import java.awt.*;
import java.util.function.Supplier;

//	not currently using the type T but idk i'll leave it here for now
public interface Renderer<T extends Frame> {
	
	void render(Graphics g, int width, int height);
	
}
