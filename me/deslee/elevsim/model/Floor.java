package me.deslee.elevsim.model;

import java.util.ArrayList;

import me.deslee.ticker.Tickable;

public class Floor implements Tickable {
	
	private ArrayList<Person> people = new ArrayList<>();
	
	public final int ID;
	
	public Floor(int ID) {
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

}
