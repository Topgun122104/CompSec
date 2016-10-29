import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Monitor {
	
    static Logger logger = Logger.getLogger("MonitoringLog");  
    static FileHandler fh;
    static List<String> list = new ArrayList<String>();

	public static void main(String[] args) {
		establishLogging();
		logger.info("Monitoring Tool Started..."); 
		if(args.length < 1){
			logger.warning("No Robot Log File Provided!");
		}else{
			fileParser(args[0]);
			logger.info("Robot Output file successfully parsed...");
			Connection conn = databaseConnect(args[1], args[2]);
		}
	}
	
	private static void establishLogging(){
	    try {  

	        // This block configures the logger with handler and formatter 
	    	logger.setUseParentHandlers(false);
	        fh = new FileHandler("MonitorLogFile.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);      
	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }
	}

	private static Connection databaseConnect(String uname, String pword) {
		logger.info("----MySQL JDBC Connection Testing -------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			logger.warning("Error locating MySQL JDBC Driver?");
			e.printStackTrace();
			return null;
		}

		logger.info("MySQL JDBC Driver Registered!");
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://" + "ec2-54-89-255-107.compute-1.amazonaws.com" + ":" + 3306 + "/" + "WatchFloor",
					uname, pword);
		} catch (SQLException e) {
			logger.warning("Connection Failed!:\n" + e.getMessage());
		}

		if (connection != null) {
			logger.info("Database Connection Test Success...");
		} else {
			logger.warning("FAILURE! Failed to make connection!");
		}
		
		return connection;
	}
	
	private static void fileParser(String filename){
		File file = new File(filename);
		BufferedReader reader = null;

		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;

		    while ((text = reader.readLine()) != null) {
		        list.add(text);
		    }
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		
	}
}
