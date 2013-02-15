package me.deslee.elevsim.model;

import java.util.ArrayList;

import me.deslee.elevsim.exception.SimException;
import me.deslee.elevsim.main.Simulator;
import me.deslee.ticker.Tickable;

public class Elevator implements Tickable {
	
	private abstract class ElevatorState implements Tickable {}
	private class ElevatorIdle extends ElevatorState {
		
		private int waitTime = 0; // milliseconds waited
		
		public ElevatorIdle() {
			Elevator.this.direction = Direction.NONE;
		}
		
		@Override
		public void tick() {
			if (waitTime < IDLE_WAITTIME) { // wait for idlewaittime ms
				waitTime+=Simulator.TICKTIME;
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
				timeMoved += Simulator.TICKTIME;
				precentToNextFloor = timeMoved / MILLISECONDS_PER_FLOOR;

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
	private final int ID;

	public static final int MAX_WEIGHT = 500; // Kilograms
	public static final int IDLE_WAITTIME = 3000; //waitTime in millis
	public static final double MILLISECONDS_PER_FLOOR = 1000; // ms to move up a floor
	
	
	public Elevator(Building building, int ID, Floor startingFloor) {
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
		}
	}
	
	private void setIdleState() {
		Simulator.logger.log(this, "Entered idle state on " + currentFloor + ". Stops: " + stops, 2);
		this.state = new ElevatorIdle();
	}
	
	public void setMovingState(Direction direction) {
		Simulator.logger.log(this, "Entered moving state. Stops: " + stops, 2);
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
			Simulator.logger.log(this, "elevexcept", 1);
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

	public Integer[] getStops() {
		Integer[] ints = new Integer[stops.size()];
		for (int i = 0; i < ints.length; ++i) {
			ints[i] = stops.get(i).ID;
		}
		return ints;
		
	}
}
