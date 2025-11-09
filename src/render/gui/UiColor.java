package render.gui;

import java.awt.*;

public class UiColor {
	public Color color;
	public Color hoverColor;
	
	
	
	public UiColor(Color color) {
		this.color = color;
	}
	public UiColor(Color color, Color hoverColor) {
		this.hoverColor = hoverColor;
		this.color = color;
	}
	
	
	
	public Color getColor(boolean isHovered) {
		if (isHovered && hoverColor != null) {
			return hoverColor;
		} else {
			return color;
		}
	}
	
}
