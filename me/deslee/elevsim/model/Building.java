package me.deslee.elevsim.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import me.deslee.elevsim.exception.SimException;
import me.deslee.elevsim.main.Simulation;
import me.deslee.ticker.Tickable;

public class Building implements Tickable {

	private class Request {
		private Direction direction;
		private Floor floor;
		private Request(Floor f, Direction d) {
			floor = f;
			direction = d;
		}
		
	}
	
	private List<Elevator> elevators = new ArrayList<>();
	private List<Floor> floors = new ArrayList<>();
	private Deque<Request> requests = new ArrayDeque<>();
	private int peopleAdded = 0;
	private Simulation simulation;
	private Integer personQueue = 0;
	
	public Building(Simulation simulation, int numFloors, int numElevators) {
		this.simulation = simulation;
		
		// instantiate the objects
		for (int i = 0; i < numFloors; ++i) {
			floors.add(new Floor(this, i));
		}
		for (int i = 0; i < numElevators; ++i) {
			Elevator e = new Elevator(this.simulation, this, i, floors.get(0));
			elevators.add(e);
			floors.get(0).addElevator(e);
		}
	}
	
	public void addPerson() {
		synchronized(personQueue) {
			personQueue++;
		}
	}
	
	public List<Elevator> getElevators() {
		return Collections.unmodifiableList(elevators);
	}
	
	public List<Floor> getFloors() {
		return Collections.unmodifiableList(floors);
	}
	
	public Floor getNextFloor(Floor f) {
		int id = f.ID;
		id++;
		try {
			return floors.get(id);
		} catch (IndexOutOfBoundsException e) {
			throw new SimException("Floor " + id + " is out of bounds.");
		}
	}
	
	public Floor getPrevFloor(Floor f) {
		int id = f.ID;
		id--;
		try {
			return floors.get(id);
		} catch (IndexOutOfBoundsException e) {
			throw new SimException("Floor " + id + " is out of bounds.");
		}
	}

	public void request(int floor, Direction direction) {

		synchronized(requests) {
			Request r = new Request(floors.get(floor), direction);
			if (!requests.contains(r)) {
				requests.addLast(r);
			}
		}
	}

	@Override
	public void tick() {
		
		synchronized(personQueue) {
			while(personQueue > 0) {
				personQueue--;
				floors.get(0).addPerson(new AIPerson(simulation, this, floors.get(0), peopleAdded++));
			}
		}
		
		synchronized(requests) {
			while (!requests.isEmpty()) {
				Request r = requests.removeFirst();
				processRequest(r);
			}
		}
		
		for(Floor f : floors) {
			f.tick();
		}
		for (Elevator e : elevators) {
			e.tick();
		}
	}

	private void processRequest(Request r) {
		if (r.floor.ID == 0 && r.direction == Direction.DOWN) {
			throw new SimException("Trying to go Down on Floor 0");
		}
		else if (r.floor.ID == floors.size()-1 && r.direction == Direction.UP) {
			throw new SimException("Trying to go Up on highest floor.");
		}
		else {
			// Elevators not doing anything get top priority.
			for (Elevator e: elevators) {
				if (e.getNumberOfStops() == 0) {
					e.addStop(r.floor);
					return;
				}
			}

			Elevator bestElevator = elevators.get(0);
			
			for (Elevator e: elevators) {
				if (e.getDistanceFromFloor(r.floor) < bestElevator.getDistanceFromFloor(r.floor)) {
					bestElevator = e;
				}
				else if (e.getDistanceFromFloor(r.floor) == bestElevator.getDistanceFromFloor(r.floor)) {
					bestElevator = e.getNumberOfStops() < bestElevator.getNumberOfStops() ? e : bestElevator;
				}
			}
			
			bestElevator.addStop(r.floor);
		}
	}

	protected void move(Elevator elevator, Direction direction) {
		Floor current = elevator.getCurrentFloor();
		Floor destination = (direction == Direction.UP) ? getNextFloor(current) : getPrevFloor(current);
		elevator.setCurrentFloor(destination);
		current.removeElevator(elevator);
		destination.addElevator(elevator);
	}
}