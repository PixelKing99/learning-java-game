package render.gui;

import game.input.InputState;
import game.input.UserInput;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Element {
	
	private String message;
	private UiColor messageColor = new UiColor(new Color(255,255,255));
	private Image image;
	private boolean fitVertically = false;
	private int imagesPerSide = 0;
	private UiColor color;
	private UiColor outlineColor;
	private int outlineWidth = 1;
	
//	these are here to store the most recent value each of these so that listeners can use them (since their checks are not synced with each frame)
	private int x;
	private int y;
	private int width;
	private int height;
	
	
	private boolean isVisible = false;
	
	
	public Element() {
	
	}
	
	public Element setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public Element setMessageColor(Color messageColor) {
		this.messageColor = new UiColor(messageColor);
		return this;
	}
	public Element setMessageColor(UiColor messageColor) {
		this.messageColor = messageColor;
		return this;
	}
	
	public Element setImage(Image image) {
		this.image = image;
		return this;
	}
	
	public Element setImage(Image image, boolean fitVertically, int imagesPerSide) {
		this.image = image;
		this.fitVertically = fitVertically;
		this.imagesPerSide = imagesPerSide;
		return this;
	}
	
	public Element setColor(UiColor color) {
		this.color = color;
		return this;
	}
	
	public Element setColor(Color color) {
		this.color = new UiColor(color);
		return this;
	}
	
	public Element setOutlineColor(UiColor outlineColor) {
		this.outlineColor = outlineColor;
		return this;
	}
	
	public Element setOutlineColor(Color outlineColor) {
		this.outlineColor = new UiColor(outlineColor);
		return this;
	}
	
	public Element setOutlineWidth(int outlineWidth) {
		this.outlineWidth = outlineWidth;
		return this;
	}
	
	public Element addMousePressListener(Runnable onClick) {
		UserInput.addMousePressListener((MouseEvent e) -> {
			if (!isVisible) {
				return;
			}
//			button1 == left click
			if (e.getButton() != MouseEvent.BUTTON1) {
				return;
			}
			
			boolean mouseOverElement = e.getX() >= x && e.getX() <= x + width && e.getY() >= y && e.getY() <= y + height;
			if (mouseOverElement) {
				onClick.run();
			}
		});
		
		return this;
	}
	
	
	/**	for checking if the user is clicking on an element so it doesnt register for an element which isnt being displayed
	 * @param isVisible
	 */
	public void updateVisibility(boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	
	public void renderRepeatingImage(Graphics g, int x, int y, int width, int height) {
		
		double imageSize;
		BiConsumer<Integer, Integer> draw;
		
		double dependentImageSize;
		int dependentDimension;
		
		if (fitVertically) {
			imageSize = (double) width / imagesPerSide;
			
			dependentImageSize = (int) Math.ceil(imageSize / image.getWidth(null) * image.getHeight(null));
			dependentDimension = height;
			
			draw = (Integer xVal, Integer yVal) -> {
				g.drawImage(image, xVal, yVal, (int) Math.ceil(imageSize), (int) Math.ceil(dependentImageSize), null);
			};
			
		} else {
			imageSize = (double) height / imagesPerSide;
			
			dependentImageSize = (int) Math.ceil(imageSize / image.getHeight(null) * image.getWidth(null));
			dependentDimension = width;
			
			draw = (Integer yVal, Integer xVal) -> {
				g.drawImage(image, xVal, yVal, (int) Math.ceil(dependentImageSize), (int) Math.ceil(imageSize), null);
			};
		}
		
		for (int i = 0; i < imagesPerSide; i++) {
			int coord1 = (int) (imageSize * i);
			
			for (double j = 0; j < dependentDimension; j+=dependentImageSize) {
				int coord2 = (int) j;
				
				draw.accept(coord1, coord2);
			}
		}
	}
	
	
	public void render(Graphics g, int x, int y, int width, int height, InputState inputState) {
//		instance attributes are for listeners to use since they arent synchronized with the frames
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		
		boolean mouseOverElement = inputState.mouse.x >= x && inputState.mouse.x <= x + width && inputState.mouse.y >= y && inputState.mouse.y <= y + height;
		
		if (color != null) {
			g.setColor(color.getColor(mouseOverElement));
			g.fillRect(x, y, width, height);
		}
		
		if (image != null) {
			
			if (imagesPerSide != 0) {
				renderRepeatingImage(g, x, y, width, height);
			} else {
//				renders the image as large as possible within the given element without stretching the image
//				currently centers the image, may have to add options later for aligning it up/down and left/right
				
				if (image.getWidth(null) / image.getHeight(null) > width / height) {
					double scale = (double) width / image.getWidth(null);
					int newHeight = (int) Math.round(image.getHeight(null) * scale);
					
					g.drawImage(image, x, y + (height - newHeight) / 2, width, newHeight, null);
				} else {
					double scale = (double) height / image.getHeight(null);
					int newWidth = (int) Math.round(image.getWidth(null) * scale);
					
					g.drawImage(image, x + (width - newWidth) / 2, y, newWidth, height, null);
				}
			}
			
		}
		
		if (message != null) {
			Font font = new Font(null, Font.PLAIN, 20);
			g.setFont(font);
			Rectangle2D rect = font.getStringBounds(message, g.getFontMetrics().getFontRenderContext());
			
			
			double newX = x + (width - rect.getWidth()) / 2;
			double newY = y + (height - rect.getHeight()) / 2;
			
//			that the size of the string will be treated as
//			g.setColor(new Color(255,0,0));
//			g.fillRect((int) Math.round(newX), (int) Math.round(newY), (int) Math.round(rect.getWidth()), (int) Math.round(rect.getHeight()));
			
			g.setColor(messageColor.getColor(mouseOverElement));
			g.drawString(message, (int) Math.round(newX), (int) Math.round(newY - rect.getY()));
		}
		
		if (outlineColor != null) {
			
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(outlineWidth));
			
			g2d.setColor(outlineColor.getColor(mouseOverElement));
			g2d.drawRect(x, y+1, width-1, height-2);
		}
		
//		was using this to make sure text is centered
//		if (color != null) {
//			g.setColor(new Color(255, 0, 255));
//			g.drawLine(x + (width/2), y, x + (width/2), y+height);
//			g.drawLine(x, y + (height/2), x + width, y+(height/2));
//		}
	}
}
