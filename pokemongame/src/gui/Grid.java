package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;

import gameutils.GameUtils;

public class Grid extends GameSpace {

	private static final long serialVersionUID = 1L;
	private int subX;
	private int subY;
	private int numColumns;
	private int numRows;
	Set<Point> openSpots = new HashSet<Point>();
	Map<Point, Spot> grid = new HashMap<Point, Spot>();
	/**
	 * @param x x location of grid
	 * @param y y location of grid
	 * @param dimension width/height of grid
	 * @param subX width of grid elements
	 * @param subY height of grid elements
	 */
	public Grid(int x, int y, Dimension dimension, int subX, int subY) {
		super(x,y, dimension);
		if (subX > getWidth() || subY > getHeight()) {
			throw new IllegalArgumentException("Can't subdivide");
		}
		setVals(subX, subY);
		for (int i = 0; x < numColumns; x++) {
			for (int j = 0; y < numRows; y++) {
				openSpots.add(new Spot(i,j));
			}
		}
	}
	public Grid(Rectangle r, int subX, int subY) {
		this(r.x, r.y, new Dimension((int)r.getWidth(), (int)r.getHeight()), subX, subY);
	}
	private void setVals(int subX, int subY) {
		this.subX = subX;
		this.subY = subY;
		int xdiff = getWidth() % subX == 0 ? 0: subX;
		int ydiff = getHeight() % subY == 0 ? 0 : subY;
		for (int x = 0; x < getWidth() - xdiff; x+= subX) {
			numColumns++;
		}
		for (int y = 0; y < getHeight()-ydiff; y+=subY) {
			numRows++;
		}
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
		
		for (int x = 0; x < numColumns; x++) {
			for (int y = 0; y < numRows; y++) {
				g2d.drawRect(x*subX, y*subY, subX, subY);
			}
		}
	}
	public boolean addGridSpaceFirstFit(GameSpace g) {
		GridSpace gridSpace = new GridSpace(g);
		Spot s = findFirstFit(gridSpace);
		if (s == null)
			return false;
		addAndSplit(s, gridSpace);
	}
	private void addAndSplit(Spot s, GridSpace g) {
		
	}
	private Spot findFirstFit(GridSpace gridSpace) {
		int targetX = gridSpace.numColumns;
		int targetY = gridSpace.numRows;
		for (Point p : openSpots) {
			if (hasRoom(p, targetX, targetY))
				return grid.get(p);
		}
		return null;
	}
	private boolean hasRoom(Point p, int numCols, int numRows) {
		for (int i = p.x; i < numCols; i++) {
			Point target = new Point(i, p.y);
			if (!grid.containsKey(target))
				return false;
			if (grid.get(target).isOccupied())
				return false;
		}
		for (int i = p.y; i < numRows; i++) {
			Point target = new Point(p.x, i);
			if (!grid.containsKey(target))
				return false;
			if (grid.get(target).isOccupied())
				return false;
		}
		return true;
	}
	/**
	 * One element of the current grid
	 * @author David O'Sullivan
	 */
	private class Spot {
		Point p;
		GridSpace resident = null;
		public Spot(int x, int y) {
			p = new Point(x,y);
		}
		public boolean isOccupied() {
			return resident != null;
		}
		@Override
		public boolean equals(Object o) {
			return p.equals(((Spot)o).p);
		}
		@Override
		public int hashCode() {
			return p.hashCode();
		}
		
	}
	/**
	 * A GameSpace alligned to the current Grid
	 * @author David O'Sullivan
	 */
	private class GridSpace extends GameSpace implements Comparable{
		private static final long serialVersionUID = 1L;
		int numColumns;
		int numRows;
		public GridSpace(int x, int y, int numCols, int numRows) {
			super(x,y,numCols*subX,numRows*subY);
			numColumns = numCols;
			this.numRows = numRows;
		}
		public GridSpace(GameSpace g) {
			super(g);
			if (!g.isEmpty()) {
				Image curr =  g.getImage();
				BufferedImage newImage = new BufferedImage(GameUtils.roundToMultiple(curr.getWidth(this), subX), GameUtils.roundToMultiple(curr.getHeight(this), subY), BufferedImage.TYPE_INT_ARGB);
				Graphics2D bGr = newImage.createGraphics();
				bGr.drawImage(curr, 0, 0, null);
				bGr.dispose();
				setImage(newImage);
				numColumns = newImage.getWidth() / subX;
				numRows = newImage.getHeight() / subY;
			}
		}
		public int getArea() {
			return numColumns * numRows;
		}
		@Override
		public int compareTo(Object arg0) {
			GridSpace g = (GridSpace) arg0;
			return g.getArea() == getArea() ? 0 : getArea() > g.getArea() ? 1 : -1;
		}
	}
	private static class SpaceIterator implements Iterable<Spot>{
		int numCols;
		int numRows;
		Map<Point, Spot> grid;
		Point start;
		public SpaceIterator(Map<Point, Spot> grid, Point start, int numCols, int numRows) {
			this.numCols = numCols;
			this.numRows = numRows;
			this.grid = grid;
			this.start = start;
		}
		public boolean noSpace() {
			return !grid.containsKey(new Point(start.x + numCols, start.y)) || !grid.containsKey(new Point(start.y + numCols, start.y));
		}
		@Override
		public Iterator<Spot> iterator() {
			Iterator<Spot> it = new Iterator<Spot>() {

	            Point currentLoc = new Point(start.x, start.y);
	            @Override
	            public boolean hasNext() {
	              
	            }

	            @Override
	            public Spot next() {
	               if (grid.containsKey(currentLoc)) {
	            	   return grid.get(currentLoc);
	               }
	               currentLoc.x++;
	            }

	            @Override
	            public void remove() {
	                throw new UnsupportedOperationException();
	            }
	        };
	        return it;
		}
	}
}
