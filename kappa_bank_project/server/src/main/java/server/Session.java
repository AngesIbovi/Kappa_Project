package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import model.SessionInformation;
import model.query.AuthenticationQuery;
import model.query.AverageDurationQuery;
import model.query.DynamiqueResearchQuery;
import model.query.GetAccountsQuery;
import model.query.GetAllAccountsQuery;
import model.query.GetAllLoanTypeQuery;
import model.query.GetAllSimsQuery;
import model.query.GetCustomersQuery;
import model.query.GetLoanQuery;
import model.query.GetSimQuery;
import model.query.GetSimsQuery;
import model.query.GetValueOfRateQuery;
import model.query.LoansQuery;
import model.query.NumberOfLoanQuery;
import model.query.SearchAccountsQuery;
import model.query.SignLoanQuery;
import model.query.evolutionOfTheSimulationsQuery;
import model.response.AuthenticationServerResponse;
import model.response.ErrorServerResponse;
import model.response.ServerResponse;
import model.response.UnauthorizedErrorServerResponse;
import util.JsonImpl;

/**
 * This class handles exactly one client, from their connection to their
 * disconnection. </br>
 * The life span of this thread is either the same as the Socket it was given in
 * its constructor's life span, or shorter, depending on whether or not an exit
 * signal is received.
 * 
 * @version R3 sprint 3 - 08/05/2016
 * @author Kappa-V
 * @changes R3 sprint 2 -> R3 sprint 3:</br>
 *          -In handleMessage, renamed getAccounts into searchAccounts, and
 *          added a new getAccounts.</br>
 *          -Now uses an instance of the new class SessionInformation</br>
 *          R3 sprint 1 -> R3 sprint 2: </br>
 *          -Removed the calls to the deprecated consult, withdrawal,
 *          deleteCustomer and newCustomer MessageHandler methods</br>
 *          -Added the calls to the getAccounts, getSims, and getSim
 *          MessageHandler methods instead</br>
 *          -Removed the unused password attribute</br>
 *          R2 sprint 1 -> R3 sprint 1: </br>
 *          -renamed Session from ProtocolHandler</br>
 *          -added user_id, password and authorization_level attributes to
 *          handle authentication</br>
 *          -added the handleMessage method which was previously in the
 *          MessageHandler class</br>
 *          -in handleMessage, added the new Auth method, and re-used
 *          prefixEnd's value when calculating the prefix String</br>
 */
public class Session extends Thread {
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(Session.class);

	/**
	 * This boolean is used to exit the run() method before the client decides
	 * to end the session. It is set to false by the exit() method.
	 */
	private boolean exit = false;

	/**
	 * This session's information. Can be updated.
	 */
	private SessionInformation sessionInformation;

	/**
	 * Main constructor for this class.
	 * 
	 * @param client
	 *            : the client this Session will handle.
	 */
	public Session(Socket client) {
		this.sessionInformation = new SessionInformation(-1, null, client);
	}

	/**
	 * Is called in a new thread when you call the start() method from the
	 * Thread superclass. Don't actually call this, call start() instead.</br>
	 * Handles a session from start to finish.
	 */
	@Override
	public void run() {
		logger.trace("Entering Session.run");
		try {
			// initialization
			PrintWriter out = new PrintWriter(sessionInformation.getSocket().getOutputStream(), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(sessionInformation.getSocket().getInputStream()));

			// Main loop
			while (!sessionInformation.getSocket().isClosed() && !exit) {
				// Read query
				String clientMessage = "";
				do {
					clientMessage += in.readLine(); // Reading the client's
													// query
				} while (in.ready()); // This loop makes it possible to receive
										// 2+ lines long messages (useful in the
										// case of pretty printed JSON)

				// Treat query
				ServerResponse serverResponse = handleMessage(clientMessage);

				// Response
				if (!sessionInformation.getSocket().isClosed()) {
					if (serverResponse == null) { // handleMessage returns null
													// if
													// clientMessage.equals("BYE")
						sessionInformation.getSocket().close();
					} else {
						out.println(serverResponse.toString());
					}
				}
			}
		} catch (IOException e) {
			if (sessionInformation.getSocket().isClosed()) {
				logger.info("Session shut down.");
			} else {
				logger.error("Session : IOException caught", e);
				try {
					sessionInformation.getSocket().close();
				} catch (IOException e1) {
					logger.warn("Exception caught while attempting to close a socket", e1);
				}
			}
		}
		logger.info("Connection terminated");
		logger.trace("Exiting Session.run");
	}

	public void exit() {
		logger.trace("Entering Session.exit");
		exit = true;
		try {
			sessionInformation.getSocket().close();
		} catch (IOException e) {
			logger.warn("Exception caught while attempting to close a socket");
		}
		logger.trace("Exiting Session.exit");
	}

	/**
	 * Analyses the message, and dispatches its handling to the correct method
	 * from the MessageHandler static methods.
	 * 
	 * @param message
	 *            : the message received from the socket, as is, without any
	 *            prior treatment
	 * @return : null if the client said "BYE", in which case the protocol
	 *         handler must be terminated. Else, the response will be returned.
	 */
	private ServerResponse handleMessage(String message) {
		logger.trace("Entering Session.handleMessage");
		if (message.equals("BYE")) {
			logger.trace("Exiting Session.handleMessage. Message was \"BYE\"");
			logger.info(this.sessionInformation.getUser_id() + " logged out successfully.");
			return null;
		}

		try {
			int prefixEnd = message.indexOf(' ');

			if (prefixEnd == -1) {
				logger.trace("Exiting Session.handleMessage");
				logger.debug("Invalid prefix. Message was : " + message);
				return new ErrorServerResponse("Invalid prefix");
			}

			String prefix = message.substring(0, prefixEnd);
			String content = message.substring(prefixEnd + 1);

			ServerResponse response;
			switch (prefix) {
			case "AUTH":
				AuthenticationQuery authQuery = JsonImpl.fromJson(content, AuthenticationQuery.class);
				response = MessageHandler.handleAuthQuery(authQuery);
				if (response instanceof AuthenticationServerResponse) { // response
																		// can
																		// also
																		// be
																		// ErrorServerResponse
																		// if
																		// the
																		// database
																		// can't
																		// be
																		// reached.
					AuthenticationServerResponse authResponse = (AuthenticationServerResponse) response;
					if (authResponse.getStatus().equals(AuthenticationServerResponse.Status.OK)) {
						this.sessionInformation = new SessionInformation(authResponse.getYour_authorization_level(),
								authQuery.getId(), this.sessionInformation.getSocket());
						logger.info(this.sessionInformation.getUser_id() + " logged in successfully.");
					}
				}
				break;
			



	case "AverageDuration":
				if (this.sessionInformation.getAuthorization_level() < 3) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 2);
				}
				AverageDurationQuery averageDurationQuery = JsonImpl.fromJson(content, AverageDurationQuery.class);
				response = MessageHandler.handleAverage();
				break;
			case "InterestBySegment":
				if (this.sessionInformation.getAuthorization_level() < 3) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 2);
				}
				DynamiqueResearchQuery dynamiqueResearch = JsonImpl.fromJson(content, DynamiqueResearchQuery.class);
				System.out.println("cession "+dynamiqueResearch.toString());
				response = MessageHandler.handleInterestBySegment(dynamiqueResearch);
				break;
				
				

			case "evolutionOfTheSimulations":
				if (this.sessionInformation.getAuthorization_level() < 3) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 2);
				}

				evolutionOfTheSimulationsQuery evolution = JsonImpl.fromJson(content,
						evolutionOfTheSimulationsQuery.class);
				
				response = MessageHandler.handleEvolutionOfTheSimulation(evolution.getDate());

				break;
			case "NumberOfLoanQuery":
				if (this.sessionInformation.getAuthorization_level() < 3) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 2);
				}
				NumberOfLoanQuery numberOfLoan = JsonImpl.fromJson(content, NumberOfLoanQuery.class);
				response = MessageHandler.handleNumberOfLoan(numberOfLoan.getDate());
				break;

			case "MustSimulatedLoan":
				if (this.sessionInformation.getAuthorization_level() < 3) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 2);
				}
				NumberOfLoanQuery numberOfLoanQuery = JsonImpl.fromJson(content, NumberOfLoanQuery.class);
				System.out.println(numberOfLoanQuery.toString());
				try {
					response = MessageHandler.handleMustSimulatedLoanQuery();
				} catch (Exception e) {

				}
				response = MessageHandler.handleMustSimulatedLoanQuery();
				break;

			case "SumOfInterest":
				if (this.sessionInformation.getAuthorization_level() < 3) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 2);
				}
				response = MessageHandler.handleSumofInterest();

				break;
				
				


			case "searchAccounts":
				if (this.sessionInformation.getAuthorization_level() < 2) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 2);
				}
				SearchAccountsQuery searchAccountsQuery = JsonImpl.fromJson(content, SearchAccountsQuery.class);
				response = MessageHandler.handleSearchAccountsQuery(searchAccountsQuery,
						this.sessionInformation.getUser_id());
				break;
			case "getAccounts":
				if (this.sessionInformation.getAuthorization_level() < 1) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 1);
				}
				GetAccountsQuery getAccountsQuery = JsonImpl.fromJson(content, GetAccountsQuery.class);
				response = MessageHandler.handleGetAccountsQuery(getAccountsQuery);
				break;
			case "getSims":
				if (this.sessionInformation.getAuthorization_level() < 1) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 1);
				}
				GetSimsQuery getSimsQuery = JsonImpl.fromJson(content, GetSimsQuery.class);
				response = MessageHandler.handleGetSimsQuery(getSimsQuery);
				break;
			case "getAllSims":
				if (this.sessionInformation.getAuthorization_level() < 1) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 1);
				}
				GetAllSimsQuery getAllSimsQuery = JsonImpl.fromJson(content, GetAllSimsQuery.class);
				response = MessageHandler.handleGetAllSimsQuery(getAllSimsQuery);
				break;
			case "getSim":
				if (this.sessionInformation.getAuthorization_level() < 1) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 1);
				}
				GetSimQuery getSimQuery = JsonImpl.fromJson(content, GetSimQuery.class);
				response = MessageHandler.handleGetSimQuery(getSimQuery);
				break;
			case "signLoan":
				if (this.sessionInformation.getAuthorization_level() < 2) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 2);
				}
				SignLoanQuery signLoanQuery = JsonImpl.fromJson(content, SignLoanQuery.class);
				response = MessageHandler.handleSignLoanQuery(signLoanQuery, sessionInformation.getUser_id());
				break;
			case "sendLoans":
				if (this.sessionInformation.getAuthorization_level() < 2) {
					return new UnauthorizedErrorServerResponse((this.sessionInformation.getUser_id() == null),
							this.sessionInformation.getAuthorization_level(), 1);
				}
				LoansQuery loans = JsonImpl.fromJson(content, LoansQuery.class);
				response = MessageHandler.handleLoansQuery(loans);
				break;
			case "GetLoanQuery":

				GetLoanQuery GetLoanQuery = JsonImpl.fromJson(content, GetLoanQuery.class);
				response = MessageHandler.handleGetRatesQuery(GetLoanQuery);
				System.out.println(response);
				break;

			case "getAllAccounts":
				GetAllAccountsQuery accountsquery = JsonImpl.fromJson(content, GetAllAccountsQuery.class);
				System.out.println(accountsquery.toString());
				try {
					response = MessageHandler.handleGetAllAccountQuery();
				} catch (Exception e) {
				}
				response = MessageHandler.handleGetAllAccountQuery();
				break;

			case "getAllLoanType":

				GetAllLoanTypeQuery loantypequry = JsonImpl.fromJson(content, GetAllLoanTypeQuery.class);
				System.out.println(loantypequry.toString());
				try {
					response = MessageHandler.handleGetAllLoanTypeQuery();
				} catch (Exception e) {
				}
				response = MessageHandler.handleGetAllLoanTypeQuery();
				break;

			case "GetValueOfRate":

				GetValueOfRateQuery ValueOfRatequery = JsonImpl.fromJson(content, GetValueOfRateQuery.class);
				System.out.println(ValueOfRatequery.toString());
				try {
					response = MessageHandler.handleGetrateQuery();
				} catch (Exception e) {
				}
				response = MessageHandler.handleGetrateQuery();
				break;

			case "getCustomers":

				GetCustomersQuery Customerquery = JsonImpl.fromJson(content, GetCustomersQuery.class);
				System.out.println(Customerquery.toString());
				try {
					response = MessageHandler.handleGetCustomersQuery(Customerquery);
				} catch (Exception e) {
				}
				response = MessageHandler.handleGetCustomersQuery(Customerquery);
				break;

			default:
				response = new ErrorServerResponse("Unknown prefix");
			}

			logger.trace("Exiting Session.handleMessage");
			return response;
		} catch (Exception e) {
			logger.trace("Exiting Session.handleMessage");
			logger.debug("Unknown format error. Message was : " + message);
			// e.printStackTrace();
			return new ErrorServerResponse("Unknown format error");
		}
	}
}
