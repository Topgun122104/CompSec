import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Monitor {

	static Logger logger = Logger.getLogger("MonitoringLog");
	static FileHandler fh;

	public static void main(String[] args) {
		establishLogging();
		logger.info("Monitoring Tool Started...");
		if (args.length < 1) {
			logger.warning("No Robot Log File Provided!");
		} else {
			ArrayList<String> list;
			list = fileParser(args[0]);
			logger.info("Robot Output file successfully parsed...");
			tableInsert(list, args[1], args[2]);
			logger.info(list.size() + " new DB entries successfully added!");
		}
	}

	private static void tableInsert(ArrayList<String> list, String uname, String pword) {
		for(int i = 0; i < list.size(); i++) {
			try {
				
				String entry = list.get(i);
				String[] entryTokens = entry.split(",");
				
				//Create and populate DB table values
				int recordID = Integer.parseInt(entryTokens[0]);
				int athleteID = Integer.parseInt(entryTokens[1]);

				String aquiredDate = entryTokens[2];
				String aquiredTime = entryTokens[3];
				String  analyzedTime = entryTokens[4];

				double sampleMass = Double.parseDouble(entryTokens[5]);
				double ppm = Double.parseDouble(entryTokens[6]);

				String disposalDate = entryTokens[7];
				String disposalTime = entryTokens[8];

				//Craft the SQL Query
				String query = "insert into Sample(Record_ID, Athlete_ID, Date_Aquired, Time_Aquired, "
						+ "Time_Analyzed, Sample_Mass, PPM, Date_Disposal, Time_Disposal)"
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

				//Make the DB connection
				Connection conn = databaseConnect(uname, pword);
				
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				
				preparedStmt.setInt(1, recordID);
				preparedStmt.setInt(2, athleteID);
				preparedStmt.setString(3, aquiredDate);
				preparedStmt.setString(4, aquiredTime);
				preparedStmt.setString(5, analyzedTime);
				preparedStmt.setDouble(6, sampleMass);
				preparedStmt.setDouble(7, ppm);
				preparedStmt.setString(8, disposalDate);
				preparedStmt.setString(9, disposalTime);

				preparedStmt.execute();
				logger.info(preparedStmt.toString() + " was successfully executed by " + uname + "!");

				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static void establishLogging() {
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
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			logger.warning("Error locating MySQL JDBC Driver?");
			e.printStackTrace();
			return null;
		}
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://" + "ec2-54-89-255-107.compute-1.amazonaws.com" + ":" + 3306 + "/" + "WatchFloor",
					uname, pword);
		} catch (SQLException e) {
			logger.warning("Connection attempted by " + uname + " Failed!:\n" + e.getMessage());
		}

		if (connection != null) {
			logger.info(uname + " successfully connected to WatchFloor...");
		} else {
			logger.warning("FAILURE! Failed to make connection!");
		}

		return connection;
	}

	private static ArrayList<String> fileParser(String filename) {
		File file = new File(filename);
		BufferedReader reader = null;
		ArrayList<String> list = new ArrayList<String>();

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

		return list;

	}
}
