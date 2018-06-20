package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import javax.swing.BorderFactory;

public class Grid extends GameSpace {

	private static final long serialVersionUID = 1L;
	private int subX;
	private int subY;
	
	/**
	 * @param x x location of grid
	 * @param y y location of grid
	 * @param dimension width/height of grid
	 * @param subX width of grid elements
	 * @param subY height of grid elements
	 */
	public Grid(int x, int y, Dimension dimension, int subX, int subY) {
		super(x,y, dimension);
		if (subX > x || subY > y) {
			throw new IllegalArgumentException("Can't subdivide");
		}
		setVals(subX, subY);
	}
	public Grid(Rectangle r, int subX, int subY) {
		super(r);
		setVals(subX, subY);
	}
	private void setVals(int subX, int subY) {
		this.subX = subX;
		this.subY = subY;
		//this.subX = getWidth() / (getWidth()/subX);
		//this.subY = getHeight() / (getHeight()/subY) ;
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (subX == 0 || subY == 0) 
			return;
		Graphics2D g2d = (Graphics2D) g;
		//Stroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0);
		//g2d.setStroke(stroke);
		g2d.setPaint(Color.gray);
		int xdiff = getWidth() % subX == 0 ? 0: subX;
		int ydiff = getHeight() % subY == 0 ? 0 : subY;
		
		for (int x = 0; x < getWidth()-xdiff; x+=subX) {
			for (int y = 0; y < getHeight()-ydiff; y+=subY) {
				int width = x+subX < getWidth() ? subX : getWidth() - x;
				int height = y+subY < getHeight() ? subY : getHeight() - y;
				g2d.drawRect(x, y, width, height);
			}
		}
	}
}
