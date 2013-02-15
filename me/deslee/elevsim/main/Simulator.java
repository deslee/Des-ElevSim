package me.deslee.elevsim.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import me.deslee.elevsim.model.Building;
import me.deslee.elevsim.view.View;
import me.deslee.logger.Logger;
import me.deslee.ticker.Ticker;

public class Simulator {

	public static final long TICKTIME = 32; // how long a tick is, in milliseconds
	private Building building;
	private ScheduledExecutorService ex;
	public static Logger logger = new Logger(Logger.CONSOLE);
	
	Simulator(int numFloors, int numElevators)  {
		building = new Building(numFloors, numElevators);
		View view = new View(building);
		JFrame frame = new JFrame();
		frame.getContentPane().add(view);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ex = Executors.newScheduledThreadPool(2);
		ex.scheduleAtFixedRate(new Ticker(building, "Model"), 0, TICKTIME, TimeUnit.MILLISECONDS);
		ex.scheduleAtFixedRate(new Ticker(view, "GUI"), 0, TICKTIME, TimeUnit.MILLISECONDS);
		

		building.request(6, me.deslee.elevsim.model.Direction.UP);
		building.request(12, me.deslee.elevsim.model.Direction.UP);
		building.request(15, me.deslee.elevsim.model.Direction.UP);
		building.request(9, me.deslee.elevsim.model.Direction.UP);
		building.request(2, me.deslee.elevsim.model.Direction.DOWN);
	}

}
