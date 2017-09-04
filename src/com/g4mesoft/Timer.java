package com.g4mesoft;

public class Timer {

	private static final long MS_PER_SEC = 1000L;
	private static final float MS_TO_SEC = 1.0f / (float)MS_PER_SEC;
	
	private float tps;
	private boolean debug;

	private long last;
	private long dMs; 
	private float dt;

	private int missingTicks;
	
	private TickCounter tpsCounter;
	private TickCounter fpsCounter;
	
	public Timer(float tps, boolean debug) {
		this.tps = tps;
		this.debug = debug;
		
		tpsCounter = new TickCounter();
		fpsCounter = new TickCounter();
	}
	
	public void initTimer() {
		last = System.currentTimeMillis();
		dMs = 0;
		dt = 0.0f;
	}

	public void update() {
		long now = System.currentTimeMillis();
		long deltaMs = now - last;
		last = now;
		dMs += deltaMs;
		dt += (float)deltaMs * MS_TO_SEC * tps;
		missingTicks = (int)dt;

		if (debug) {
			if (dMs >= MS_PER_SEC) {
				if (dMs >= MS_PER_SEC * 2) {
					dMs = 0;
				} else {
					dMs -= MS_PER_SEC;
				}
				
				tpsCounter.cycle();
				fpsCounter.cycle();
				System.out.println(String.format("%d tps, %d fps", tpsCounter.ticksLastCycle, fpsCounter.ticksLastCycle));
			}
		}
	}

	public float getDeltaTick() {
		return dt;
	}

	public int getMissingTicks() {
		return missingTicks;
	}

	public void tickPassed() {
		dt--;
		tpsCounter.tickPassed();
	}
	
	public void framePassed() {
		fpsCounter.tickPassed();
	}

	public void sleep(float minFps) {
		long msPassed = System.currentTimeMillis() - last;

		minFps = minFps - (minFps % tps);
		long msToSleep = (long)((float)MS_PER_SEC / minFps) - msPassed;
		if (msToSleep > 0) {
			try {
				Thread.sleep(msToSleep);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setTps(float tps) {
		this.tps = tps;
	}
	
	public void setDebug(boolean state) {
		debug = state;
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
