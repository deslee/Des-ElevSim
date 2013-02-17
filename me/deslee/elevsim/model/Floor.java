package me.deslee.elevsim.model;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import me.deslee.elevsim.exception.SimException;
import me.deslee.ticker.Tickable;

public class Floor implements Tickable, SimObject  {
	
	public final int ID;
	private Building building;
	private Set<Elevator> elevators = new TreeSet<>();
	private Set<Person> people = new HashSet<>();
	
	public Floor(Building b, int ID) {
		this.building = b;
		this.ID = ID;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Floor && ((Floor)o).ID == ID;
	}
	
	public Set<Elevator> getElevators() {
		return Collections.unmodifiableSet(elevators);
	}

	public Set<Person> getPeople() {
		return Collections.unmodifiableSet(people);
	}

	@Override
	public void tick() {
		for (Person p : people) {
			if (p.currentElevator != null || p.currentFloor == null) {
				throw new SimException("Person ticked on floor, " + 
			"but either currentFloor is null or currentElevator is not null. " + 
			"CurrentFloor: " + p.currentFloor + " CurrentElevator: " + p.currentElevator);
			}
			p.tick();
		}
	}
	
	@Override
	public String toString() {
		return "Floor " + ID;
	}
	
	protected void addElevator(Elevator e) {
		elevators.add(e);
	}
	
	protected void removeElevator(Elevator e) {
		elevators.remove(e);
	}

	protected void addPerson(Person person) {
		people.add(person);
	}
	
	protected void removePerson(Person person) {
		people.remove(person);
	}

}
