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
import guiutils.GuiUtils;

/**
 * Grid for things, pokemon, etc. to live on. 
 * <br> Note that the "_g" convention is appended to any object that is referring not to an absolute location but to a grid coordinate (e.g. Point p_g = (0,1) is the second box in the grid))</br>
 * @author David O'Sullivan
 */
public class Grid extends GameSpace {

	private static final long serialVersionUID = 1L;
	private int subX;
	private int subY;
	private int numColumns;
	private int numRows;
	private Set<Point> openSpots = new HashSet<Point>();
	private Map<Point, Spot> grid = new HashMap<Point, Spot>();
	private GridSpace highlighted;
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
		for (int i = 0; i < numColumns; i++) {
			for (int j = 0; j < numRows; j++) {
				Point p = new Point(i, j);
				openSpots.add(p);
				grid.put(p, new Spot(p));
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
	private void setHighlight(GridSpace g) {
		if (!hasRoom(g))
			return;
		if (highlighted != null)
			remove(highlighted);
		g = generateGridSpace(new GameSpace(GuiUtils.FillIn(g.getImage(), Color.YELLOW)));
		this.highlighted = g;
		add(g);
	}
	/**
	 * Changes the location of active highlight. Only changes width/height if currently no active highlight. If out of bounds will remove highlight
	 * @param p upper left hand corner, absolute point
	 * @param g the gridspace whose size is used for highlighting size
	 */
	public void setHighlight(Point p, GridSpace g) {
		if (highlighted == null)
			setHighlight(new GridSpace(getSnapPoint(p), g.numColumns, g.numRows));
		else 
			updateHighlight(getSnapPoint(p));
	}
	private void updateHighlight(Point p_g) {
		highlighted.setPoint(p_g);
		if (!hasRoom(highlighted))
			removeHighlight();
	}
	public void removeHighlight() {
		if (highlighted != null) {
			remove(highlighted);
			this.highlighted = null;
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
		for (int x = 0; x < numColumns; x++) {
			for (int y = 0; y < numRows; y++) {
				g2d.drawRect(x*subX, y*subY, subX, subY);
				if (highlighted != null && highlighted.containsGridPoint(new Point(x,y))) {
				}
			}
		}
	}
	/**
	 * @param p the absolute point
	 * @return The Point in the grid
	 */
	private Point getSnapPoint(Point p) {
		int x_g = p.x / subX;
		int y_g = p.y / subY;
		return new Point(x_g, y_g);
	}
	public Point getAbsoluteSnapPoint(Point p) {
		int x = (p.x / subX)*subX;
		int y = (p.y / subY)*subY;
		return new Point(x, y);
	}
	public GameSpace addGridSpaceSnapToGrid(GameSpace g, Point p) {
		if (p.x > subX*numColumns || p.y > subY*numRows)
			return null;
		return addGridSpace(g, getSnapPoint(p));
		
	}
	/**
	 * @param g The GameSpace to add. Will align to size of current grid and add at point p if there is room.
	 * @param p_g Upper left hand corner grid location to add to
	 * @return null if there was not room at that location for that GameSpace, the gridspace otherwise
	 */
	public GameSpace addGridSpace(GameSpace g, Point p_g) {
		GridSpace gridSpace = new GridSpace(g, p_g);
		if (!hasRoom(p_g, gridSpace.numColumns, gridSpace.numRows))
			return null;
		addAndSplit(grid.get(p_g), gridSpace);
		return gridSpace;
	}
	/**
	 * @param g The GameSpace to add. Will align it to size of current grid and add in the first free spot
	 * @return null if there is no available spot in current grid, the gridspace otherwise
	 */
	public GameSpace addGridSpaceFirstFit(GameSpace g) {
		GridSpace gridSpace = new GridSpace(g);
		Spot s = findFirstFit(gridSpace);
		if (s == null)
			return null;
		gridSpace.setGridLocation(s.p_g);
		addAndSplit(s, gridSpace);
		return gridSpace;
	}
	/**
	 * Adds A gridspace and removes that location from the openSpots set
	 * @param s The upper left hand corner spot to add the gridspace
	 * @param g The gridspace to add
	 */
	private void addAndSplit(Spot s, GridSpace g) {
		SpaceIterator si = new SpaceIterator(grid, s.p_g, g.numColumns, g.numRows);
		for (Spot spot: si) {
			openSpots.remove(spot.p_g);
			spot.resident = g;
		}
		add(g);
	}
	/**
	 * @param gridSpace The Gridspace to look for a spot for
	 * @return The Spot of the upper left hand corner that will fit the gridspace
	 */
	private Spot findFirstFit(GridSpace gridSpace) {
		int targetX = gridSpace.numColumns;
		int targetY = gridSpace.numRows;
		for (Point p : openSpots) {
			if (hasRoom(p, targetX, targetY))
				return grid.get(p);
		}
		return null;
	}
	/**
	 * @param p The Upper left hand corner to look at
	 * @return true if the numRowsxnumCols grid with upper left hand corner at p is all unoccupied and all points are within the bounds of the grid. 
	 */
	private boolean hasRoom(Point p_g, int numCols, int numRows) {
		SpaceIterator si = new SpaceIterator(grid, p_g, numCols, numRows);
		if (si.noSpace())
			return false;
		for (Spot s: si) {
			if (s == null || s.isOccupied())
				return false;
		}
		return true;
	}
	private boolean hasRoom(GridSpace gs) {
		return hasRoom(gs.p_g, gs.numColumns, gs.numRows);
	}
	/**
	 * One element of the current grid
	 * @author David O'Sullivan
	 */
	private class Spot {
		Point p_g;
		GridSpace resident = null;
		public Spot(Point p) {
			this.p_g = p;
		}
		public Spot(int x, int y) {
			p_g = new Point(x,y);
		}
		public boolean isOccupied() {
			return resident != null;
		}
		@Override
		public boolean equals(Object o) {
			return p_g.equals(((Spot)o).p_g);
		}
		@Override
		public int hashCode() {
			return p_g.hashCode();
		}
		
	}
	public GridSpace generateGridSpace(GameSpace g) {
		return new GridSpace(g);
	}
	/**
	 * A GameSpace alligned to the current Grid
	 * @author David O'Sullivan
	 */
	public class GridSpace extends GameSpace implements Comparable{
		private static final long serialVersionUID = 1L;
		private int numColumns;
		private int numRows;
		private Point p_g;
		private GridSpace(int x_g, int y_g, int numCols, int numRows) {
			super(x_g*subX,y_g*subY,numCols*subX,numRows*subY);
			numColumns = numCols;
			this.numRows = numRows;
			p_g = new Point(x_g, y_g);
		}
		private GridSpace(Point p_g, int numCols, int numRows) {
			this(p_g.x, p_g.y, numCols, numRows);
		}
		private GridSpace(GameSpace g, int x_g, int y_g) {
			super(g, x_g*subX, y_g*subY);
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
		private GridSpace(GameSpace g, Point p_g) {
			this(g, p_g.x, p_g.y);
		}
		private GridSpace(GameSpace g) {
			this(g,0,0);
		}
		
		public void setGridLocation(Point p_g) {
			this.setLocation(p_g.x*subX, p_g.y*subY);
		}
		public int getArea() {
			return numColumns * numRows;
		}
		@Override
		public int compareTo(Object arg0) {
			GridSpace g = (GridSpace) arg0;
			return g.getArea() == getArea() ? 0 : getArea() > g.getArea() ? 1 : -1;
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			this.setBorder(BorderFactory.createLineBorder(Color.black));
		}
		public boolean containsGridPoint(Point testp_g) {
			return (testp_g.x >= p_g.x && testp_g.x < p_g.x+numColumns) && (testp_g.y >= p_g.y && testp_g.y < p_g.y+numRows);
		}
		public void setPoint(Point p_g) {
			this.p_g = p_g;
			this.setLocation(p_g.x*subX, p_g.y*subY);
		}
	}
	/**
	 * Iterates through as much of grid as possible, ignoring any thing that goes over the edge
	 * @author David O'Sullivan
	 */
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
		
		/**
		 * @return true if desired region of iteration expands past edge of grid
		 */
		public boolean noSpace() {
			return !grid.containsKey(start) || !grid.containsKey(new Point(start.x + numCols-1, start.y)) || !grid.containsKey(new Point(start.y + numCols-1, start.y));
		}
		@Override
		public Iterator<Spot> iterator() {
			Iterator<Spot> it = new Iterator<Spot>() {

	            Point currentLoc = new Point(start.x, start.y);
	            int lastCol = start.x + numCols - 1;
	            int lastRow = start.y + numRows -1;
	            @Override
	            public boolean hasNext() {
	              if (currentLoc.x <= lastCol && currentLoc.y <= lastRow)
	            	  return true;
	              return false;
	            }

	            @Override
	            public Spot next() {
	               Spot result = null;
	               if (grid.containsKey(currentLoc)) {
	            	   result =  grid.get(currentLoc);
	               }
	               currentLoc.x++;
	               if (currentLoc.x > lastCol) {
	            	   currentLoc.x = start.x;
	            	   currentLoc.y++;
	               }
	               return result;
	            	   
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
