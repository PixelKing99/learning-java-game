package util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class DynamicString {
	
	List<Boolean> isStringBools = new LinkedList<>();
	List<String> strings = new LinkedList<>();
	List<Supplier<String>> functions = new LinkedList<>();
	
	public DynamicString() {}
	
	public DynamicString(Supplier<String> function) {
		isStringBools.add(false);
		functions.add(function);
	}
	
	public DynamicString(String string) {
		isStringBools.add(true);
		strings.add(string);
	}
	
	public DynamicString add(Supplier<String> function) {
		isStringBools.add(false);
		functions.add(function);
		
		return this;
	}
	
	public DynamicString add(String string) {
		isStringBools.add(true);
		strings.add(string);
		
		return this;
	}
	
	public String get() {
		int stringsIndex = 0;
		int functionsIndex = 0;
		
		StringBuilder result = new StringBuilder();
		
		for (boolean isString : isStringBools) {
			if (isString) {
				result.append(strings.get(stringsIndex));
				stringsIndex++;
			} else {
				result.append(functions.get(functionsIndex).get());
				functionsIndex++;
			}
		}
		
		return result.toString();
	}
	
}
