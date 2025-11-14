package render.gui;

import game.input.InputState;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Supplier;

//	all of the generics stuff in here is so fcked idk how bad of practice everything here is but i hope it doesnt cause issues later
public class Partition<T extends Element> extends Element {
	private T[] elements; // may have to make ArrayList but rn we'll make it fixed length
	private int[] weights;
	private int totalWeight;
	
	private Boolean splitVertically = true;
	
	public Partition(int size) {
		init(size, () -> {return (T) new Element();});
	}
	
	
	public Partition(int size, Supplier<T> defaultElementSupplier) {
		init(size, defaultElementSupplier);
	}
	
	
	
	private void init(int size, Supplier<T> defaultElementSupplier) {
		
		T[] elements = (T[]) Array.newInstance(defaultElementSupplier.get().getClass(), size);
		this.elements = elements;
		
//		cannot use Array.fill cause it only assigns the reference and doesnt create a new object
		for (int i = 0; i < size; i++) {
			elements[i] = defaultElementSupplier.get();
		}
		
		this.weights = new int[size];
		totalWeight = size;
		Arrays.fill(weights, 1);
	}
	
	
	
	
	public Partition(T[] elements) {
		this.elements = elements;
		
		this.weights = new int[elements.length];
		Arrays.fill(weights, 1);
		totalWeight = elements.length;
	}
	
	
	
	
	public <K extends Element> K get(int index) {
		return (K) elements[index];
	}
	
	
	
//	the generics is so that it will the object it was set to without simplifying it to an Element
//	ie. if set to a Partition it will return that partition so partition methods can be used on it, not just element methods
	public <K extends Element> K set(int index, K element) {
		elements[index] = (T) element;
		return (K) elements[index];
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
	public void updateVisibility(boolean isVisible) {
		super.updateVisibility(isVisible);
		for (T element : elements) {
			element.updateVisibility(isVisible);
		}
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
