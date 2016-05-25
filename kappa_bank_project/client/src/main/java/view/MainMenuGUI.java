package view;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import comparison.ComparisonGUI;
import model.SessionInformation;
import util.JsonImpl;
import util.KappaProperties;

/**
 * A GUI containing tabs.</br> 
 * You can navigate between tabs by clicking on their names at the top of the frame.
 * @author Kappa-V
 * @version R3 sprint 2 - 01/05/2016
 * @changes
 * 		R3 sprint 2 -> R3 sprint 3:</br>
 * 			-Now implements SessionSpecific instead of OnSuccessfulLoginRunnable
 */
@SuppressWarnings("serial") // Is not going to be serialized
public class MainMenuGUI extends JDialog implements SessionSpecific { // JDialog disables the minimize and maximize buttons
	/**
	 * The set of all tabs that could be used.</br>
	 * The user's authorizationLevel is used to determine which ones to display.
	 */
	private Set<Tab> tabs;
	
	/**
	 * This session's socket, initialized in AuthGUI.OnSuccessfulLoginRunable.run
	 */
	private Socket socket = null;
	
	/**
	 * The tabbed pane.</br>
	 * It is initialized in the constructor, and filled in AuthGUI.OnSuccessfulLoginRunable.run
	 */
	private JTabbedPane tabbedPane;
	
	
	/**
	 * Default constructor.
	 * @param tabs : the set of tabs from which a subset will be selected when the user's authorization level is known.
	 */
	public MainMenuGUI(Set<Tab> tabs) {
		this.tabs = tabs;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// Necessary because setDefaultCloseOperation doesn't exist for JDialogs
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                if(socket != null) {
                	if(!socket.isClosed()) {
                		try {
                			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                			out.println("BYE");
                			socket.close();
                		} catch (IOException e) {
                			// Do nothing
                		}
                	}
                }
            }
        });
		
		// full screen parameters
		setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());
		setResizable(false);
		
		// Layout
		tabbedPane = new JTabbedPane();
		setContentPane(tabbedPane);
	}

	/**
	 * @param authorization_level : used to determine which tabs to display
	 * @param S : the socket for this session
	 */
	@Override
	public void setSessionInformation(SessionInformation sessionInformation) {
		this.socket = sessionInformation.getSocket();
		
		for(Tab t : tabs) {
			if(t.authorizationLevel <= sessionInformation.getAuthorization_level()) {
				t.setSessionInformation(sessionInformation);
				tabbedPane.addTab(t.name, t);
			}
		}
		
		setVisible(true);
	}
	
	/**
	 * Runs everything properly
	 * @param tabs : feeds it to a MainMenuGUI which will be displayed after on through an AuthGUI.
	 */
	public static void launch(Set<Tab> tabs) {
		// Tools initialization
		try {
			KappaProperties.init();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		JsonImpl.init();
		
		
		try {
			// GUI initialization
			final AuthGUI frame = new AuthGUI(new MainMenuGUI(tabs));
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					frame.setVisible(true);
				}
			});
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Unable to connect to the server. Please try again later.");
		}
	}
	
	/**
	 * The main method for the client.</br>
	 * Calls the launch method on a set of Tabs each corresponding to one of the individual UCs. 
	 * @param args : not used
	 */
	public static void main(String[] args) {
		Set<Tab> tabs = new HashSet<>();

		Tab t = new ComparisonGUI();
		tabs.add(t);

		Tab t3 = new VariableLoanGUI();
		tabs.add(t3);
		
		Tab t5 = new AnalyseOfIndicatorsGUI();
		tabs.add(t5);
		launch(tabs);
	}
}
