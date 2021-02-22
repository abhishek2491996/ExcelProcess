package utility.timeUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

public class TimeUtility {

	public TimeUtility(){
		
	}
	
	public long dateInUTC (){
		Calendar cal = Calendar.getInstance();
		long time = cal.getTimeInMillis();
		System.out.println("UTC Time "+time);
		System.out.println(dateInTimeZone(time, "GMT", "yyyyMMdd"));
		
		return time;
		
	}
	
	public String getCurrentDate(String timeZone,String dateFormat){
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		long time = cal.getTimeInMillis();
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(dateFormat);
		
		String dateInTimeZone =dateFormatGmt.format(time);
		
		System.out.println("Date "+dateInTimeZone);
		
		return dateInTimeZone;
		
	}
	
	public static  String DateInDays(String date,int noOfDays,String dateFormat,int actionVal,String dayMonthYear){
		int calendarType=0;
		int nDays = noOfDays * actionVal;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar c = Calendar.getInstance();
		try{
		   //Setting the date to the given date
		   c.setTime(sdf.parse(date));
		}catch(Exception e){
			System.out.println("Exception ex "+e);
		 }
		if("D".equalsIgnoreCase(dayMonthYear))
			calendarType = Calendar.DAY_OF_MONTH;
		if("M".equalsIgnoreCase(dayMonthYear))
			calendarType = Calendar.MONTH;
		if("Y".equalsIgnoreCase(dayMonthYear))
			calendarType = Calendar.YEAR;
		c.add(calendarType, nDays);  	
		String newDate = sdf.format(c.getTime());  
		
		return newDate;
		
	}
	
	public  String dateFormatIdentifier (String date,String format){
		if(date==null){
			return "Invalid Date Format";
		}
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd");
		return date;
		
	}

	public static  String dateInTimeZone(long time,String timeZone,String dateFormatUser){
		
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(dateFormatUser);
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone(timeZone));
		String dateInTimeZone =dateFormatGmt.format(time);
        System.out.println(timeZone+"  "+ dateFormatGmt.format(time));
		return dateInTimeZone;
	}

	@SuppressWarnings("unused")
	public  boolean dateValidation(String dateToValidate,String dateFromat){
		if(dateToValidate == null)
		 return false;
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);
		try {
			Date date = sdf.parse(dateToValidate);
		} catch (ParseException e) {
			return false;
		}
		
		return true;
		
	}
	
	public String forwardDateInDays(String dateInTimeZone, int noOfDays, String dateFormat) {
		String fDateInDays =  TimeUtility.DateInDays(dateInTimeZone, noOfDays, dateFormat, 1,"D");
		System.out.println ("Number of days forward  "+noOfDays +" Current "+dateInTimeZone+" = "+fDateInDays);
		return fDateInDays;
	}
	
	public String backwardDateInDays( String dateInTimeZone,  int noOfDays,  String dateFormat) {
		String bDateInDays =  DateInDays(dateInTimeZone, noOfDays, dateFormat, -1,"D");
		System.out.println ("Number of days backward  "+noOfDays +" Current "+dateInTimeZone+" = "+bDateInDays);
		return bDateInDays;
	}
	
	public String forwardDateInMonth(String dateInTimeZone, int noOfMonth, String dateFormat) {
		String fDateInMonth =  DateInDays(dateInTimeZone, noOfMonth, dateFormat, 1,"M");
		System.out.println ("Number of Month forward  "+noOfMonth +" Current "+dateInTimeZone+" = "+fDateInMonth);
		return fDateInMonth;
	}
	
	public  String backwardDateInMonth(String dateInTimeZone, int noOfMonth, String dateFormat) {
		String bDateInMonth =  DateInDays(dateInTimeZone, noOfMonth, dateFormat, -1,"M");
		System.out.println ("Number of month backward  "+noOfMonth +" Current "+dateInTimeZone+" = "+bDateInMonth);
		return bDateInMonth;

	}

	public  String forwardDateInYear(String dateInTimeZone, int noOfYear, String dateFormat) {
		String fDateInYear =  DateInDays(dateInTimeZone, noOfYear, dateFormat, 1,"Y");
		System.out.println ("Number of year forward  "+noOfYear +" Current "+dateInTimeZone+" = "+fDateInYear);
		return fDateInYear;
	}

	public  String backwardDateInYear(String dateInTimeZone, int noOfYear, String dateFormat) {
		String bDateInYear =  DateInDays(dateInTimeZone, noOfYear, dateFormat, -1,"Y");
		System.out.println ("Number of year backward  "+noOfYear +" Current "+dateInTimeZone+" = "+bDateInYear);
		return bDateInYear;
	}
	

	public long getDateDifference
	(String dateStart,String dateStop,String dateFormat,String timeDuration){

		long dateDiff=0L;
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);

		Date d1 = null;
		Date d2 = null;

		try {
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);

			//in milliseconds
			long diff = d2.getTime() - d1.getTime();
		
			if("D".equalsIgnoreCase(timeDuration)){
				dateDiff = diff / (24 * 60 * 60 * 1000);;
			}
			if("H".equalsIgnoreCase(timeDuration)){
				dateDiff = diff / (60 * 60 * 1000) % 24;;
			}
			if("M".equalsIgnoreCase(timeDuration)){
				dateDiff = diff / (60 * 1000) % 60;;
			}
			if("S".equalsIgnoreCase(timeDuration)){
				dateDiff = diff / (60 * 1000) % 60;;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateDiff;
	}
	
	public static String getMilliSecondsTimeZone(String dateFormat, String dateValue,String timeZone){
		String dateMilli="";
		try{
	        SimpleDateFormat simpleDateOnly = new SimpleDateFormat(dateFormat);
	        simpleDateOnly.setTimeZone(TimeZone.getTimeZone(timeZone));
	        Calendar cal = Calendar.getInstance();
	        Date dateVal = simpleDateOnly.parse(dateValue);
	        cal.setTime(dateVal);
	        dateMilli = String.valueOf(cal.getTimeInMillis());
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		
		return dateMilli;
	}

	public static String  getMilliseconds(String dateFormat,String dateValue){
		String dateMilli="";
		try{
        SimpleDateFormat simpleDateOnly = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        Date dateVal = simpleDateOnly.parse(dateValue);
        cal.setTime(dateVal);
        dateMilli = String.valueOf(cal.getTimeInMillis());
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return dateMilli;
	}
	
	public static void main(String args[]){
		
		System.out.println(getMilliSecondsTimeZone("dd.MM.yyyy","10.11.2020","Asia/Kolkata"));
		//dateInTimeZone(1595874600000L,"Asia/Kolkata","MM/dd/yyyy HH:mm:ss a");
		
	//	long dateData =getDateDifference("23.09.2020","25.09.2020","dd.MM.yyyy","D");
	}
	
	
}

