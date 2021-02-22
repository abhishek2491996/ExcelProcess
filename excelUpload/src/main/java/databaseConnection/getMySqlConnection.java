package databaseConnection;


import java.sql.Connection;
import java.sql.SQLException;

public class getMySqlConnection {
	public static void main(String args[]) throws ClassNotFoundException, SQLException {
		MySqlConnection mysql=new MySqlConnection();
		Connection con=mysql.getConnection();
		
		System.out.println("Connection"+con);
	}

}
