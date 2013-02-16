package me.deslee.elevsim.main;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import me.deslee.elevsim.gui.GUI;
import me.deslee.elevsim.model.Building;
import me.deslee.logger.Logger;
import me.deslee.ticker.Ticker;

public class Simulation {

	private Building building;
	private ScheduledExecutorService ex;
	private GUI gui;
	private boolean ended = false;
	private boolean running;
	public final Logger logger = new Logger();
	
	public static final long TICKTIME = 31; // how long a tick is, in milliseconds
	
	public static final int ELEVATOR_MAX_WEIGHT = 500; // Kilograms
	public static final int ELEVATOR_IDLE_WAITTIME = 3000; //waitTime in millis
	public static final double ELEVATOR_MILLISECONDS_PER_FLOOR = 1000; // ms to move up a floor
	
	public Simulation(int numFloors, int numElevators)  {
		building = new Building(this, numFloors, numElevators);

		try {
			SwingUtilities.invokeAndWait(
			new Runnable() {
				public void run() {
					 gui = new GUI(Simulation.this, building);
				}
			}
			);
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		ex = Executors.newScheduledThreadPool(2);
		ex.scheduleAtFixedRate(new Ticker(this, building, "Model"), 0, TICKTIME, TimeUnit.MILLISECONDS);
		ex.scheduleAtFixedRate(new Ticker(this, gui, "GUI"), 0, TICKTIME, TimeUnit.MILLISECONDS);
		running = true;
		
		building.request(6, me.deslee.elevsim.model.Direction.UP);
		building.request(12, me.deslee.elevsim.model.Direction.UP);
		building.request(15, me.deslee.elevsim.model.Direction.UP);
		building.request(9, me.deslee.elevsim.model.Direction.UP);
		building.request(2, me.deslee.elevsim.model.Direction.DOWN);
		building.addPerson();
		building.addPerson();
		building.addPerson();
	}
	
	public GUI getGui() {
		return gui;
	}


	public void end() {
		// TODO: do stuff before exiting
		if (running) {
			stop();
		}
		ended  = true;
		gui.disposeFrame();
		synchronized(this) {
			this.notifyAll();
		}
	}

	public void stop() {
		logger.log("Stopping Simulation", 1);
		ex.shutdown();
		running = false;
	}

	public boolean isEnded() {
		return ended;
	}
	
	public boolean isRunning() {
		return running;
	}

}
