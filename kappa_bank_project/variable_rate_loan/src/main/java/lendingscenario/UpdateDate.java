package lendingscenario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class UpdateDate {

	public ArrayList funcionUpdate(String date, int duration) throws ParseException {

		ArrayList<String> dateArray = new ArrayList<String>();

		String dt = date; // Start date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		Calendar takingdate = Calendar.getInstance();
		c.setTime(sdf.parse(dt));

		int i = 1;
		while (i <= duration) {

			for (int j = 1; j <= 12; j++) {
				c.add(takingdate.DATE, 30); // number of days to add
				dt = sdf.format(c.getTime()); // dt is now the new date
				dateArray.add(dt);
			}

			i++;
		}

		return dateArray;
	}

	public static void main(String[] args) throws ParseException {
		UpdateDate up = new UpdateDate();
		ArrayList<String> ar = new ArrayList<String>();
		ar = up.funcionUpdate("2016-05-20", 3);

		for (String string : ar) {
			System.out.println(string);
			
			
			
		}
	}
}
