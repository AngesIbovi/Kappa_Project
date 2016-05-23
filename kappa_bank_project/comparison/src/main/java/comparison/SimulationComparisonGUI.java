package comparison;



import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import comparison.elements.AmortizationElement;
import comparison.elements.MainVolumesElement;
import comparison.elements.SimulationComparisonElement;
import comparison.elements.SimulationListElement;
import model.SessionInformation;
import model.query.ClientQuery;
import model.query.GetSimQuery;
import model.response.GetSimServerResponse;
import model.response.GetSimsServerResponse.SimulationIdentifier;
import util.JsonImpl;
import util.KappaProperties;
import view.SessionSpecific;

/**
 * The GUI for comparing two or more simulations whose IDs are already known.</br>
 * If you need to go through the id selection process, use ComparisonGUI instead.
 * @see ComparisonGUI
 * @author Kappa-V
 * @version R3 sprint 3 - 13/05/2016
 */
@SuppressWarnings("serial") // Is not going to get serialized.
public abstract class SimulationComparisonGUI extends JPanel implements SessionSpecific {
	private final SimulationComparisonGUI thisObject = this;
	private final JButton returnButton;
	private Socket socket;
	private List<GetSimServerResponse> simulations;
	private final List<SimulationComparisonElement> elements = new ArrayList<>();
	

	/**
	 * This method is called when the user presses the return button.</br>
	 * The idea is that the class which instantiates a SimulationComparisonGUI defines the behavior it wants here.</br>
	 * If the behavior you want to define uses long calculations, waits for user input or network responses, do not forget to start a new thread.
	 */
	public abstract void onReturn();
	
	/**
	 * Fetches the simulation's details in the database.</br>
	 * Updates the simulations attribute. Calls the private method compare().
	 * @param L - the list of IDs of the simulations the user wants to compare
	 */
	public void getSimulations(final List<SimulationIdentifier> L) {
		// All network operations are carried out in a thread so that the GUI doesn't freeze.
		new Thread(new Runnable() {
			public void run() {
				// Flow initialization
				PrintWriter out;
				BufferedReader in;
				try {
					out = new PrintWriter(socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch (Exception e1) { // Reached if an IO exception occurs, or if the socket is not connected anymore
					JOptionPane.showMessageDialog(thisObject, "Erreur: connection au serveur interrompue. Vérifiez votre connection Internet, puis essayez de vous re-connecter.");
					return;
				}

				// Server transaction
				simulations = new ArrayList<>();
				for(SimulationIdentifier S : L) {
					ClientQuery query = new GetSimQuery(S.getId());
					out.println(query.toString());
					try {
						String message = in.readLine();
						
						// Prefix/Content identification
						int prefixEnd = message.indexOf(' ');
						if(prefixEnd == -1) {
							throw new Exception("No prefix");
						}
						
						String prefix = message.substring(0, prefixEnd);
						String content = message.substring(prefixEnd + 1);
						
						switch (prefix) {
						case "OK":
							GetSimServerResponse response = JsonImpl.fromJson(content, GetSimServerResponse.class);
							simulations.add(response);
							break;
						default:
							throw new Exception("ERR or UNAUTHORIZED");
						}
					} catch (IOException e) {
						JOptionPane.showMessageDialog(thisObject, "Erreur: Connexion au serveur interrompue. Vérifiez votre connection Internet, puis essayez de vous re-connecter.");
						return;
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(thisObject, "Erreur: Essayez de télécharger la nouvelle version de ce logiciel.");
						return;
					}
				}
				
				// GUI update
				compare();
			}
		}).start();
	}
	
	/**
	 * Initializes all Swing components.
	 */
	public SimulationComparisonGUI() {
		// Elements
		SimulationListElement simulationList = new SimulationListElement();
		elements.add(simulationList);
		
		MainVolumesElement mainVolumes = new MainVolumesElement();
		elements.add(mainVolumes);
		
		AmortizationElement amortization = new AmortizationElement();
		elements.add(amortization);

		
		// Layout
		returnButton = new JButton("Retour");
		this.add(returnButton);
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onReturn();
			}
		});
		
		for(SimulationComparisonElement e : elements) {
			this.add(e);
		}
	}
	
	/**
	 * Updates the various elements
	 */
	private void compare() {
		for(SimulationComparisonElement e : elements) {
			e.setSimulations(simulations);
		}
	}
	
	@Override
	public void setSessionInformation(SessionInformation sessionInformation) {
		this.socket = sessionInformation.getSocket();
	}
	
	/**
	 * TEMPORARY DEBUG MAIN
	 */
	public static void main(String[] args) {
		try {
			KappaProperties.init();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		JsonImpl.init();
		
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());
		
		SimulationComparisonGUI gui = new SimulationComparisonGUI() {
			@Override
			public void onReturn() {
				frame.dispose();
			}
		};
		gui.simulations = new ArrayList<>();
		gui.simulations.add(JsonImpl.fromJson("{\"name\":\"Simu achat maison 1\",\"id\":\"3\",\"events\":[],\"repayments\":[{\"date\":\"ao�t 28, 2016\",\"capital\":212.12,\"interest\":128.33333,\"insurance\":1.5},{\"date\":\"sept. 28, 2016\",\"capital\":212.12,\"interest\":127.36112,\"insurance\":1.5},{\"date\":\"oct. 28, 2016\",\"capital\":212.12,\"interest\":126.38891,\"insurance\":1.5},{\"date\":\"nov. 28, 2016\",\"capital\":212.12,\"interest\":125.416695,\"insurance\":1.5},{\"date\":\"d�c. 28, 2016\",\"capital\":212.12,\"interest\":124.44448,\"insurance\":1.5},{\"date\":\"janv. 28, 2017\",\"capital\":212.12,\"interest\":123.47227,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2017\",\"capital\":212.12,\"interest\":122.50005,\"insurance\":1.5},{\"date\":\"mars 28, 2017\",\"capital\":212.12,\"interest\":121.52784,\"insurance\":1.5},{\"date\":\"avr. 28, 2017\",\"capital\":212.12,\"interest\":120.555626,\"insurance\":1.5},{\"date\":\"mai 28, 2017\",\"capital\":212.12,\"interest\":119.58343,\"insurance\":1.5},{\"date\":\"juin 28, 2017\",\"capital\":212.12,\"interest\":118.611206,\"insurance\":1.5},{\"date\":\"juil. 28, 2017\",\"capital\":212.12,\"interest\":117.639,\"insurance\":1.5},{\"date\":\"ao�t 28, 2017\",\"capital\":212.12,\"interest\":116.66679,\"insurance\":1.5},{\"date\":\"sept. 28, 2017\",\"capital\":212.12,\"interest\":115.69457,\"insurance\":1.5},{\"date\":\"oct. 28, 2017\",\"capital\":212.12,\"interest\":114.72236,\"insurance\":1.5},{\"date\":\"nov. 28, 2017\",\"capital\":212.12,\"interest\":113.750145,\"insurance\":1.5},{\"date\":\"d�c. 28, 2017\",\"capital\":212.12,\"interest\":112.77793,\"insurance\":1.5},{\"date\":\"janv. 28, 2018\",\"capital\":212.12,\"interest\":111.80572,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2018\",\"capital\":212.12,\"interest\":110.8335,\"insurance\":1.5},{\"date\":\"mars 28, 2018\",\"capital\":212.12,\"interest\":109.86129,\"insurance\":1.5},{\"date\":\"avr. 28, 2018\",\"capital\":212.12,\"interest\":108.88908,\"insurance\":1.5},{\"date\":\"mai 28, 2018\",\"capital\":212.12,\"interest\":107.91687,\"insurance\":1.5},{\"date\":\"juin 28, 2018\",\"capital\":212.12,\"interest\":106.94465,\"insurance\":1.5},{\"date\":\"juil. 28, 2018\",\"capital\":212.12,\"interest\":105.97244,\"insurance\":1.5},{\"date\":\"ao�t 28, 2018\",\"capital\":212.12,\"interest\":105.00022,\"insurance\":1.5},{\"date\":\"sept. 28, 2018\",\"capital\":212.12,\"interest\":104.028015,\"insurance\":1.5},{\"date\":\"oct. 28, 2018\",\"capital\":212.12,\"interest\":103.05579,\"insurance\":1.5},{\"date\":\"nov. 28, 2018\",\"capital\":212.12,\"interest\":102.08359,\"insurance\":1.5},{\"date\":\"d�c. 28, 2018\",\"capital\":212.12,\"interest\":101.11138,\"insurance\":1.5},{\"date\":\"janv. 28, 2019\",\"capital\":212.12,\"interest\":100.13917,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2019\",\"capital\":212.12,\"interest\":99.166954,\"insurance\":1.5},{\"date\":\"mars 28, 2019\",\"capital\":212.12,\"interest\":98.19474,\"insurance\":1.5},{\"date\":\"avr. 28, 2019\",\"capital\":212.12,\"interest\":97.22253,\"insurance\":1.5},{\"date\":\"mai 28, 2019\",\"capital\":212.12,\"interest\":96.25031,\"insurance\":1.5},{\"date\":\"juin 28, 2019\",\"capital\":212.12,\"interest\":95.2781,\"insurance\":1.5},{\"date\":\"juil. 28, 2019\",\"capital\":212.12,\"interest\":94.305885,\"insurance\":1.5},{\"date\":\"ao�t 28, 2019\",\"capital\":212.12,\"interest\":93.33367,\"insurance\":1.5},{\"date\":\"sept. 28, 2019\",\"capital\":212.12,\"interest\":92.36146,\"insurance\":1.5},{\"date\":\"oct. 28, 2019\",\"capital\":212.12,\"interest\":91.38925,\"insurance\":1.5},{\"date\":\"nov. 28, 2019\",\"capital\":212.12,\"interest\":90.41703,\"insurance\":1.5},{\"date\":\"d�c. 28, 2019\",\"capital\":212.12,\"interest\":89.444824,\"insurance\":1.5},{\"date\":\"janv. 28, 2020\",\"capital\":212.12,\"interest\":88.47262,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2020\",\"capital\":212.12,\"interest\":87.5004,\"insurance\":1.5},{\"date\":\"mars 28, 2020\",\"capital\":212.12,\"interest\":86.52819,\"insurance\":1.5},{\"date\":\"avr. 28, 2020\",\"capital\":212.12,\"interest\":85.55598,\"insurance\":1.5},{\"date\":\"mai 28, 2020\",\"capital\":212.12,\"interest\":84.58376,\"insurance\":1.5},{\"date\":\"juin 28, 2020\",\"capital\":212.12,\"interest\":83.61155,\"insurance\":1.5},{\"date\":\"juil. 28, 2020\",\"capital\":212.12,\"interest\":82.639336,\"insurance\":1.5},{\"date\":\"ao�t 28, 2020\",\"capital\":212.12,\"interest\":81.66712,\"insurance\":1.5},{\"date\":\"sept. 28, 2020\",\"capital\":212.12,\"interest\":80.69491,\"insurance\":1.5},{\"date\":\"oct. 28, 2020\",\"capital\":212.12,\"interest\":79.722694,\"insurance\":1.5},{\"date\":\"nov. 28, 2020\",\"capital\":212.12,\"interest\":78.75048,\"insurance\":1.5},{\"date\":\"d�c. 28, 2020\",\"capital\":212.12,\"interest\":77.77827,\"insurance\":1.5},{\"date\":\"janv. 28, 2021\",\"capital\":212.12,\"interest\":76.80606,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2021\",\"capital\":212.12,\"interest\":75.83385,\"insurance\":1.5},{\"date\":\"mars 28, 2021\",\"capital\":212.12,\"interest\":74.861626,\"insurance\":1.5},{\"date\":\"avr. 28, 2021\",\"capital\":212.12,\"interest\":73.88941,\"insurance\":1.5},{\"date\":\"mai 28, 2021\",\"capital\":212.12,\"interest\":72.9172,\"insurance\":1.5},{\"date\":\"juin 28, 2021\",\"capital\":212.12,\"interest\":71.94497,\"insurance\":1.5},{\"date\":\"juil. 28, 2021\",\"capital\":212.12,\"interest\":70.972755,\"insurance\":1.5},{\"date\":\"ao�t 28, 2021\",\"capital\":212.12,\"interest\":70.00054,\"insurance\":1.5},{\"date\":\"sept. 28, 2021\",\"capital\":212.12,\"interest\":69.02833,\"insurance\":1.5},{\"date\":\"oct. 28, 2021\",\"capital\":212.12,\"interest\":68.05611,\"insurance\":1.5},{\"date\":\"nov. 28, 2021\",\"capital\":212.12,\"interest\":67.08389,\"insurance\":1.5},{\"date\":\"d�c. 28, 2021\",\"capital\":212.12,\"interest\":66.11167,\"insurance\":1.5},{\"date\":\"janv. 28, 2022\",\"capital\":212.12,\"interest\":65.13946,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2022\",\"capital\":212.12,\"interest\":64.167244,\"insurance\":1.5},{\"date\":\"mars 28, 2022\",\"capital\":212.12,\"interest\":63.19502,\"insurance\":1.5},{\"date\":\"avr. 28, 2022\",\"capital\":212.12,\"interest\":62.222805,\"insurance\":1.5},{\"date\":\"mai 28, 2022\",\"capital\":212.12,\"interest\":61.250584,\"insurance\":1.5},{\"date\":\"juin 28, 2022\",\"capital\":212.12,\"interest\":60.27837,\"insurance\":1.5},{\"date\":\"juil. 28, 2022\",\"capital\":212.12,\"interest\":59.306152,\"insurance\":1.5},{\"date\":\"ao�t 28, 2022\",\"capital\":212.12,\"interest\":58.33394,\"insurance\":1.5},{\"date\":\"sept. 28, 2022\",\"capital\":212.12,\"interest\":57.36172,\"insurance\":1.5},{\"date\":\"oct. 28, 2022\",\"capital\":212.12,\"interest\":56.389507,\"insurance\":1.5},{\"date\":\"nov. 28, 2022\",\"capital\":212.12,\"interest\":55.417286,\"insurance\":1.5},{\"date\":\"d�c. 28, 2022\",\"capital\":212.12,\"interest\":54.44507,\"insurance\":1.5},{\"date\":\"janv. 28, 2023\",\"capital\":212.12,\"interest\":53.47285,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2023\",\"capital\":212.12,\"interest\":52.50063,\"insurance\":1.5},{\"date\":\"mars 28, 2023\",\"capital\":212.12,\"interest\":51.528416,\"insurance\":1.5},{\"date\":\"avr. 28, 2023\",\"capital\":212.12,\"interest\":50.5562,\"insurance\":1.5},{\"date\":\"mai 28, 2023\",\"capital\":212.12,\"interest\":49.58398,\"insurance\":1.5},{\"date\":\"juin 28, 2023\",\"capital\":212.12,\"interest\":48.611767,\"insurance\":1.5},{\"date\":\"juil. 28, 2023\",\"capital\":212.12,\"interest\":47.639545,\"insurance\":1.5},{\"date\":\"ao�t 28, 2023\",\"capital\":212.12,\"interest\":46.66733,\"insurance\":1.5},{\"date\":\"sept. 28, 2023\",\"capital\":212.12,\"interest\":45.695114,\"insurance\":1.5},{\"date\":\"oct. 28, 2023\",\"capital\":212.12,\"interest\":44.722897,\"insurance\":1.5},{\"date\":\"nov. 28, 2023\",\"capital\":212.12,\"interest\":43.750675,\"insurance\":1.5},{\"date\":\"d�c. 28, 2023\",\"capital\":212.12,\"interest\":42.77846,\"insurance\":1.5},{\"date\":\"janv. 28, 2024\",\"capital\":212.12,\"interest\":41.806244,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2024\",\"capital\":212.12,\"interest\":40.834026,\"insurance\":1.5},{\"date\":\"mars 28, 2024\",\"capital\":212.12,\"interest\":39.86181,\"insurance\":1.5},{\"date\":\"avr. 28, 2024\",\"capital\":212.12,\"interest\":38.88959,\"insurance\":1.5},{\"date\":\"mai 28, 2024\",\"capital\":212.12,\"interest\":37.917377,\"insurance\":1.5},{\"date\":\"juin 28, 2024\",\"capital\":212.12,\"interest\":36.945156,\"insurance\":1.5},{\"date\":\"juil. 28, 2024\",\"capital\":212.12,\"interest\":35.97294,\"insurance\":1.5},{\"date\":\"ao�t 28, 2024\",\"capital\":212.12,\"interest\":35.00072,\"insurance\":1.5},{\"date\":\"sept. 28, 2024\",\"capital\":212.12,\"interest\":34.028507,\"insurance\":1.5},{\"date\":\"oct. 28, 2024\",\"capital\":212.12,\"interest\":33.05629,\"insurance\":1.5},{\"date\":\"nov. 28, 2024\",\"capital\":212.12,\"interest\":32.084072,\"insurance\":1.5},{\"date\":\"d�c. 28, 2024\",\"capital\":212.12,\"interest\":31.111855,\"insurance\":1.5},{\"date\":\"janv. 28, 2025\",\"capital\":212.12,\"interest\":30.139639,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2025\",\"capital\":212.12,\"interest\":29.167421,\"insurance\":1.5},{\"date\":\"mars 28, 2025\",\"capital\":212.12,\"interest\":28.1952,\"insurance\":1.5},{\"date\":\"avr. 28, 2025\",\"capital\":212.12,\"interest\":27.222984,\"insurance\":1.5},{\"date\":\"mai 28, 2025\",\"capital\":212.12,\"interest\":26.250769,\"insurance\":1.5},{\"date\":\"juin 28, 2025\",\"capital\":212.12,\"interest\":25.278551,\"insurance\":1.5},{\"date\":\"juil. 28, 2025\",\"capital\":212.12,\"interest\":24.306335,\"insurance\":1.5},{\"date\":\"ao�t 28, 2025\",\"capital\":212.12,\"interest\":23.334116,\"insurance\":1.5},{\"date\":\"sept. 28, 2025\",\"capital\":212.12,\"interest\":22.3619,\"insurance\":1.5},{\"date\":\"oct. 28, 2025\",\"capital\":212.12,\"interest\":21.38968,\"insurance\":1.5},{\"date\":\"nov. 28, 2025\",\"capital\":212.12,\"interest\":20.417465,\"insurance\":1.5},{\"date\":\"d�c. 28, 2025\",\"capital\":212.12,\"interest\":19.445248,\"insurance\":1.5},{\"date\":\"janv. 28, 2026\",\"capital\":212.12,\"interest\":18.47303,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2026\",\"capital\":212.12,\"interest\":17.500814,\"insurance\":1.5},{\"date\":\"mars 28, 2026\",\"capital\":212.12,\"interest\":16.528597,\"insurance\":1.5},{\"date\":\"avr. 28, 2026\",\"capital\":212.12,\"interest\":15.55638,\"insurance\":1.5},{\"date\":\"mai 28, 2026\",\"capital\":212.12,\"interest\":14.584163,\"insurance\":1.5},{\"date\":\"juin 28, 2026\",\"capital\":212.12,\"interest\":13.611945,\"insurance\":1.5},{\"date\":\"juil. 28, 2026\",\"capital\":212.12,\"interest\":12.639729,\"insurance\":1.5},{\"date\":\"ao�t 28, 2026\",\"capital\":212.12,\"interest\":11.667511,\"insurance\":1.5},{\"date\":\"sept. 28, 2026\",\"capital\":212.12,\"interest\":10.695293,\"insurance\":1.5},{\"date\":\"oct. 28, 2026\",\"capital\":212.12,\"interest\":9.723076,\"insurance\":1.5},{\"date\":\"nov. 28, 2026\",\"capital\":212.12,\"interest\":8.750859,\"insurance\":1.5},{\"date\":\"d�c. 28, 2026\",\"capital\":212.12,\"interest\":7.7786427,\"insurance\":1.5},{\"date\":\"janv. 28, 2027\",\"capital\":212.12,\"interest\":6.8064265,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2027\",\"capital\":212.12,\"interest\":5.83421,\"insurance\":1.5},{\"date\":\"mars 28, 2027\",\"capital\":212.12,\"interest\":4.8619933,\"insurance\":1.5},{\"date\":\"avr. 28, 2027\",\"capital\":212.12,\"interest\":3.8897762,\"insurance\":1.5},{\"date\":\"mai 28, 2027\",\"capital\":212.12,\"interest\":2.9175596,\"insurance\":1.5},{\"date\":\"juin 28, 2027\",\"capital\":212.12,\"interest\":1.945343,\"insurance\":1.5},{\"date\":\"juil. 28, 2027\",\"capital\":212.12,\"interest\":0.9731264,\"insurance\":1.5}],\"effectiveDate\":\"ao�t 28, 2016\",\"capital\":28000.0,\"remainingOwedCapital\":28000.0,\"repaymentFrequency\":12,\"remainingRepayments\":132,\"repaymentConstant\":212.0,\"amortizationType\":\"degressive\",\"loan_type_id\":\"2\",\"account_id\":\"3\"}", GetSimServerResponse.class));
		gui.simulations.add(JsonImpl.fromJson("{\"name\":\"Simu achat maison 2 - paiement ts les 4 mois\",\"id\":\"4\",\"events\":[],\"repayments\":[{\"date\":\"ao�t 28, 2016\",\"capital\":848.48,\"interest\":128.33333,\"insurance\":1.5},{\"date\":\"d�c. 28, 2016\",\"capital\":848.48,\"interest\":124.444466,\"insurance\":1.5},{\"date\":\"avr. 28, 2017\",\"capital\":848.48,\"interest\":120.5556,\"insurance\":1.5},{\"date\":\"ao�t 28, 2017\",\"capital\":848.48,\"interest\":116.666725,\"insurance\":1.5},{\"date\":\"d�c. 28, 2017\",\"capital\":848.48,\"interest\":112.77786,\"insurance\":1.5},{\"date\":\"avr. 28, 2018\",\"capital\":848.48,\"interest\":108.888985,\"insurance\":1.5},{\"date\":\"ao�t 28, 2018\",\"capital\":848.48,\"interest\":105.00012,\"insurance\":1.5},{\"date\":\"d�c. 28, 2018\",\"capital\":848.48,\"interest\":101.111244,\"insurance\":1.5},{\"date\":\"avr. 28, 2019\",\"capital\":848.48,\"interest\":97.22238,\"insurance\":1.5},{\"date\":\"ao�t 28, 2019\",\"capital\":848.48,\"interest\":93.33352,\"insurance\":1.5},{\"date\":\"d�c. 28, 2019\",\"capital\":848.48,\"interest\":89.44465,\"insurance\":1.5},{\"date\":\"avr. 28, 2020\",\"capital\":848.48,\"interest\":85.55578,\"insurance\":1.5},{\"date\":\"ao�t 28, 2020\",\"capital\":848.48,\"interest\":81.66691,\"insurance\":1.5},{\"date\":\"d�c. 28, 2020\",\"capital\":848.48,\"interest\":77.77804,\"insurance\":1.5},{\"date\":\"avr. 28, 2021\",\"capital\":848.48,\"interest\":73.88917,\"insurance\":1.5},{\"date\":\"ao�t 28, 2021\",\"capital\":848.48,\"interest\":70.0003,\"insurance\":1.5},{\"date\":\"d�c. 28, 2021\",\"capital\":848.48,\"interest\":66.11143,\"insurance\":1.5},{\"date\":\"avr. 28, 2022\",\"capital\":848.48,\"interest\":62.222565,\"insurance\":1.5},{\"date\":\"ao�t 28, 2022\",\"capital\":848.48,\"interest\":58.3337,\"insurance\":1.5},{\"date\":\"d�c. 28, 2022\",\"capital\":848.48,\"interest\":54.444824,\"insurance\":1.5},{\"date\":\"avr. 28, 2023\",\"capital\":848.48,\"interest\":50.555958,\"insurance\":1.5},{\"date\":\"ao�t 28, 2023\",\"capital\":848.48,\"interest\":46.667088,\"insurance\":1.5},{\"date\":\"d�c. 28, 2023\",\"capital\":848.48,\"interest\":42.778217,\"insurance\":1.5},{\"date\":\"avr. 28, 2024\",\"capital\":848.48,\"interest\":38.889347,\"insurance\":1.5},{\"date\":\"ao�t 28, 2024\",\"capital\":848.48,\"interest\":35.000484,\"insurance\":1.5},{\"date\":\"d�c. 28, 2024\",\"capital\":848.48,\"interest\":31.111618,\"insurance\":1.5},{\"date\":\"avr. 28, 2025\",\"capital\":848.48,\"interest\":27.22275,\"insurance\":1.5},{\"date\":\"ao�t 28, 2025\",\"capital\":848.48,\"interest\":23.333883,\"insurance\":1.5},{\"date\":\"d�c. 28, 2025\",\"capital\":848.48,\"interest\":19.445017,\"insurance\":1.5},{\"date\":\"avr. 28, 2026\",\"capital\":848.48,\"interest\":15.556151,\"insurance\":1.5},{\"date\":\"ao�t 28, 2026\",\"capital\":848.48,\"interest\":11.667285,\"insurance\":1.5},{\"date\":\"d�c. 28, 2026\",\"capital\":848.48,\"interest\":7.778418,\"insurance\":1.5},{\"date\":\"avr. 28, 2027\",\"capital\":848.48,\"interest\":3.8895514,\"insurance\":1.5},{\"date\":\"ao�t 28, 2016\",\"capital\":137.95668,\"interest\":128.33333,\"insurance\":1.5},{\"date\":\"sept. 28, 2016\",\"capital\":138.58899,\"interest\":127.70103,\"insurance\":1.5},{\"date\":\"oct. 28, 2016\",\"capital\":139.22418,\"interest\":127.065834,\"insurance\":1.5},{\"date\":\"nov. 28, 2016\",\"capital\":139.8623,\"interest\":126.427704,\"insurance\":1.5},{\"date\":\"d�c. 28, 2016\",\"capital\":140.50333,\"interest\":125.78668,\"insurance\":1.5},{\"date\":\"janv. 28, 2017\",\"capital\":141.14731,\"interest\":125.14271,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2017\",\"capital\":141.79422,\"interest\":124.49578,\"insurance\":1.5},{\"date\":\"mars 28, 2017\",\"capital\":142.44412,\"interest\":123.84589,\"insurance\":1.5},{\"date\":\"avr. 28, 2017\",\"capital\":143.09698,\"interest\":123.19302,\"insurance\":1.5},{\"date\":\"mai 28, 2017\",\"capital\":143.75284,\"interest\":122.53716,\"insurance\":1.5},{\"date\":\"juin 28, 2017\",\"capital\":144.41171,\"interest\":121.878296,\"insurance\":1.5},{\"date\":\"juil. 28, 2017\",\"capital\":145.07361,\"interest\":121.21641,\"insurance\":1.5},{\"date\":\"ao�t 28, 2017\",\"capital\":145.73853,\"interest\":120.55148,\"insurance\":1.5},{\"date\":\"sept. 28, 2017\",\"capital\":146.4065,\"interest\":119.883514,\"insurance\":1.5},{\"date\":\"oct. 28, 2017\",\"capital\":147.07751,\"interest\":119.21249,\"insurance\":1.5},{\"date\":\"nov. 28, 2017\",\"capital\":147.75162,\"interest\":118.53838,\"insurance\":1.5},{\"date\":\"d�c. 28, 2017\",\"capital\":148.42883,\"interest\":117.86118,\"insurance\":1.5},{\"date\":\"janv. 28, 2018\",\"capital\":149.10913,\"interest\":117.180885,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2018\",\"capital\":149.79254,\"interest\":116.49746,\"insurance\":1.5},{\"date\":\"mars 28, 2018\",\"capital\":150.4791,\"interest\":115.81091,\"insurance\":1.5},{\"date\":\"avr. 28, 2018\",\"capital\":151.1688,\"interest\":115.12122,\"insurance\":1.5},{\"date\":\"mai 28, 2018\",\"capital\":151.86163,\"interest\":114.428375,\"insurance\":1.5},{\"date\":\"juin 28, 2018\",\"capital\":152.55768,\"interest\":113.73234,\"insurance\":1.5},{\"date\":\"juil. 28, 2018\",\"capital\":153.2569,\"interest\":113.03311,\"insurance\":1.5},{\"date\":\"ao�t 28, 2018\",\"capital\":153.95932,\"interest\":112.33068,\"insurance\":1.5},{\"date\":\"sept. 28, 2018\",\"capital\":154.66498,\"interest\":111.62504,\"insurance\":1.5},{\"date\":\"oct. 28, 2018\",\"capital\":155.37384,\"interest\":110.91616,\"insurance\":1.5},{\"date\":\"nov. 28, 2018\",\"capital\":156.08597,\"interest\":110.20404,\"insurance\":1.5},{\"date\":\"d�c. 28, 2018\",\"capital\":156.80136,\"interest\":109.48864,\"insurance\":1.5},{\"date\":\"janv. 28, 2019\",\"capital\":157.52005,\"interest\":108.769966,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2019\",\"capital\":158.242,\"interest\":108.048004,\"insurance\":1.5},{\"date\":\"mars 28, 2019\",\"capital\":158.96729,\"interest\":107.32272,\"insurance\":1.5},{\"date\":\"avr. 28, 2019\",\"capital\":159.69589,\"interest\":106.594124,\"insurance\":1.5},{\"date\":\"mai 28, 2019\",\"capital\":160.42783,\"interest\":105.86218,\"insurance\":1.5},{\"date\":\"juin 28, 2019\",\"capital\":161.16312,\"interest\":105.12689,\"insurance\":1.5},{\"date\":\"juil. 28, 2019\",\"capital\":161.9018,\"interest\":104.38822,\"insurance\":1.5},{\"date\":\"ao�t 28, 2019\",\"capital\":162.64383,\"interest\":103.64617,\"insurance\":1.5},{\"date\":\"sept. 28, 2019\",\"capital\":163.38928,\"interest\":102.90073,\"insurance\":1.5},{\"date\":\"oct. 28, 2019\",\"capital\":164.13815,\"interest\":102.151855,\"insurance\":1.5},{\"date\":\"nov. 28, 2019\",\"capital\":164.89044,\"interest\":101.39956,\"insurance\":1.5},{\"date\":\"d�c. 28, 2019\",\"capital\":165.64621,\"interest\":100.6438,\"insurance\":1.5},{\"date\":\"janv. 28, 2020\",\"capital\":166.40543,\"interest\":99.88459,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2020\",\"capital\":167.16812,\"interest\":99.121895,\"insurance\":1.5},{\"date\":\"mars 28, 2020\",\"capital\":167.9343,\"interest\":98.35571,\"insurance\":1.5},{\"date\":\"avr. 28, 2020\",\"capital\":168.70398,\"interest\":97.58602,\"insurance\":1.5},{\"date\":\"mai 28, 2020\",\"capital\":169.4772,\"interest\":96.8128,\"insurance\":1.5},{\"date\":\"juin 28, 2020\",\"capital\":170.254,\"interest\":96.03602,\"insurance\":1.5},{\"date\":\"juil. 28, 2020\",\"capital\":171.0343,\"interest\":95.2557,\"insurance\":1.5},{\"date\":\"ao�t 28, 2020\",\"capital\":171.81824,\"interest\":94.47178,\"insurance\":1.5},{\"date\":\"sept. 28, 2020\",\"capital\":172.60571,\"interest\":93.68429,\"insurance\":1.5},{\"date\":\"oct. 28, 2020\",\"capital\":173.39682,\"interest\":92.89318,\"insurance\":1.5},{\"date\":\"nov. 28, 2020\",\"capital\":174.19156,\"interest\":92.09844,\"insurance\":1.5},{\"date\":\"d�c. 28, 2020\",\"capital\":174.98993,\"interest\":91.30007,\"insurance\":1.5},{\"date\":\"janv. 28, 2021\",\"capital\":175.79199,\"interest\":90.49802,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2021\",\"capital\":176.59772,\"interest\":89.6923,\"insurance\":1.5},{\"date\":\"mars 28, 2021\",\"capital\":177.4071,\"interest\":88.882904,\"insurance\":1.5},{\"date\":\"avr. 28, 2021\",\"capital\":178.22021,\"interest\":88.06979,\"insurance\":1.5},{\"date\":\"mai 28, 2021\",\"capital\":179.03708,\"interest\":87.25294,\"insurance\":1.5},{\"date\":\"juin 28, 2021\",\"capital\":179.85767,\"interest\":86.43235,\"insurance\":1.5},{\"date\":\"juil. 28, 2021\",\"capital\":180.682,\"interest\":85.60801,\"insurance\":1.5},{\"date\":\"ao�t 28, 2021\",\"capital\":181.51013,\"interest\":84.779884,\"insurance\":1.5},{\"date\":\"sept. 28, 2021\",\"capital\":182.34204,\"interest\":83.94797,\"insurance\":1.5},{\"date\":\"oct. 28, 2021\",\"capital\":183.17776,\"interest\":83.112236,\"insurance\":1.5},{\"date\":\"nov. 28, 2021\",\"capital\":184.01733,\"interest\":82.27267,\"insurance\":1.5},{\"date\":\"d�c. 28, 2021\",\"capital\":184.86075,\"interest\":81.42926,\"insurance\":1.5},{\"date\":\"janv. 28, 2022\",\"capital\":185.70804,\"interest\":80.58197,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2022\",\"capital\":186.5592,\"interest\":79.73081,\"insurance\":1.5},{\"date\":\"mars 28, 2022\",\"capital\":187.41426,\"interest\":78.87575,\"insurance\":1.5},{\"date\":\"avr. 28, 2022\",\"capital\":188.27325,\"interest\":78.01676,\"insurance\":1.5},{\"date\":\"mai 28, 2022\",\"capital\":189.13617,\"interest\":77.15385,\"insurance\":1.5},{\"date\":\"juin 28, 2022\",\"capital\":190.00305,\"interest\":76.286964,\"insurance\":1.5},{\"date\":\"juil. 28, 2022\",\"capital\":190.8739,\"interest\":75.416115,\"insurance\":1.5},{\"date\":\"ao�t 28, 2022\",\"capital\":191.74873,\"interest\":74.541275,\"insurance\":1.5},{\"date\":\"sept. 28, 2022\",\"capital\":192.62758,\"interest\":73.66243,\"insurance\":1.5},{\"date\":\"oct. 28, 2022\",\"capital\":193.51047,\"interest\":72.77955,\"insurance\":1.5},{\"date\":\"nov. 28, 2022\",\"capital\":194.39737,\"interest\":71.89263,\"insurance\":1.5},{\"date\":\"d�c. 28, 2022\",\"capital\":195.28836,\"interest\":71.00164,\"insurance\":1.5},{\"date\":\"janv. 28, 2023\",\"capital\":196.18344,\"interest\":70.10657,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2023\",\"capital\":197.08261,\"interest\":69.2074,\"insurance\":1.5},{\"date\":\"mars 28, 2023\",\"capital\":197.9859,\"interest\":68.3041,\"insurance\":1.5},{\"date\":\"avr. 28, 2023\",\"capital\":198.89334,\"interest\":67.39666,\"insurance\":1.5},{\"date\":\"mai 28, 2023\",\"capital\":199.80493,\"interest\":66.48507,\"insurance\":1.5},{\"date\":\"juin 28, 2023\",\"capital\":200.72072,\"interest\":65.56929,\"insurance\":1.5},{\"date\":\"juil. 28, 2023\",\"capital\":201.64069,\"interest\":64.64932,\"insurance\":1.5},{\"date\":\"ao�t 28, 2023\",\"capital\":202.56487,\"interest\":63.72514,\"insurance\":1.5},{\"date\":\"sept. 28, 2023\",\"capital\":203.49329,\"interest\":62.79672,\"insurance\":1.5},{\"date\":\"oct. 28, 2023\",\"capital\":204.42598,\"interest\":61.864033,\"insurance\":1.5},{\"date\":\"nov. 28, 2023\",\"capital\":205.36292,\"interest\":60.92709,\"insurance\":1.5},{\"date\":\"d�c. 28, 2023\",\"capital\":206.30417,\"interest\":59.98584,\"insurance\":1.5},{\"date\":\"janv. 28, 2024\",\"capital\":207.24973,\"interest\":59.04028,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2024\",\"capital\":208.19963,\"interest\":58.09038,\"insurance\":1.5},{\"date\":\"mars 28, 2024\",\"capital\":209.15387,\"interest\":57.13614,\"insurance\":1.5},{\"date\":\"avr. 28, 2024\",\"capital\":210.1125,\"interest\":56.17751,\"insurance\":1.5},{\"date\":\"mai 28, 2024\",\"capital\":211.07552,\"interest\":55.214497,\"insurance\":1.5},{\"date\":\"juin 28, 2024\",\"capital\":212.04294,\"interest\":54.24707,\"insurance\":1.5},{\"date\":\"juil. 28, 2024\",\"capital\":213.0148,\"interest\":53.275208,\"insurance\":1.5},{\"date\":\"ao�t 28, 2024\",\"capital\":213.99112,\"interest\":52.29889,\"insurance\":1.5},{\"date\":\"sept. 28, 2024\",\"capital\":214.97191,\"interest\":51.3181,\"insurance\":1.5},{\"date\":\"oct. 28, 2024\",\"capital\":215.9572,\"interest\":50.33281,\"insurance\":1.5},{\"date\":\"nov. 28, 2024\",\"capital\":216.947,\"interest\":49.343006,\"insurance\":1.5},{\"date\":\"d�c. 28, 2024\",\"capital\":217.94135,\"interest\":48.348663,\"insurance\":1.5},{\"date\":\"janv. 28, 2025\",\"capital\":218.94025,\"interest\":47.349766,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2025\",\"capital\":219.94373,\"interest\":46.346287,\"insurance\":1.5},{\"date\":\"mars 28, 2025\",\"capital\":220.9518,\"interest\":45.338215,\"insurance\":1.5},{\"date\":\"avr. 28, 2025\",\"capital\":221.9645,\"interest\":44.32552,\"insurance\":1.5},{\"date\":\"mai 28, 2025\",\"capital\":222.98183,\"interest\":43.30818,\"insurance\":1.5},{\"date\":\"juin 28, 2025\",\"capital\":224.00383,\"interest\":42.286182,\"insurance\":1.5},{\"date\":\"juil. 28, 2025\",\"capital\":225.03052,\"interest\":41.2595,\"insurance\":1.5},{\"date\":\"ao�t 28, 2025\",\"capital\":226.0619,\"interest\":40.228107,\"insurance\":1.5},{\"date\":\"sept. 28, 2025\",\"capital\":227.09802,\"interest\":39.19199,\"insurance\":1.5},{\"date\":\"oct. 28, 2025\",\"capital\":228.13889,\"interest\":38.15113,\"insurance\":1.5},{\"date\":\"nov. 28, 2025\",\"capital\":229.18451,\"interest\":37.105495,\"insurance\":1.5},{\"date\":\"d�c. 28, 2025\",\"capital\":230.23494,\"interest\":36.055065,\"insurance\":1.5},{\"date\":\"janv. 28, 2026\",\"capital\":231.29019,\"interest\":34.99982,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2026\",\"capital\":232.35027,\"interest\":33.939743,\"insurance\":1.5},{\"date\":\"mars 28, 2026\",\"capital\":233.4152,\"interest\":32.874805,\"insurance\":1.5},{\"date\":\"avr. 28, 2026\",\"capital\":234.48502,\"interest\":31.804987,\"insurance\":1.5},{\"date\":\"mai 28, 2026\",\"capital\":235.55975,\"interest\":30.730263,\"insurance\":1.5},{\"date\":\"juin 28, 2026\",\"capital\":236.63939,\"interest\":29.650614,\"insurance\":1.5},{\"date\":\"juil. 28, 2026\",\"capital\":237.72398,\"interest\":28.56602,\"insurance\":1.5},{\"date\":\"ao�t 28, 2026\",\"capital\":238.81357,\"interest\":27.476448,\"insurance\":1.5},{\"date\":\"sept. 28, 2026\",\"capital\":239.90813,\"interest\":26.381887,\"insurance\":1.5},{\"date\":\"oct. 28, 2026\",\"capital\":241.00769,\"interest\":25.28231,\"insurance\":1.5},{\"date\":\"nov. 28, 2026\",\"capital\":242.11232,\"interest\":24.17769,\"insurance\":1.5},{\"date\":\"d�c. 28, 2026\",\"capital\":243.222,\"interest\":23.068008,\"insurance\":1.5},{\"date\":\"janv. 28, 2027\",\"capital\":244.33676,\"interest\":21.95324,\"insurance\":1.5},{\"date\":\"f�vr. 28, 2027\",\"capital\":245.45665,\"interest\":20.833363,\"insurance\":1.5},{\"date\":\"mars 28, 2027\",\"capital\":246.58165,\"interest\":19.708353,\"insurance\":1.5},{\"date\":\"avr. 28, 2027\",\"capital\":247.71182,\"interest\":18.578188,\"insurance\":1.5},{\"date\":\"mai 28, 2027\",\"capital\":248.84717,\"interest\":17.442842,\"insurance\":1.5},{\"date\":\"juin 28, 2027\",\"capital\":249.98772,\"interest\":16.302292,\"insurance\":1.5},{\"date\":\"juil. 28, 2027\",\"capital\":251.1335,\"interest\":15.156516,\"insurance\":1.5}],\"effectiveDate\":\"ao�t 28, 2016\",\"capital\":28000.0,\"remainingOwedCapital\":28000.0,\"repaymentFrequency\":3,\"remainingRepayments\":33,\"repaymentConstant\":848.0,\"amortizationType\":\"degressive\",\"loan_type_id\":\"2\",\"account_id\":\"3\"}", GetSimServerResponse.class));
		gui.simulations.add(JsonImpl.fromJson("{\"name\":\"Simu achat maison 3 - échéances constantes\",\"id\":\"5\",\"events\":[],\"repayments\":[],\"effectiveDate\":\"ao�t 28, 2016\",\"capital\":28000.0,\"remainingOwedCapital\":28000.0,\"repaymentFrequency\":12,\"remainingRepayments\":132,\"repaymentConstant\":267.0,\"amortizationType\":\"steady\",\"loan_type_id\":\"2\",\"account_id\":\"3\"}", GetSimServerResponse.class));
		
		gui.compare();
		
		frame.add(gui);
		frame.setVisible(true);
	}
}
