package me.deslee.elevsim.model;

import java.util.ArrayList;

import me.deslee.elevsim.exception.SimException;
import me.deslee.elevsim.main.Simulation;
import me.deslee.ticker.Tickable;

public class Elevator implements Tickable, SimObject {
	
	private abstract class ElevatorState implements Tickable {}
	private class ElevatorIdle extends ElevatorState {
		
		private int waitTime = 0; // milliseconds waited
		
		public ElevatorIdle() {
			Elevator.this.direction = Direction.NONE;
		}
		
		@Override
		public void tick() {
			if (waitTime < Simulation.ELEVATOR_IDLE_WAITTIME) { // wait for idlewaittime ms
				waitTime+=Simulation.TICKTIME;
				return;
			}
			if (!stops.isEmpty()) {
				// get first stop
				Floor closest = stops.get(0);
				for (Floor f : stops) {
					if (getDistanceFromFloor(f) < getDistanceFromFloor(closest)) {
						closest = f;
					}
				}
				
				Direction direction = getDirectionToGo(closest);
				setMovingState(direction);
			}
			else {
				// do nothing
			}
		}
	}
	
	private class ElevatorMoving extends ElevatorState {
		
		private double precentToNextFloor = 0.0;
		private int timeMoved = 0; // milliseconds moving
		
		public ElevatorMoving(Direction direction) {
			Elevator.this.direction = direction;
		}
				
		@Override
		public void tick() {
			// check to see if you are at a stop
			if (stops.contains(currentFloor)) {
				stops.remove(currentFloor);
				setIdleState();
			}
			else {
				// free to move
				timeMoved += Simulation.TICKTIME;
				precentToNextFloor = ((double)timeMoved) / Simulation.ELEVATOR_MILLISECONDS_PER_FLOOR;

				if (precentToNextFloor >= 1) {
					precentToNextFloor -= 1;
					timeMoved = 0;
					assert direction != Direction.DOWN;
					currentFloor = (direction == Direction.UP) ? building.getNextFloor(currentFloor) : building.getPrevFloor(currentFloor);
				}
			}
		}
		
	}

	private ArrayList<Person> people = new ArrayList<>();
	private ArrayList<Floor> stops = new ArrayList<>();
	private ElevatorState state;
	private Direction direction;
	private Floor currentFloor;
	private Building building;
	private Direction committedDirection;
	public final int ID;
	private Simulation simulation;
	public Elevator(Simulation simulation, Building building, int ID, Floor startingFloor) {
		this.simulation = simulation;
		this.ID = ID;
		this.building = building;
		this.currentFloor = startingFloor;
		this.committedDirection = ID % 2 == 0 ? Direction.UP : Direction.DOWN;
		direction = Direction.NONE;
		setIdleState();
	}

	@Override
	public void tick() {
		for(Person p : people) {
			p.tick();
		}
		state.tick();
	}

	public void addStop(Floor floor) {
		if (!stops.contains(floor)) {
			stops.add(floor);
			simulation.logger.log(this, "Stop to " + floor + " added. Stops: " + stops);
		}
	}
	
	private void setIdleState() {
		simulation.logger.log(this, "Entered idle state on " + currentFloor + ". Stops: " + stops);
		this.state = new ElevatorIdle();
	}
	
	public void setMovingState(Direction direction) {
		simulation.logger.log(this, "Entered moving state. Stops: " + stops);
		this.state = new ElevatorMoving(direction);		
	}
	
	private Direction getDirectionToGo(Floor destination) {
		if (currentFloor.ID < destination.ID) {
			return Direction.UP;
		}
		else if (currentFloor.ID > destination.ID) {
			return Direction.DOWN;
		}
		else {
			simulation.logger.log(this, "elevexcept", 1);
			throw new SimException("Compared same directions. Current direction not removed from stops.");
		}
	}
	
	@Override
	public String toString() {
		return "Elevator " + ID;
	}
	
	public double getPercentToNextFloor() {
		if (state instanceof ElevatorMoving) {
			return ((ElevatorMoving) state).precentToNextFloor;
		}
		else {
			return 0;
		}
	}

	public Floor getCurrentFloor() {
		return currentFloor;
	}

	public int getNumberOfStops() {
		return stops.size();
	}

	public boolean isMoving() {
		return state instanceof ElevatorMoving;
	}

	public Direction getDirection() {
		return direction;
	}

	public Direction getCommittedDirection() {
		return committedDirection;
	}
	
	public int getDistanceFromFloor(Floor f) {
		return Math.abs(currentFloor.ID - f.ID);
	}

	public int getDistanceFromFloor(int floor) {
		return Math.abs(currentFloor.ID - floor);
	}

	public ArrayList<Floor> getStops() {
		return stops;
	}
	
	public ArrayList<Person> getPeople() {
		return people;
	}
}
