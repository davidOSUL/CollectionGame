package gui.mvpFramework.view;

import javax.swing.JComponent;
import javax.swing.JFrame;

import gui.gameComponents.GameSpace;
import gui.gameComponents.grid.GridSpace;
import gui.gameComponents.grid.GridSpace.GridSpaceData;
import gui.mvpFramework.presenter.AddType;
import gui.mvpFramework.presenter.Presenter;

/**
 * The GUI/"View" for the game. This and the Model are what the presenter interacts with. 
 * @author David O'Sullivan
 *
 */
public interface ViewInterface {

	/**
	 * Sets the presenter that coordinates with this ViewInterface
	 * @param p the presenter to coordinate with
	 */
	void setPresenter(Presenter p);

	/**
	 * @return the presenter associated with this ViewInterface
	 */
	Presenter getPresenter();

	/**
	 * Undo the adding of a gridspace
	 */
	void cancelGridSpaceAdd();

	/**
	 * Adds to the POPUP_LAYER of this ViewInterface's LayeredPane() the provided JComponent. Displays in the center of the JFrame.
	 * @param jp the JComponent to center and display as a pop up
	 */
	void displayComponentCentered(JComponent jp);

	/**
	 * Removes the provided JComponent from the POPUP_LAYER
	 * @param jp the JComponent to remove
	 */
	void removeDisplay(JComponent jp);

	/**
	 * Starts the process of attempting to add the newly created provided GameSpace to the maingamePanel.
	 * Converts the GameSpace to a gridSpace on the default grid and then adds. 
	 * @param gameSpace the GameSpace to add
	 * @param type the context of the add (e.g. Creature from queue, moving an existing GameSpace, etc.)
	 */
	void attemptNewGridSpaceAdd(GameSpace gameSpace, AddType type);

	/**
	 * Starts the process of attempting to add the existing GridSpace to the maingamePanel
	 * @param gridSpace the GameSpace to add
	 * @param type the context of the add (e.g. Creature from queue, moving an existing GameSpace, etc.)
	 */
	void attemptExistingGridSpaceAdd(GridSpace gridSpace, AddType type);

	/**
	 * Sets the value of the notification button
	 * @param num the number to set the notification button to
	 */
	void setWildCreatureCount(int num);

	/**
	 * Updates the GUI display of the gold and popularity of the model
	 * @param gold the new gold of the model
	 * @param popularity the new popularity of the model
	 */
	void setModelAttributes(int gold, int popularity);

	/**
	 * Revalidates and repaints the display
	 */
	void updateDisplay();

	/**
	 * Toggles enabledness of buttons on maingamepanel
	 * @param enabled whether or not the buttons should be enabled
	 */
	void setEnabledForButtons(boolean enabled);

	/**
	 * Adds a GridSpace that was previously on the ModelInterface and was saved away using the specified data. Note that this is only a UI change. (doesn't notify presenter)
	 * @param gameSpace the gamespace to add
	 * @param data the data corresponding to the new GridSpace
	 * @return the newly generated and added gridspace
	 */
	GridSpace addNewGridSpaceFromSave(GameSpace gameSpace, GridSpaceData data);
	/**
	 * returns the JFrame that houses this view
	 * @return the grame that houses this view
	 */
	JFrame getFrame();

}