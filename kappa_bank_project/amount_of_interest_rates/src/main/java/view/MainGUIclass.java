package view;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollBar;
import javax.swing.JList;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import model.response.GetLoanServerResponse.RateList;
import util.JsonImpl;
import util.KappaProperties;
import model.query.GetLoanQuery;
import model.response.GetLoanServerResponse;
import model.response.GetLoanServerResponse.RateList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SelectableChannel;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes.Name;

import model.query.GetLoanQuery;

public class MainGUIclass extends JFrame {

		//this.setlayout(new BorderLayout());
	// private JFrame frame;
	// private JTextField textField;
	// private JList list;
	// private JTextField textField_2;
	// private JTextField textField_1;
		

	 JFrame frame;

     JTextField textField;
     JTextField textField_1;


	public MainGUIclass() throws ClassNotFoundException, SQLException,
			NumberFormatException, IOException {
		super("Calcule des taux");
		final MainGUIclass thisObject = this;
		// Initializing tools
		// System.out.println(args);
		/* Network connection */
		KappaProperties.init();
		JsonImpl.init();

		// Socket initialization
		Properties prop = KappaProperties.getInstance();
		// System.out.println(prop);
		final Socket connection = new Socket("localhost", Integer.parseInt(prop
				.getProperty("SERVER_PORT")));
		final PrintWriter out = new PrintWriter(connection.getOutputStream(),
				true);
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		// Cleanup planning
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// TODO: check if on successful login, when the auth window gets
		// disposed, this listener is called
		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				// TODO Auto-generated method stub
				if (e.getNewState() == WindowEvent.WINDOW_CLOSED) {
					out.println("BYE");
					try {
						connection.close();
					} catch (IOException e1) {
						e1.printStackTrace(); // For debug purposes.
					}
				}
			}

		});

		// JPanel chartPanel = createChartPanel();
		// add(chartPanel, BorderLayout.CENTER);

		// JPanel dimensions and position
		
        // PAn1
		final JPanel panel = new JPanel();
		panel.setBounds(22, 22, 400, 530);
		getContentPane().add(panel, BorderLayout.NORTH);
			
//		//PAn2
//		final JPanel panel2 = new JPanel();
//		panel.setBounds(22, 22, 400, 530);
//		getContentPane().add(panel2, BorderLayout.CENTER);
		
		//PAn3
		final JPanel panel3 = new JPanel(new GridLayout(2,2));
		panel3.setBounds(22, 22, 400, 530);
		getContentPane().add(panel3);
		
		//PAn4
		final JPanel panel4 = new JPanel();
		panel.setBounds(22, 22, 400, 530);
		getContentPane().add(panel4);



		frame = new JFrame();
		frame.setAlwaysOnTop(true);
		frame.getContentPane();
		//frame.setLayout(new GridLayout(4,2));
		

		textField = new JTextField();
		textField.setBounds(0, 0, 80, 20);
		
	
		//panel3.add(textField);
		

		textField_1 = new JTextField();
		textField_1.setBounds(199, 184, 80, 20);
		frame.getContentPane().add(textField_1);
		
		textField_1.setColumns(10);
		panel3.add(textField_1);

		JButton btnValider = new JButton("Valider");
		btnValider.setBounds(66, 228, 89, 23);
		frame.getContentPane().add(btnValider);
		panel4.add(btnValider);

		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.setBounds(224, 228, 89, 23);
		frame.getContentPane().add(btnAnnuler);
		panel4.add(btnAnnuler);



		JLabel lblNiveauDeRisque = new JLabel("Niveau de risque");
		lblNiveauDeRisque.setBounds(23, 187, 100, 14); 
		panel3.add(lblNiveauDeRisque); 
		panel3.add(textField);

		
		//LoanType(Label)
		JLabel lblTypesDuPrt = new JLabel("Types du prêt");
		lblTypesDuPrt.setBounds(26, 85, 82, 14);
		frame.getContentPane().add(lblTypesDuPrt);
		panel.add(lblTypesDuPrt);

		final JComboBox<RateList> cbScenChoice = new JComboBox<RateList>();
		cbScenChoice.setEditable(true);
		cbScenChoice.setFont(new Font("Tahoma", Font.PLAIN, 12));
		// this.add(cbScenChoice);
		panel.add(cbScenChoice);

		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		//getContentPane().setLayout(null);
		cbScenChoice.setSelectedItem("- Choisir -");
		setSize(1240, 680);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		
		
		
		//ReferenceRate(Label)
		JLabel lblTauxIndicateur = new JLabel("Taux indicateur");
		lblTauxIndicateur.setBounds(26, 126, 100, 17);
		frame.getContentPane().add(lblTauxIndicateur);
		panel3.add(lblTauxIndicateur);
		
		
		
		
		
		

		// Sending the account_id over to the server
		GetLoanQuery query = new GetLoanQuery("-1");
		out.println(query.toString());
		// Receiving the server's response
		String message = in.readLine();
		System.out.print(query.toString());
		// Treating the server's response
		try {
			// Prefix and content detection
			int prefixEnd = message.indexOf(' ');

			if (prefixEnd == -1) {
				throw new Exception("No prefix");
			}

			String prefix = message.substring(0, prefixEnd);
			String content = message.substring(prefixEnd + 1);

			// System.out.print(message);
			// Prefix identification
			switch (prefix) {
			case "ERR":
				System.out.println(query);

				JOptionPane.showMessageDialog(thisObject,
						"Format error. Try downloading the newest version.");
				break;

			case "OK":
				// De-serialization
				GetLoanServerResponse response = JsonImpl.fromJson(content,
						GetLoanServerResponse.class);
				// System.out.println(response);
				List<RateList> listSims = response.getRate_list();
				for (int i = 0; i < listSims.toArray().length; i++) {
					// System.out.println(listSims.toArray()[i]);
					cbScenChoice.addItem((RateList) listSims.toArray()[i]);
				}
				// System.out.println(listSims.toArray().length);

				// System.out.println(theList);
				// for(int i=0; i<=theList.length;i++){
				// System.out.println(theList[i]);
				// }
				// cbScenChoice.setModel(response);
				break;

			default:
				throw new Exception("Unknown prefix");
			}
		} catch (Exception e1) {
			System.out.print(e1);
			JOptionPane
					.showMessageDialog(
							thisObject,
							"Unknown response format. Please try again later or download the newest version.");
		}

		// Action on select item in combox list of scenario
		cbScenChoice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg) {
				// TODO Auto-generated method stub
				EventQueue.invokeLater(new Runnable() { // Starting a thread is
														// long, so we need to
														// clear the eventqueue
														// first
							public void run() {
								new Thread(new Runnable() { // We launch a new
															// thread for this
															// treatment, so
															// that the GUI can
															// still update.
															// This new thread
															// will be the host
															// for the
															// onSuccessfulLogin
															// callable
											public void run() {
												try {
													System.out
															.print(((RateList) cbScenChoice
																	.getSelectedItem())
																	.getLoan_type_id());
												} catch (NumberFormatException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
										}).start();
							}
						});
			}

		});

	}

	public static void main(String[] args) {
		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainGUIclass mr = null;
				try {
					mr = new MainGUIclass();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mr.setVisible(true);

			}
		});

	}
}