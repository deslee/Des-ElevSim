package me.deslee.elevsim.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import me.deslee.elevsim.gui.GUI;
import me.deslee.elevsim.model.Building;
import me.deslee.elevsim.model.Direction;
import me.deslee.elevsim.model.Floor;
import me.deslee.ticker.Tickable;


public class View extends JPanel implements Tickable, MouseListener {
	private static final long serialVersionUID = 7496980334914245849L;
	private static final int INITIAL_VIEW_WIDTH = 400;
	private static final int INITIAL_VIEW_HEIGHT = 400;
	private static final int ELEVATOR_WIDTH = 20;
	
	private int numFloors;
	private double floorHeight;
	private Rectangle2D.Double[] floors;
	private Rectangle2D.Double[] elevators;
	private GUI gui;
	private Building building;
	
	public View(GUI gui, Building building) {
		this.gui = gui;
		this.building = building;
		this.numFloors = building.getFloors().length;
		this.setPreferredSize(new Dimension(INITIAL_VIEW_WIDTH,INITIAL_VIEW_HEIGHT));

		floors = new Rectangle2D.Double[building.getFloors().length];
		elevators = new Rectangle2D.Double[building.getElevators().length];
		for (int i = 0; i < floors.length; ++i) {
			floors[i] = new Rectangle2D.Double();
		}
		for (int i = 0; i < elevators.length; ++i) {
			elevators[i] = new Rectangle2D.Double();
		}
		this.addMouseListener(this);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.floorHeight = getHeight() / ((double) numFloors);
		drawFloors((Graphics2D) g);
		drawElevators((Graphics2D) g);
	}

	private void drawFloors(Graphics2D g2d) {
		for (int i = 0; i < numFloors; ++i) {
			floors[floors.length-1-i].setRect(0, i * floorHeight, getWidth(), floorHeight);
			g2d.draw(floors[floors.length-1-i]);
		}
	}

	private void drawElevators(Graphics2D g2d) {
		int numElevators = building.getElevators().length;
		
		double wRatio = getWidth() / numElevators;
		for (int i = 0; i < numElevators; ++i) {
			g2d.setColor(Color.black);
			double elevPosX = i * wRatio + wRatio/3;
			double yOffset = building.getElevators()[i].getPercentToNextFloor() * floorHeight;
			
			if (building.getElevators()[i].getDirection() == Direction.DOWN) {
				yOffset *= -1;
			}
			
			double elevPosY = floorHeight * (numFloors - 1 - building.getElevators()[i].getCurrentFloor().ID) - yOffset;
			double elevHeight = floorHeight;
			double shaftPosX = elevPosX+(ELEVATOR_WIDTH/2);
			Line2D.Double shaft = new Line2D.Double(shaftPosX, 0, shaftPosX, getHeight());
			
			g2d.draw(shaft);
			
			elevators[i].setRect(elevPosX,elevPosY,ELEVATOR_WIDTH,elevHeight);
			
			g2d.setColor(Color.white);
			g2d.fill(elevators[i]);
			g2d.setColor(Color.black);			
			g2d.draw(elevators[i]);
			g2d.drawString(Integer.toString(i), (float) elevators[i].x + ELEVATOR_WIDTH/3, (float) (elevators[i].y + elevHeight/1.5));
			
			//draw the stops
			g2d.setColor(Color.blue);
			for (Floor stop: building.getElevators()[i].getStops()) {
				double pointX = i * wRatio + wRatio/3;
				double pointY = floorHeight * (numFloors - 1 - stop.ID);
				g2d.draw(new Rectangle2D.Double(pointX, pointY, ELEVATOR_WIDTH, elevHeight));
			}	
		}
		
	}

	@Override
	public void tick() {
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for(int i = 0; i < elevators.length; ++i) {
			if (elevators[i].contains(e.getX(), e.getY())) {
				gui.focusOn(building.getElevators()[i]);
				return;
			}
		}
		
		for(int i = 0; i < floors.length; ++i) {
			if (floors[i].contains(e.getX(), e.getY())) {
				gui.focusOn(building.getFloors()[i]);
				return;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
	
	
}
