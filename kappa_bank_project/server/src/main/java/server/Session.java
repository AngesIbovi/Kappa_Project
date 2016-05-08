package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import model.query.AuthenticationQuery;
import model.query.GetAccountsQuery;
import model.query.GetSimQuery;
import model.query.GetSimsQuery;
import model.response.AuthenticationServerResponse;
import model.response.ErrorServerResponse;
import model.response.ServerResponse;
import model.response.UnauthorizedErrorServerResponse;

import org.apache.log4j.Logger;

import util.JsonImpl;

/**
 * This class handles exactly one client, from their connection to their disconnection. </br>
 * The life span of this thread is either the same as the Socket it was given in its constructor's life span,
 * or shorter, depending on whether or not an exit signal is received.
 * @version R3 sprint 1 - 13/04/2016
 * @author Kappa-V
 * @changes
 * 		R3 sprint 1 -> R3 sprint 2: </br>
 * 			-Removed the calls to the deprecated consult, withdrawal, deleteCustomer and newCustomer MessageHandler methods
 * 			-Added the calls to the getAccounts, getSims, and getSim MessageHandler methods instead
 * 			-Removed the unused password attribute
 * 		R2 sprint 1 -> R3 sprint 1: </br>
 * 			-renamed Session from ProtocolHandler</br>
 * 			-added user_id, password and authorization_level attributes to handle authentication</br>
 * 			-added the handleMessage method which was previously in the MessageHandler class
 * 			-in handleMessage, added the new Auth method, and re-used prefixEnd's value when calculating the prefix String
 */
public class Session extends Thread {
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(Session.class);
	
	/**
	 * The socket of this Session's client.
	 */
	private Socket client;
	
	/**
	 * This boolean is used to exit the run() method before the client decides to end the session. It is set to false by the exit() method.
	 */
	private boolean exit = false;
	
	/**
	 * User id provided by the client during the authentication phase
	 */
	private String user_id = null;
	
	/**
	 * User authorization level as dictated by the database during the authentication phase.
	 */
	private int authorization_level = 3;
	
	/**
	 * Main constructor for this class.
	 * @param client : the client this Session will handle.
	 */
	public Session(Socket client) {
		this.client = client;
	}
	
	/**
	 * Is called in a new thread when you call the start() method from the Thread superclass. Don't actually call this, call start() instead.</br>
	 * Handles a session from start to finish.
	 */
	@Override
	public void run() {
		logger.trace("Entering Session.run");
		try {
			// initialization
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			// Main loop
			while (!client.isClosed() && !exit) {
				// Read query
				String clientMessage = "";
				do {
					clientMessage += in.readLine(); // Reading the client's query
				} while (in.ready()); // This loop makes it possible to receive 2+ lines long messages (useful in the case of pretty printed JSON)
				
				// Treat query
				ServerResponse serverResponse = handleMessage(clientMessage);
				
				// Response
				if(!client.isClosed()) {
					if(serverResponse == null) { // handleMessage returns null if clientMessage.equals("BYE")
						client.close();
					} else {
						out.println(serverResponse.toString());
					}
				}
			}
		} catch (IOException e) {
			if(client.isClosed()) {
				logger.info("Session shut down.");
			} else {
				logger.error("Session : IOException caught", e);
				try {
					client.close();
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
			client.close();
		} catch (IOException e) {
			logger.warn("Exception caught while attempting to close a socket");
		}
		logger.trace("Exiting Session.exit");
	}
	
	/**
	 * Analyses the message, and dispatches its handling to the correct method from the MessageHandler static methods.
	 * @param message : the message received from the socket, as is, without any prior treatment
	 * @return : null if the client said "BYE", in which case the protocol handler must be terminated. Else, the response will be returned.
	 */
	private ServerResponse handleMessage(String message) {
		logger.trace("Entering Session.handleMessage");
		if(message.equals("BYE")) {
			logger.trace("Exiting Session.handleMessage. Message was \"BYE\"");
			logger.info(this.user_id + " logged out successfully.");
			return null;
		}
		
		try {
			int prefixEnd = message.indexOf(' ');
			
			if(prefixEnd == -1) {
				logger.trace("Exiting Session.handleMessage");
				logger.debug("Invalid prefix. Message was : " + message);
				return new ErrorServerResponse("Invalid prefix");
			}
			
			String prefix = message.substring(0, prefixEnd);
			String content = message.substring(prefixEnd + 1);
			
			ServerResponse response;
			switch(prefix) {
			case "AUTH":
				AuthenticationQuery authQuery = JsonImpl.fromJson(content, AuthenticationQuery.class);
				response = MessageHandler.handleAuthQuery(authQuery);
				if(response instanceof AuthenticationServerResponse) { // response can also be ErrorServerResponse if the database can't be reached.
					AuthenticationServerResponse authResponse = (AuthenticationServerResponse) response;
					if(authResponse.getStatus().equals(AuthenticationServerResponse.Status.OK)) {
						this.authorization_level = authResponse.getYour_authorization_level();
						this.user_id = authQuery.getId();
						logger.info(this.user_id + " logged in successfully.");
					}
				}
				break;
			case "getAccounts":
				if(authorization_level < 2) {
					return new UnauthorizedErrorServerResponse((this.user_id==null), this.authorization_level, 2);
				}
				GetAccountsQuery getAccountsQuery = JsonImpl.fromJson(content, GetAccountsQuery.class);
				response = MessageHandler.handleGetAccountsQuery(getAccountsQuery, this.user_id);
				break;
			case "getSims":
				if(authorization_level < 1) {
					return new UnauthorizedErrorServerResponse((this.user_id == null), this.authorization_level, 1);
				}
				GetSimsQuery getSimsQuery = JsonImpl.fromJson(content, GetSimsQuery.class);
				response = MessageHandler.handleGetSimsQuery(getSimsQuery);
				break;
			case "getSim":
				if(authorization_level < 1) {
					return new UnauthorizedErrorServerResponse((this.user_id == null), this.authorization_level, 1);
				}
				GetSimQuery getSimQuery = JsonImpl.fromJson(content, GetSimQuery.class);
				response = MessageHandler.handleGetSimQuery(getSimQuery);
				break;
			default:
				response = new ErrorServerResponse("Unknown prefix");
			}
			
			logger.trace("Exiting Session.handleMessage");
			return response;
		} catch(Exception e) {
			logger.trace("Exiting Session.handleMessage");
			logger.debug("Unknown format error. Message was : " + message);
			return new ErrorServerResponse("Unknown format error");
		}
	}
}
