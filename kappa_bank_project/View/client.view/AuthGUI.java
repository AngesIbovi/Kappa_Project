package client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import model.query.AuthenticationQuery;
import model.response.AuthenticationServerResponse;
import util.JsonImpl;
import util.KappaProperties;

/**
 * A Jframe used for the authentication phase.
 * @version R3 Sprint 2 - 29/04/2016
 * @Author Kappa-V
 * @Changes
 * 		R3 sprint 1 -> R3 sprint 2:</br>
 * 			-Moved the main method to the MainMenuGUI class
 */
@SuppressWarnings("serial") // Is not going to be serialized
public class AuthGUI extends JFrame {

	/**
	 * An interface used like the Runnable interface, but with parameters.
	 */
	public interface OnSuccessfulLoginRunnable {
		public void run(Socket S, int authorization_level);
	}
	
	/**
	 * Main constructor. Initializes the socket connection to the server, plans its own cleanup process, 
	 * positions the various Swing components properly, and handles the authentication process in an event handler.
	 * @param onSuccesfulLogin - code to be executed when the user succesfully logs in. 
	 * In example, this can be used to display the main menu. 
	 * @throws IOException - if the server is unavailable.
	 * @throws NullPointerException - if JsonImpl.init() had not been called yet.
	 */
	public AuthGUI(final OnSuccessfulLoginRunnable onSuccessfulLogin) throws IOException, NullPointerException {
		final AuthGUI thisObject = this;
		
		/* Network connection */
		
		// Socket initialization
		Properties prop = KappaProperties.getInstance();
		final Socket connection = new Socket("localhost", Integer.parseInt(prop.getProperty("SERVER_PORT")));
		final PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
		final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		
		// Cleanup planning  
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// TODO: check if on successful login, when the auth window gets disposed, this listener is called
		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				if(e.getNewState() == WindowEvent.WINDOW_CLOSED) {
					out.println("BYE");
					try {
						connection.close();
					} catch (IOException e1) {
						e1.printStackTrace(); // For debug purposes.
					}
				}
			}
		});
		
		
		
		/* Swing components */
		
		// JFrame dimensions and position
		setSize(270, 127);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation((int) ((dimension.getWidth() - getWidth()) / 2), (int) ((dimension.getHeight() - getHeight()) / 2));
	    
	    // Layout
		final JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		// Login label
		final JLabel loginLabel = new JLabel("Login:");
		GridBagConstraints gbc_loginLabel = new GridBagConstraints();
		gbc_loginLabel.insets = new Insets(0, 0, 5, 5);
		gbc_loginLabel.anchor = GridBagConstraints.EAST;
		gbc_loginLabel.gridx = 0;
		gbc_loginLabel.gridy = 1;
		contentPane.add(loginLabel, gbc_loginLabel);
		
		// Login text field
		final JTextField loginField = new JTextField();
		GridBagConstraints gbc_loginField = new GridBagConstraints();
		gbc_loginField.insets = new Insets(0, 0, 5, 5);
		gbc_loginField.fill = GridBagConstraints.HORIZONTAL;
		gbc_loginField.gridx = 1;
		gbc_loginField.gridy = 1;
		contentPane.add(loginField, gbc_loginField);
		loginField.setColumns(10);
		
		// Wrong login label
		final JLabel wrongLoginLabel = new JLabel("Wrong login");
		wrongLoginLabel.setForeground(Color.RED);
		GridBagConstraints gbc_wrongLoginLabel = new GridBagConstraints();
		gbc_wrongLoginLabel.anchor = GridBagConstraints.WEST;
		gbc_wrongLoginLabel.insets = new Insets(0, 0, 5, 0);
		gbc_wrongLoginLabel.gridwidth = 2;
		gbc_wrongLoginLabel.gridx = 2;
		gbc_wrongLoginLabel.gridy = 1;
		contentPane.add(wrongLoginLabel, gbc_wrongLoginLabel);
		wrongLoginLabel.setVisible(false);
		
		// Password label
		final JLabel passwordLabel = new JLabel("Password");
		GridBagConstraints gbc_passwordLabel = new GridBagConstraints();
		gbc_passwordLabel.anchor = GridBagConstraints.EAST;
		gbc_passwordLabel.insets = new Insets(0, 0, 5, 5);
		gbc_passwordLabel.gridx = 0;
		gbc_passwordLabel.gridy = 2;
		contentPane.add(passwordLabel, gbc_passwordLabel);
		
		// Password field
		final JPasswordField passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 2;
		contentPane.add(passwordField, gbc_passwordField);
		
		// Wrong password label
		final JLabel wrongPasswordLabel = new JLabel("Wrong password");
		wrongPasswordLabel.setForeground(Color.RED);
		GridBagConstraints gbc_wrongPasswordLabel = new GridBagConstraints();
		gbc_wrongPasswordLabel.anchor = GridBagConstraints.WEST;
		gbc_wrongPasswordLabel.insets = new Insets(0, 0, 5, 0);
		gbc_wrongPasswordLabel.gridwidth = 2;
		gbc_wrongPasswordLabel.gridx = 2;
		gbc_wrongPasswordLabel.gridy = 2;
		contentPane.add(wrongPasswordLabel, gbc_wrongPasswordLabel);
		wrongPasswordLabel.setVisible(false);
		
		// Connect button
		final JButton connectButton = new JButton("Connect");
		GridBagConstraints gbc_connectButton = new GridBagConstraints();
		gbc_connectButton.insets = new Insets(0, 0, 5, 5);
		gbc_connectButton.gridx = 1;
		gbc_connectButton.gridy = 3;
		contentPane.add(connectButton, gbc_connectButton);
		
		
		
		/* Connection process */
		SwingUtilities.getRootPane(connectButton).setDefaultButton(connectButton); // Makes it possible to press enter
		
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() { // Starting a thread is long, so we need to clear the eventqueue first
					public void run() {
						new Thread(new Runnable() { // We launch a new thread for this treatment, so that the GUI can still update. This new thread will be the host for the onSuccessfulLogin callable 
							public void run() {
								// Re-initializing the error labels
								wrongPasswordLabel.setVisible(false);
								wrongLoginLabel.setVisible(false);
								
								try {
									// Sending the login credentials over to the server
									AuthenticationQuery query = new AuthenticationQuery(loginField.getText(), new String(passwordField.getPassword()));
									out.println(query.toString());
									
									// Receiving the server's response
									String message = in.readLine();
									
									//Treating the server's response
									try {
										// Prefix and content detection
										int prefixEnd = message.indexOf(' ');
										
										if(prefixEnd == -1) {
											throw new Exception("No prefix");
										}
										
										String prefix = message.substring(0, prefixEnd);
										String content = message.substring(prefixEnd + 1);
										
										// Prefix identification
										switch(prefix) {
										case "ERR":
											JOptionPane.showMessageDialog(thisObject, "Format error. Try downloading the newest version.");
											break;
										
										case "OK":
											// De-serialization
											AuthenticationServerResponse response = JsonImpl.fromJson(content, AuthenticationServerResponse.class);
											
											// Treatment
											switch(response.getStatus()) {
											
											// Successful connection
											case OK:
												EventQueue.invokeLater(new Runnable() {
													public void run() {
														thisObject.dispose();
													}
												});
												onSuccessfulLogin.run(connection, response.getYour_authorization_level()); // This is where the callable is used
												break;
											
											// Unsuccessful connection attempt
											case KO:
												// We can conclude on the nature of the error based on the wrong_id attribute of the response.
												if(response.isWrong_id()) {
													wrongLoginLabel.setVisible(true);
												} else {
													wrongPasswordLabel.setVisible(true);
												}
												break;
											}
											break;
										
										default:
											throw new Exception("Unknown prefix");
										}
									} catch (Exception e1) {
										JOptionPane.showMessageDialog(thisObject, "Unknown response format. Please try again later or download the newest version.");
									}
								} catch (IOException e1) {
									JOptionPane.showMessageDialog(thisObject, "Unable to connect to the server. Please try again later.");
								}
							}
						}).start();
					}
				});
			}
		});
	}
}
