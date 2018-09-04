package gui.gameComponents.grid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.BorderFactory;

import gameutils.GameUtils;
import gui.gameComponents.GameSpace;
import gui.gameComponents.grid.Grid.GridData;
import gui.gameComponents.grid.Grid.GridPoint;
import gui.guiutils.GUIConstants;
import gui.guiutils.GuiUtils;
import gui.mouseAdapters.DoubleClickWithThreshold;
import gui.mouseAdapters.SelectionWindowBuilder;
import gui.mvpFramework.GameView;

/**
 * A GameSpace aligned ("snapped") to space(s) on a Grid
 * @author David O'Sullivan
 */
public class GridSpace extends GameSpace implements Comparable<GridSpace>{
	private static final long serialVersionUID = 1L;
	private int numColumns;
	private int numRows;
	private GridPoint p_g;
	private Grid grid;
	private int num90Rotations = 0;
	private static final int CLICK_DIST_THRESH = GUIConstants.CLICK_DIST_THRESH;
	private final List<MouseListener> listeners = new ArrayList<MouseListener>();
	/**
	 * Returns a GridSpaceData instance containing data necessary to serialize/deserialize this GridSpace
	 * @return Returns a GridSpaceData instance containing data necessary to serialize/deserialize this GridSpace 
	 */
	public GridSpaceData getData() {
		return new GridSpaceData(numColumns, numRows, p_g, num90Rotations, grid.getData());
	}
	/**
	 * Creates a new GridSpace at the specified location, copying size/image from the provided gameSpace (and snapping to this grid accordingly).
	 * This will be "formatted" to the grid. Note that this does NOT add the Grid to the GridSpace
	 * @param grid the grid that this GridSpace is within
	 * @param g the GameSpace reference
	 * @param x_g the x coordinate of the GridPoint 
	 * @param y_g the ycoordinate of the GridPoint
	 */
	protected GridSpace(final Grid grid, final GameSpace g, final int x_g, final int y_g) {
		super(g, x_g*grid.getSubX(), y_g*grid.getSubY());
		setName(g.getName());
		setGridAndGridPoint(grid, new GridPoint(x_g, y_g));
		if (!g.isEmpty()) {
			setImage( g.getImage());
		}
	}
	/**
	 * Creates a new GridSpace at the specified location, copying size/image from the provided gameSpace (and snapping to this grid accordingly).
	 * This will be "formatted" to the grid. Note that this does NOT add the Grid to the GridSpace
	 * @param grid the grid that this GridSpace is within
	 * @param g the GameSpace reference
	 * @param p_g the location of the GridSpace
	 */
	protected GridSpace(final Grid grid, final GameSpace g, final GridPoint p_g) {
		this(grid, g, p_g.x, p_g.y);
	}
	/**
	 * Changes the grid that houses this GridSpace. Essentially "formats" the grid space so that it fits. 
	 * Note that this does NOT add the Grid to the GridSpace
	 * @param grid the new grid
	 * @param p_g the GridPoint within that grid
	 */
	protected void setGridAndGridPoint(final Grid grid, final GridPoint p_g) {
		this.grid = grid;
		setGridPoint(p_g);
		updateDimension();
	}
	/**
	 * Recalculates numColumns/numRows
	 */
	private void updateDimension() {
		numColumns = getWidth() / grid.getSubX();
		numRows = getHeight() / grid.getSubY();
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
	public int compareTo(final GridSpace g) {
		return g.getArea() == getArea() ? 0 : getArea() > g.getArea() ? 1 : -1;
	}
	/**
	 * Calls GameSpaces paint method and also creates a border
	 * @see gui.gameComponents.GameSpace#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}
	/**
	 * Assigns the location of this GridSpace to the provided GridPoint (i.e. updates p_g and its location)
	 * @param p_g the GridPoint to change the location to
	 */
	protected void setGridPoint(final GridPoint p_g) {
		this.p_g = p_g;
		this.setLocation(p_g.x*grid.getSubX(), p_g.y*grid.getSubY());
	}
	/**
	 * Return the number of columns that this GridSpace occupies
	 * @return
	 */
	protected int getNumColumns() {
		return numColumns;
	}
	/**
	 * Return the number of rows that this GridSpace occupies
	 * @return
	 */
	protected int getNumRows() {
		return numRows;
	}
	/**
	 * Return the upper left hand GridPoint of this GridSpace
	 * @return  the upper left hand GridPoint of this GridSpace
	 */
	protected GridPoint getGridPoint() {
		return p_g;
	}

	private void addListeners(final boolean hasSellBackOption, final boolean canRemove, final boolean canSellBack) {
		removeListeners();
		final BiConsumer<GameView, MouseEvent> onDoubleClick = (gv, e) -> {
			gv.getPresenter().attemptMoveGridSpace(this);
		};
		
		final MouseListener dubClickListener = new DoubleClickWithThreshold<GameView>(CLICK_DIST_THRESH, onDoubleClick, grid.getGameView());
		addMouseListener(dubClickListener);
		listeners.add(dubClickListener);
		
		final MouseListener options = getDefaultPopupListener(hasSellBackOption, canRemove, canSellBack);
		addMouseListener(options);
		listeners.add(options);
		
	}
	private MouseListener getDefaultPopupListener(final boolean hasSellBackOption, final boolean canRemove, final boolean canSellBack) {
		final SelectionWindowBuilder<GameView> swb = new SelectionWindowBuilder<GameView>(CLICK_DIST_THRESH, "Options");
		final BiConsumer<GameView, MouseEvent> onClickDelete = (gv, e) -> {			
			gv.getPresenter().attemptDeleteGridSpace(this);
		};
		final BiConsumer<GameView, MouseEvent> onClickSellBack = (gv, e) -> {			
			gv.getPresenter().attemptSellBackGridSpace(this);
		};
		if (hasSellBackOption) {
			final String sellBackString = grid.getGameView().getPresenter().getSellBackString(this);
			swb.addOption(sellBackString,
					onClickSellBack, grid.getGameView(), canSellBack);
		}
		if (canRemove) {
			swb.addOption(grid.getGameView().getPresenter().getDiscardText(this), onClickDelete, grid.getGameView());
		}
		return swb
				.addOption("Move " + getName() + " To Bank")
				.addDoOnMenuVisible(() -> grid.getGameView().getPresenter().notifyRightClickedGridSpace())
				.addDoOnMenuClose(() -> grid.getGameView().getPresenter().notifyDoneWithRightClick())
				.getListener();
		
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
	 * @param canRemove whether or not the user should have the ability to remove this GridSpace
	 * @param canSellBack whether or not the user should have the ability to sell this GridSpace back to the shop
	 */
	public void updateListeners(final boolean hasSellBackOption, final boolean canRemove, final boolean canSellBack) {
		addListeners(hasSellBackOption, canRemove, canSellBack);
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
	 * Sets the image of this gridspace to one rotated 90 degrees. setImage will also update numColumns/numRows
	 */
	public void rotateClockwise90(){
		num90Rotations = (num90Rotations + 1)%4;
		setImage(GuiUtils.rotateImage90ClockwiseAndTrim(getImage()));
	}
	/**
	 * Makes image "snap" to the size of this Grid
	 * @see gui.gameComponents.GameSpace#setImage(java.awt.Image)
	 */
	@Override
	public void setImage(final Image curr) {
		if (grid == null) {
			super.setImage(curr);
			return;
		}
		final int newImageWidth = GameUtils.roundToMultiple(curr.getWidth(this), grid.getSubX());
		final int newImageHeight = GameUtils.roundToMultiple(curr.getHeight(this), grid.getSubY());
		final BufferedImage newImage = new BufferedImage(newImageWidth, newImageHeight, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D bGr = newImage.createGraphics();
		bGr.drawImage(curr, 0, 0, null);
		bGr.dispose();
		super.setImage(newImage);
		updateDimension();
	}
	/**
	 * Used to store the data necessary to serialize and deserialize a GridSpace
	 * @author David O'Sullivan
	 *
	 */
	public static class GridSpaceData implements Serializable{
		/**
		 * 
		 */
		public static final long serialVersionUID = 1L;
		/**
		 * The number of columns this GridSpace occupies
		 */
		public final int numColumns;
		/**
		 * The number of rows this GridSpace occupies
		 */
		public final int numRows;
		/**
		 * The upper left hand GridPoint of this GridSpace
		 */
		public final GridPoint p_g;
		/**
		 * The number of 90 degree rotations that this GridSpace has undergone
		 */
		public final int num90Rotations;
		/**
		 * The GridData for the Grid that this GridSpace is within
		 */
		public final GridData gridData;
		/**
		 * Creates a new GridSpaceData 
		 * @param numColumns the number of columns this GridSpace occupies
		 * @param numRows the number of rows this GridSpace occupies
		 * @param p_g the upper left hand GridPoint of this GridSpace
		 * @param num90Rotations the number of 90 degree rotations that this GridSpace has undergone
		 * @param gridData the GridData for the Grid that this GridSpace is within
		 */
		private GridSpaceData(final int numColumns, final int numRows, final GridPoint p_g, final int num90Rotations, final GridData gridData) {
			this.numColumns = numColumns;
			this.numRows = numRows;
			this.p_g = p_g;
			this.num90Rotations = num90Rotations;
			this.gridData = gridData;
		}
		
	}
}