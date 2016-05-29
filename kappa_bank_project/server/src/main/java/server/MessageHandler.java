package server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import model.query.AuthenticationQuery;
import model.query.DynamiqueResearchQuery;
import model.query.GetAccountsQuery;
import model.query.GetAllSimsQuery;
import model.query.GetCustomersQuery;
import model.query.GetLoanQuery;
import model.query.GetSimQuery;
import model.query.GetSimsQuery;
import model.query.LoansQuery;
import model.query.SearchAccountsQuery;
import model.query.SignLoanQuery;
import model.response.AuthenticationServerResponse;
import model.response.AverageDurationResponse;
import model.response.AverageDurationResponse.AverageClass;
import model.response.DynamiqueResearchResponse;
import model.response.DynamiqueResearchResponse.SumInterest;
import model.response.ErrorServerResponse;
import model.response.GetAccountsServerResponse;
import model.response.GetAllAcountsServerResponse;
import model.response.GetAllLoanTypeServerReponse;
import model.response.GetLoanServerResponse;
import model.response.GetLoanServerResponse.RateList;
import model.response.GetSimsServerResponse;
import model.response.GetSimsServerResponse.SimulationIdentifier;
import model.response.GetValueOfRateServerResponse;
import model.response.LoansResponse;
import model.response.MustSimulatedLoanResponse;
import model.response.NumberOfLoanResponse;
import model.response.ServerResponse;
import model.response.SignLoanServerResponse;
import model.response.SumOfInterestResponse;
import model.response.SumOfInterestResponse.Interest;
import model.response.evolutionOfTheSimulationsResponse;
import model.response.evolutionOfTheSimulationsResponse.ListResult;
import model.simulation.Event;
import model.simulation.RateChange;
import model.simulation.Repayment;
import model.simulation.Simulation;

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
	
	
	
	
	public static ServerResponse handleLoansQuery( LoansQuery loans){
		
		Connection databaseConnection;
				
				try {
					databaseConnection = ConnectionPool.acquire();
				} catch (IllegalStateException | ClassNotFoundException | SQLException e) {
					logger.trace("Exiting MessageHandler.handleAuthQuery");
					logger.warn("Can't acquire a connection from the pool", e);
					return new ErrorServerResponse("Server-side error. Please retry later.");
				}
				
				try {
					String SQLQuery = 
							"INSERT INTO LOANS (LOAN_ID,IS_REAL,ACCOUNT_ID,LOAN_TYPE_ID,EFFECTIVE_DATE,CAPITAL,REMAININGOWEDCAPITAL,REPAYMENT_FREQUENCY,REMAINING_REPAYMENTS,REPAYMENT_CONSTANT,RATE_NATURE,AMORTIZATION_TYPE,NAME,INSURANCE,PROCESSING_FEE,DURATIONS) "
							
							+ " values "
					
						
							+"( LOANS_SEQ.NEXTVAL ,'"
							+loans.getIs_real()+"',"
							+" (select account_id from accounts where account_num='"+loans.getAccount_id()+"'),"
							+"(select loan_type_id from loan_types where name= '"+loans.getLoan_type()+"'),'"
							+loans.getEffective_date()+"',"
							+loans.getCapital()+","
							+loans.getRemaining_repayment()+","
							+loans.getRepayment_frequency()+","
							+loans.getRemaining_repayment()+","
							+loans.getRepayment_constant()+",'"
							+loans.getRate_nature()+"','"
							+loans.getAmortization_type()+"','"
							+loans.getName()+"',"
							+loans.getInsurance()+","
							+loans.getProgressing_fee()+","
							+loans.getDuration()+")";
							
							
							
							
					
					Statement statement = databaseConnection.createStatement();
					System.out.println(SQLQuery);
					try {
						boolean results = statement.execute(SQLQuery);
						databaseConnection.commit();
						if(results)System.out.println("insére");
						
						LoansResponse loansresponse = new LoansResponse();
						
						
						logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
						return loansresponse;
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
		
		String SQLquery = "SELECT Loan_Id, Name FROM Loans WHERE Account_Id='" + query.getAccount_id() + "'";
		
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
			String SQLQuery = "select  * from  loan_rate_history where lrh_id=(select max(lrh_id) from loan_rate_history)";

			Statement statement = databaseConnection.createStatement();
			
			try {
				ResultSet results = statement.executeQuery(SQLQuery);
				float value=0;
				
				while(results.next()){
					value = results.getFloat("value");
					System.out.println("resultat "+results.getFloat("value"));
				}
				
				
				
				GetValueOfRateServerResponse getvalueofrate = new GetValueOfRateServerResponse ();
				getvalueofrate.setRate(value);
				
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
					+ "lt.NAME as Loan_Type,"
					+ "RATE_NATURE "
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
		String SQLquery4 = "SELECT LRH.CHANGE_DATE, LRH.VALUE "
				 + "FROM LOANS INNER JOIN LOAN_RATE_HISTORY LRH ON LOANS.LOAN_TYPE_ID = LRH.LOAN_TYPE_ID "
				 + "WHERE LOANS.Loan_Id = '" + query.getSim_id() + "' "
				 + "ORDER BY LRH.CHANGE_DATE ASC";
	
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
				Simulation response = new Simulation();
	
				/* Repayments */
				ResultSet results = statement.executeQuery(SQLquery1);
				while(results.next()) {
					response.getRepayments().add(new Repayment(
						results.getDate("Date"),
						results.getFloat("Capital"),
						results.getFloat("Interest"),
						results.getFloat("Insurance")
					));
				}
				
				
				/* Events */ 
				results = statement.executeQuery(SQLquery2);
				while(results.next()) {
					response.getEvents().add(new Event(
						Event.EventType.valueOf(results.getString("Type")),
						results.getDate("StartDate"),
						results.getDate("EndDate"),
						results.getFloat("Value"),
						results.getBoolean("Is_Real")
					));
				}
				
				
				/* Rate Changes */
				results = statement.executeQuery(SQLquery4);
				while(results.next()) {
					response.getRateHistory().add(new RateChange(
							results.getDate("CHANGE_DATE"),
							results.getDouble("VALUE")
					));
				}
				
				/* Other attributes */
				results = statement.executeQuery(SQLquery3);
				if(results.next()) {
					response.setAmortizationType(Simulation.AmortizationType.valueOf(results.getString("Amortization_Type")));
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
					response.setProcessing_fee((results.getFloat("PROCESSING_FEE")));
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

 
	/**
	 * Searches for accounts.
	 * @param getAccountsQuery : contains the login of the customer whose accounts this method is supposed to return.
	 * @return the server's response to the query. Never null nor an exception
	 */
	public static ServerResponse handleGetCustomersQuery(GetCustomersQuery getCustQuery) {
		logger.trace("Entering MessageHandler.handleGetCustomersQuery");
		
		// Constructing the SQL query
		String SQLquery = "SELECT A.Account_Id, A.Account_Num, C.First_Name, C.Last_Name FROM ACCOUNTS A"
				+ " INNER JOIN CUSTOMERS C ON A.Customer_Id=C.Customer_Id"
				+ " WHERE C.User_login<>'" + getCustQuery.getCust_login() + "'";
		

		logger.trace("Exiting MessageHandler.handleGetCustomersQuery");
		return handleGetOrSearchHandleQuery(SQLquery);
	}

 

	/**
	 * Transforms a simulation into a real loan.
	 * @param query : contains the simulation id.
	 * @return the server's response to the query. Never null nor an exception.
	 */
	public static ServerResponse handleSignLoanQuery(SignLoanQuery query, String username) {
		logger.trace("Entering MessageHandler.handleSignLoanQuery");
		
		String SQLquery1 = "SELECT \"LOGIN\" FROM USERS WHERE \"LOGIN\" LIKE '" + username + "' AND \"PASSWORD\" LIKE '" + query.getPassword() + "'";
		
		Connection databaseConnection;
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (Exception e) {
			logger.trace("Exiting MessageHandler.handleSignLoanQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		// Verifying the user's identity
		try {
			Statement statement = databaseConnection.createStatement();
			
			try {
				ResultSet results = statement.executeQuery(SQLquery1);
				
				if(!results.next()) {
					logger.trace("Exiting MessageHandler.handleSignLoanQuery");
					return new SignLoanServerResponse(SignLoanServerResponse.Status.KO, SignLoanServerResponse.Status.KO);
				}
			} catch (SQLException e) {
				throw e;
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			logger.warn("SQLException caught", e);
			logger.trace("Exiting MessageHandler.handleSignLoanQuery");
			return new ErrorServerResponse("Database error");
		} finally {
			// Good practice : the cleanup code is in a finally block.
			ConnectionPool.release(databaseConnection);
		}
		
		
		
		
		// Making the loan real in the database
		ServerResponse getSimServerResponse = handleGetSimQuery(new GetSimQuery(query.getSimId()));
		if(!(getSimServerResponse instanceof Simulation)) {
			return getSimServerResponse;
		}
		
		Simulation simulation = (Simulation) getSimServerResponse;
		
		List<Event> events = new ArrayList<>();
		for(Event e : simulation.getEvents()) {
			if(e.isReal()) {
				events.add(e);
			}
		}
		
		simulation.setRepayments(new ArrayList<Repayment>());
		simulation.calculateAmortizationTable();
		
		String SQLQuery2 = "DELETE FROM Repayments WHERE \"Loan_Id\"='" + query.getSimId() + "'";
		String SQLQuery3 = "DELETE FROM EVENTS WHERE IS_REAL='N' AND LOAN_ID='" + query.getSimId() + "'";
		String SQLQuery4 = "UPDATE ACCOUNTS SET BALANCE=BALANCE + " + simulation.getCapital() + " WHERE ACCOUNT_ID='" + simulation.getAccountId() + "'";
		String SQLQuery5 = "UPDATE LOANS SET IS_REAL='Y' WHERE LOAN_ID='" + query.getSimId() + "'";
		
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (Exception e) {
			logger.trace("Exiting MessageHandler.handleSignLoanQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		// Verifying the user's identity
		try {
			Statement statement = databaseConnection.createStatement();
			
			try {
				statement.executeUpdate(SQLQuery2);
				statement.executeUpdate(SQLQuery3);
				statement.executeUpdate(SQLQuery4);
				statement.executeUpdate(SQLQuery5);
				
				for(Repayment repayment : simulation.getRepayments()) {
					String repaymentSQLQuery = "INSERT INTO REPAYMENTS VALUES (REPAYMENTS_SEQ.NEXTVAL, "
									 + "'" + query.getSimId() + "', "
									 + "'" + repayment.getDate() + "', "
									 + repayment.getCapital() + ", "
									 + repayment.getInterest() + ", "
									 + repayment.getInsurance() + ")";
					
					statement.executeUpdate(SQLQuery4);
				}
				
				return new SignLoanServerResponse(SignLoanServerResponse.Status.OK, SignLoanServerResponse.Status.OK);
			} catch (Exception e) {
				throw e;
			} finally {
				statement.close();
			}
		} catch (Exception e) {
			try {
				databaseConnection.rollback(); // If anything -ANYTHING- bad happens, the changes to the tables are rolled back for security reasons.
			} catch (SQLException e2) {
				// Do nothing
			}
			logger.warn("SQLException caught", e);
			logger.trace("Exiting MessageHandler.handleSignLoanQuery");
			return new ErrorServerResponse("Database error");
		} finally {
			// Good practice : the cleanup code is in a finally block.
			ConnectionPool.release(databaseConnection);
		}
	}
	
	
public static ServerResponse handleNumberOfLoan(String date){
		
		Connection databaseConnection;
		
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (IllegalStateException | ClassNotFoundException | SQLException e) {
			logger.trace("Exiting MessageHandler.handleAuthQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		try {
			String SQLQuery = "select COUNT(effective_date) as effcount from loans where extract(year from EFFECTIVE_DATE )= "+date+" and is_real='Y'";
			String SQLQuery2 = "select COUNT(effective_date) as allLoan from loans where extract(year from EFFECTIVE_DATE )= "+date;
			Statement statement = databaseConnection.createStatement();
			Statement statement2 = databaseConnection.createStatement();
			try {
				ResultSet results = statement.executeQuery(SQLQuery);
				ResultSet result2 = statement2.executeQuery(SQLQuery2);
				
				
				int numberOffLon = 20;
				int allLoan = 0;
				
				while(results.next()) {
					
				numberOffLon=results.getInt("effcount");
				
				System.out.println("nombre of loan "+results.getString("effcount"));
				}
				while(result2.next()){
					allLoan=result2.getInt("allLoan");
				}
				System.out.println("Number of simulation"+allLoan);
				
				NumberOfLoanResponse response = new NumberOfLoanResponse();
				
				response.setNumberOfLoans(numberOffLon);
				response.setNumberOfSimulations(allLoan);
			//	System.out.println(response.getNumberOfLoans() +" : "+response.getNumberOfLoans());
				logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
				return response; 
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





public static ServerResponse handleInterestBySegment(DynamiqueResearchQuery Dynamiquequery){
		
	System.out.println(Dynamiquequery.toString());
		logger.trace("Entering MessageHandler.handleGetSimsQuery");
		String SQLquery = null;
		int agetranche ;
		System.out.println(Dynamiquequery.getTypeOfLoans());
		
		System.out.println(Dynamiquequery.getDate());
		if(Dynamiquequery.getAgeRange()<=25){
			agetranche=25;
			SQLquery=" select lt.name ,sum(rp.INTEREST)as sumInterest"+
					 ",extract(year from rp.\"Date\") as dates from customers c, "
					+ "accounts a, loans l, loan_types lt,repayments rp where c.AGE<"
					+agetranche+" and lt.NAME='"+Dynamiquequery.getTypeOfLoans()
							+"' and extract(year from rp.\"Date\")="+Dynamiquequery.getDate()+""
									+ " and c.CUSTOMER_id=a.customer_id and l.account_id=a.account_id "
									+ "and l.loan_type_id=lt.LOAN_TYPE_ID and rp.\"Loan_Id\"=l.\"LOAN_ID\""
									+ " group by lt.name, extract(year from rp.\"Date\")"
							+" order by extract(year from rp.\"Date\")";
		}
			
		
		else if(Dynamiquequery.getAgeRange()>25 && Dynamiquequery.getAgeRange()<50){
			 agetranche=50;
				SQLquery=" select lt.name ,sum(rp.INTEREST)as sumInterest"+
						 ",extract(year from rp.\"Date\") as dates from customers c, "
						+ "accounts a, loans l, loan_types lt,repayments rp where c.AGE<"
						+agetranche+" and c.age>25 and lt.NAME='"+Dynamiquequery.getTypeOfLoans()
								+"' and extract(year from rp.\"Date\")="+Dynamiquequery.getDate()+""
										+ " and c.CUSTOMER_id=a.customer_id and l.account_id=a.account_id "
										+ "and l.loan_type_id=lt.LOAN_TYPE_ID and rp.\"Loan_Id\"=l.\"LOAN_ID\""
										+ " group by lt.name, extract(year from rp.\"Date\")"
								+" order by extract(year from rp.\"Date\")";	
		}
	
	
		else  {
			SQLquery=" select lt.name ,sum(rp.INTEREST)as sumInterest"+
					 ",extract(year from rp.\"Date\") as dates from customers c, "
					+ "accounts a, loans l, loan_types lt,repayments rp where c.AGE>"
					+51+" and lt.NAME='"+Dynamiquequery.getTypeOfLoans()
							+"' and extract(year from rp.\"Date\")="+Dynamiquequery.getDate()+""
									+ " and c.CUSTOMER_id=a.customer_id and l.account_id=a.account_id "
									+ "and l.loan_type_id=lt.LOAN_TYPE_ID and rp.\"Loan_Id\"=l.\"LOAN_ID\""
									+ " group by lt.name, extract(year from rp.\"Date\")"
							+" order by extract(year from rp.\"Date\")";
					 
			
		}
		
		 
	System.out.println(SQLquery);
		
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
				
				DynamiqueResearchResponse response = new DynamiqueResearchResponse();
				
				SumInterest sumclass = new SumInterest();
				ArrayList<SumInterest> sumInterest = new ArrayList<SumInterest>(); 
				
				while(results.next()) {
					
				sumInterest.add(new SumInterest(results.getString("NAME"),results.getFloat("SUMINTEREST"), results.getString("dates")));
				
				}
				
				for (SumInterest som:sumInterest){
					System.out.println(som.toString());
				}
				
				response.setArray(sumInterest);
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



public static ServerResponse handleSumofInterest(){
		
		logger.trace("Entering MessageHandler.handleGetSimsQuery");
		
		String SQLquery = "select extract( YEAR from \"MOIS_ANNEE\") as dates,name,sum(\"SOMME_INTERET\") as sum from vue group by  extract( YEAR from \"MOIS_ANNEE\"),name order by dates";
		System.out.println(SQLquery);
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
				
				SumOfInterestResponse response = new SumOfInterestResponse();
				Interest sumclass = new Interest();
				ArrayList<Interest> average = new ArrayList<Interest>(); 
				
				while(results.next()) {
				
					average.add(new Interest(results.getString("dates"),results.getString("name"),results.getFloat("sum")));
					
				}
				response.setArray(average);
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
		



public static ServerResponse handleAverage(){
	 {
			logger.trace("Entering MessageHandler.handleGetSimsQuery");
			
			String SQLquery = 
"select lp.name, Round(avg(durations),2) as moyenne_duree from "+
" loans l , LOAN_TYPES lp where lp.LOAN_TYPE_ID=l.LOAN_TYPE_ID "+
"group by lp.name ";
			
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
					
					AverageDurationResponse response = new AverageDurationResponse();
					AverageClass averageclass = new AverageClass();
					ArrayList<AverageClass> average = new ArrayList<AverageClass>(); 
					
					while(results.next()) {
						average.add(new AverageClass(results.getString("name"),results.getFloat("moyenne_duree")));
						System.out.println(results.getString("name")+" : "+results.getFloat("moyenne_duree"));
					}
					response.setArray(average);
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
		
	}




public static ServerResponse handleEvolutionOfTheSimulation(String date){
		Connection databaseConnection;
		
		try {
			databaseConnection = ConnectionPool.acquire();
		} catch (IllegalStateException | ClassNotFoundException | SQLException e) {
			logger.trace("Exiting MessageHandler.handleAuthQuery");
			logger.warn("Can't acquire a connection from the pool", e);
			return new ErrorServerResponse("Server-side error. Please retry later.");
		}
		
		try {
			
			String SQLquery = "SELECT COUNT(effective_date) as counts, "
					+ " effective_date from loans where extract(year from EFFECTIVE_DATE )= "+date+" and is_real='N' group by effective_date order by effective_date";
			System.out.println(SQLquery);
			Statement statement = databaseConnection.createStatement();
			
			try {
				
				
				ResultSet results = statement.executeQuery(SQLquery);
				
				evolutionOfTheSimulationsResponse response = new evolutionOfTheSimulationsResponse();
				ListResult result ;
				
				ArrayList<ListResult> array = new ArrayList<ListResult>();
				String dateReturn;
				System.out.println("Bonjour");
				while(results.next()) {
					System.out.println(results.getDate("effective_date")+""+results.getInt("counts"));
					dateReturn = new SimpleDateFormat("dd/MM/yyyy").format(results.getDate("effective_date")) ;
					System.out.println(dateReturn+" : "+results.getInt("counts"));
					result	= new ListResult(dateReturn,results.getInt("counts"));
					
					array.add(result);
					
				}
				for(ListResult lr: array){
					System.out.println(lr.getCount());
					System.out.println(lr.getDate());
				}
				response.setArray(array);
				
				System.out.println(array);
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





public static ServerResponse handleMustSimulatedLoanQuery() throws IllegalStateException, ClassNotFoundException{
Connection databaseConnection = null ;

try {
	String SQLQuery = "select loan_type_id from  loans where is_real ='N' group by loan_type_id having count(*)>=(select max(count(loan_type_id)) from loans where is_real='N' group by loan_type_id)";
	  databaseConnection = ConnectionPool.acquire();

	Statement statement = databaseConnection.createStatement();
	Statement statement2 = databaseConnection.createStatement();
	
	try {
		ResultSet results = statement.executeQuery(SQLQuery);
		
		int idOfMustSimulatedLoan=0;
		
		while(results.next()){
			idOfMustSimulatedLoan = results.getInt("loan_type_id");
			System.out.println("id "+idOfMustSimulatedLoan);
		}
		
		String SQLQuery2="SELECT NAME FROM LOAN_TYPES WHERE LOAN_TYPE_ID= "+idOfMustSimulatedLoan;
		ResultSet results2 = statement2.executeQuery(SQLQuery2);
		String Name = null;
		
		while (results2.next()) {
			Name=results2.getString("Name");
			
		}
		
		
		MustSimulatedLoanResponse mustSimulatedResponse = new MustSimulatedLoanResponse ();
		
		mustSimulatedResponse.setMessage(Name);
		
		logger.trace("Exiting MessageHandler.handleGetAccountsQuery");
		return mustSimulatedResponse;
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
	
	
		
	
	


} 
