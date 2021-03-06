package me.deslee.ticker;

import me.deslee.elevsim.main.Simulation;

public class Ticker implements Runnable {
	private Tickable tickable;
	public final String name;
	private Simulation simulation;
	private boolean running = true;
	
	public Ticker(Simulation simulation, Tickable tickable, String name) {
		this.simulation = simulation;
		this.tickable = tickable;
		this.name = name;
	}
	
	@Override
	public void run() {
		synchronized(this) {
			if (running) {
				try {
					tickable.tick();
				} catch(Exception e) {
					e.printStackTrace();
					simulation.logger.log(this, "Exception encountered. Stopping thread", 1);
					throw e;
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return "Ticker " + name;
	}
	
	public void pause() {
		synchronized(this) {
			running = false;
		}
	}
	
	public void resume() {
		synchronized(this) {
			running = true;
			this.notifyAll();
		}
	}
	
	
	
}
