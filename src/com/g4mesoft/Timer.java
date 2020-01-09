package com.g4mesoft;

import com.g4mesoft.math.MathUtils;

public class Timer {

	private static final long NS_PER_SEC = 1000000000L;
	private static final long MS_PER_SEC = 1000L;
	private static final long NS_PER_MS = NS_PER_SEC / MS_PER_SEC;
	
	private final Application application;
	
	private double tps;
	private double nsPerTick;

	private long lastNs;
	private double dt;

	private long lastMs;
	private int missingTicks;
	
	private TickCounter tpsCounter;
	private TickCounter fpsCounter;
	
	public Timer(Application application, double tps) {
		this.application = application;
		
		tpsCounter = new TickCounter();
		fpsCounter = new TickCounter();

		setTps(tps);
	}
	
	public void initTimer() {
		lastNs = System.nanoTime();
		lastMs = System.currentTimeMillis();
		dt = 1.0; // init to 1 tick on startup
	}

	public void update() {
		long nowNs = System.nanoTime();
		long deltaNs = nowNs - lastNs;
		lastNs = nowNs;
		
		dt += deltaNs / nsPerTick;
		missingTicks = (int)dt;
		dt -= missingTicks;
		
		if (application.isDebug())
			printDebugInfo();
	}
	
	private void printDebugInfo() {
		long nowMs = System.currentTimeMillis();
		if (nowMs - lastMs >= MS_PER_SEC) {
			lastMs += MS_PER_SEC;

			if (nowMs - lastMs >= MS_PER_SEC)
				lastMs = nowMs;
			
			tpsCounter.cycle();
			fpsCounter.cycle();
			
			int ticks = tpsCounter.ticksLastCycle;
			int frames = fpsCounter.ticksLastCycle;
			
			System.out.println(ticks + " tps, " + frames + " fps");
		}
	}

	public double getDeltaTick() {
		return dt;
	}

	public int getMissingTicks() {
		return missingTicks;
	}

	public void tickPassed() {
		tpsCounter.tickPassed();
	}
	
	public void framePassed() {
		fpsCounter.tickPassed();
	}

	public void sleep(double minFps) {
		long nsToSleep = MathUtils.min((long)((1.0 - dt) * NS_PER_SEC / tps), 
		                               (long)(NS_PER_SEC / minFps));
		
		nsToSleep -= System.nanoTime() - lastNs;
		
		if (nsToSleep > 0) {
			long msToSleep = nsToSleep / NS_PER_MS;
			nsToSleep %= NS_PER_MS;
			
			try {
				Thread.sleep(msToSleep, (int)nsToSleep);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setTps(double tps) {
		this.tps = tps;
		
		nsPerTick = NS_PER_SEC / tps;
	}
	
	public double getTps() {
		return tps;
	}
	
	private static class TickCounter {
		
		private long totalTicks;
		private long totalCycles;
		
		private int ticksLastCycle;
		
		private int ticks;

		public void tickPassed() {
			ticks++;
		}
		
		public void cycle() {
			totalTicks += ticks;
			totalCycles++;

			ticksLastCycle = ticks;
			ticks = 0;
		}
		
		@SuppressWarnings("unused")
		public float getAverageTps() {
			return (float)totalTicks / totalCycles;
		}
	}
}
