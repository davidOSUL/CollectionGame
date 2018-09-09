package gui.gameComponents.grid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import gui.gameComponents.GameSpace;
import gui.gameComponents.grid.GridSpace.GridSpaceData;
import gui.guiutils.GuiUtils;
import gui.mvpFramework.view.ViewInterface;

/**
 * Grid for things, creatures, etc. to live on. Is made up of "Spots" which are the individual squares of the grid, and GridSpaces which are 
 * objects that live on this grid and have sizes such that they "snap" to the proportions of this grid 
 * @author David O'Sullivan
 */
public final class Grid extends GameSpace {
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
	 * Used for purposes of serialization. Easy way to identify the grid
	 */
	private final int gridID;
	/**
	 * The set of all spots on this grid that aren't occupied
	 */
	private final Set<GridPoint> openSpots = new HashSet<GridPoint>();
	/**
	 * The map betwen GridPoint and the spot that that GridPoint corresponds to 
	 */
	private final Map<GridPoint, Spot> grid = new HashMap<GridPoint, Spot>();
	/**
	 * A Temporary GridSpace that highlights (turns yellow) when hovered over a viable spot to place
	 */
	private GridSpace highlighted;
	/**
	 * Whether or not the GridSpace "highlighted", is currently visible
	 */
	private boolean highlightVisible = false;
	/**
	 * The View Interface that houses this object
	 */
	private final ViewInterface vi;

	
	/**
	 * Creates a new Grid with specified subDimensions, location, and width/height
	 * @param x x location of grid
	 * @param y y location of grid
	 * @param dimension width/height of grid
	 * @param subX width of grid elements
	 * @param subY height of grid elements
	 * @param vi the ViewInterface that houses this Grid
	 * @param gridID an integer identification for this grid
	 */
	public Grid(final int x, final int y, final Dimension dimension, final int subX, final int subY, final ViewInterface vi, final int gridID) {
		super(x,y, dimension);
		if (subX > getWidth() || subY > getHeight()) {
			throw new IllegalArgumentException("Can't subdivide");
		}
		setVals(subX, subY);
		for (int i = 0; i < numColumns; i++) {
			for (int j = 0; j < numRows; j++) {
				final GridPoint p = new GridPoint(i, j);
				openSpots.add(p);
				grid.put(p, new Spot(p));
			}
		}
		this.vi = vi;
		this.gridID = gridID;
	}
	/**
	 * Creates a new Grid with specified subDimensions, location, and width/height
	 * @param r The rectangle representing this grids location, width/height
	 * @param subX width of grid elements
	 * @param subY height of grid elements
	 * @param vi the ViewInterface that houses this Grid
	 * @param gridID an integer identification for this grid
	 */
	public Grid(final Rectangle r, final int subX, final int subY, final ViewInterface vi, final int gridID) {
		this(r.x, r.y, new Dimension((int)r.getWidth(), (int)r.getHeight()), subX, subY, vi, gridID);
	}
	
	/**
	 * @return the gridID associated with this grid upon creation
	 */
	public int getID(){
		return gridID;
	}
	
	/**
	 * Sets the numCols/numRows for this grid based on the subdivisions
	 * @param subX width of grid elements
	 * @param subY height of grid elements
	 */
	private void setVals(final int subX, final int subY) {
		this.subX = subX;
		this.subY = subY;
		final int xdiff = getWidth() % subX == 0 ? 0: subX;
		final int ydiff = getHeight() % subY == 0 ? 0 : subY;
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
	public void updateHighlight(final Point p) { 
		updateHighlightGP(getSnapPoint(p));
	}
	/**
	 * Updates the location of highlighted. If there is not room for the highlighted grid space, the highlight will be removed
	 * @param p_g The <b><i>Grid Point</i></b> to update the highlighted to
	 */
	private void updateHighlightGP(final GridPoint p_g) {
		highlighted.setGridPoint(p_g);
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
	/** 
	 * @see gui.gameComponents.GameSpace#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (subX == 0 || subY == 0) 
			return;
		final Graphics2D g2d = (Graphics2D) g;
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
	private GridPoint getSnapPoint(final Point p) {
		final int x_g = p.x / subX;
		final int y_g = p.y / subY;
		return new GridPoint(x_g, y_g);
	}
	/**
	 * Returns the closest Absolute Point (relative to this component) to the provided Absolute Point (relative to this component) that corresponds to a valid GridPoint and
	 * @param p the Absolute Point (relative to this component)
	 * @return The Absolute point (relative to this component) that is a "snap point" (a multiple of the subdivisions of this grid)
	 */
	public Point getAbsoluteSnapPoint(final Point p) {
		final int x = (p.x / subX)*subX;
		final int y = (p.y / subY)*subY;
		return new Point(x, y);
	}
	/**
	 * Adds a GridSpace at the Spot closest to the provided Absolute Point (relative to this component), generated by the given GameSpace
	 * @param g The GameSpace to add
	 * @param p the Absolute Point (relative to this component)
	 * @return null if unable to add the component (e.g. no room in grid), the GridSpace that was added otherwise
	 */
	@AddToGrid
	public GridSpace addGridSpaceSnapToGrid(final GridSpace g, final Point p) {
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
	public GridSpace addGridSpace(final GridSpace gridSpace, final GridPoint p_g) {
		if (!hasRoom(p_g, gridSpace.getNumColumns(), gridSpace.getNumRows()))
			return null;	
		gridSpace.setGridAndGridPoint(this, p_g);
		return addAndSplit(grid.get(p_g), gridSpace);
	}
	/**
	 * Adds a GridSpace at the first location that will fit the grif space
	 * @param gridSpace The GameSpace to add. Will align it to size of current grid and add in the first free spot
	 * @return null if there is no available spot in current grid, the gridspace assigned to grid otherwise
	 */
	@AddToGrid
	public GridSpace addGridSpaceFirstFit(final GridSpace gridSpace) {
		final Spot s = findFirstFit(gridSpace);
		if (s == null)
			return null;
		gridSpace.setGridAndGridPoint(this, s.p_g);
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
	private GridSpace addAndSplit(final Spot s, final GridSpace g) {
		if (g.getGrid() != this)
			throw new RuntimeException("GridSpace to add doesn't have proper grid");
		final SpaceIterator si = new SpaceIterator(grid, s.p_g, g.getNumColumns(), g.getNumRows());
		for (final Spot spot: si) {
			boolean test = true;
			test &= openSpots.remove(spot.p_g);
			test &= spot.resident == null;
			spot.resident = g;
			if (!test)
				GuiUtils.displayError(new RuntimeException("ATTEMPTED TO ADD TO OCCUPIED SPACE"), this);
		}
		add(g);
		return g;
	}
	/**
	 * Removes the GridSpace from the grid, also removes its listeners
	 * @param g The GridSpace to be removed
	 * @invariant g exists on this Grid
	 */
	public void removeGridSpace(final GridSpace g) {
		final SpaceIterator si = new SpaceIterator(grid, g.getGridPoint(), g.getNumColumns(), g.getNumRows());
		for (final Spot spot: si) {
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
	private Spot findFirstFit(final GridSpace gridSpace) {
		final int targetX = gridSpace.getNumColumns();
		final int targetY = gridSpace.getNumRows();
		for (final GridPoint p : openSpots) {
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
	private boolean hasRoom(final GridPoint p_g, final int numCols, final int numRows) {
		final SpaceIterator si = new SpaceIterator(grid, p_g, numCols, numRows);
		if (si.noSpace())
			return false;
		for (final Spot s: si) {
			if (s == null || s.isOccupied())
				return false;
		}
		return true;
	}
	/**
	 * Checks if the GridSpace can reside where it wants to
	 * @param gridSpace The GridSpace to check
	 * @return true if it can fit in the grid at its location
	 */
	private boolean hasRoom(final GridSpace gridSpace) {
		return hasRoom(gridSpace.getGridPoint(), gridSpace.getNumColumns(), gridSpace.getNumRows());
	}
	/**
	 * One element of the current grid
	 * @author David O'Sullivan
	 */
	private class Spot {
		GridPoint p_g;
		GridSpace resident = null;
		public Spot(final GridPoint p_g) {
			this.p_g = p_g;
		}
		public boolean isOccupied() {
			return resident != null;
		}
		@Override
		public boolean equals(final Object o) {
			return p_g.equals(((Spot)o).p_g);
		}
		@Override
		public int hashCode() {
			return p_g.hashCode();
		}
		
	}
	/**
	 * Returns the gridData for this grid. This is Serializable data that can be used to load the grid later
	 * @return the GridData for this grid
	 */
	public GridData getData() {
		return new GridData(subX, subY, numColumns, numRows, gridID);
	}
	/**
	 * Creates a new GridSpace to fit this grid formed from a GameSpace
	 * @param g The GameSpace to created off of
	 * @return the generated GridSpace
	 */
	public GridSpace generateGridSpace(final GameSpace g) {
		return new GridSpace(this, g, 0,0);
	}
	/**
	 * Takes in a new GameSpace (unrotated), and saved gridspace data and generates and adds as a new GridSpace (after rotation) to this grid, and then returns
	 * that gridspace.
	 * @param g the GameSpace to generate a grid space from
	 * @param data the GridSpaceData to use to generate a GridSpace with
	 * @return the generated gridspace
	 */
	public GridSpace generateRotateAndAddGridSpaceFromData(final GameSpace g, final GridSpaceData data) {
		final GridSpace gs = new GridSpace(this, g, data.p_g);
		for (int i = 0; i < data.num90Rotations; i++)
			gs.rotateClockwise90();
		addAndSplit(grid.get(data.p_g), gs);
		return gs;
	}
	/**
	 * Returns the length of a square on this grid in the x dimension
	 * @return the length of a square on this grid in the x dimension
	 */
	public int getSubX() {
		return subX;
	}
	/**
	 * Returns the length of a square on this grid in the y dimension
	 * @return the length a square on this grid in the y dimension
	 */
	public int getSubY() {
		return subY;
	}
	/**
	 * Returns the ViewInterface that houses this grid
	 * @return
	 */
	protected ViewInterface getViewInterface() {
		return vi;
	}
	/**
	 *Very simple implementation of Point. Exactly the same but with a different name, used to help distinguish
	 *between absolute points and grid points
	 * @author David O'Sullivan
	 *
	 */
	protected static class GridPoint extends Point implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs a new GridPoint
		 * @param x the x coordinate of the GridPoint
		 * @param y the y coordinate of the GridPoint
		 */
		protected GridPoint(final int x, final int y) {super(x,y);}
	}
	/**
	 * Iterates through as much of grid as possible, ignoring any thing that goes over the edge
	 * @author David O'Sullivan
	 */
	private static class SpaceIterator implements Iterable<Spot>{
		private final int numCols;
		private final int numRows;
		private final Map<GridPoint, Spot> grid;
		private final GridPoint start;
		public SpaceIterator(final Map<GridPoint, Spot> grid, final GridPoint start, final int numCols, final int numRows) {
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
			final Iterator<Spot> it = new Iterator<Spot>() {

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
	/**
	 * Used to store the data necessary to serialize and deserialize a grid
	 * @author David O'Sullivan
	 *
	 */
	public final static class GridData implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * The subX of the grid
		 */
		public final int subX;
		/**
		 * the subY of the grid
		 */
		public final int subY;
		/**
		 * the number of columns in the grid
		 */
		public final int numColumns;
		/**
		 * the number of rows in the grid
		 */
		public final int numRows;
		/**
		 * the grid's identification number
		 */
		public final int gridID;
		/**
		 * Creates a new GrdData
		 * @param subX The subX of the grid
		 * @param subY The subX of the grid
		 * @param numColumns the number of columns in the grid
		 * @param numRows the number of rows in the grid
		 * @param gridID the grid's identification number
		 */
		private GridData(final int subX, final int subY, final int numColumns, final int numRows, final int gridID) {
			this.subX = subX;
			this.subY = subY;
			this.numColumns = numColumns;
			this.numRows = numRows;
			this.gridID = gridID;
		}
	}
}
