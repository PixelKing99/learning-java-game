package util;

public class ConcurrentRateLoop implements Runnable {
	private int loopsPerSecond;
	private double actualLoopsPerSecond = 0;
	private int availableTime;
	private long prevStartOfCode = System.currentTimeMillis();
	private PassedTimeCache loopSpeedCache;
	private Runnable runnable;
	private String threadName;
	private long sleepTime;
	private boolean shouldLoop = true;
	
	
	
	public ConcurrentRateLoop(int loopsPerSecond, Runnable run, String threadName) {
		this.loopsPerSecond = loopsPerSecond;
		
//		stores the last 'loopsPerSecond' loop times to calculate the actual LPS
		loopSpeedCache = new PassedTimeCache(this.loopsPerSecond);
		
//		the amount of time a frame can take to run and still be running 'loopPerSecond' times per seconds
//		only able to have precision to the ms which can cause it to not be 100% accurate
		availableTime = (int) Math.round(1000.0 / this.loopsPerSecond) - 1; // has -1 to keep acutal LPS close to the desired value since it tends to be lower due to lack of precision
		
//		function that gets called every loop
		this.runnable = run;
		
		
		this.threadName = threadName;
		
		Thread thread = new Thread(this, this.threadName);
		thread.start();
	}
	
	
	
	
	private void loop() {
		
		limitSpeed();
		
		
		try {
			runnable.run();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	
	
	
	private void limitSpeed() {
//		finds the amount of time the code took to run and finds the amount of time to sleep until the start of the next frame
		long startOfLoop = System.currentTimeMillis(); // can have a precision of only 10ms apparently
		long codeRunTime = startOfLoop - prevStartOfCode;
		sleepTime = availableTime - codeRunTime;
		
//		sleeps
		try {
			Thread.sleep(Math.max(sleepTime, 0));
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
		
//		stores data used to find loops per second
		long startOfCode = System.currentTimeMillis();
		loopSpeedCache.add(startOfCode - prevStartOfCode);
		
		prevStartOfCode = startOfCode;
		
		
		actualLoopsPerSecond = 1000 / loopSpeedCache.getAverage();
	}
	
	
	
	
	public String getDebugData() {
//		should really just be returning LPS but im just using it for debugging rn
		return String.format("%.1f", actualLoopsPerSecond) + "  " + availableTime + "  " + sleepTime;
	}
	
	
	
//	could be used to have some sort of "safe exit" or smthn but idk how to set that up and i cant be bothered to loop into it rn
	public void stopLoop() {
		shouldLoop = false;
	}
	
	
	
	
	@Override
	public void run() {
		while (shouldLoop) {
			loop();
		}
	}
}
