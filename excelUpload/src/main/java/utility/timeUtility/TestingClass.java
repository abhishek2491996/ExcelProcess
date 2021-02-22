package utility.timeUtility;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class TestingClass {
	
	public static void main(String args[]){
		long startTime = System.currentTimeMillis();
		TimeUtility utility = new TimeUtility();
		String dateInTimeZone = utility.getCurrentDate("UTC", "dd.MM.yyyy HH:mm:ss");
		
		System.out.println(utility.forwardDateInDays(dateInTimeZone, 100, "dd.MM.yyyy"));
		System.out.println(utility.backwardDateInDays(dateInTimeZone, 700, "dd.MM.yyyy"));
		System.out.println(utility.forwardDateInMonth(dateInTimeZone, 5, "dd.MM.yyyy"));
		System.out.println(utility.backwardDateInMonth(dateInTimeZone, 7, "dd.MM.yyyy"));
		System.out.println(utility.forwardDateInYear(dateInTimeZone, 7, "dd.MM.yyyy"));
		System.out.println(utility.backwardDateInYear(dateInTimeZone, 7, "dd.MM.yyyy"));
		
		long endTime = System.currentTimeMillis();
		System.out.println("Time taken to complete activity "+((endTime-startTime)));
		
		Calendar cal = Calendar.getInstance();
	      cal.add(Calendar.DATE, -1 *1);
	      long millisecond = cal.getTimeInMillis();
	      DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	      String dateValue=df.format(millisecond);
	       //formatted value of current Date
	       System.out.println("Millisecondfds to Date: " +dateValue);

	
	       
	       long dateData =utility.getDateDifference("09.02.2021","09.10.2020","dd.MM.yyyy","D");
	       System.out.println("Milliseconds to Dadfte: " +dateData);
	       
	       if(dateData<0){
				System.out.println("Not Past - Need by Date Greater Than a Week");
			}
			if(dateData>=0 && dateData <=7){
				System.out.println("Not Past - Need by Date Within a Week");
			}
			if(dateData>7){
				System.out.println("Past Need By Date");
			}

			
		/*	
			// input format: MM/yy
			SimpleDateFormat parser = new SimpleDateFormat("MM d, yy");
			// output format: yyyy-MM-dd
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
			try {
				System.out.println(formatter.format(parser.parse("October 08, 2020")));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // 20*/
			
			
				
				System.out.println(getCompletedDate("HDT","dd.MM.yyyy"));
				
				System.out.println(utility.getMilliseconds("dd.MM.yyyy","23.10.2020"));
				
	}


public static String getCompletedDate(String userFormat,String USER_DATE_FORMAT){
  String completedDate="";
  Calendar cal = Calendar.getInstance();
  long time = cal.getTimeInMillis();
 // LOGGER_API.error("Current Milliseconds {}",time);
  SimpleDateFormat dateFormatGmt = new SimpleDateFormat(USER_DATE_FORMAT);
  dateFormatGmt.setTimeZone(TimeZone.getTimeZone(userFormat));
  String dateInTimeZone =dateFormatGmt.format(time);
 // LOGGER_API.error("Timezone of user "+ userFormat +"  Converted Date "+dateFormatGmt.format(time));
  return dateInTimeZone;
}
}
