package render;


import java.awt.*;

//	not currently using the type T but idk i'll leave it here for now
public interface Renderer<T extends Frame> {
	
	void render(Graphics g, int width, int height);
	
	/**	need this for checks with buttons to make sure they are currently displayed before running their code
	 * @param isVisible
	 */
	void updateVisibility(boolean isVisible);
	
}
