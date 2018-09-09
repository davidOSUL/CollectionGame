package model;

import java.io.Serializable;

import effects.GlobalModifierOption;
import modifiers.Modifier;
import thingFramework.Creature;
import thingFramework.Thing;
/**
 * The "Model" in the MVP model. Manages the game state in memory.
 * <br> NOTE: newSession should be called at the beginning of a new session. </br>
 * @author David O'Sullivan
 *
 */
public interface ModelInterface extends Serializable {

	/**
	 * To be called on every game tick. Updates time and events
	 */
	void update();

	/**
	 * @param thing the thing to add
	 */
	void addThing(Thing thing);

	/**
	 * @param thing The thing to add
	 */
	void removeThing(Thing thing);

	/**
	 * To be called whenever the game is rebooted, regardless of time of last save
	 */
	void onStartUp();

	/**
	 * Pause the game timer, will not resume until unPause() is called
	 */
	void pause();

	/**
	 * Unpause game timer
	 */
	void unPause();

	/**
	 * Returns the time since start, the time of the current session, and the total in game time as a formatted string
	 * @return the time since start, the time of the current session, and the total in game time as a formatted string
	 */
	String getTimeStats();

	/**
	 * Increase the % chance (0-100) of a legendary creature appearing
	 * @param increase the percentage (0-100) to increase by
	 */
	void increaseLegendaryChance(int increase);

	/**
	 * Decrease the % chance (0-100) of a legendary creature  appearing
	 * @param decrease the percentage (0-100) to decrease by
	 */
	void decreaseLegendaryChance(int decrease);

	/**
	 * @return total time both on and offline
	 */
	long getTotalTimeSinceStart();

	/**
	 * @return total time in this game session
	 */
	long getSessionGameTime();

	/**
	 * @return total time throughout all game sessions
	 */
	long getTotalInGameTime();

	/**
	 * Returns the amount of gold that the model has
	 * @return the amount of gold that the model has
	 */
	int getGold();

	/**
	 * Sets the amount of gold that the model has
	 * @param gold the new amount of gold
	 */
	void setGold(int gold);

	/**
	 * Returns the amount of popularity that the model has
	 * @return the amount of popularity that the model has
	 */
	int getPopularity();

	/**
	 * Sets the amount of popularity that the model has
	 * @param popularity the new amount of popularity
	 */
	void setPopularity(int popularity);

	/**
	 * Adds the amount of gold provided to the total amount of gold on the model
	 * @param gold the amount to add
	 */
	void addGold(int gold);

	/**
	 * Subtracts the amount of gold provided to the total amount of gold on the model. Equivalent to addGold(-gold)
	 * @param gold the amount to subtract
	 */
	void subtractGold(int gold);

	/**
	 * Adds the amount of popularity to the total amount of popularity on the model
	 * @param popularity the amount of popularity to add
	 */
	void addPopularity(int popularity);

	/**
	 * Subtracts the amount of popularity provided to the total amount of popularity on the model. Equivalent to addPopularity(-popularity)
	 * @param popularity the amount of popularity to subtract
	 */
	void subtractPopularity(int popularity);

	/**
	 * @return true if there is a creature in the foundCreatures Queue (that is, a wild creature spawned)
	 */
	boolean wildCreaturePresent();

	/**
	 * For debugging purposes only. Gets the creature with the given name
	 * @param name the name of the creature
	 * @return the new creature
	 */
	Creature getCreature(String name);

	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	String toString();

	/**
	 * @return the next wild creature in the queue, null if there is none, temporarily removes from queue. Call undoGrab() to place back and confirmGrab() to confirm removal
	 */
	Creature grabWildCreature();

	/**
	 * Undo the effects of grabWildCreature() and place grabbed creature back at front of queue
	 */
	void undoGrab();

	/**
	 * Confirm removal of creature from queue
	 * @return The Creature that was confirmed to be grabbed
	 */
	Creature confirmGrab();

	/**
	 * @return the currently Grabbed Creature
	 */
	Creature getGrabbed();

	/**
	 * Gets and removes creature from queue
	 * @return The next creature in the queue
	 */
	Creature grabAndConfirm();

	/**
	 * @return the number of creatures waiting the queue
	 */
	int numCreaturesWaiting();

	/**
	 * Applies the given modifier to all things. All things added
	 * in the future will be affected as long as the modifier remains. Also starts mod.startCount(...).
	 * @param mod the Thing modifier to add
	 */
	void applyGlobalModifier(Modifier mod);

	/**
	 * Applies the given modifier to specified things based on options. All currently present Thing meeting those stipulations, as well as those added
	 * in the future will be affected as long as the modifier remains. Also starts mod.startCount(...).
	 * @param mod the Thing modifier to add
	 * @param option the preference for what types of Things should be affected by this modifier
	 */
	void applyGlobalModifier(Modifier mod, GlobalModifierOption option);

	/**
	 * Removes the given Modifier from the model. All Things currently affected by the modifier will have the modifier removed
	 *@param mod the Modifier to remove
	 */
	void removeGlobalModifier(Modifier mod);

	/**
	 * Add the provided thing to queue of things that the model wants to remove
	 * @param t
	 */
	void addToRemoveRequest(Thing t);

	/**
	 * The model is requesting to remove something
	 * @return true if the model has at least one thing to remove
	 */
	boolean hasRemoveRequest();

	/**
	 * @return the next thing the model is requesting to remove (null if none)
	 */
	Thing getNextRemoveRequest();

	/**
	 * Returns a formatted string representation of the legendary percent chance, look for new creature period, chance that on a new creature period a creature is found
	 * @return the formatted string showing advanced model stats
	 */
	String getAdvancedStats();

	/**
	 * Adds to the total amount that whatever the calculated creature period is, it is decreased by that amount.
	 * In other words. if the period is currently 1 minute, calling amountToDecrease(1000) will make it 59 seconds, 
	 * whereas calling amountToDecrease(-1000) will make it 61 seconds. 
	 * @param amountToDecreaseMillis the amount to decrease the look for new creature period by
	 */
	void addToPeriodDecrease(long amountToDecreaseMillis);

	/**
	 * Removes all creatures currently on the model
	 * @return the number of creatures removed this way
	 */
	int removeAllCreatures();
	/**
	 * Returns the ShopWindow for this ModelInterface
	 * @return the ShopWindow for this ModelInterface
	 */
	ShopWindow getShopWindow();

	/**
	 * Looks for a creature, but gurantees that one is found (as supposed to having a percent chance that none are found)
	 */
	void lookForCreatureGuranteedFind();

	/**
	 * To be called by an event on calculated period. Will check if a creature is even found using 
	 * getPercentChanceCreatureFound(), and if it is, will find a creature, with lower rarity creatures being
	 * more likely
	 */
	void lookForCreature();

	/**
	 * Returns the period at which the game checks for new creatures
	 * @return The period at which the game checks for new creatures.
	 *  
	 */
	double getLookForCreaturesPeriod();

}