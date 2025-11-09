package util;

import java.util.Arrays;

// is more general than its name but idk what to call it, this is just what its used for
public class PassedTimeCache {
	private long[] cache;
	private int index = 0;
	
//	only stores the last 'size' values
	public PassedTimeCache(int size) {
		this.cache = new long[size];
	}
	
	public void add(long value) {
		cache[index] = value;
		index += 1;
		if (index > cache.length - 1) {
			index = 0;
		}
	}
	
	public double getAverage() {
		long sum = 0;
		int count = 0;
		
		for (long tickTime : cache) {
			if (tickTime != 0) {
				sum += tickTime;
				count++;
			}
		}
		
// 		this basically says that if there are no changes in time registered then the space between each interval is assumed to be the max possible
//		it would be more accurate to assume the change in time is the time between the start of the program and now but i cant be bothered since this hardly matters
		if (count == 0) {
			return Integer.MAX_VALUE;
		} else {
			return sum / (double) count;
		}
	}
	
	public void reset() {
		Arrays.fill(cache, 0);
	}
}