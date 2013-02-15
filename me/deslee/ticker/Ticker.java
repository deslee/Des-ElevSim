package me.deslee.ticker;

import me.deslee.elevsim.main.Simulator;

public class Ticker implements Runnable {
	private Tickable tickable;
	public final String name;
	
	public Ticker(Tickable tickable, String name) {
		this.tickable = tickable;
		this.name = name;
	}
	
	@Override
	public void run() {
		try {
			tickable.tick();
		} catch(Exception e) {
			e.printStackTrace();
			Simulator.logger.log(this, "Stopping thread", 1);
			throw e;
		}
	}
	
	@Override
	public String toString() {
		return "Ticker " + name;
	}
	
}
