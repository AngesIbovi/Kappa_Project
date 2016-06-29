package view;

import serverCommunication.ServerCommunication;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import controler.Controler;
import model.SessionInformation;
import view.Tab;

public class risk_level extends Tab {

	Socket socket = null;

	public risk_level() {
		super(" Le montant des taux d'intérêt de l'agence", 3);
	}

	// Declarations
	public JTextField DureeEmprText;
	static float rate;
	static float rate1;
	static float rate2;
	static float rate3;
	static float ratePretEtud;

	public JRadioButton Radio2, Radio3, Radio4, Radio1, radioIlness,
			RadioHealthy;
	public JLabel rateLabel, RatePerc, DureeLabel, maxDuration, health;
	public JTextField finalRateText, maxDurationTex, RefRateText;
	private JButton btnNewButton, btnannuler;
	private JPanel cards, card1, card2, card3, card4;
	private JComboBox<String> comboBox;

	private void initialize() {
		CardLayout cards = new CardLayout();
		this.setLayout(cards);

		setFont(new Font("Tahoma", Font.BOLD, 11));
		setForeground(Color.BLACK);
		setBounds(300, 300, 900, 600);
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		DureeLabel = new JLabel("Age de l'emprunteur");
		DureeLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		DureeLabel.setBounds(10, 200, 158, 19);
		add(DureeLabel);

		Radio1 = new JRadioButton("18 à 30 ans");
		Radio1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Radio1.setBounds(201, 200, 109, 23);

		Radio1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		add(Radio1);

		Radio2 = new JRadioButton("De 30 à 45");
		Radio2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Radio2.setBounds(329, 200, 114, 23);
		Radio2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		add(Radio2);

		Radio3 = new JRadioButton("De 45 à 60");
		Radio3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Radio3.setBounds(471, 200, 120, 23);
		Radio3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});

		add(Radio3);

		Radio4 = new JRadioButton("60 et plus");
		Radio4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		Radio4.setBounds(628, 200, 109, 23);
		Radio4.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});
		add(Radio4);

		final ButtonGroup group = new ButtonGroup();
		group.add(Radio1);
		group.add(Radio2);
		group.add(Radio3);
		group.add(Radio4);

		JLabel DureeEmprlabel = new JLabel("Durée de l'emprunt");
		DureeEmprlabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		DureeEmprlabel.setBounds(10, 391, 159, 19);
		add(DureeEmprlabel);

		DureeEmprText = new JTextField();
		DureeEmprText.setFont(new Font("Tahoma", Font.PLAIN, 15));
		DureeEmprText.setBounds(308, 391, 115, 22);
		DureeEmprText.setColumns(10);
		add(DureeEmprText);

		maxDuration = new JLabel("Durée Maximale");
		maxDuration.setFont(new Font("Tahoma", Font.BOLD, 15));
		maxDuration.setBounds(600, 391, 158, 20);
		add(maxDuration);

		maxDurationTex = new JTextField();
		maxDurationTex.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxDurationTex.setBounds(730, 391, 114, 22);
		maxDurationTex.setColumns(10);
		add(maxDurationTex);
		maxDurationTex.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				Controler contr = new Controler();

			}
		});

		JLabel lblAns = new JLabel("(ans)");
		lblAns.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblAns.setBounds(424, 396, 46, 14);
		add(lblAns);

		DureeEmprText.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});

		health = new JLabel("Etat de santé :");
		health.setFont(new Font("Tahoma", Font.BOLD, 15));
		health.setBounds(10, 300, 158, 20);
		add(health);

		radioIlness = new JRadioButton("Etat aggravé");
		radioIlness.setFont(new Font("Tahoma", Font.PLAIN, 15));
		radioIlness.setBounds(200, 300, 500, 20);
		add(radioIlness);

		RadioHealthy = new JRadioButton("Etat satisfaisant");
		RadioHealthy.setFont(new Font("Tahoma", Font.PLAIN, 15));
		RadioHealthy.setBounds(700, 300, 250, 20);
		add(RadioHealthy);

		final ButtonGroup group2 = new ButtonGroup();
		group2.add(RadioHealthy);
		group2.add(radioIlness);

		//
		rateLabel = new JLabel("Le Taux :");
		rateLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		rateLabel.setBounds(10, 500, 158, 19);
		add(rateLabel);

		finalRateText = new JTextField();
		finalRateText.setForeground(Color.RED);
		finalRateText.setFont(new Font("Tahoma", Font.PLAIN, 11));
		finalRateText.setBounds(308, 500, 114, 23);
		add(finalRateText);
		finalRateText.setColumns(10);

		RatePerc = new JLabel("(%)");
		RatePerc.setFont(new Font("Tahoma", Font.PLAIN, 10));
		RatePerc.setBounds(424, 500, 46, 14);
		add(RatePerc);

		btnannuler = new JButton("Annuler");
		btnannuler.setBounds(600, 600, 100, 33);
		add(btnannuler);

		btnannuler.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
			
				group.clearSelection();
				group2.clearSelection();
				finalRateText.setText("");
				maxDurationTex.setText("");
				RefRateText.setText("");
				comboBox.setSelectedIndex(0);
				DureeEmprText.setText("");
			}
		});

		btnNewButton = new JButton("Valider");
		btnNewButton.setBounds(450, 600, 100, 33);
		add(btnNewButton);

		btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				Final();

			}
		});

		Label label_5 = new Label("Types du prêt");
		label_5.setFont(new Font("Tahoma", Font.BOLD, 15));
		label_5.setBounds(10, 36, 130, 20);
		add(label_5);

		JLabel label_6 = new JLabel("Taux Indicateur");
		label_6.setFont(new Font("Tahoma", Font.BOLD, 15));
		label_6.setBounds(10, 105, 158, 20);
		add(label_6);

		RefRateText = new JTextField();
		RefRateText.setEditable(false);
		RefRateText.setColumns(10);
		RefRateText.setBounds(308, 107, 114, 20);
		add(RefRateText);

		RefRateText.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

			}
		});

		JLabel label_4 = new JLabel("(%)");
		label_4.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_4.setBounds(424, 110, 46, 14);
		add(label_4);

		btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String dura = DureeEmprText.getText();

				System.out.println("La durée est : " + dura);

				float rate5;

			}
		});

	}

	/**
	 * Launch the application.
	 */

	public void actionPerformed(ActionEvent e) {

		Controler contr = new Controler();
		String Name = (String) comboBox.getSelectedItem();

	}

	// Manage the margin according to the health
	public float choice() {

		float rateHealth = 0;
		if (RadioHealthy.isSelected()) {
			rateHealth = rateHealth + 0.9f;

		} else if (radioIlness.isSelected()) {
			rateHealth = rateHealth + 5;

		}

		return rateHealth;

	}

	// Manage the margin according to the Age
	public float choiceAge() {
		float rateAge = 0;
		if (Radio1.isSelected()) {

			rateAge = (float) (rateAge - 0.5);
		} else if (Radio2.isSelected()) {
			rateAge = rateAge + 0.02f;
		} else if (Radio3.isSelected()) {
			rateAge = rateAge + 0.9f;
		} else if (Radio4.isSelected()) {
			rateAge = rateAge + 1.9f;
		}
		return rateAge;
	}

	//

	public void Final() {

		float f0 = choiceAge();

		// recupere le contenu dans taux indicateur
		String indicatorratestr;
		indicatorratestr = RefRateText.getText();
		// le convertir en float
		float f1 = Float.parseFloat(indicatorratestr);
		// recuperer les taux concernanat l'état de santé
		float f2 = choice();

		// recuperer le taux concernanat la durée de l'emprunt
		float f3 = choiceDurr();

		float floatrate = f0 + f1 + f2 + f3;
		System.out.println(floatrate);

		// Finaly: The final interest rate

		String finalInterestRate = Float.toString(floatrate);
		System.out.println("sldkurghslrkugh " + finalInterestRate);

		finalRateText.setText(finalInterestRate);
	}

	// Manage the margin according to the duration
	public float choiceDurr() {

		// recuoerer la duree max
		float f3 = 0;

		String duremax;
		duremax = maxDurationTex.getText();
		// convertir en entier
		int duremaxint = Integer.parseInt(duremax);

		// recuperer la duree de l'emprunt
		String duremprunt;
		duremprunt = DureeEmprText.getText();
		// convertir en entier
		int durempruntint = Integer.parseInt(duremprunt);

		// Tests
		if (durempruntint == duremaxint) {
			f3 = f3 + 2;
		} else if (durempruntint == duremaxint / 2) {
			f3 = f3 + 0.9f;
		} else if (durempruntint > duremaxint / 2) {
			f3 = f3 + 1.9f;
		} else if (durempruntint < duremaxint / 2) {
			f3 = f3 + 0.02f;
		}

		return f3;
	}

	public void setSessionInformation(SessionInformation sessionInformation) {
		initialize();
		socket = sessionInformation.getSocket();

		ServerCommunication servercommunication = new ServerCommunication();
		comboBox = new JComboBox<String>();
		comboBox.setBounds(307, 36, 115, 20);
		comboBox.addItem(" ");

		comboBox.setSelectedIndex(0);

		ArrayList<String> array = new ArrayList<String>();
		array = servercommunication.getAlltypeofLoan(socket);

		System.out.println(array);
		// array.getMax_Duration();

		for (String string : array) {
			System.out.println(string);
			comboBox.addItem(string);

		}
		add(comboBox);

		comboBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Controler contr = new Controler();
				System.out.println("selecton " + comboBox.getSelectedItem());
				System.out.println("rate  "
						+ contr.getIndicatorRate(socket,
								(String) comboBox.getSelectedItem()));
				System.out.println();

				String RefRateString = Float.toString(contr.getIndicatorRate(
						socket, (String) comboBox.getSelectedItem()));
				RefRateText.setText(RefRateString);

				int duree = contr.getDuration(
						(String) comboBox.getSelectedItem(), socket);
				System.out.println("durée " + duree);
				String dureeString = Integer.toString(duree);
				maxDurationTex.setText(dureeString);

			}
		});

	}

}
