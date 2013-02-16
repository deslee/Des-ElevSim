package me.deslee.elevsim.main;

import java.util.ArrayList;

public class ElevSim {

	/**
	 * Patterns used:
	 * 		State pattern
	 * 		Statistics
	 * @throws InterruptedException 
	 * 
	 */
	static ArrayList<Simulation> simulations = new ArrayList<>();
	public static void main(String[] args) throws InterruptedException {
		simulations.add(new Simulation(20, 8));
		for (Simulation sim : simulations) {
			while (!sim.isEnded()) {
				synchronized(sim) {
					sim.wait();
				}
			}
		}
		System.exit(0);
	}

}
