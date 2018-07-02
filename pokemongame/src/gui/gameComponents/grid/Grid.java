package gui.gameComponents.grid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.swing.BorderFactory;

import gameutils.GameUtils;
import gui.gameComponents.GameSpace;
import gui.guiutils.GUIConstants;
import gui.guiutils.GuiUtils;
import gui.mouseAdapters.DoubleClickWithThreshold;
import gui.mouseAdapters.SelectionWindowBuilder;
import gui.mvpFramework.GameView;

/**
 * Grid for things, pokemon, etc. to live on. Is made up of "Spots" which are the individual squares of the grid, and GridSpaces which are 
 * objects that live on this grid and have sizes such that they "snap" to the proportions of this grid 
 * @author David O'Sullivan
 */
public class Grid extends GameSpace {
/*
 * Note that the "_g" convention is appended to any object that is referring not to an absolute location but to a grid coordinate,
 * these objects are also named "GridPoint", an absolute Point is just "Point" (e.g. GridPoint p_g = (0,1) is the second box in the grid, whereas
 * Point p = (0,1) is the point at pixel x = 0, y = 1)
 */
	private static final long serialVersionUID = 1L;
	private int subX;
	private int subY;
	private int numColumns;
	private int numRows;
	/**
	 * The set of all spots on this grid that aren't occupied
	 */
	private Set<GridPoint> openSpots = new HashSet<GridPoint>();
	/**
	 * The map betwen GridPoint and the spot that that GridPoint corresponds to 
	 */
	private Map<GridPoint, Spot> grid = new HashMap<GridPoint, Spot>();
	/**
	 * A Temporary GridSpace that highlights (turns yellow) when hovered over a viable spot to place
	 */
	private GridSpace highlighted;
	/**
	 * Whether or not the GridSpace "highlighted", is currently visible
	 */
	private boolean highlightVisible = false;
	/**
	 * The GameView that houses this object
	 */
	private GameView gv;

	protected static final int CLICK_DIST_THRESH = GUIConstants.CLICK_DIST_THRESH;
	/**
	 * Creates a new Grid with specified subDimensions, location, and width/height
	 * @param x x location of grid
	 * @param y y location of grid
	 * @param dimension width/height of grid
	 * @param subX width of grid elements
	 * @param subY height of grid elements
	 * @param gv the GameView that houses this Grid
	 */
	public Grid(int x, int y, Dimension dimension, int subX, int subY, GameView gv) {
		super(x,y, dimension);
		if (subX > getWidth() || subY > getHeight()) {
			throw new IllegalArgumentException("Can't subdivide");
		}
		setVals(subX, subY);
		for (int i = 0; i < numColumns; i++) {
			for (int j = 0; j < numRows; j++) {
				GridPoint p = new GridPoint(i, j);
				openSpots.add(p);
				grid.put(p, new Spot(p));
			}
		}
		this.gv = gv;
	}
	/**
	 * Creates a new Grid with specified subDimensions, location, and width/height
	 * @param r The rectangle representing this grids location, width/height
	 * @param subX width of grid elements
	 * @param subY height of grid elements
	 *  @param gv the GameView that houses this Grid
	 */
	public Grid(Rectangle r, int subX, int subY, GameView gv) {
		this(r.x, r.y, new Dimension((int)r.getWidth(), (int)r.getHeight()), subX, subY, gv);
	}
	/**
	 * Sets the numCols/numRows for this grid based on the subdivisions
	 * @param subX width of grid elements
	 * @param subY height of grid elements
	 */
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
	/**
	 * Sets the highlighted object for this grid, and then updates the location. It should be noted that updateHighlight will 
	 * remove the highlight if there isn't room for the gridspace. 
	 * @param g the GridSpace to make a highlight out of 
	 */
	public void setHighlight(GridSpace g) {
		if (!g.isEmpty())
			g = generateGridSpace(new GameSpace(GuiUtils.FillIn(g.getImage(), Color.YELLOW)));
		removeHighlight();
		this.highlighted = g;
		add(highlighted);
		highlightVisible = true;
	}
	/**
	 * Updates the location of  highlighted. If there is not room for the highlighted grid space, the highlight will be removed
	 * @param p An absolute point (relative to this component, that is (0,0) is the upper left hand corner of this grid) representing the new location (note: this is not a GridPoint)
	 */
	public void updateHighlight(Point p) { 
		updateHighlightGP(getSnapPoint(p));
	}
	/**
	 * Updates the location of highlighted. If there is not room for the highlighted grid space, the highlight will be removed
	 * @param p_g The <b><i>Grid Point</i></b> to update the highlighted to
	 */
	private void updateHighlightGP(GridPoint p_g) {
		highlighted.setPoint(p_g);
		if (!hasRoom(highlighted)) {
			remove(highlighted);
			highlightVisible = false;
		}
		else if (!highlightVisible) {
			add(highlighted);
			highlightVisible = true;
		}
	}
	/**
	 * Removes highlighted piece and stops it from being painted
	 */
	public void removeHighlight() {
		if (highlighted != null) {
			remove(highlighted);
			this.highlighted = null;
			highlightVisible = false;
		}
	
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (subX == 0 || subY == 0) 
			return;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(Color.gray);
		for (int x = 0; x < numColumns; x++) {
			for (int y = 0; y < numRows; y++) {
				g2d.drawRect(x*subX, y*subY, subX, subY);
			}
		}
	}
	/**
	 * Returns the GridPoint closest to the given absolute point
	 * @param p the absolute point (relative to this component)
	 * @return The GridPoint that is closest to this absolute point
	 */
	private GridPoint getSnapPoint(Point p) {
		int x_g = p.x / subX;
		int y_g = p.y / subY;
		return new GridPoint(x_g, y_g);
	}
	/**
	 * Returns the closest Absolute Point (relative to this component) to the provided Absolute Point (relative to this component) that corresponds to a valid GridPoint and
	 * @param p the Absolute Point (relative to this component)
	 * @return The Absolute point (relative to this component) that is a "snap point" (a multiple of the subdivisions of this grid)
	 */
	public Point getAbsoluteSnapPoint(Point p) {
		int x = (p.x / subX)*subX;
		int y = (p.y / subY)*subY;
		return new Point(x, y);
	}
	/**
	 * Adds a GridSpace at the Spot closest to the provided Absolute Point (relative to this component), generated by the given GameSpace
	 * @param g The GameSpace to add
	 * @param p the Absolute Point (relative to this component)
	 * @return null if unable to add the component (e.g. no room in grid), the GridSpace that was added otherwise
	 */
	@AddToGrid
	public GridSpace addGridSpaceSnapToGrid(GridSpace g, Point p) {
		if (p.x > subX*numColumns || p.y > subY*numRows)
			return null;
		return addGridSpace(g, getSnapPoint(p));
		
	}
	/**
	 * Adds a GridSpace at the given GridPoint, generated by the given GameSpace
	 * @param gridSpace The GameSpace to add. Will align to size of current grid and add at point p if there is room.
	 * @param p_g Upper left hand corner grid location to add to
	 * @return null if there was not room at that location for that GameSpace, the gridspace assigned to this grid otherwise
	 */
	@AddToGrid
	public GridSpace addGridSpace(GridSpace gridSpace, GridPoint p_g) {
		gridSpace.setGrid(this, p_g);
		if (!hasRoom(p_g, gridSpace.getNumColumns(), gridSpace.getNumRows()))
			return null;	
		return addAndSplit(grid.get(p_g), gridSpace);
	}
	/**
	 * Adds a GridSpace at the first location that will fit the grif space
	 * @param gridSpace The GameSpace to add. Will align it to size of current grid and add in the first free spot
	 * @return null if there is no available spot in current grid, the gridspace assigned to grid otherwise
	 */
	@AddToGrid
	public GridSpace addGridSpaceFirstFit(GridSpace gridSpace) {
		gridSpace.setGrid(this, new GridPoint(0, 0));
		Spot s = findFirstFit(gridSpace);
		if (s == null)
			return null;
		gridSpace.setGridLocation(s.p_g);
		return addAndSplit(s, gridSpace);
	}
	/**
	 * Adds A gridspace and removes that location from the openSpots set. 
	 * @invariant All "Add To Grid" Functions call this
	 * @invariant g.getGrid() == this
	 * @param s The upper left hand corner spot to add the gridspace
	 * @param g The gridspace to add
	 * @return The GridSpace (assigned to this grid if it wasn't before)
	 */
	private GridSpace addAndSplit(Spot s, GridSpace g) {
		if (g.getGrid() != this)
			throw new RuntimeException("GridSpace to add doesn't have proper grid");
		SpaceIterator si = new SpaceIterator(grid, s.p_g, g.getNumColumns(), g.getNumRows());
		for (Spot spot: si) {
			openSpots.remove(spot.p_g);
			spot.resident = g;
		}
		add(g);
		return g;
	}
	/**
	 * Removes the GridSpace from the grid, also removes its listeners
	 * @param g The GridSpace to be removed
	 * @invariant g exists on this Grid
	 */
	public void removeGridSpace(GridSpace g) {
		SpaceIterator si = new SpaceIterator(grid, g.getGridPoint(), g.getNumColumns(), g.getNumRows());
		for (Spot spot: si) {
			openSpots.add(spot.p_g);
			if (spot.resident != g) {
				throw new RuntimeException("Grid Removal Not Syncd");
			}
			spot.resident = null;
		}
		remove(g);
		g.removeListeners();
	}
	/**
	 * Finds the first viable fit for the given gridspace. Returns null if can't find any spot.
	 * @param gridSpace The Gridspace to look for a spot for
	 * @return The Spot of the upper left hand corner that will fit the gridspace, null if there is no room
	 */
	private Spot findFirstFit(GridSpace gridSpace) {
		int targetX = gridSpace.getNumColumns();
		int targetY = gridSpace.getNumRows();
		for (GridPoint p : openSpots) {
			if (hasRoom(p, targetX, targetY))
				return grid.get(p);
		}
		return null;
	}

	/**
	 * Check if there is room at the given Grid Point 
	 * @param p_g The upper left hand corner grid point
	 * @param numCols the number of columns starting at p_g
	 * @param numRows the number of rows starting at p_g
     * @return true if the numRowsxnumCols grid with upper left hand corner at p is all unoccupied and all points are within the bounds of the grid. 
	 */
	private boolean hasRoom(GridPoint p_g, int numCols, int numRows) {
		SpaceIterator si = new SpaceIterator(grid, p_g, numCols, numRows);
		if (si.noSpace())
			return false;
		for (Spot s: si) {
			if (s == null || s.isOccupied())
				return false;
		}
		return true;
	}
	/**
	 * Checks if the GridSpace can reside where it wants to
	 * @param gs The GridSpace to check
	 * @return true if it can fit in the grid at its location
	 */
	private boolean hasRoom(GridSpace gs) {
		return hasRoom(gs.getGridPoint(), gs.getNumColumns(), gs.getNumRows());
	}
	/**
	 * One element of the current grid
	 * @author David O'Sullivan
	 */
	private class Spot {
		GridPoint p_g;
		GridSpace resident = null;
		public Spot(GridPoint p_g) {
			this.p_g = p_g;
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
	/**
	 * Creates a new GridSpace to fit this grid formed from a GameSpace
	 * @param g The GameSpace to created off of
	 * @return the generated GridSpace
	 */
	public GridSpace generateGridSpace(GameSpace g) {
		return new GridSpace(this, g, 0,0);
	}
	public int getSubX() {
		return subX;
	}
	public int getSubY() {
		return subY;
	}
	protected GameView getGameView() {
		return gv;
	}
	/**
	 *Very simple implementation of Point. Exactly the same but with a different name, used to help distinguish
	 *between absolute points and grid points
	 * @author DOSullivan
	 *
	 */
	protected static class GridPoint extends Point {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected GridPoint(int x, int y) {super(x,y);}
	}
	/**
	 * Iterates through as much of grid as possible, ignoring any thing that goes over the edge
	 * @author David O'Sullivan
	 */
	private static class SpaceIterator implements Iterable<Spot>{
		private int numCols;
		private int numRows;
		private Map<GridPoint, Spot> grid;
		private GridPoint start;
		public SpaceIterator(Map<GridPoint, Spot> grid, GridPoint start, int numCols, int numRows) {
			this.numCols = numCols;
			this.numRows = numRows;
			this.grid = grid;
			this.start = start;
		}
		
		/**
		 * @return true if desired region of iteration expands past edge of grid
		 */
		public boolean noSpace() {
			return !grid.containsKey(start) || !grid.containsKey(new GridPoint(start.x + numCols-1, start.y)) || !grid.containsKey(new GridPoint(start.y + numCols-1, start.y));
		}
		/**
		 * Returns the iterator that will go through every Spot in the grid within the bounds specified by the SpaceIterator object
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<Spot> iterator() {
			Iterator<Spot> it = new Iterator<Spot>() {

	            GridPoint currentLoc = new GridPoint(start.x, start.y);
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
