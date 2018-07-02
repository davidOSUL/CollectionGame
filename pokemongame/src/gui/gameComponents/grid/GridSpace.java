package gui.gameComponents.grid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.BorderFactory;

import gameutils.GameUtils;
import gui.gameComponents.GameSpace;
import gui.gameComponents.grid.Grid.GridPoint;
import gui.guiutils.GuiUtils;
import gui.mouseAdapters.DoubleClickWithThreshold;
import gui.mouseAdapters.SelectionWindowBuilder;
import gui.mvpFramework.GameView;

/**
 * A GameSpace aligned to a Grid
 * @author David O'Sullivan
 */
public class GridSpace extends GameSpace implements Comparable<GridSpace>{
	private static final long serialVersionUID = 1L;
	private int numColumns;
	private int numRows;
	private GridPoint p_g;
	private Grid grid;
	private final List<MouseListener> listeners = new ArrayList<MouseListener>();
	/**
	 * Creates a new GridSpace at a specified GridPoint and with specified dimensions.
	 * @param x_g the x GridPoint coordinate
	 * @param y_g the y GridPoint coordinate
	 * @param numCols the number of columns (in spots) starting at the provided coordinate
	 * @param numRows the number of rows (in spots) starting at the provided coordinate
	 */
	private GridSpace(Grid grid, int x_g, int y_g, int numCols, int numRows) {
		super(x_g*grid.getSubX(),y_g*grid.getSubY(),numCols*grid.getSubX(),numRows*grid.getSubY());
		numColumns = numCols;
		this.numRows = numRows;
		this.grid = grid;
		p_g = new GridPoint(x_g, y_g);
	}
	/**
	 * Creates a new GridSpace at a specified GridPoint and with specified dimensions.
	 * @param p_g the GridPoint
	 * @param numCols the number of columns (in spots) starting at the provided coordinate
	 * @param numRows the number of rows (in spots) starting at the provided coordinate
	 */
	private GridSpace(Grid grid, GridPoint p_g, int numCols, int numRows) {
		this(grid, p_g.x, p_g.y, numCols, numRows);
	}
	/**
	 * Creates a new GridSpace at the specified location, copying size/image from the provided gameSpace (and snapping to this grid accordingly).
	 * This will be "formatted" to the grid. Note that this does NOT add the Grid to the GridSpace
	 * @param g the GameSpace reference
	 * @param x_g the x coordinate of the GridPoint 
	 * @param y_g the ycoordinate of the GridPoint
	 */
	protected GridSpace(Grid grid, GameSpace g, int x_g, int y_g) {
		super(g, x_g*grid.getSubX(), y_g*grid.getSubY());
		setName(g.getName());
		p_g = new GridPoint(x_g, y_g);
		setGrid(grid, p_g);
		if (!g.isEmpty()) {
			setImage( g.getImage());
		}
	}

	/**
	 * Changes the grid that houses this GridSpace. Essentially "formats" the grid space so that it fits. 
	 * Note that this does NOT add the Grid to the GridSpace
	 * @param grid the new grid
	 * @param p_g the GridPoint within that grid
	 */
	protected void setGrid(Grid grid, GridPoint p_g) {
		this.grid = grid;
		this.p_g = p_g;
		setLocation(p_g.x*grid.getSubX(), p_g.y*grid.getSubY());
		numColumns = getWidth() / grid.getSubX();
		numRows = getHeight() / grid.getSubY();
	}
	/**
	 * Creates a new GridSpace at the specified location, copying size/image from the provided gameSpace (and snapping to this grid accordingly)
	 * @param g the GameSpace reference
	 * @param p_g the GridPoint
	 */
	private GridSpace(GridSpace g, GridPoint p_g) {
		this(g.getGrid(), g, p_g.x, p_g.y);
	}
	/**
	 * Creates a new GridSpace at location p_g = (0,0) copying size/image from the provided gameSpace (and snapping to this grid accordingly)
	 * @param g
	 */
	private GridSpace(GridSpace g) {
		this(g.getGrid(), g,0,0);
	}
	/**
	 * Sets the location of this Grid to the specified GridPoint
	 * @param p_g the grid point to set the location to 
	 */
	protected void setGridLocation(GridPoint p_g) {
		this.setLocation(p_g.x*grid.getSubX(), p_g.y*grid.getSubY());
	}
	/**
	 * @return the area of this GridSpace
	 */
	public int getArea() {
		return numColumns * numRows;
	}
	/**
	 * Compares areas of GridSpaces
	 * @param gridSpace The GridSpace to compare to
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GridSpace g) {
		return g.getArea() == getArea() ? 0 : getArea() > g.getArea() ? 1 : -1;
	}
	/**
	 * Calls GameSpaces paint method and also creates a border
	 * @see gui.gameComponents.GameSpace#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}
	/**
	 * Checks if the provided GridPoint is in this GridSpace
	 * @param testp_g
	 * @return true if the grid point resides within this GridSpace
	 */
	private boolean containsGridPoint(GridPoint testp_g) {
		return (testp_g.x >= p_g.x && testp_g.x < p_g.x+numColumns) && (testp_g.y >= p_g.y && testp_g.y < p_g.y+numRows);
	}
	/**
	 * Assigns the location of this GridSpace to the provided GridPoint
	 * @param p_g the GridPoint to change the location to
	 */
	protected void setPoint(GridPoint p_g) {
		this.p_g = p_g;
		this.setLocation(p_g.x*grid.getSubX(), p_g.y*grid.getSubY());
	}
	protected int getNumColumns() {
		return numColumns;
	}
	protected int getNumRows() {
		return numRows;
	}
	protected GridPoint getGridPoint() {
		return p_g;
	}

	private void addListeners(boolean hasSellBackOption) {
		removeListeners();
		BiConsumer<GameView, MouseEvent> onDoubleClick = (gv, e) -> {
			gv.getPresenter().attemptMoveGridSpace(this);
		};
		MouseListener dubClickListener = new DoubleClickWithThreshold<GameView>(Grid.CLICK_DIST_THRESH, onDoubleClick, grid.getGameView());
		this.addMouseListener(dubClickListener);
		listeners.add(dubClickListener);
		MouseListener options = getDefaultPopupListener(hasSellBackOption);
		listeners.add(options);
		addMouseListener(options);
	}
	private MouseListener getDefaultPopupListener(boolean hasSellBackOption) {
		SelectionWindowBuilder<GameView> swb = new SelectionWindowBuilder<GameView>(Grid.CLICK_DIST_THRESH, "Options");
		BiConsumer<GameView, MouseEvent> onClickDelete = (gv, e) -> {			
			gv.getPresenter().attemptDeleteGridSpace(this);
		};
		BiConsumer<GameView, MouseEvent> onClickSellBack = (gv, e) -> {			
			gv.getPresenter().attemptSellBackGridSpace(this);
		};
		if (hasSellBackOption) {
			swb.addOption("Sell " + getName() + " back for " + grid.getGameView().getPresenter().getGridSpaceSellBackValue(this) + 
					GuiUtils.getToolTipDollar(),
					onClickSellBack, grid.getGameView());
		}
		return swb.addOption(grid.getGameView().getPresenter().getDiscardText(this), onClickDelete, grid.getGameView())
				.addOption("Move " + getName() + " To Bank").getListener();
		
	}
	/**
	 * Removes this GridSpace from whatever grid it is in, also removes listeners. Does NOT set grid to null (nor should it: moving)
	 * @see Grid#removeGridSpace(GridSpace)
	 */
	public void removeFromGrid() {
		grid.removeGridSpace(this);
	}
	/**
	 * Updates the listeners
	 * @param hasSellBackOption if set to true will add option to sell back to shop
	 */
	public void updateListeners(boolean hasSellBackOption) {
		addListeners(hasSellBackOption);
	}
	/**
	 * Removes all mouse listeners
	 */
	public void removeListeners() {
		listeners.forEach(ml -> this.removeMouseListener(ml));
	}
	/**
	 * @return the Grid Associated with this GridSpace
	 */
	public Grid getGrid() {
		return grid;
	}


	/**
	 * Makes image "snap" to the size of this Grid
	 * @see gui.gameComponents.GameSpace#setImage(java.awt.Image)
	 */
	@Override
	public void setImage(Image curr) {
		if (grid == null) {
			super.setImage(curr);
			return;
		}
		int newImageWidth = GameUtils.roundToMultiple(curr.getWidth(this), grid.getSubX());
		int newImageHeight = GameUtils.roundToMultiple(curr.getHeight(this), grid.getSubY());
		BufferedImage newImage = new BufferedImage(newImageWidth, newImageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bGr = newImage.createGraphics();
		bGr.drawImage(curr, 0, 0, null);
		bGr.dispose();
		super.setImage(newImage);
	}
}