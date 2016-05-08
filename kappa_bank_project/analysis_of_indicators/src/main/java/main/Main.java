package main;

import java.io.IOException;
import java.util.Scanner;

import controler.ProtocoleHandler;

public class Main {

	public void DisplayMenu() throws IOException {
		System.out.println("------------------------Bienvenue à la banque mutuelle -----------------------------");
		System.out.println();
		System.out.println("                A) Tapez 1 pour vous autentifier");
		System.out.println();
		System.out.println("                B) Tapez une autre touche pour quitter");

		System.out.println("------------------------------------------------------------------------------------");
		System.out.print("votre Choix: ");
		Scanner scanner = new Scanner(System.in);

		int choice = scanner.nextInt();

		switch (choice) {
		case 1:
			Scanner sc = new Scanner(System.in);
			// I check the login/Password informations
			System.out.println("Donner votre Login :");
			String login = sc.nextLine();
			System.out.println("Donner votre password: ");
			String password = sc.next();

			// I send the information to the serveur

			String reponseautentification = new ProtocoleHandler().authentification(login, password);
			if (reponseautentification == "OK") {
				System.out
						.println("-----------------------------Vous étes connecter :" + login + "------------------------");
				System.out.println();
				System.out.println("----------------------------Analyse des Indicateurs-------------------------------");
				System.out.println();
				System.out.println("tapez 1 pour consulter les types de prêts  ");
				System.out.println("tapez 2 voir la durée moyenne des prêts");
				System.out.println("tapez 3	consulter les intérêts perçus  j'usqu'a une date");
				System.out.println("tapez 4 pour consulter les simulations");
				System.out.println("tapez 5 pour la consultation  avancée");
				System.out.println("tapez sur une autre touche pour sortir");

				int choiceOperation;

				choiceOperation = scanner.nextInt();

				switch (choiceOperation) {

				case 1:

					break;
				case 2:

					break;
				case 3:

					break;
				case 4:

					break;
				case 5:

					break;

				default:
					System.exit(0);
				}

			} else {
				System.out.println("Login/Password incorrectes");
				new Main().DisplayMenu();
			}
		default:
			System.exit(0);
		}
		System.out.println(choice);
	}

	public void MenuResponsableAgence() {

	}

	public static void main(String[] args) throws IOException {

		new Main().DisplayMenu();
	}
}
