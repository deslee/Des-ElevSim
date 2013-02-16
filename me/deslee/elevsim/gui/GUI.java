package me.deslee.elevsim.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import me.deslee.elevsim.main.Simulation;
import me.deslee.elevsim.model.Building;
import me.deslee.elevsim.model.Elevator;
import me.deslee.elevsim.model.Floor;
import me.deslee.elevsim.view.View;
import me.deslee.ticker.Tickable;

// TODO: restructure GUI class. make everything panels. take away all frames.
public class GUI implements Tickable, WindowListener {
	private JFrame frame;
	private JPanel panel;
	
	private TextLog textLog;
	private InfoPane infoPane;
	private View view;
	public final Simulation simulation;
	public GUI(Simulation simulation, Building building) {
		this.simulation = simulation;
		this.view = new View(this, building);
		frame = new JFrame();
		panel = new JPanel(new BorderLayout());
		frame.setContentPane(panel);
		frame.setJMenuBar(new SimMenu(this));
		infoPane = new InfoPane(simulation, this);
		frame.getContentPane().add(view, BorderLayout.CENTER);
		frame.getContentPane().add(infoPane, BorderLayout.EAST);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setTitle("Simulation");
		frame.addWindowListener(this);
		
		createTextLog();
	}

	private void createTextLog() {
		textLog = new TextLog(simulation, this);
	}
	
	public void showTextLog() {
		textLog.setVisible(true);
	}
	
	public void hideTextLog() {
		textLog.setVisible(false);
	}

	public void toggleTextLog() {
		if (textLog.isVisible()) {
			hideTextLog();
		}
		else {
			showTextLog();
		}
	}

	public void focusOn(Floor floor) {
		infoPane.setFocus(floor);
	}

	public void focusOn(Elevator elevator) {
		infoPane.setFocus(elevator);
		
	}
	
	public View getView() {
		return view;
	}

	@Override
	public void tick() {
		view.tick();
		infoPane.tick();
	}
	
	public void disposeFrame() {
		frame.dispose();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {	
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		simulation.end();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
