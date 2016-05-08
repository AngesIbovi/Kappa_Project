package server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.query.*;
import model.response.*;
import model.response.GetSimsServerResponse.SimulationIdentifier;

import org.apache.log4j.Logger;

/**
 * Handles messages by interpreting them, and using JDBC connections acquired from the connection pool to treat them.</br>
 * What's important is that except in the case of the "BYE" message, the server always answers.</br>
 * This version of the protocol uses a two-level verification system : server responsed are prefixed by either
 * "OK" or "ERR" (if the query was ill-formatted, or a server-side issue makes handling it impossible), and if the prefix
 * was "OK", the JSON object contained within the response tells if the operation was carried out properly.
 * @version R3 sprint 1 - 18/04/2016
 * @author Kappa-V
 * @changes
 * 		R3 sprint 1 -> R3 sprint 2:</br>
 * 			-Removed the deprecated methods
 * 		R2 sprint 1 -> R3 sprint 1: </br>
 * 			-addition of the handleAuthQuery method</br>
 * 			-removal of the handleMessage method. It was moved to the Session class instead.</br>
 */
public abstract class MessageHandler {
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(MessageHandler.class);
	
	
	
	
	/**
	 * Tries to use the user id and password in the query to aunthentify.
	 * @param authQuery : the client's query
	 * @return the server's response to the query. 
	 * Typically an AuthenticationServerResponse, but can also be an ErrorServerResponse.
	 */
	public static ServerResponse handleAuthQuery(AuthenticationQuery authQuery) {
		logger.trace("Entering MessageHandler.handleAuthQuery");
		
		// Acquiring the JDBC connection from the pool
		Connection databaseConnection;
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (IllegalStateException | ClassNotFoundException | SQLException e) {
			logger.trace("Exiting MessageHandler.handleAuthQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		try {
			String SQLQuery = "SELECT * FROM USERS "
					+ "WHERE \"Login\" LIKE '" + authQuery.getId() + "'";
			
			Statement statement = databaseConnection.createStatement();
			
			try {
				ResultSet results = statement.executeQuery(SQLQuery);
				
				if(results.next()) {
					if(authQuery.getPassword().equals(results.getString("Password"))) {
						return new AuthenticationServerResponse(results.getInt("Authorization_Level"));
					} else {
						return new AuthenticationServerResponse(false);
					}
				} else {
					return new AuthenticationServerResponse(true);
				}
			} catch (SQLException e) {
				logger.warn("SQLException caught", e);
				throw e;
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			logger.warn("SQLException caught", e);
			logger.trace("Exiting MessageHandler.handleAuthQuery");
			return new ErrorServerResponse("Database error");
		} finally {
			// Good practice : the cleanup code is in a finally block.
			ConnectionPool.release(databaseConnection);
		}
	}
	
	/**
	 * Searches for accounts.
	 * @param query : contains optional search parameters:</br>
	 * If firstName or lastName are not null, they will be used as search parameters.</br>
	 * If myCustomers is true, the search will only take into account customers whose 
	 * adviser is the current user.
	 * @return the server's response to the query. Never null nor an exception.
	 */
	public static ServerResponse handleGetAccountsQuery(GetAccountsQuery query, String user_id) {
		logger.trace("Entering MessageHandler.handleGetAccountsQuery");
		
		// Constructing the SQL query
		String SQLquery = "SELECT A.Account_Id, A.Account_Num FROM ACCOUNTS A";
		
		if((query.getFirstName() != null) || (query.getLastName() != null) || (query.isMyCustomers())) {
			 SQLquery+= " INNER JOIN CUSTOMERS C ON A.Customer_Id=C.Customer_Id WHERE ";
		}
		
		boolean first = true;
		if(query.getFirstName() != null) {
			first = false;
			
			SQLquery += "C.First_Name LIKE '" + query.getFirstName() + "'";
		}
		
		if(query.getFirstName() != null) {
			if(!first) {
				SQLquery += " AND ";
			} else {
				first = false;
			}
			
			SQLquery += "C.Last_Name LIKE '" + query.getLastName() + "'";
		}
		
		if(query.isMyCustomers()) {
			if(!first) {
				SQLquery += " AND ";
			}
			
			SQLquery += "C.Advisor_Id IN (SELECT Advisor_Id FROM EMPLOYEES WHERE User_login='" + user_id + "')";
		}
		
		
		
		// Connection and treatment
		Connection databaseConnection;
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (Exception e) {
			logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		try {
			Statement statement = databaseConnection.createStatement();
			
			try {
				ResultSet results = statement.executeQuery(SQLquery);
				
				GetAccountsServerResponse response = new GetAccountsServerResponse();
				
				while(results.next()) {
					response.addAccount(results.getString("Account_Id"), results.getString("Account_Num"));
				}
				
				logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
				return response;
			} catch (SQLException e) {
				throw e;
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			logger.warn("SQLException caught", e);
			logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
			return new ErrorServerResponse("Database error");
		} finally {
			// Good practice : the cleanup code is in a finally block.
			ConnectionPool.release(databaseConnection);
		}
	}
	
	/**
	 * Searches for simulations associated with a particular account.
	 * @param query : contains the account id.
	 * @return the server's response to the query. Never null nor an exception.
	 */
	public static ServerResponse handleGetSimsQuery(GetSimsQuery query) {
		logger.trace("Entering MessageHandler.handleGetSimsQuery");
		
		String SQLquery = "SELECT Loan_Id, Name FROM Loans WHERE Is_Real='N' AND Account_Id='" + query.getAccount_id() + "'";
		
		Connection databaseConnection;
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (Exception e) {
			logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		try {
			Statement statement = databaseConnection.createStatement();

			try {
				ResultSet results = statement.executeQuery(SQLquery);
				
				GetSimsServerResponse response = new GetSimsServerResponse();
				
				while(results.next()) {
					response.addSimulation(new SimulationIdentifier(results.getString("Name"), results.getString("Loan_Id")));
				}
				
				logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
				return response;
			} catch (SQLException e) {
				throw e;
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			logger.warn("SQLException caught", e);
			logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
			return new ErrorServerResponse("Database error");
		} finally {
			// Good practice : the cleanup code is in a finally block.
			ConnectionPool.release(databaseConnection);
		}
	}

	/**
	 * Searches for one simulation in particular
	 * @param query : contains the simulation id.
	 * @return the server's response to the query. Never null nor an exception.
	 */
	public static ServerResponse handleGetSimQuery(GetSimQuery query) {
		logger.trace("Entering MessageHandler.handleGetSimQuery");
		
		// SQL queries
		String SQLquery1 = "SELECT * FROM Repayments WHERE \"Loan_Id\"='" + query.getSim_id() + "'";
		String SQLquery2 = "SELECT * FROM Events WHERE Loan_Id='" + query.getSim_id() + "'";
		String SQLquery3 = "SELECT * FROM Loans WHERE Loan_Id='" + query.getSim_id() + "'";
		
		// Connection and treatment
		Connection databaseConnection;
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (Exception e) {
			logger.trace("Exiting MessageHandler.handleGetSimQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		try {
			Statement statement = databaseConnection.createStatement();

			try {
				GetSimServerResponse response = new GetSimServerResponse();
				
				/* Repayments */
				ResultSet results = statement.executeQuery(SQLquery1);
				while(results.next()) {
					response.getRepayments().add(new GetSimServerResponse.Repayment(
						results.getDate("Date"),
						results.getFloat("Capital"),
						results.getFloat("Interest"),
						results.getFloat("Insurance")
					));
				}
				
				
				/* Events */ 
//				results = statement.executeQuery(SQLquery2);
//				while(results.next()) {
//					response.getEvents().add(new GetSimServerResponse.Event(
//						GetSimServerResponse.Event.EventType.valueOf(results.getString("Type")),
//						results.getDate("StartDate"),
//						results.getDate("EndDate"),
//						results.getFloat("Value"),
//						results.getBoolean("Is_Real")
//					));
//				}
				
				/* Other attributes */
				results = statement.executeQuery(SQLquery3);
				if(results.next()) {
					response.setAmortizationType(GetSimServerResponse.AmortizationType.valueOf(results.getString("Amortization_Type")));
					response.setCapital(results.getFloat("Capital"));
					response.setEffectiveDate(results.getDate("Effective_Date"));
					response.setId(query.getSim_id());
					response.setName(results.getString("Name"));
					response.setRemainingOwedCapital(results.getFloat("RemainingOwedCapital"));
					response.setRemainingRepayments(results.getInt("Remaining_Repayments"));
					response.setRepaymentConstant(results.getInt("Repayment_Constant"));
					response.setRepaymentFrequency(results.getInt("Repayment_Frequency"));
				}
				
				/* Return */
				logger.trace("Exiting MessageHandler.handleGetSimQuery");
				return response;
			} catch (SQLException e) {
				throw e;
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			logger.warn("SQLException caught", e);
			logger.trace("Exiting MessageHandler.handleGetSimQuery");
			return new ErrorServerResponse("Database error");
		} finally {
			// Good practice : the cleanup code is in a finally block.
			ConnectionPool.release(databaseConnection);
		}
	}
}