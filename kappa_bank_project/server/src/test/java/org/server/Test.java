package org.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import util.KappaProperties;

/**
 * Test main.</br>
 * Fakes a client trying to connect to and discuss with the server.</br>
 * Uses the file given in the command line arguments, or, by default, tests.txt, 
 * which contains a list of queries to be executed.
 * @version R3 sprint 1 - 17/03/2016
 * @author Kappa-V
 * @changes
 * 		R2 sprint 1 -> R3 sprint 1:</br>
 * 			-Now uses a file containing one query per line. Give the file's path as command line argument - by default tests.txt will be used.
 */
public class Test {
	public static void main(String[] args) throws IOException {
		// Opening and reading the file
		FileReader file;
		if(args.length > 0) {
			file = new FileReader(args[0]);
		} else {
			file = new FileReader("tests.txt");
		}
		
		BufferedReader reader = new BufferedReader(file);
		
		List<String> queries = new ArrayList<>();
		String newquery;
		while((newquery = reader.readLine()) != null) {
			queries.add(newquery);
		}

		file.close();
		reader.close();
		
		// Getting the properties
		try {
			KappaProperties.init();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Properties props = KappaProperties.getInstance();
		
		
		try {
			// Connection
			Socket S = new Socket("localhost", Integer.parseInt(props.getProperty("SERVER_PORT")));

			// Streams
			PrintWriter out = new PrintWriter(S.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(S.getInputStream()));

			// Test loop
			for(String query : queries) {
				out.println(query);
				String response = in.readLine();
				System.out.println(query + " -> " + response);
				System.out.println();
			}
			
			// Cleanup
			in.close();
			out.close();
			S.close();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
}
