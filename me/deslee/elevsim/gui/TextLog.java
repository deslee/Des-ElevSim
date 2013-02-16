package me.deslee.elevsim.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import me.deslee.elevsim.main.Simulation;

public class TextLog extends JFrame {
	
	private static final long serialVersionUID = 737041100007061943L;
	private GUI gui;
	private JScrollPane high;
	private JScrollPane medium;
	private JScrollPane low;
	private Simulation simulation;
	public TextLog(Simulation simulation, GUI gui) {
		this.simulation = simulation;
		high = new JScrollPane(this.simulation.logger.high);
		medium = new JScrollPane(this.simulation.logger.medium);
		low = new JScrollPane(this.simulation.logger.low);
		this.gui = gui;
		this.setPreferredSize(new Dimension(600, 240));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Text log");
		addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				TextLog.this.gui.hideTextLog();
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {}

			@Override
			public void windowDeiconified(WindowEvent arg0) {}

			@Override
			public void windowIconified(WindowEvent arg0) {}

			@Override
			public void windowOpened(WindowEvent arg0) {}
			
		});
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;
		
		menu = new JMenu("File");
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Close");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextLog.this.gui.hideTextLog();
			}
			
		});
		menu.add(menuItem);
		
		menu = new JMenu("Filter");
		menuBar.add(menu);

		menuItem = new JMenuItem("Important messages");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setContentPane(high);
				revalidate();
			}
			
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Default messages");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setContentPane(medium);
				revalidate();
			}
			
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("All messages");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setContentPane(low);
				revalidate();
			}
			
		});
		menu.add(menuItem);
		
		this.setJMenuBar(menuBar);
		this.setContentPane(medium);
		pack();
	}
}
