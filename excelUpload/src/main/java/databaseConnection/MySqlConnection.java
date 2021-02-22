package databaseConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;



public class MySqlConnection {
	private static Logger log = Logger.getLogger(MySqlConnection.class);
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		  log.info("Tryin to fetc sql connection");
			  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // conn = DriverManager.getConnection("jdbc:sqlserver://mssql.cgsz9m1vdfuu.us-east-1.rds.amazonaws.com;databaseName=MDO_US_PROD", "Abhishek_k_gupta", "Abhishek@#");
			//  conn = DriverManager.getConnection("jdbc:sqlserver://mssqlmultiaz.cgsz9m1vdfuu.us-east-1.rds.amazonaws.com;databaseName=MDO_HESS_PROD", "om_sharma", "Welcome@123");
			//  conn = DriverManager.getConnection("jdbc:sqlserver://mssqlmultiaz.cgsz9m1vdfuu.us-east-1.rds.amazonaws.com;databaseName=MDO_DOW_PREPROD", "abhishek_gupta", "Welcome@123");
			//  conn = DriverManager.getConnection("jdbc:sqlserver://mssqlmultiaz.cgsz9m1vdfuu.us-east-1.rds.amazonaws.com;databaseName=MDO_US_PROD", "abhishek_gupta", "Welcome@123");
			// conn = DriverManager.getConnection("jdbc:sqlserver://mssqlmultiaz.cfzlzz2nhprv.ap-southeast-2.rds.amazonaws.com;databaseName=MDO_UPL_PROD", "Om_Prakash", "Welcome@123");
			// No use   conn = DriverManager.getConnection("jdbc:sqlserver://mssql.cdf1cbpxzgyx.ap-southeast-1.rds.amazonaws.com;databaseName=MDO_UPL_PROD", "Om_sharma", "WSX#@!321");
			//conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=MDO_DB", "sa", "enter");
			// conn = DriverManager.getConnection("jdbc:sqlserver://10.0.3.3;databaseName=SPOTLESS_DEV_3", "sa", "Prospecta@123");
			// conn = DriverManager.getConnection("jdbc:sqlserver://mssql.cgsz9m1vdfuu.us-east-1.rds.amazonaws.com;databaseName=MDO_US_PROD", "rahulgupta", "Welcome@321");
			//  conn = DriverManager.getConnection("jdbc:sqlserver://mssqlmultiaz.cdf1cbpxzgyx.ap-southeast-1.rds.amazonaws.com;databaseName=MDO_US_QA", "OM_SHARMA", "Welcome@123");
			 conn = DriverManager.getConnection("jdbc:sqlserver://mssqlmultiaz.cdf1cbpxzgyx.ap-southeast-1.rds.amazonaws.com;databaseName=MDO_UPL_QA", "OM_SHARMA", "Welcome@123");
			//  conn = DriverManager.getConnection("jdbc:sqlserver://mssqlmultiaz.cfzlzz2nhprv.ap-southeast-2.rds.amazonaws.com;databaseName=MDO_INFINEUM_PROD", "abhishek", "Welcome@321");
			//  conn = DriverManager.getConnection("jdbc:sqlserver://mssqlmultiaz.cdf1cbpxzgyx.ap-southeast-1.rds.amazonaws.com;databaseName=MDO_GREENFOODS_QA", "abhijeet", "Welcome@123");
           //  conn = DriverManager.getConnection("jdbc:sqlserver://mssqlmultiaz.cgsz9m1vdfuu.us-east-1.rds.amazonaws.com;databaseName=MDO_HESS_PROD", "eiyaz", "Areeba@123");
			 System.out.println("Connection id "+conn);
			 return conn;  
			}  

	
}
