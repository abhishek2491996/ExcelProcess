package utility.DateUtility;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;

public class DateUtilityTest {
	
	private static Logger log = Logger.getLogger(DateUtility.class);
	public static void main(String ars[]){
		DateUtility utility=new DateUtility();
		log.info(utility.identifyDateFormat("02-19-1996"));
				
	}

}
