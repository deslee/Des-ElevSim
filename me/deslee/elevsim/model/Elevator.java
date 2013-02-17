package me.deslee.elevsim.model;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import me.deslee.elevsim.main.Simulation;
import me.deslee.ticker.Tickable;

public class Elevator implements Tickable, SimObject, Comparable<Elevator> {

	private abstract class ElevatorState implements Tickable {}
	
	private class ElevatorIdle extends ElevatorState {
		
		private int waitTime = 0; // milliseconds waited
		
		private ElevatorIdle() {
			direction = Direction.NONE;
			if (stops.size() == 0) {
				committedDirection = Direction.NONE;
			}
		}
		
		@Override
		public void tick() {
			
			for (Person p : people) {
				p.tick();
			}
			
			if (waitTime < Simulation.ELEVATOR_IDLE_WAITTIME) { // wait for idlewaittime ms
				waitTime+=Simulation.TICKTIME;
			}
			else
			{
				if (!stops.isEmpty()) {
					// get first stop
					Iterator<Floor> it = stops.iterator();
					Floor closest = it.next();
	
					while(it.hasNext()) {
						Floor f = it.next();
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
	}
	private class ElevatorMoving extends ElevatorState {
		
		private double precentToNextFloor = 0.0;
		private int timeMoved = 0; // milliseconds moving
		
		private ElevatorMoving(Direction direction) {
			Elevator.this.direction = direction;
			if (committedDirection == Direction.NONE) {
				committedDirection = direction;
			}
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
					building.move(Elevator.this, direction);
				}
			}
		}
		
	}
	

	public final int ID;
	private Building building;
	private Direction committedDirection;
	private Floor currentFloor;
	private Direction direction;
	private Set<Person> people = new HashSet<>();
	private Simulation simulation;
	private ElevatorState state;
	private Set<Floor> stops = new HashSet<>();
	private Deque<Floor> stopsRequested = new ArrayDeque<>();
	
	public Elevator(Simulation simulation, Building building, int ID, Floor startingFloor) {
		this.simulation = simulation;
		this.ID = ID;
		this.building = building;
		this.currentFloor = startingFloor;
		this.committedDirection = Direction.NONE;
		direction = Direction.NONE;
		setIdleState();
	}

	@Override
	public int compareTo(Elevator e) {
		return ID - e.ID;
	}

	public Direction getCommittedDirection() {
		return committedDirection;
	}
	
	public Floor getCurrentFloor() {
		return currentFloor;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getDistanceFromFloor(Floor f) {
		return Math.abs(currentFloor.ID - f.ID);
	}
	
	public int getDistanceFromFloor(int floor) {
		return Math.abs(currentFloor.ID - floor);
	}
	
	public int getNumberOfStops() {
		return stops.size();
	}

	public Set<Person> getPeople() {
		return Collections.unmodifiableSet(people);
	}

	public double getPercentToNextFloor() {
		if (state instanceof ElevatorMoving) {
			return ((ElevatorMoving) state).precentToNextFloor;
		}
		else {
			return 0;
		}
	}

	public Set<Floor> getStops() {
		return Collections.unmodifiableSet(stops);
	}

	public boolean isMoving() {
		return state instanceof ElevatorMoving;
	}

	@Override
	public void tick() {
		synchronized(stopsRequested) {
			while(!stopsRequested.isEmpty()) {
				addStop(stopsRequested.removeFirst());
			}
		}
		
		for(Person p : people) {
			p.tick();
		}
		state.tick();
	}

	@Override
	public String toString() {
		return "Elevator " + ID;
	}
	
	protected Direction getDirectionToGo(Floor destination) {
		if (currentFloor.ID < destination.ID) {
			return Direction.UP;
		}
		else if (currentFloor.ID > destination.ID) {
			return Direction.DOWN;
		}
		else {
			return Direction.NONE;
		}
	}

	private void setIdleState() {
		simulation.logger.log(this, "Entered idle state on " + currentFloor + ". Stops: " + stops);
		this.state = new ElevatorIdle();
	}

	private void setMovingState(Direction direction) {
		simulation.logger.log(this, "Entered moving state. Stops: " + stops);
		this.state = new ElevatorMoving(direction);		
	}
	
	protected void addStop(Floor floor) {
		stops.add(floor);
		simulation.logger.log(this, "Stop to " + floor + " added. Stops: " + stops);
	}

	protected void setCurrentFloor(Floor currentFloor) {
		this.currentFloor = currentFloor;
	}
	
	protected void addPerson(Person p) {
		people.add(p);
	}
	
	protected void removePerson(Person p) {
		people.remove(p);
	}
	
	protected void setCommittedDirection(Direction d) {
		this.committedDirection = d;
	}

	public void pushButton(Floor destinationFloor) {
		synchronized(stopsRequested) {
			stopsRequested.addLast(destinationFloor);
		}
	}
}
