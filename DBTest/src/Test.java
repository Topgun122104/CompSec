import java.sql.*;

class Test{
	public static void main(String[] args) {

	    System.out.println("----MySQL JDBC Connection Testing -------");
	    
	    try {
	        Class.forName("com.mysql.jdbc.Driver");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Where is your MySQL JDBC Driver?");
	        e.printStackTrace();
	        return;
	    }

	    System.out.println("MySQL JDBC Driver Registered!");
	    Connection connection = null;

	    try {
	        connection = DriverManager.
	                getConnection("jdbc:mysql://" + "ec2-54-89-255-107.compute-1.amazonaws.com" + ":" + 3306 + "/" + 
	                              "WatchFloor", "Admin", "PA$$w0rd1234!!");
	    } catch (SQLException e) {
	        System.out.println("Connection Failed!:\n" + e.getMessage());
	    }

	    if (connection != null) {
	        System.out.println("SUCCESS!!!! You made it, take control of your database now!");
	    } else {
	        System.out.println("FAILURE! Failed to make connection!");
	    }

	}
}
