package serverCommunication;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.query.GetAllAccountsQuery;
import model.query.GetAllLoanTypeQuery;
import model.query.GetValueOfRateQuery;
import model.query.LoansQuery;
import model.response.GetAllAcountsServerResponse;
import model.response.GetAllLoanTypeServerReponse;
import model.response.GetValueOfRateServerResponse;



public class ServerCommunication {

	
	static Socket socket = null;
	static Properties properties = new Properties();
	static String propFileName = "kappa.properties";
	
	public static ArrayList<String> getAllAcounts(Socket socket) {

	//This function get all of account number into the arraylist	
		ArrayList<String> array = new ArrayList<String>();

		try {

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String message = "please";
			System.out.println("message");

			GetAllAccountsQuery allaccounts = new GetAllAccountsQuery(message);
			// System.out.println(allaccounts.toString());
			Gson gson = new Gson();
			String query = "getAllAccounts " + gson.toJson(allaccounts);
			System.out.println(query);

			out.println(query);

			// Managment of the answer

			String response = in.readLine();

			System.out.println(response);

			int prefixEnd = response.indexOf(' ');

			String prefix = response.substring(0, prefixEnd);
			System.out.println(prefix);

			String content = response.substring(prefixEnd + 1);

			GetAllAcountsServerResponse repallAccounts = gson.fromJson(content, GetAllAcountsServerResponse.class);

			for (int i = 0; i < repallAccounts.array.size(); i++) {
				array.add(repallAccounts.array.get(i));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return array;

	}

	public void getAllLoanType() {

	}
	
	public static ArrayList<String> getAlltypeofLoan(Socket socket) {
		
		ArrayList<String> array = new ArrayList<String>();
		
		try {		
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String name="Type_loan";
		
			
			GetAllLoanTypeQuery allaccounts = new GetAllLoanTypeQuery(name);
			Gson gson = new Gson();
			String query = "getAllLoanType " + gson.toJson(allaccounts);
			
			out.println(query);

			
			String response = in.readLine();

		

			int prefixEnd = response.indexOf(' ');

			String prefix = response.substring(0, prefixEnd);
		

			String content = response.substring(prefixEnd + 1);

			GetAllLoanTypeServerReponse repalTypeLoan = gson.fromJson(content, GetAllLoanTypeServerReponse.class);
			for (int i = 0; i < repalTypeLoan.array.size(); i++) {
				array.add(repalTypeLoan.array.get(i));

			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	
		return array;
	
	}
	
	
	public static float getrate(Socket socket){
		
		float value=0;
		
		try {
			
	
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			int rate=0;
		
			
			GetValueOfRateQuery allaccounts = new GetValueOfRateQuery(rate);
			Gson gson = new Gson();
			String query = "GetValueOfRate " + gson.toJson(allaccounts);
			out.println(query);

			
			String response = in.readLine();

			int prefixEnd = response.indexOf(' ');

			String prefix = response.substring(0, prefixEnd);

			String content = response.substring(prefixEnd + 1);

			GetValueOfRateServerResponse repalrate = gson.fromJson(content, GetValueOfRateServerResponse.class);
			
			value= repalrate.getRate();
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return value;
		
	}	
	
	public void sendLoans(LoansQuery loans, Socket socket)
	{
		try {
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String query = "sendLoans " + gson.toJson(loans);
		out.println(query);
		
		//to read the response
		String response="";
		
			response += in.readLine() + '\n';
		
		System.out.println(response);

		
		} catch (Exception e) {
			// TODO: handle exception
			
			
		}
		
	}

	public static void main(String[] args) {
		ServerCommunication comm = new ServerCommunication();
		//comm.getAllAcounts();
		//comm.getAlltypeofLoan();
		//comm.getrate();
	}
}
