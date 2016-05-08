package model.response;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Communication class. See the protocol's documentation for more details.
 * @author Kappa-V
 * @version R3 sprint 2 - 28/04/2016
 */
public class GetSimServerResponse extends ServerResponse {
	/* Attributes */
	private String name;
	private String id;
	private List<Event> events;
	private List<Repayment> repayments;
	private Date effectiveDate;
	private float capital;
	private float remainingOwedCapital;
	private int repaymentFrequency;
	private int remainingRepayments;
	private float repaymentConstant;
	private AmortizationType amortizationType;
	
	
	
	/* Constructors */
	
	public GetSimServerResponse(String name, String id, List<Event> events, List<Repayment> repayments,
			Date effectiveDate, float capital, float remainingOwedCapital, int repaymentFrequency,
			int remainingRepayments, float repaymentConstant, AmortizationType amortizationType) {
		super();
		this.name = name;
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
	}
	
	public GetSimServerResponse() {
		super();
		this.events = new ArrayList<>();
		this.repayments = new ArrayList<>();
	}
	
	
	
	/* Getters and setters */
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	/**
	 * if this is "steady", then the repayment constant refers to the amount of money repaid each month in total</br>
	 * if this is "degressive", then the repayment constant attribute refers to the amount of capital repaid each month instead  
	 */
	public AmortizationType getAmortizationType() {
		return amortizationType;
	}

	public void setAmortizationType(AmortizationType amortizationType) {
		this.amortizationType = amortizationType;
	}

	
	
	/* Inner classes */
	
	public static enum AmortizationType {
		steady,
		degressive
	}
	
	/**
	 * Events are used to calculate the repayment table.
	 * @author Kappa-V
	 */
	public static class Event {
		public static enum EventType {
			LoanRedemption,
			LoanDurationChange,
			TransfertOfPayment,
			PaymentFrequencyChange,
			RateModificationEvent,
			IncomeChange,
			RepaymentConstantChange
		}
		
		private EventType type;
		private Date startDate;
		private Date endDate;
		private float value;
		private boolean isReal;
		
		public Event(EventType type, Date startDate, Date endDate, float value, boolean isReal) {
			super();
			this.type = type;
			this.startDate = startDate;
			this.endDate = endDate;
			this.value = value;
			this.isReal = isReal;
		}

		public EventType getType() {
			return type;
		}

		public void setType(EventType type) {
			this.type = type;
		}

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public float getValue() {
			return value;
		}

		public void setValue(float value) {
			this.value = value;
		}

		public boolean isReal() {
			return isReal;
		}

		public void setReal(boolean isReal) {
			this.isReal = isReal;
		}
	}
	
	/**
	 * Repayments are used to visualize a simulation's outcome
	 * @author Kappa-V
	 */
	public static class Repayment {
		private Date date;
		private float capital;
		private float interest;
		private float insurance;
		
		public Repayment(Date date, float capital, float interest, float insurance) {
			super();
			this.date = date;
			this.capital = capital;
			this.interest = interest;
			this.insurance = insurance;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public float getCapital() {
			return capital;
		}

		public void setCapital(float capital) {
			this.capital = capital;
		}

		public float getInterest() {
			return interest;
		}

		public void setInterest(float interest) {
			this.interest = interest;
		}

		public float getInsurance() {
			return insurance;
		}

		public void setInsurance(float insurance) {
			this.insurance = insurance;
		}
	}
}