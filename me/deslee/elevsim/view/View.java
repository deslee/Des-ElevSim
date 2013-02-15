package me.deslee.elevsim.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import me.deslee.elevsim.model.Building;
import me.deslee.elevsim.model.Direction;
import me.deslee.ticker.Tickable;


public class View extends JPanel implements Tickable {
	private static final long serialVersionUID = 7496980334914245849L;
	private static final int VIEW_WIDTH = 800;
	private static final int VIEW_HEIGHT = 800;
	private static final int ELEVATOR_WIDTH = 20;
	
	private Building building;
	private int numFloors;
	private double floorHeight;
	
	public View(Building building) {
		this.building = building;
		this.numFloors = building.getNumberOfFloors();
		this.floorHeight = VIEW_HEIGHT / numFloors;
		this.setPreferredSize(new Dimension(VIEW_WIDTH,VIEW_HEIGHT));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawFloors((Graphics2D) g);
		drawElevators((Graphics2D) g);
	}

	private void drawFloors(Graphics2D g2d) {
		
		Rectangle2D.Double floor = new Rectangle2D.Double(0, 0, VIEW_WIDTH, floorHeight);
		
		for (int i = 0; i < numFloors; ++i) {
			g2d.draw(floor);
			floor.y += floorHeight;
		}
	}

	private void drawElevators(Graphics2D g2d) {
		int numElevators = building.getNumberOfElevators();
		
		double wRatio = VIEW_WIDTH / numElevators;
		for (int i = 0; i < numElevators; ++i) {
			g2d.setColor(Color.black);
			double elevPosX = i * wRatio + wRatio/3;
			double yOffset = building.getPercentToNextFloor(i) * floorHeight;
			
			if (building.getDirection(i) == Direction.DOWN) {
				yOffset *= -1;
			}
			
			double elevPosY = floorHeight * (numFloors - 1 - building.getCurrentFloor(i)) - yOffset;
			double elevHeight = floorHeight;
			double shaftPosX = elevPosX+(ELEVATOR_WIDTH/2);
			Line2D.Double shaft = new Line2D.Double(shaftPosX, 0, shaftPosX, VIEW_HEIGHT);
			
			g2d.draw(shaft);
			
			Rectangle2D.Double elevator = new Rectangle2D.Double(elevPosX,elevPosY,ELEVATOR_WIDTH,elevHeight);
			
			g2d.setColor(Color.white);
			g2d.fill(elevator);
			g2d.setColor(Color.black);			
			g2d.draw(elevator);
			g2d.drawString(Integer.toString(i), (float) elevator.x + ELEVATOR_WIDTH/3, (float) (elevator.y + elevHeight/1.5));
			
			//draw the stops
			g2d.setColor(Color.blue);
			for (int stop: building.getStops(i)) {
				double pointX = i * wRatio + wRatio/3;
				double pointY = floorHeight * (numFloors - 1 - stop);
				g2d.draw(new Rectangle2D.Double(pointX, pointY, ELEVATOR_WIDTH, elevHeight));
			}	
		}
		
	}

	@Override
	public void tick() {
		repaint();
	}
	
	
	
	
}
