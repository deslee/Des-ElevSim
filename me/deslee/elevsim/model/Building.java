package me.deslee.elevsim.model;

import java.util.ArrayDeque;
import java.util.Deque;

import me.deslee.elevsim.exception.SimException;
import me.deslee.elevsim.main.Simulation;
import me.deslee.ticker.Tickable;

public class Building implements Tickable {

	private class Request {
		private Floor floor;
		private Direction direction;
		private Request(Floor f, Direction d) {
			floor = f;
			direction = d;
		}
		
	}
	
	private Floor[] floors;
	private Elevator[] elevators;
	private Deque<Request> requests = new ArrayDeque<>();
	private Simulation simulation;
	public Building(Simulation simulation, int numFloors, int numElevators) {
		this.simulation = simulation;
		floors = new Floor[numFloors];
		elevators = new Elevator[numElevators];
		
		// instantiate the objects
		for (int i = 0; i < numFloors; ++i) {
			floors[i] = new Floor(this, i);
		}
		for (int i = 0; i < numElevators; ++i) {
			elevators[i] = new Elevator(this.simulation, this, i, floors[0]);
		}
	}
	
	private void processRequest(Request r) {
		if (r.floor.ID == 0 && r.direction == Direction.DOWN) {
			throw new SimException("Trying to go Down on Floor 0");
		}
		else if (r.floor.ID == floors.length-1 && r.direction == Direction.UP) {
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

			Elevator bestElevator = elevators[0];
			
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
	
	@Override
	public void tick() {
		
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
	
	public void request(int floor, Direction direction) {

		synchronized(requests) {
			Request r = new Request(floors[floor], direction);
			if (!requests.contains(r)) {
				requests.addLast(r);
			}
		}
	}
	
	public Floor getNextFloor(Floor f) {
		int id = f.ID;
		id++;
		try {
			return floors[id];
		} catch (IndexOutOfBoundsException e) {
			throw new SimException("Floor " + id + " is out of bounds.");
		}
	}
	
	public Floor getPrevFloor(Floor f) {
		int id = f.ID;
		id--;
		try {
			return floors[id];
		} catch (IndexOutOfBoundsException e) {
			throw new SimException("Floor " + id + " is out of bounds.");
		}
	}

	public Floor[] getFloors() {
		return floors;
	}

	public Elevator[] getElevators() {
		return elevators;
	}


}