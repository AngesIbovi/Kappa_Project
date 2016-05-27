package model.simulation;

import java.sql.Date;

/**
 * A class containing info about rate changes for loans
 * @author Kappa-V
 */
public class RateChange {
	private final Date date;
	private final double value;
	
	public RateChange(Date date, double value) {
		super();
		this.date = date;
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public double getValue() {
		return value;
	}
}
