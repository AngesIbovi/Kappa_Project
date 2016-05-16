package view;

import javax.swing.JPanel;

/**
 * The class each individual module extends. They are then fed to a MainMenuGUI.
 * @author Kappa-V
 * @version R3 sprint 3 - 08/05/2016
 * @changes
 * 		R3 sprint 2 -> R3 sprint 3:</br>
 * 			-setSocket removed. Now implements InitializableObject
 */
@SuppressWarnings("serial") // Is not going to be serialized
public abstract class Tab extends JPanel implements SessionSpecific {
	/**
	 * This tab's name, which will be displayed in the JTabbedPane from MainMenuGUI
	 */
	public final String name;
	
	/**
	 * The authorization level needed to access this tab. It will only be displayed if the user's authorization level is sufficient.
	 */
	public final int authorizationLevel;
	
	/**
	 * All Swing components are initialized during a tab's constructor.
	 * @param name : This tab's name, which will be displayed in the JTabbedPane from MainMenuGUI
	 * @param authorizationLevel : The authorization level needed to access this tab. It will only be displayed if the user's authorization level is sufficient.
	 */
	public Tab(String name, int authorizationLevel) {
		this.name = name;
		this.authorizationLevel = authorizationLevel;
	}
}
