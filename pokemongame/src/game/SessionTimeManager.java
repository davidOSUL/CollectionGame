package game;

import java.io.Serializable;

/**
 * Manages the Game Time
 * @author David O'Sullivan
 */
public class SessionTimeManager implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long timeElapsedOnStartup = 0;
	private long totalGameTime = 0;
	private long pauseDeficit = 0;
	private transient long sessionGameTime = 0;
	private transient long timeOnPause = 0;
	private transient boolean paused = false;
	private transient final long startOfSession = System.currentTimeMillis();
	private transient long sessionPauseDeficit = 0;
	/**
	 * Create a new Time Manager with 0 already elapsed time. Should be called at the beginning of a new game. 
	 */
	public SessionTimeManager() {
		
	}
	/**
	 * Creates a new Time Manager with some given elapsed time. Should be called at the beginning of a new session. This will restart the sessionTIme to 0, but will continue counting the total Game TIme from the value given
	 * @param totalElapsedTime the total game time so far
	 */
	public SessionTimeManager(long totalElapsedTime) {
		this.timeElapsedOnStartup = totalElapsedTime;
	}
	/**
	 * @return The total Game Time through all sessions
	 */
	public long getTotalGameTime() {
		return totalGameTime;
	}
	/**
	 * @return The total time elapsed this session
	 */
	public long getSessionGameTime() {
		return sessionGameTime;
	}
	/**
	 * To be called on every game tick, updates the elapsed time
	 */
	public void updateGameTime() {
		if (!paused) {
		totalGameTime = timeElapsedOnStartup+(System.currentTimeMillis() - startOfSession)-pauseDeficit;
		sessionGameTime = (System.currentTimeMillis() - startOfSession)-sessionPauseDeficit;
		}
	}
	/**
	 * Temporarily stops counting time
	 */
	public void pause() {
		paused = true;
		timeOnPause = System.currentTimeMillis();
	}
	/**
	 * Resumes counting time
	 */
	public void unPause() {
		paused = false;
		pauseDeficit += System.currentTimeMillis()-timeOnPause;
		sessionPauseDeficit+=System.currentTimeMillis()-timeOnPause;
	}
}
