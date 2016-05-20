package view;

import model.SessionInformation;

/**
 * Objects implementing this interface need to be initialized by feeding them a socket before they can be used.
 * @author Kappa-V
 * @version R3 sprint 3 - 08/05/2016
 */
public interface SessionSpecific {
	/**
	 * All Swing event listeners are created in a tab's setSocket method.</br>
	 * This method is only called if the user successfully connected, and has a sufficient authorization level.
	 * @param sessionInformation : This session's session information.
	 */
	public void setSessionInformation(SessionInformation sessionInformation);
}
