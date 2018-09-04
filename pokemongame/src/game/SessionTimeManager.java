package game;

import java.io.Serializable;

/**
 * Manages the Game Time
 * @author David O'Sullivan
 */
public class SessionTimeManager implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Total game time, both in game and when game is closed
	 */
	private long totalTimeSinceStart = 0;
	/**
	 * Total amount of time spent in pause throughout all sessions
	 */
	private long totalPauseDeficit = 0;
	/**
	 * Sum of all ingame ("online) game times. This only goes up to the time of the last save
	 */
	private long totalInGameTime = 0;
	/**
	 * Total time in current game session
	 */
	private long sessionGameTime = 0;
	/**
	 * The time in millseconds that it was when the game was paused
	 */
	private long timeOnPause = 0;
	/**
	 * Whether or not the game is paused right now
	 */
	private boolean paused = false;
	/**
	 * The start of the current game session
	 */
	private long startOfSession = System.currentTimeMillis();
	/**
	 * How much time was spent in pause this session only
	 */
	private long sessionPauseDeficit = 0;
	/**
	 * The original time when this game was started
	 */
	private final long startOfGame = System.currentTimeMillis();
	/**
	 * Create a new Time Manager with 0 already elapsed time. Should be called at the beginning of a new game. 
	 */
	private long totalInGameTimeOnStart = 0;
	/**
	 * Creates a new SessionTimeManager, starting a new session
	 */
	public SessionTimeManager() {
		signifyNewSession();
	}
	/**
	 * Must be called whenever a new session is started to reset all session dependent values
	 */
	public void signifyNewSession() {
		startOfSession = System.currentTimeMillis();
		sessionPauseDeficit = 0;
		paused = false;
		totalInGameTimeOnStart = totalInGameTime;
	}
	/**
	 * @return The total time since the beginning of this game, both on and off screen
	 */
	public long getTotalTimeSinceStart() {
		return totalTimeSinceStart;
	}
	/**
	 * @return The total time elapsed this session
	 */
	public long getSessionGameTime() {
		return sessionGameTime;
	}
	/**
	 * Return the total time that was elapsed throughout all sessions
	 * @return the total time that was elapsed throughout all sessions
	 */
	public long getTotalInGameTime() {
		return totalInGameTime;
	}
	/**
	 * To be called on every game tick, updates the elapsed time
	 */
	public void updateGameTime() {
		if (!paused) {
			totalTimeSinceStart = (System.currentTimeMillis() - startOfGame)-totalPauseDeficit;
			sessionGameTime = (System.currentTimeMillis() - startOfSession)-sessionPauseDeficit;
			totalInGameTime = sessionGameTime + totalInGameTimeOnStart;
		}
	}
	/**
	 * Temporarily stops counting time
	 */
	public void pause() {
		if (paused)
			return;
		paused = true;
		timeOnPause = System.currentTimeMillis();
	}
	/**
	 * Resumes counting time
	 */
	public void unPause() {
		if (!paused)
			return;
		paused = false;
		totalPauseDeficit += System.currentTimeMillis()-timeOnPause;
		sessionPauseDeficit+=System.currentTimeMillis()-timeOnPause;
	}
}
