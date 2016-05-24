package server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.query.*;
import model.response.*;
import model.response.GetLoanServerResponse.RateList;
import model.response.GetSimsServerResponse.SimulationIdentifier;

import org.apache.log4j.Logger;

/**
 * Handles messages by interpreting them, and using JDBC connections acquired from the connection pool to treat them.</br>
 * What's important is that except in the case of the "BYE" message, the server always answers.</br>
 * This version of the protocol uses a two-level verification system : server responsed are prefixed by either
 * "OK" or "ERR" (if the query was ill-formatted, or a server-side issue makes handling it impossible), and if the prefix
 * was "OK", the JSON object contained within the response tells if the operation was carried out properly.
 * @version R3 sprint 3 - 08/05/2016
 * @author Kappa-V
 * @changes
 * 		R3 sprint 2 -> R3 sprint 3:</br>
 * 			-Renamed handleGetAccountsQuery into handleSearchAccountsQuery</br>
 * 			-Added a new method, handleGetAccountsQuery. Since the code for that new method and the now renamed 
 * 			 handleSearchAccountsQuery are very similar, a new private handleGetOrSearchHandleQuery method was created to factorise
 * 			 the database transaction code that was common to both. 
 * 			-handleSearchAccounts now fetches the name of the owner of each account in the Customers table.</br>
 * 		R3 sprint 1 -> R3 sprint 2:</br>
 * 			-Removed the deprecated methods</br>
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
					+ "WHERE \"LOGIN\" LIKE '" + authQuery.getId() + "'";
			
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
	 * @param getAccountsQuery : contains the login of the customer whose accounts this method is supposed to return.
	 * @return the server's response to the query. Never null nor an exception
	 */
	public static ServerResponse handleGetAccountsQuery(GetAccountsQuery getAccountsQuery) {
		logger.trace("Entering MessageHandler.handleGetAccountsQuery");
		
		// Constructing the SQL query
		String SQLquery = "SELECT A.Account_Id, A.Account_Num, C.First_Name, C.Last_Name FROM ACCOUNTS A"
				+ " INNER JOIN CUSTOMERS C ON A.Customer_Id=C.Customer_Id"
				+ " WHERE C.User_login='" + getAccountsQuery.getCust_login() + "'";
		

		logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
		return handleGetOrSearchHandleQuery(SQLquery);
	}
	
	/**
	 * Searches for accounts.
	 * @param query : contains optional search parameters:</br>
	 * If firstName or lastName are not null, they will be used as search parameters.</br>
	 * If myCustomers is true, the search will only take into account customers whose 
	 * adviser is the current user.
	 * @return the server's response to the query. Never null nor an exception.
	 */
	public static ServerResponse handleSearchAccountsQuery(SearchAccountsQuery query, String user_id) {
		logger.trace("Entering MessageHandler.handleSearchAccountsQuery");
		
		// Constructing the SQL query
		String SQLquery = "SELECT A.Account_Id, A.Account_Num, C.First_Name, C.Last_Name FROM ACCOUNTS A INNER JOIN CUSTOMERS C ON A.Customer_Id=C.Customer_Id";
		
		if((query.getFirstName() != null) || (query.getLastName() != null) || (query.isMyCustomers())) {
			 SQLquery+= " WHERE ";
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
		
		logger.trace("Exiting MessageHandler.handleSearchAccountsQuery");
		return handleGetOrSearchHandleQuery(SQLquery);
	}
	
	/**
	 * GetAccounts and SearchAccounts return similar results, only the SQLquery is different. This method is used to factorise the code for both methods.
	 * @param SQLquery : the SQL query constructed in handleGetAccountsQuery or handleSearchAccountsQuery
	 * @return the server's response. Never null not an exception.
	 */
	private static ServerResponse handleGetOrSearchHandleQuery(String SQLquery) {
		logger.trace("Entering MessageHandler.handleGetOrSearchHandleQuery");
		
		// Connection and treatment
		Connection databaseConnection;
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (Exception e) {
			logger.trace("Exiting MessageHandler.handleGetOrSearchHandleQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		try {
			Statement statement = databaseConnection.createStatement();
			
			try {
				ResultSet results = statement.executeQuery(SQLquery);
				
				GetAccountsServerResponse response = new GetAccountsServerResponse();
				
				while(results.next()) {
					String id = results.getString("Account_Id");
					String num = results.getString("Account_Num");
					String name = results.getString("First_Name") + ' ' + results.getString("Last_Name");
					
					response.addAccount(id, num, name);
				}
				
				logger.trace("Exiting MessageHandler.handleGetOrSearchHandleQuery");
				return response;
			} catch (SQLException e) {
				throw e;
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			logger.warn("SQLException caught", e);
			logger.trace("Exiting MessageHandler.handleGetOrSearchHandleQuery");
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
	 * Get all Simulations.
	 * @param query : contains the account id.
	 * @return the server's response to the query. Never null nor an exception.
	 */
	public static ServerResponse handleGetAllSimsQuery(GetAllSimsQuery query) {
		logger.trace("Entering MessageHandler.handleGetSimsQuery");
		
		String SQLquery = "SELECT Loan_Id, Name FROM Loans WHERE Account_Id<>'" + query.getAccount_id() + "'";
		
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
	 * Get a list of account number of customer and send this to GUI of variable loan
	 * @param Account : all account number
	 * @return the server's response to the query. 
	 * Typically an GetAllaccountsServerResponse, but can also be an ErrorServerResponse.
	 */
	
	
	public static ServerResponse handleGetAllLoanTypeQuery(){
		Connection databaseConnection;
		ArrayList<String> array = new ArrayList<>();
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (IllegalStateException | ClassNotFoundException | SQLException e) {
			logger.trace("Exiting MessageHandler.handleAuthQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		try {
			String SQLQuery = "SELECT NAME FROM LOAN_TYPES";
			
			Statement statement = databaseConnection.createStatement();
			
			try {
				ResultSet results = statement.executeQuery(SQLQuery);
				
				GetAllLoanTypeServerReponse getAlltypeloan = new GetAllLoanTypeServerReponse ();
				
				while(results.next()) {
					array.add(results.getString("Name"));
				}
				getAlltypeloan.setArray(array);
				logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
				return getAlltypeloan;
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
	
	

	

	public static ServerResponse handleGetrateQuery(){
		Connection databaseConnection;
		
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (IllegalStateException | ClassNotFoundException | SQLException e) {
			logger.trace("Exiting MessageHandler.handleAuthQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		try {
			String SQLQuery = "select  MAX(value) as value from loan_rate_history where change_date like (current_date)";
			System.out.println(SQLQuery);
			Statement statement = databaseConnection.createStatement();
			
			try {
				ResultSet results = statement.executeQuery(SQLQuery);
				//double value=0;
				
				while(results.next()){
				//	value = results.getDouble("value");
					System.out.println("resultat "+results.getFloat("value"));
				}
				
				
				
				GetValueOfRateServerResponse getvalueofrate = new GetValueOfRateServerResponse ();
				//getvalueofrate.setRate(value);
				
				logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
				return getvalueofrate;
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
	 * Get a list of account number of customer and send this to GUI of variable loan
	 * @param Account : all account number
	 * @return the server's response to the query. 
	 * Typically an GetAllaccountsServerResponse, but can also be an ErrorServerResponse.
	 */

	public static ServerResponse handleGetAllAccountQuery(){
		Connection databaseConnection;
		ArrayList<String> array = new ArrayList<>();
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (IllegalStateException | ClassNotFoundException | SQLException e) {
			logger.trace("Exiting MessageHandler.handleAuthQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		try {
			String SQLQuery = "SELECT ACCOUNT_NUM FROM ACCOUNTS";
			
			Statement statement = databaseConnection.createStatement();
			
			try {
				ResultSet results = statement.executeQuery(SQLQuery);
				
				GetAllAcountsServerResponse getAllaccounts = new GetAllAcountsServerResponse();
				
				while(results.next()) {
					array.add(results.getString("ACCOUNT_NUM"));
				}
				getAllaccounts.setArray(array);
				logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
				return getAllaccounts;
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
	 * Get loan list. 
	 * @return the server's response to the query. 
	 * Typically an AuthenticationServerResponse, but can also be an ErrorServerResponse.
	 */
	public static ServerResponse handleGetRatesQuery(GetLoanQuery query) {
		logger.trace("Entering MessageHandler.handleGetRatesQuery");
		
		String SQLquery = "SELECT * FROM LOAN_TYPES WHERE LOAN_TYPE_ID<>'" + query.getRate_id() + "'";
		 
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

			
			// DetermineTheMountOFInterestRate
			try {
				ResultSet results = statement.executeQuery(SQLquery);
				
				GetLoanServerResponse response = new GetLoanServerResponse();
				while(results.next()) {
					response.getRate_list().add(new RateList(results.getString("Loan_Type_Id"), results.getString("Name"), results.getFloat("Max_Duration")));
				}
				
				 System.out.println(results);
				logger.trace("Exiting MessageHandler.handleGetLoanQuery");
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
		String SQLquery3 = 
				"SELECT Insurance, "
					+ "PROCESSING_FEE, "
					+ "Is_Real, "
					+ "Amortization_Type,"
					+ "Capital,cust.AGE as AGE,"
					+ "Effective_Date,"
					+ "lo.Name as Name,"
					+ "RemainingOwedCapital,"
					+ "Remaining_Repayments,"
					+ "Repayment_Constant,"
					+ "Repayment_Frequency,"
					+ "lo.AGE as AGE,"
					+ "CONCAT (cust.FIRST_NAME ,' '|| cust.LAST_NAME) as User_login,"
					+ "lo.LOAN_TYPE_ID,"
					+ "Account_Num,"
					+ "lt.NAME as Loan_Type "
				+ "FROM "
					+ "Loans lo, "
					+ "Loan_Types lt,"
					+ "Accounts ac, "
					+ "Customers cust "
				+ "WHERE "
					+ "lo.ACCOUNT_ID=ac.ACCOUNT_ID "
					+ "AND lo.LOAN_TYPE_ID=lt.LOAN_TYPE_ID "
					+ "AND cust.CUSTOMER_ID=ac.CUSTOMER_ID "
					+ "AND lo.Loan_Id='" + query.getSim_id() + "'";
	
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
	//			results = statement.executeQuery(SQLquery2);
	//			while(results.next()) {
	//				response.getEvents().add(new GetSimServerResponse.Event(
	//					GetSimServerResponse.Event.EventType.valueOf(results.getString("Type")),
	//					results.getDate("StartDate"),
	//					results.getDate("EndDate"),
	//					results.getFloat("Value"),
	//					results.getBoolean("Is_Real")
	//				));
	//			}
				
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
					response.setAccountId(results.getString("User_login"));
					response.setLoanTypeId(results.getString("LOAN_TYPE_ID")); 
					response.setAccountNum(results.getString("Account_Num"));
					response.setTypeSim(results.getString("Loan_Type"));
					response.setIs_reel("Y".equals(results.getString("Is_Real")));
					response.setAge((results.getString("AGE")));
					response.setInsurance((results.getInt("Insurance")));
					response.setProcessing_fee((results.getInt("PROCESSING_FEE")));
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