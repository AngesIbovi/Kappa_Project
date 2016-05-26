package model.simulation;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import model.response.ServerResponse;
import util.JsonImpl;

/**
 * Communication class. See the protocol's documentation for more details.</br>
 * This one is at the heart of this simulator project, so it was given a separate package.
 * @author Kappa-V
 * @version R3 sprint 2 - 28/04/2016
 * @changes
 * 		R3 sprint 2 -> R3 sprint 4:</br>
 * 			-Renamed from GetSimServerResponse to Simulation, and moved from the response package to the simulation package</br>
 * 			-Added the calculateAmortizationTable used by the comparison, fixed rate simulation, and variable rate simulation use cases.</br>
 * 			-Inner classes are now in separate files except for the AmortizationType enum.
 */
public class Simulation extends ServerResponse {
	/* Attributes */
	private String name;
	private String id;
	private List<Event> events;
	private List<Repayment> repayments;
	private List<RateChange> rateHistory;
	private Date effectiveDate;
	private float capital;
	private float remainingOwedCapital;
	private int repaymentFrequency;
	private int remainingRepayments;
	private float repaymentConstant;
	private AmortizationType amortizationType;
	private String age;
	private String loan_type_id;
	private String user_login;
	private String account_num;
	private String type_sim;
	private boolean is_reel;
	private float insurance;
	private float processing_fee;
	private boolean fixedRate;
	
	
	/**
	 * Calculates a loan's amortization table, based on its parameters, and its events. Updates it.</br>
	 * Here is the minimum list of attributes that have to be initialized for tha calculation to be successful :</br>
	 * fixedRate, amortizationType, capital, insurance, repaymentFrequency, remainingRepayments, effectiveDate, rateHistory, events</br>
	 * @see a AmortizationTableTest in the client module, in the test package.
	 * @return whether the operation was carried out properly or not.
	 */
	public boolean calculateAmortizationTable() {
		// Variable initialization
		double remainingOwedCapital = getCapital();
		Date currentDate = getEffectiveDate();
		boolean degressiveRepayments = AmortizationType.degressive.equals(getAmortizationType());
		boolean fixedRate = isFixedRate();
		int monthsBetweenRepayments = 12 / getRepaymentFrequency();
		double monthlyInsurance = toCurrency(getInsurance() / getRemainingRepayments());
		
		double interestRate = -1;
		for(RateChange rateChange : getRateHistory()) {
			if(rateChange.getDate().before(currentDate)) {
				interestRate = (float) rateChange.getValue();
			}
		}
		if(interestRate == -1) {
			return false;
		}
		
		// Repayment calculation
		ArrayList<Repayment> newRepayments = new ArrayList<>();
		for(int remainingRepayments = getRemainingRepayments() ; remainingRepayments > 0 ; remainingRepayments --) {
			// Calculating the next repayment's date
			Date nextDate = (Date) currentDate.clone();
			nextDate.setMonth(currentDate.getMonth() + monthsBetweenRepayments);
			
			// Handling rate changes for variable rate loans
			if(!fixedRate) {
				for(RateChange rateChange : getRateHistory()) {
					if((currentDate.before(rateChange.getDate())) && (nextDate.after(rateChange.getDate()))) {
						interestRate = rateChange.getValue();
					}
				}
			}
			
			// Event handling
			for(Event event : events) {
				if((currentDate.before(event.getStartDate())) && (nextDate.after(event.getStartDate()))) {
					switch(event.getType()) {
					case IncomeChange: // This is strictly an indication for the bank
						// Do nothing
						break;
					case LoanDurationChange: 
						// Here, value refers to the new amount of remaining repayments. 
						// Only the insurance has to be re-calculated, because everything else is going to be calculated anyway.
						monthlyInsurance = toCurrency(monthlyInsurance * remainingRepayments / event.getValue());
						remainingRepayments = (int) event.getValue();
						break;
					case PaymentFrequencyChange:
						monthsBetweenRepayments = 12 / (int) event.getValue();
						break;
					case LoanRedemption: 
						// Here, the loop is bypassed : the loan is bought immediately
						double capital = toCurrency(remainingOwedCapital);
						double interests = toCurrency(getPeriodicInterestRate(currentDate, nextDate, interestRate) * remainingOwedCapital);
						double insurance = toCurrency(monthlyInsurance * remainingRepayments);
						Repayment lastRepayment = new Repayment(nextDate, (float) capital, (float) interests, (float) insurance);
						newRepayments.add(lastRepayment);
						setRepayments(newRepayments);
						return true;
					case RateModificationEvent:
						interestRate = event.getValue();
						break;
					case TransfertOfPayment:
						nextDate = event.getEndDate();
						break;
					}
				}
			}
			
			// Calculating this repayment's interests
			double interests = toCurrency(getPeriodicInterestRate(currentDate, nextDate, interestRate) * remainingOwedCapital);
			
			// Calculating this repayment's capital
			double capital;
			if(remainingRepayments == 1) { // First case : last repayment of the loan
				capital = toCurrency(remainingOwedCapital);
			} else if (degressiveRepayments) { // Second case : degressive repayments
				capital = toCurrency(remainingOwedCapital / remainingRepayments);
			} else { // Third case : constant repayments
				double periodicInterestRate = getPeriodicInterestRate(currentDate, nextDate, interestRate);
				double repaymentTotal = (float) ((remainingOwedCapital * periodicInterestRate) / (1 - Math.pow(1 + periodicInterestRate, -remainingRepayments)));
				
				capital = toCurrency(repaymentTotal - monthlyInsurance - interests);
			}
			
			// Creating the repayment
			Repayment repayment = new Repayment(nextDate, (float) capital, (float) interests, (float) monthlyInsurance);
			newRepayments.add(repayment);
			
			
			// Preparing the next iteration
			currentDate = nextDate;
			remainingOwedCapital -= capital;
		}

		setRepayments(newRepayments);
		return true;
	}
	
	/**
	 * A private method used by calculateAmortizationTable where it is used fairly often.</br>
	 * It is used to calculate the periodic interest rate for the next repayment.
	 * @return the periodic interest rate
	 */
	private double getPeriodicInterestRate(Date lastRepaymentDate, Date nextRepaymentDate, double yearlyInterestRate) {
		int monthsDifference = nextRepaymentDate.getMonth() - lastRepaymentDate.getMonth();
		if(monthsDifference < 0) {
			monthsDifference += 12;
		}
		
		return (yearlyInterestRate / 12) * monthsDifference;
	}
	
	/**
	 * Rounds down a float value to 2 digits after units.</br>
	 * Is used in calculateAmortizationTable a lot.</br>
	 * Its purpose is to transform the results of mathematical operations into displayable price tags. 
	 * @param value : the value to be treated
	 * @return : a currency version of the param.
	 */
	private double toCurrency(double value) {
		return ((double)((int) (value * 100)))/100;
	}
	
	

	/* Inner enum */
	public static enum AmortizationType {
		steady,
		degressive
	}
	
	
	/* Constructors */
	public Simulation(float insurance, float processing_fee, String is_reel, String name, String age, String type_sim, String user_login, String account_num, String loan_type_id,String id, List<Event> events, List<Repayment> repayments,
			List<RateChange> rateHistory, Date effectiveDate, float capital, float remainingOwedCapital, int repaymentFrequency,
			int remainingRepayments, float repaymentConstant, AmortizationType amortizationType, boolean fixedRate) {
		super();
		this.name = name;
		this.age = age;
		this.user_login = user_login;
		this.loan_type_id = loan_type_id;
		this.id = id;
		this.events = events;
		this.repayments = repayments;
		this.effectiveDate = effectiveDate;
		this.capital = capital;
		this.remainingOwedCapital = remainingOwedCapital;
		this.repaymentFrequency = repaymentFrequency;
		this.remainingRepayments = remainingRepayments;
		this.repaymentConstant = repaymentConstant;
		this.amortizationType = amortizationType;
		this.account_num = account_num;
		this.type_sim = type_sim; 
		this.insurance=insurance;
		this.processing_fee=processing_fee;
		this.rateHistory = rateHistory;
	}
	
	public Simulation() {
		super();
		this.events = new ArrayList<>();
		this.repayments = new ArrayList<>();
		this.rateHistory = new ArrayList<>();
	}
	
	
	
	/* Getters and setters */
	
	
	public boolean getIs_reel() {
		return is_reel;
	}
	public boolean isFixedRate() {
		return fixedRate;
	}
	public void setFixedRate(boolean fixedRate) {
		this.fixedRate = fixedRate;
	}
	public List<RateChange> getRateHistory() {
		return rateHistory;
	}
	public void setRateHistory(List<RateChange> rateHistory) {
		this.rateHistory = rateHistory;
	}
	public float getInsurance() {
		return insurance;
	}
	public void setInsurance(float insurance) {
		this.insurance = insurance;
	}
	public float getProcessing_fee() {
		return processing_fee;
	}
	public void setProcessing_fee(float processing_fee) {
		this.processing_fee = processing_fee;
	}
	public void setIs_reel(boolean is_reel) {
		this.is_reel = is_reel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getTypeSim() {
		return type_sim;
	}
	public void setTypeSim(String type_sim) {
		this.type_sim = type_sim;
	}
	public String getAcountNum() {
		return account_num;
	}
	public void setAccountNum(String account_num) {
		this.account_num = account_num;
	}
	public String getLoanTypeId() {
		return loan_type_id;
	}
	public void setLoanTypeId(String loan_type_id) {
		this.loan_type_id = loan_type_id;
	}
	public String getAccountId() {
		return user_login;
	}
	public void setAccountId(String user_login) {
		this.user_login = user_login;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	public List<Repayment> getRepayments() {
		return repayments;
	}
	public void setRepayments(List<Repayment> repayments) {
		this.repayments = repayments;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public float getCapital() {
		return capital;
	}
	public void setCapital(float capital) {
		this.capital = capital;
	}
	public float getRemainingOwedCapital() {
		return remainingOwedCapital;
	}
	public void setRemainingOwedCapital(float remainingOwedCapital) {
		this.remainingOwedCapital = remainingOwedCapital;
	}
	public int getRepaymentFrequency() {
		return repaymentFrequency;
	}
	public void setRepaymentFrequency(int repaymentFrequency) {
		this.repaymentFrequency = repaymentFrequency;
	}
	public int getRemainingRepayments() {
		return remainingRepayments;
	}
	public void setRemainingRepayments(int remainingRepayments) {
		this.remainingRepayments = remainingRepayments;
	}
	public float getRepaymentConstant() {
		return repaymentConstant;
	}
	public void setRepaymentConstant(float repaymentConstant) {
		this.repaymentConstant = repaymentConstant;
	}
	public AmortizationType getAmortizationType() {
		return amortizationType;
	}
	public void setAmortizationType(AmortizationType amortizationType) {
		this.amortizationType = amortizationType;
	}
}