package me.deslee.elevsim.model;

import me.deslee.elevsim.main.Simulation;
import me.deslee.ticker.Tickable;

public abstract class Person implements Tickable, SimObject  {
	
	protected Building building;
	protected Elevator currentElevator;
	protected Floor currentFloor;
	protected Simulation simulator;
	
	public Person(Simulation simulator, Building building, Floor startingFloor) {
		this.simulator = simulator;
		this.building = building;
		currentFloor = startingFloor;
	}

}
