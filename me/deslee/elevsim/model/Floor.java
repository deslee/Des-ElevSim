package me.deslee.elevsim.model;

import java.util.ArrayList;

import me.deslee.ticker.Tickable;

public class Floor implements Tickable, SimObject  {
	
	private ArrayList<Person> people = new ArrayList<>();
	
	public final int ID;

	private Building building;
	
	public Floor(Building b, int ID) {
		this.building = b;
		this.ID = ID;
	}
	
	@Override
	public void tick() {
		for (Person p : people) {
			p.tick();
		}
	}
	
	@Override
	public String toString() {
		return "Floor " + ID;
	}

	public Elevator[] getElevators() {
		Elevator[] elevators = building.getElevators();
		Elevator[] list = new Elevator[elevators.length];
		int i = 0;
		
		// invariant: i is the index of the elevator to be inserted
		// 			i is also the number of elevators inserted
		for (Elevator e: elevators) {
			if (e.getCurrentFloor().equals(this)) {
				list[i++] = e;
			}
		}
		Elevator[] listT = new Elevator[i];
		System.arraycopy(list, 0, listT, 0, listT.length);
		return listT;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Floor && ((Floor)o).ID == ID;
	}

}
