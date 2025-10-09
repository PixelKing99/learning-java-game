package util;

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
		double count = 0; // double so when calculating average its a double
		
		for (long tickTime : cache) {
			if (tickTime != 0) {
				sum += tickTime;
				count++;
			}
		}
		
		return sum / count;
	}
}