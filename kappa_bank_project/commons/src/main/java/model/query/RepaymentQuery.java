package model.query;
import java.util.ArrayList;
import java.util.List;

import model.simulation.Repayment;

public class RepaymentQuery {

ArrayList<Repayment> repayments ;

public ArrayList<Repayment> getRepayments() {
	return repayments;
}

public void setRepayments(ArrayList<Repayment> repayments) {
	this.repayments = repayments;
}

public RepaymentQuery(ArrayList<Repayment> repayments) {
	super();
	this.repayments = repayments;
}

public RepaymentQuery() {
	super();
}

	

}
