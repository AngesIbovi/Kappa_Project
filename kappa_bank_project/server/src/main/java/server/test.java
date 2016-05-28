package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.google.gson.Gson;

import model.SessionInformation;
import model.query.DynamiqueResearchQuery;
import model.query.MustSimulatedLoanQuery;
import model.query.evolutionOfTheSimulationsQuery;
import model.response.AverageDurationResponse;
import model.response.DynamiqueResearchResponse;
import model.response.DynamiqueResearchResponse.SumInterest;
import model.response.MustSimulatedLoanResponse;
import model.response.NumberOfLoanResponse;
import model.response.SumOfInterestResponse;
import model.response.evolutionOfTheSimulationsResponse;
import view.Tab;



public class test extends Tab {
	Socket socket;
	
	public test(String name, int authorizationLevel) {
		super(name, authorizationLevel);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ArrayList<SumInterest>  InterestBySegment(DynamiqueResearchQuery InterestQuery,Socket socket){
		ArrayList<SumInterest> array= new ArrayList<SumInterest>();
		
		
		 DynamiqueResearchResponse DynResponse = null ;
		try{

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			InterestQuery = new model.query.DynamiqueResearchQuery();
			Gson gson = new Gson();
			String query = "InterestBySegment " + gson.toJson(InterestQuery);
		
			out.println(query);

			
			// manage the response of the server
			
			String response = in.readLine();

			System.out.println(response);

			int prefixEnd = response.indexOf(' ');

			String prefix = response.substring(0, prefixEnd);
			

			String content = response.substring(prefixEnd + 1);
			
		
			DynResponse= gson.fromJson(content, DynamiqueResearchResponse.class);
			System.out.println(DynResponse.toString());
			
		}catch(Exception e){
			e.toString();
		}
		DynResponse.setArray(array);
		return array;
	}
	
	public void SumofInterest(Socket socket){
		
		 SumOfInterestResponse sumResponse ;
		try{
			

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			

			model.query.SumOfInterestQuery sumInterest = new model.query.SumOfInterestQuery();
			Gson gson = new Gson();
			String query = "SumOfInterest " + gson.toJson(sumInterest);
		
			out.println(query);

			
			// manage the response of the server
			
			String response = in.readLine();

			System.out.println(response);

			int prefixEnd = response.indexOf(' ');

			String prefix = response.substring(0, prefixEnd);
			

			String content = response.substring(prefixEnd + 1);
			
		
			 sumResponse= gson.fromJson(content, SumOfInterestResponse.class);
			System.out.println(sumResponse.toString());
			
		}catch(Exception e){
			e.toString();
		}	
	}
	
	public void AverageLoan(Socket socket){

	
		
		 AverageDurationResponse averageLoan ;
		try{
			

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			

			model.query.AverageDurationQuery average = new model.query.AverageDurationQuery();
		//	System.out.println(	"evolution"+evSimulation.toString());
			Gson gson = new Gson();
			String query = "AverageDuration " + gson.toJson(average);
		
			out.println(query);

			
			// manage the response of the server
			
			String response = in.readLine();

			System.out.println(response);

			int prefixEnd = response.indexOf(' ');

			String prefix = response.substring(0, prefixEnd);
			

			String content = response.substring(prefixEnd + 1);
			
		
			 averageLoan = gson.fromJson(content, AverageDurationResponse.class);
			System.out.println(averageLoan.toString());
			
		}catch(Exception e){
			e.toString();
		}	
	}
	
	public void NumberOfLoanQuery(String date,Socket socket){

		
		 NumberOfLoanResponse numberLoans ;
		try{
			
			

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			

			model.query.NumberOfLoanQuery numberOfLoanQuery = new model.query.NumberOfLoanQuery(date);
		//	System.out.println(	"evolution"+evSimulation.toString());
			Gson gson = new Gson();
			String query = "NumberOfLoanQuery " + gson.toJson(numberOfLoanQuery);
			
			System.out.println(query);
			out.println(query);

			
			// manage the response of the server
			
			String response = in.readLine();

			System.out.println(response);

			int prefixEnd = response.indexOf(' ');

			String prefix = response.substring(0, prefixEnd);
			

			String content = response.substring(prefixEnd + 1);
			
		
			 numberLoans = gson.fromJson(content, NumberOfLoanResponse.class);
			System.out.println(numberLoans.toString());
			
		}catch(Exception e){
			e.toString();
		}
		
		
	}
	
public void mustSimulatedLoan(Socket socket){
	
	 MustSimulatedLoanResponse evolution ;
	try{
		
	
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		

		MustSimulatedLoanQuery loan = new MustSimulatedLoanQuery();
	//	System.out.println(	"evolution"+evSimulation.toString());
		Gson gson = new Gson();
		String query = "MustSimulatedLoan " + gson.toJson(loan);
		
		
		out.println(query);

		
		// manage the response of the server
		
		String response = in.readLine();

		System.out.println(response);

		int prefixEnd = response.indexOf(' ');

		String prefix = response.substring(0, prefixEnd);
		

		String content = response.substring(prefixEnd + 1);
		
	
		 evolution = gson.fromJson(content, MustSimulatedLoanResponse.class);
		System.out.println(evolution.toString());
		
	}catch(Exception e){
		e.toString();
	}	
	}

	// evolution of the simulation year by year
	public void evolutionOfTheSimulations(String date,Socket socket){

		 evolutionOfTheSimulationsResponse evolution ;
		try{
			

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			

			evolutionOfTheSimulationsQuery evSimulation = new evolutionOfTheSimulationsQuery(date);
		//	System.out.println(	"evolution"+evSimulation.toString());
			Gson gson = new Gson();
			String query = "evolutionOfTheSimulations " + gson.toJson(evSimulation);
			
			
			out.println(query);

			
			// manage the response of the server
			
			String response = in.readLine();

			System.out.println(response);

			int prefixEnd = response.indexOf(' ');

			String prefix = response.substring(0, prefixEnd);
			

			String content = response.substring(prefixEnd + 1);
			
		
			 evolution = gson.fromJson(content, evolutionOfTheSimulationsResponse.class);
			System.out.println(evolution.toString());
			
		}catch(Exception e){
			e.toString();
		}
		
		
	
	}
	
public static void main(String[] args) throws UnknownHostException, IOException {
	

	

}

@Override
public void setSessionInformation(SessionInformation sessionInformation) {
	// TODO Auto-generated method stub
	socket = sessionInformation.getSocket();
	test t = new test("bocar",4);
	t.mustSimulatedLoan(socket);
}
}
