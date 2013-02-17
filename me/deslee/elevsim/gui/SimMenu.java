package me.deslee.elevsim.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class SimMenu extends JMenuBar {
	private static final long serialVersionUID = 6795532491077678342L;
	private GUI gui;
	public SimMenu(GUI gui) {
		this.gui = gui;
		JMenu menu;
		JMenuItem item;
		
		menu = new JMenu("File");
		add(menu);
		
		final JMenuItem pause = new JMenuItem("Pause Simulation");
		final JMenuItem resume = new JMenuItem("Resume Simulation");
		resume.setEnabled(false);
		pause.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pause.setEnabled(false);
				resume.setEnabled(true);
				SimMenu.this.gui.simulation.pause();
			}
			
		});
		menu.add(pause);
		
		resume.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resume.setEnabled(false);
				pause.setEnabled(true);
				SimMenu.this.gui.simulation.resume();
			}
			
		});
		menu.add(resume);
		
		final JMenuItem stop = new JMenuItem("Stop Simulation");
		stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stop.setEnabled(false);
				SimMenu.this.gui.simulation.stop();
			}
			
		});
		menu.add(stop);
		
		item = new JMenuItem("Exit");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SimMenu.this.gui.simulation.end();
			}
			
		});
		menu.add(item);
		
		menu = new JMenu("View");
		add(menu);
		
		item = new JMenuItem("Log");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SimMenu.this.gui.toggleTextLog();
			}
			
		});
		menu.add(item);
	}
}
