package me.deslee.elevsim.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Set;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import me.deslee.elevsim.main.Simulation;
import me.deslee.elevsim.model.Elevator;
import me.deslee.elevsim.model.Floor;
import me.deslee.elevsim.model.Person;
import me.deslee.elevsim.model.SimObject;
import me.deslee.ticker.Tickable;

public class InfoPane extends JPanel implements Tickable {
	private static final long serialVersionUID = 5726273903668868642L;
	private GUI gui;
	private JEditorPane text;
	private SimObject focused;
	private long lastTicked = 0;
	private Simulation simulation;
	public InfoPane(Simulation simulation, GUI gui) {
		this.simulation = simulation;
		this.gui = gui;
		text = new JEditorPane();
		text.setContentType("text/html"); 
		this.setPreferredSize(new Dimension(250, this.gui.getView().getHeight()));
		this.setLayout(new BorderLayout());
		this.add(text, BorderLayout.CENTER);
		text.setBackground(this.getBackground());
		text.setEditable(false);
	}
	
	public void setFocus(SimObject s) {
		focused = s;
		simulation.logger.log("focusing on " + s, 3);
	}
	
	public void tick() {
		if (System.currentTimeMillis() - lastTicked > 90) {
			lastTicked = System.currentTimeMillis();		
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (focused instanceof Elevator) {
						elevatorInfo((Elevator) focused);
					} else if (focused instanceof Floor) {
						floorInfo((Floor) focused);
					} else if (focused instanceof Person) {
						personInfo((Person) focused);
					}
				}
			});	
		}
	}

	private void elevatorInfo(Elevator e) {
		String s = "<h1>" + e + "</h1>" +
			"<b>Current floor:</b> " + e.getCurrentFloor() + "<br>";
		s += "<b>Number of Stops:</b> " + e.getStops().size() + "<br>";
		
		if (e.getStops().size() > 0) {
			s += "<b>Stops:</b> ";
			Set<Floor> floors = e.getStops();
			for (Floor f : floors) {
				s += "F"+f.ID + ", ";
			}
			s = s.substring(0, s.length()-2);
			s += "<br>";
		}
		
		s += "<b>Passengers:</b> " + e.getPeople().size() + "<br>";
		text.setText(s);
	}

	private void floorInfo(Floor f) {
		String s = "<h1>" + f + "</h1>";
		
		Set<Person> people = f.getPeople();
		s += "<b>People:</b> " + people.size() + "<br>";
		
		Set<Elevator> elevators = f.getElevators();
		if (elevators.size() > 0) {
			s += "<b>Elevators:</b> ";
			for (Elevator e : elevators) {
				s += "E"+e.ID + ", ";
			}
			s = s.substring(0, s.length()-2);
			s += "<br>";
		}
		
		text.setText(s);
	}
	
	private void personInfo(Person p) {
		text.setText("<h1>Person " + p + "</h1>");
	}
}
