package util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

// this shi's jank so there probably some sort of bad practice or whatever that's a vulnerability but idgaf at this point. tbf this runs on a client so like
public class MergeArray <T>{
	int newLength = 0;
	public List<T> arrays = new ArrayList<>();
	public IntFunction<T> arrayCreator; // got ai help here tbh
	Class<?> componentType;
	
//	for arrayCreator it needs primitive[]::new to be passed in ig
	public MergeArray(IntFunction<T> arrayCreator) {
		this.arrayCreator = arrayCreator;
	}
	
	public void addArray(T array) {
//		used ai to help with this aswell
		componentType = array.getClass().getComponentType();
		if (!componentType.isPrimitive()) {
			throw new IllegalArgumentException("must be a primitive array");
		}
		
		// have to use the whole Array.method thing cause T stores an array but T is not an array, ie. T = byte[] not T[] = byte
		newLength += Array.getLength(array);
		arrays.add(array);
	}
	
//	public <t> void addElement(t element) {
////	want to check that type of element is the same as the elements of the arrays but it seems super fricking complicated so i give up, ill just put it in an array before passing it in
//	}

//	i realize now that i can probably use system arraycopy for this or smthn but i dont feel like it rn
	public T getMergedArray() {
		T newArray = arrayCreator.apply(newLength);
		
		int i = 0;
		for (T arr : arrays) {
			for (int j = 0; j < Array.getLength(arr); j++) {
				
				Array.set(newArray, i, Array.get(arr, j));
				i++;
			}
		}
		
		return newArray;
	}
}
