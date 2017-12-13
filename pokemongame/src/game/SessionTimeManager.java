package game;

import java.io.Serializable;

public class SessionTimeManager implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long timeElapsedOnStartup = 0;
	private long totalGameTime = 0;
	private transient final long startOfSession = System.currentTimeMillis();
	public SessionTimeManager() {
		
	}
	public SessionTimeManager(long totalElapsedTime) {
		this.timeElapsedOnStartup = totalElapsedTime;
	}
	public long getTotalGameTime() {
		return totalGameTime;
	}
	public long getSessionGameTime() {
		return (System.currentTimeMillis() - startOfSession);
	}
	public void updateGameTime() {
		totalGameTime = timeElapsedOnStartup+(System.currentTimeMillis() - startOfSession);
	}
}
