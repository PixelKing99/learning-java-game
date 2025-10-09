package game.input;

import java.awt.*;

public class Mouse {
	public boolean left;
	public boolean right;
	public int x;
	public int y;
	
	public boolean mouseOutsideWindow;
	
	public Mouse() {
	
	}
	
	public Mouse(boolean left, boolean right, Point mousePos) {
		this.left = left;
		this.right = right;
		setMousePos(mousePos);
	}
	
	public void setMousePos(Point pos) {
		if (pos != null) {
			this.mouseOutsideWindow = false;
			this.x = pos.x;
			this.y = pos.y;
		} else {
			mouseOutsideWindow = true;
		}
		
	}
}