package util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// this shi's jank so there probably some sort of bad practice or whatever that's a vulnerability but idgaf at this point. tbf this runs on a client so like (it will actually run on the server but idk)
public class MergeArray <T>{
	private int newLength = 0;
	private List<T> arrays = new ArrayList<>();
	private Function<Integer, T> arrayCreator;
	private Class<?> componentType;
	
//	for arrayCreator it needs primitive[]::new to be passed in ig
	public MergeArray(Function<Integer, T> arrayCreator) {
		
		componentType = arrayCreator.apply(0).getClass().getComponentType();
		if (!componentType.isPrimitive()) {
			throw new IllegalArgumentException("must be a primitive array");
		}
		
		this.arrayCreator = arrayCreator;
	}
	
	public void addArray(T array) {
		Class<?> componentType = array.getClass().getComponentType();
		if (!componentType.isPrimitive()) {
			throw new IllegalArgumentException("must be a primitive array");
		}
		if (!componentType.equals(this.componentType)) {
			throw new IllegalArgumentException("passed array of type: " + componentType.getName() + "\nMergeArray currently storing type: " + this.componentType.getName());
		}
		
		// have to use the whole Array.method thing cause T is an array, ie. T = byte[] not T = byte -> T[] = byte[]
		newLength += Array.getLength(array);
		arrays.add(array);
	}

//	havent actually tested this method so idk if it works
	public <t> void addElement(t element) {

		Class<?> componentType = element.getClass().getComponentType();
		if (!componentType.equals(this.componentType)) {
			throw new IllegalArgumentException("passed element of type: " + componentType.getName() + "\nMergeArray currently storing type: " + this.componentType.getName());
		}
		
//		creating and adding a one element array to 'arrays'
		newLength += 1;
		
		T arr = arrayCreator.apply(1);
		Array.set(arr, 0, element);
		
		arrays.add(arr);
	}


	public T getMergedArray() {
		T newArray = arrayCreator.apply(newLength);
		
		int i = 0;
		for (T arr : arrays) {
			
			System.arraycopy(arr, 0, newArray, i, Array.getLength(arr));
			i += Array.getLength(arr);
		}
		
		return newArray;
	}
	
//	only here if i need it for debug or smthn
	public String getComponentTypeName() {
		return componentType.getName();
	}
}
