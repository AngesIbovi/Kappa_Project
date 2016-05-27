package model.simulation;
import java.sql.Date;

/**
 * Events are used to calculate the repayment table.
 * @author Kappa-V
 */
public class Event {
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