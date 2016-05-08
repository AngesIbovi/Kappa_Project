package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;

import util.JsonImpl;
import util.KappaProperties;

/**
 * Main class of the project. </br>
 * Handles the initialization and cleanup processes, as well as the ServerSocket and the creation of ProtocolHandlers in separate threads.
 * @version R2 sprint 1 - 18/03/2016
 * @author Kappa-V
 */
public class Server {
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(Server.class);
	
	/**
	 * Private empty constructor : makes it impossible to instantiate
	 */
	private Server() {}
	
	/**
	 * Status attribute. All methods in this implementation can throw IllegalStateExceptions.
	 */
	private static ServerState state = ServerState.initial;
	
	/**
	 * The server's ServerSocket, through which new clients connect.
	 */
	private static ServerSocket serverSocket;
	
	/**
	 * This set contains a reference every ProtocolHandler which has been launched since the server was launched.</br>
	 * It is used to signal all of these ProtocolHandler when cleaning up.
	 */
	private static Set<Session> clients;
	
	/**
	 * Initializes every class that needs to be, in the right order. </br>
	 * Must be called before launch() can be.
	 * @throws IOException : if Properties can't be found.
	 * @throws SQLException : if an error occurs while attempting to connect to the database
	 * @throws ClassNotFoundException : if the JDBC driver can't be found
	 * @throws IllegalStateException : if the server was already initialized
	 */
	public static void initAll() throws IOException, IllegalStateException, ClassNotFoundException, SQLException {
		logger.trace("Entering Server.initAll");
		
		if(state == ServerState.ready) {
			logger.trace("Exiting Server.initAll with an IllegalStateException");
			throw new IllegalStateException("Server initAll - already initialized");
		}
		
		// Initializing tools
		try {
			KappaProperties.init();
		} catch(IOException e) {
			logger.trace("Exiting Server.initAll with an IOException");
			throw e;
		}
		JsonImpl.init();
		
		// Initializing server attributes
		exit = false;
		clients = new HashSet<>();
		
		// Initializing server components
		try {
			Properties prop = KappaProperties.getInstance();
			serverSocket = new ServerSocket(Integer.parseInt(prop.getProperty("SERVER_PORT")));
		} catch(Throwable t) {
			logger.trace("Exiting Server.initAll with a " + t.getClass().getName());
			throw t;
		}
		try {
			ConnectionPool.init();
		} catch (IllegalStateException | ClassNotFoundException | SQLException e) {
			logger.trace("Exiting Server.initAll with a " + e.getClass().getName());
			serverSocket.close(); // Cleans up the already initialized components before exiting
			throw e;
		}
		
		state = ServerState.ready;
	}
	
	/**
	 * Cleans up everything properly. </br>
	 * Must be called before exiting the application. <
	 */
	private static void cleanupAll() {
		logger.trace("Entering Server.cleanupAll");
		
		if(state == ServerState.initial) {
			logger.trace("Exiting Server.cleanupAll with no treatment needed");
			return; // Method exists because Server is already clean (not yet initialized, or already cleaned up)
		} else
			// Changing the state before actually going through with the cleanup stops other methods from doing unsafe operations.
			state = ServerState.initial;
		
		
		// Terminating all clients
		for(Session client : clients) {
			client.exit();
		}
		// Waiting for all clients to be properly terminated
		for(Session client : clients) {
			try {
				if(client.isAlive())
					client.join();
			} catch (InterruptedException e) {
				logger.warn("Caught an InterruptedException during ProtocolHandler cleanup", e);
			}
		}
		
		
		ConnectionPool.cleanup(); // Once all clients are terminated, the connection pool is cleaned up
		
		state = ServerState.initial;
		
		logger.trace("Exiting Server.cleanupAll");
	}
	
	/**
	 * This boolean is used to exit the otherwise infinite loop in the launch() method. It is set to false by the exit() method.
	 */
	private static boolean exit = false;
	
	/**
	 * Handles the ServerSocket and the creation of ProtocolHandlers in separate threads. </br>
	 * Also handles cleanup after the exit() signal is received.
	 */
	public static void launch() throws IllegalStateException {
		logger.trace("Entering Server.launch");
		
		if(state != ServerState.ready)
			throw new IllegalStateException("Server launch - not yet initialized, or already cleanedup");
		
		while(!exit) {
			try {
				Socket client = serverSocket.accept(); 
				logger.info("Connection received");
				Session handler = new Session(client);
				clients.add(handler); // For future cleanup
				handler.start();
			} catch (IOException e) {
				if(serverSocket.isClosed()) {
					logger.info("Server shutdown signal received.");
				} else {
					logger.error("IOException caught during ServerSocket.accept, but not a regular server shutdown signal.", e);
				}
			}
		}
		
		logger.trace("Exiting Server.launch");
	}
	
	/**
	 * Signals the server to cease all operations ASAP.
	 */
	public static void exit() {
		logger.trace("Entering Server.exit");
		exit = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.warn("Exception caught while closing ServerSocket.", e);
		}
		logger.trace("Exiting Server.exit");
	}
	
	
	/**
	 * Main function. Properly initializes and cleans up everything.
	 * @param args : not used.
	 */
	public static void main(String[] args) {
		logger.trace("Entering Server.main");
		
		//Initialization
		try {
			initAll();
		} catch (IllegalStateException | ClassNotFoundException | IOException | SQLException e) {
			logger.fatal("Can't initialize properly.", e);
			logger.trace("Exiting Server.main with an Exception");
			return;
		}
		
		logger.info("Server initialized.");
		
		// Exit signal management - TODO : check best practices for this.
		new Thread(new Runnable() {
			public void run() {
				Scanner sc = new Scanner(System.in);
				System.out.println("Press enter to exit");
				sc.nextLine();
				sc.close();
				exit();
			}
		}).start();
		
		
		// Server launch
		launch();
		
		// Cleanup
		cleanupAll();
		
		
		logger.info("Server shut down.");
		logger.trace("Exiting Server.main");
	}
}

enum ServerState {
	initial,
	ready
}
