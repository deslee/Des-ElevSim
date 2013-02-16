package me.deslee.elevsim.model;

import me.deslee.elevsim.main.Simulation;
import me.deslee.ticker.Tickable;

public class AIPerson extends Person {
	
	private abstract class PersonState implements Tickable {}
	private class OnFloorState extends PersonState {

		@Override
		public void tick() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class OnElevatorState extends PersonState {

		@Override
		public void tick() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private PersonState state;
	private final int ID;
	
	public AIPerson(Simulation simulator, Building building, Floor startingFloor, int ID) {
		super(simulator, building, startingFloor);
		this.ID = ID;
		setOnFloorState();
	}

	@Override
	public void tick() {
		state.tick();
	}
	
	@Override
	public String toString() {
		return "Person " + ID;
	}
	
	private void setOnFloorState() {
		state = new OnFloorState();
		simulator.logger.log(this, "Idle on " + currentFloor, 3);
	}
	
	private void setOnElevatorState() {
		state = new OnElevatorState();
		simulator.logger.log(this, "Went on " + currentElevator, 3);
	}

}
