package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.*;
import org.apache.log4j.Logger;

import util.KappaProperties;

/**
 * This class grants access to database connections in a very quick fashion, by managing a pool of them
 * created on program launch.
 * @version R2 sprint 1 - 16/03/2016
 * @author Kappa-V
 */
public class ConnectionPool {
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(ConnectionPool.class);
	
	/**
	 * Private empty constructor : makes it impossible to instantiate ConnexionPool
	 */
	private ConnectionPool() {}
	
	
	/**
	 * The structure containing the JDBC connections not yet dispensed.
	 * In this implementation, we use a PriorityBlockingQueue. 
	 */
	private static PriorityBlockingQueue<ComparableConnectionWrapper> availableConnections = new PriorityBlockingQueue<>();
	
	
	/**
	 * Status attribute. All methods in this implementation can throw IllegalStateExceptions.
	 */
	private static ConnexionPoolState state = ConnexionPoolState.initial;
	
	/**
	 * The timeout of the acquire function, which will trigger the creation of a new JDBC connection to avoid starvation, should it be reached.
	 * It is in milliseconds.
	 */
	private static int timeout;
	
	
	
	
	/**
	 * Must be called first.
	 * @throws IllegalStateException : if ConnectionPool was already initialized, or already cleaned up.
	 * @throws ClassNotFoundException : if the JDBC driver can't be found
	 * @throws SQLException : if the database can't be connected to
	 */
	public static synchronized void init() throws IllegalStateException, ClassNotFoundException, SQLException {
		logger.trace("Entering ConnectionPool.init");
		//State management
		switch(state) {
		case ready:
			logger.trace("Exiting ConnectionPool.init with an IllegalStateException");
			throw new IllegalStateException("ConnectionPool init - already initialized");
		default:
			break;
		}
		
		//Loading properties
		Properties prop = KappaProperties.getInstance();
		
		String DB_DRIVER_NAME = prop.getProperty("DB_DRIVER_NAME");
		String DB_URL = prop.getProperty("DB_URL");
		String DB_CONNECTION_LOGIN = prop.getProperty("DB_CONNECTION_LOGIN");
		String DB_CONNECTION_PASSWORD = prop.getProperty("DB_CONNECTION_PASSWORD");
		int DB_CONNECTION_POOL_SIZE = Integer.parseInt(prop.getProperty("DB_CONNECTION_POOL_SIZE"));
		timeout = Integer.parseInt(prop.getProperty("DB_CONNECTION_POOL_ACQUIRE_TIMEOUT"));
		
		//Creating JDBC connections
		try {
			Class.forName(DB_DRIVER_NAME);
			
			for(int i = 0 ; i < DB_CONNECTION_POOL_SIZE ; i++) {
				Connection newCo = DriverManager.getConnection(DB_URL, DB_CONNECTION_LOGIN, DB_CONNECTION_PASSWORD);
				newCo.setAutoCommit(false);
				availableConnections.add(new ComparableConnectionWrapper(newCo));
			}
		} catch (ClassNotFoundException e) {
			logger.trace("Exiting ConnectionPool.init with a ClassNotFoundException");
			throw e;
		} catch (SQLException e) {
			logger.trace("Exiting ConnectionPool.init with a SQLException");
			throw e;
		}
		
		state = ConnexionPoolState.ready;
		logger.trace("Exiting ConnectionPool.init");
	}
	
	
	/**
	 * Call this method before shutting down the server. 
	 * In this implementations, all connections must be released before cleanup. See the Server activity diagram for details.
	 * As a security measure, all connections will receive a rollback before being closed.
	 * @throws IllegalStateException : if ConnectionPool was already cleaned up.
	 * @version R2 sprint 1 - 16/03/2016
	 */
	public static synchronized void cleanup() {
		logger.trace("Entering ConnectionPool.cleanup");
		
		if (state == ConnexionPoolState.initial) {
			logger.trace("Exiting ConnectionPool.cleanup with no treatment needed");
			return; //Method exists because the ConnectionPool is already clean (not yet initialized, or already cleaned up)
		} else
			// Changing the state before actually going through with the cleanup stops other methods from doing unsafe operations.
			state = ConnexionPoolState.initial;

		// Actual cleanup. The try block is inside the for cycle so that in case an exception is raised, the rest of the cleanup can still occur.
		for(ComparableConnectionWrapper wrapper : availableConnections) {
			try {
					Connection co = wrapper.getConnection();
					co.rollback();
					co.close();
			} catch (SQLException e) {
				logger.warn("SQLException raised while closing a JDBC connection. Cleanup continues.", e);
			}
		}
		
		logger.trace("Exiting ConnectionPool.cleanup");
	}
	
	
	
	
	
	/**
	 * Retrieves and removes an already open and well configurated JDBC connection from the pool. </br>
	 * This implementation of the acquire() method has a timeout defined in the server properties. 
	 * In case that timeout is reached, a new JDBC connection is opened to avoid starvation.
	 * @return a Connection which must be released after exactly one transaction has been performed.
	 * @throws IllegalStateException : if the Connection Pool is not yet initialized, or already cleaned up
	 * @throws SQLException : if a new JDBC connection can't be created (in example, if a database access error occurs)
	 * @throws ClassNotFoundException : shouldn't happen usually.
	 */
	public static Connection acquire() throws IllegalStateException, SQLException, ClassNotFoundException {
		logger.trace("Entering ConnectionPool.acquire");
		
		//State management
		switch(state) {
		case initial:
			logger.trace("Exiting ConnectionPool.acquire with an IllegalStateException");
			throw new IllegalStateException("ConnectionPool acquire - not yet initialized, or already cleaned up");
		default:
			break;
		}
		
		// Trying to take a connection from the blocking queue until timeout is reached.
		ComparableConnectionWrapper wrapper = null;
		try {
			wrapper = availableConnections.poll(timeout, TimeUnit.MILLISECONDS); // Returns null if timeout is reached.
		} catch (InterruptedException e) {
			// Do nothing
		}
		
		if(wrapper != null) {
			logger.trace("Exiting ConnectionPool.acquire");
			return wrapper.getConnection();
		}
		
		logger.trace("ConnectionPool.acquire : timeout reached, creating a new connection.");
		/* Creating a new JDBC connection to avoid starvation */
		
		//Loading properties
		Properties prop = KappaProperties.getInstance();
		
		String DB_DRIVER_NAME = prop.getProperty("DB_DRIVER_NAME");
		String DB_URL = prop.getProperty("DB_URL");
		String DB_CONNECTION_LOGIN = prop.getProperty("DB_CONNECTION_LOGIN");
		String DB_CONNECTION_PASSWORD = prop.getProperty("DB_CONNECTION_PASSWORD");
		
		//Creating a new JDBC connection
		Class.forName(DB_DRIVER_NAME);
		Connection newCo = DriverManager.getConnection(DB_URL, DB_CONNECTION_LOGIN, DB_CONNECTION_PASSWORD);
		newCo.setAutoCommit(false);
		
		logger.trace("Exiting ConnectionPool.acquire after timeout was reached.");
		return newCo;
	}
	
	/**
	 * Inserts the JDBC connection you were given by the acquire() method back into the connection pool.
	 * @param c : the JDBC connection you were given by the acquire() method.
	 */
	public static void release(Connection c) {
		logger.trace("Entering ConnectrionPool.release");
		availableConnections.offer(new ComparableConnectionWrapper(c));
		logger.trace("Exiting ConnectionPool.release");
	}
}

enum ConnexionPoolState {
	initial,
	ready
}

/**
 * Wrapper class for Connection which implements Comparable.
 * This class is necessary for ConnectionPool, which uses a PriorityBlockingQueue, an implementation which requires comparable elements.</br>
 * This implementations compares the toString() values of the connections.
 * @version R2 sprint 1 - 16/03/2016
 * @author Kappa
 *
 */
class ComparableConnectionWrapper implements Comparable<ComparableConnectionWrapper> {
	private final Connection c;
	
	public ComparableConnectionWrapper(Connection c) {
		this.c = c;
	}
	
	public Connection getConnection() {
		return c;
	}
	
	/**
	 * Grants the ability to compare connections.</br>
	 * Compares the toString() values of the connections.
	 */
	@Override
	public int compareTo(ComparableConnectionWrapper comparableConnection) {
		return this.c.toString().compareTo(comparableConnection.c.toString());
	}
}