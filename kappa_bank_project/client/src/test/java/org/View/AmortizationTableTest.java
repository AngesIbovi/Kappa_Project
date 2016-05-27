package org.View;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import util.JsonImpl;
import util.KappaProperties;
import model.simulation.Event;
import model.simulation.RateChange;
import model.simulation.Repayment;
import model.simulation.Simulation;
import model.simulation.Simulation.AmortizationType;

public class AmortizationTableTest {
	public static void main(String[] args) {
		// Tools initialization
		try {
			KappaProperties.init();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		JsonImpl.init();
		
		
		// Setting enough simulation parameters that it can be displayed in Marc's UC.
		Simulation simulation = new Simulation();
		simulation.setName("Test");
		
		// Main volumes
		simulation.setFixedRate(true);
		simulation.setAmortizationType(AmortizationType.degressive);
		simulation.setCapital(21000.0f);
		simulation.setInsurance(360f);
		
		// Duration
		simulation.setRepaymentFrequency(12);
		simulation.setRemainingRepayments(132);
		simulation.setEffectiveDate(JsonImpl.fromJson("\"déc. 21, 2012\"", Date.class));
		
		// Loan rate history
		List<RateChange> LRH = new ArrayList<>();
		LRH.add(new RateChange(JsonImpl.fromJson("\"janv. 1, 2008\"", Date.class), 0.0210f));
		LRH.add(new RateChange(JsonImpl.fromJson("\"janv. 1, 2009\"", Date.class), 0.0215f));
		LRH.add(new RateChange(JsonImpl.fromJson("\"janv. 1, 2010\"", Date.class), 0.0220f));
		LRH.add(new RateChange(JsonImpl.fromJson("\"janv. 1, 2011\"", Date.class), 0.0225f));
		LRH.add(new RateChange(JsonImpl.fromJson("\"janv. 1, 2012\"", Date.class), 0.0230f));
		LRH.add(new RateChange(JsonImpl.fromJson("\"janv. 1, 2013\"", Date.class), 0.0225f));
		LRH.add(new RateChange(JsonImpl.fromJson("\"janv. 1, 2014\"", Date.class), 0.0220f));
		LRH.add(new RateChange(JsonImpl.fromJson("\"janv. 1, 2015\"", Date.class), 0.0230f));
		LRH.add(new RateChange(JsonImpl.fromJson("\"janv. 1, 2016\"", Date.class), 0.0240f));
		simulation.setRateHistory(LRH);
		
		// Events
		List<Event> events = new ArrayList<>();
		events.add(new Event(Event.EventType.RateModificationEvent, JsonImpl.fromJson("\"janv. 1,  2017\"", Date.class), null, 0.0235f, true));
		events.add(new Event(Event.EventType.LoanDurationChange, JsonImpl.fromJson("\"janv. 1,  2018\"", Date.class), null, 60, true));
		events.add(new Event(Event.EventType.TransfertOfPayment, JsonImpl.fromJson("\"mai 1, 2020\"", Date.class), JsonImpl.fromJson("\"déc. 1, 2021\"", Date.class), 0, true));
		events.add(new Event(Event.EventType.LoanDurationChange, JsonImpl.fromJson("\"janv. 20,  2022\"", Date.class), null, 10, true));
		events.add(new Event(Event.EventType.PaymentFrequencyChange, JsonImpl.fromJson("\"janv. 21,  2022\"", Date.class), null, 4, true));
		events.add(new Event(Event.EventType.LoanRedemption, JsonImpl.fromJson("\"sept. 3,  2023\"", Date.class), null, 0, true));
		simulation.setEvents(events);
		
		
		
		
		
		
		// Amortization table calculation and display
		simulation.calculateAmortizationTable();
		
		System.out.println("Nombre de mensualités : " + simulation.getRepayments().size());
		System.out.println();
		float roc = simulation.getCapital();
		float interestsSum = 0;
		float insuranceSum = 0;
		System.out.println("Date\t\t\t\tCapital\t\t\tInterests\t\tInsurance\t\tTotal\t\t\tROC\t\t\t\tInterests Sum\t\tInsurance Sum");
		System.out.println();
		for(Repayment repayment : simulation.getRepayments()) {
			float total = toCurrency(repayment.getCapital() + repayment.getInterest() + repayment.getInsurance());
			roc = toCurrency(roc - repayment.getCapital());
			interestsSum = toCurrency(interestsSum + repayment.getInterest());
			insuranceSum = toCurrency(insuranceSum + repayment.getInsurance());
			System.out.println(repayment.getDate() + "\t\t\t" 
							 + repayment.getCapital() + "\t\t\t"
							 + repayment.getInterest() + "\t\t\t"
							 + repayment.getInsurance() + "\t\t\t"
							 + total + "\t\t\t"
							 + roc + "     \t\t\t"
							 + interestsSum + "\t\t\t"
							 + insuranceSum + "\t\t\t");
		}
	}
	
	private static float toCurrency(float value) {
		return ((float)((int) (value * 100)))/100;
	}
}
