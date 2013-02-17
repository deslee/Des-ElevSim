package me.deslee.elevsim.model;

import java.util.Set;

import me.deslee.elevsim.main.Simulation;
import me.deslee.ticker.Tickable;

public class AIPerson extends Person {
	
	private abstract class PersonState implements Tickable {}
	private class OnFloorState extends PersonState {
		
		int state = NOTHING;
		
		public static final int NOTHING = 0;
		public static final int CHOOSING = 1;
		public static final int WAITING = 2;
		
		private int waitTime = (int) (Math.random() * Simulation.AI_IDLE_WAITING_MAX);
		
		@Override
		public void tick() {
			if (state == NOTHING) {
				if (waitTime >= 0) {
					waitTime -= Simulation.TICKTIME;
				}
				else {
					state = CHOOSING;
				}
			}
			else if (state == CHOOSING) {
				destinationFloor = building.getFloors().get((int) (Math.random() * building.getFloors().size()));
				// determine what direction that is.
				Direction destinationDirection;
				if (destinationFloor.ID > currentFloor.ID) {
					destinationDirection = Direction.UP;
				}
				else if (destinationFloor.ID < currentFloor.ID) {
					destinationDirection = Direction.DOWN;
				}
				else {
					return; //bad floor. pick another one next tick
				}
				
				building.pushButton(AIPerson.this, destinationDirection);
				state = WAITING;
				
			}
			else if (state == WAITING) {
				Set<Elevator> elevators = currentFloor.getElevators();
				for (Elevator e : elevators) {
					if (e.getDirection() == Direction.NONE && 
							(e.getCommittedDirection() == destinationDirection 
							|| e.getCommittedDirection() == Direction.NONE)
						) {
						setOnElevatorState(e);
						break;
					}
				}
			}
		}
		
	}
	
	private class OnElevatorState extends PersonState {
		private OnElevatorState() {
			currentElevator.pushButton(destinationFloor);	
		}
		
		@Override
		public void tick() {
			// check if the person's elevator is equal to the destination floor
			if (currentElevator.getDirection() == Direction.NONE &&
					currentElevator.getCurrentFloor().ID == destinationFloor.ID) {
				moveToFloor();
				setOnFloorState();
			}
		}
		
	}
	
	private PersonState state;
	private Floor destinationFloor;
	private Direction destinationDirection;
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
	
	private void moveToFloor() {
		destinationFloor.addPerson(this);
		currentElevator.removePerson(this);
		currentElevator = null;
		currentFloor = destinationFloor;
		destinationFloor = null;
		destinationDirection = null;
	}
	
	private void setOnElevatorState(Elevator e) {
		e.addPerson(this);
		currentFloor.removePerson(this);
		currentFloor = null;
		currentElevator = e;
		state = new OnElevatorState();
		simulator.logger.log(this, "Went on " + currentElevator, 3);
	}
	
	public Direction getDirectionTo(Floor f) {
		if (f.ID < currentFloor.ID) {
			return Direction.DOWN;
		}
		else if (f.ID > currentFloor.ID){ 
			return Direction.UP;
		}
		else {
			return Direction.NONE;
		}
	}
	
	public PersonState getState() {
		return state;
	}

}
