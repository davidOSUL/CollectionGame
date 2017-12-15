package game;

import java.io.Serializable;

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
	public SessionTimeManager() {
		
	}
	public SessionTimeManager(long totalElapsedTime) {
		this.timeElapsedOnStartup = totalElapsedTime;
	}
	public long getTotalGameTime() {
		return totalGameTime;
	}
	public long getSessionGameTime() {
		return sessionGameTime;
	}
	public void updateGameTime() {
		if (!paused) {
		totalGameTime = timeElapsedOnStartup+(System.currentTimeMillis() - startOfSession)-pauseDeficit;
		sessionGameTime = (System.currentTimeMillis() - startOfSession)-sessionPauseDeficit;
		}
	}
	public void pause() {
		paused = true;
		timeOnPause = System.currentTimeMillis();
	}
	public void unPause() {
		paused = false;
		pauseDeficit += System.currentTimeMillis()-timeOnPause;
		sessionPauseDeficit+=System.currentTimeMillis()-timeOnPause;
	}
}
