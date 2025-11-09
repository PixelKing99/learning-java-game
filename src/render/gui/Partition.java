package render.gui;

import game.input.InputState;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Supplier;

public class Partition extends Element {
	private Element[] elements; // may have to make ArrayList but rn we'll make it fixed length
	private int[] weights;
	private int totalWeight;
	
	public Boolean splitVertically = true;
	
	public Partition(int size) {
		init(size, Element::new);
	}
	
	
	public Partition(int size, Supplier<Element> defaultElementSupplier) {
		init(size, defaultElementSupplier);
	}
	
	
	
	private void init(int size, Supplier<Element> defaultElementSupplier) {
		
		this.elements = new Element[size];
		
//		cannot use Array.fill cause it only assigns the reference and doesnt create a new object
		for (int i = 0; i < size; i++) {
			elements[i] = defaultElementSupplier.get();
		}
		
		this.weights = new int[size];
		totalWeight = size;
		Arrays.fill(weights, 1);
	}
	
	
	
	
	public Partition(Element[] elements) {
		this.elements = elements;
		
		this.weights = new int[elements.length];
		Arrays.fill(weights, 1);
		totalWeight = elements.length;
	}
	
	
	
	
	public Element get(int index) {
		return elements[index];
	}
	
	
	
//	the generics is so that it will the object it was set to without simplifying it to an Element
//	ie. if set to a Partition it will return that partition so partition methods can be used on it, not just element methods
	public <T extends Element> T set(int index, T element) {
		elements[index] = element;
		return (T) elements[index];
	}
	
	
	
	public Partition setWeight(int index, int weight) {
		totalWeight -= weights[index];
		totalWeight += weight;
		weights[index] = weight;
		return this;
	}
	
	
	
	
	public Partition splitHorizontally() {
		splitVertically = false;
		return this;
	}
	
	
	
	
	@Override
	public void render(Graphics g, int x, int y, int width, int height, InputState inputState) {
		super.render(g, x, y, width, height, inputState);
		
		int totalPrevWeight = 0;
		for (int i = 0; i < elements.length; i++) {
			int newX = x;
			int newY = y;
			int newWidth = width;
			int newHeight = height;
			
			if (splitVertically) {
				newX += (int) (width / (double) totalWeight * totalPrevWeight);
				newWidth = (int) Math.ceil(width / (double) totalWeight * weights[i]);
				
			} else {
				newY += (int) (height / (double) totalWeight * totalPrevWeight);
				newHeight = (int) Math.ceil(height / (double) totalWeight * weights[i]);
			}
			
			
			elements[i].render(g, newX, newY, newWidth, newHeight, inputState);
			totalPrevWeight += weights[i];
		}
	}
}
