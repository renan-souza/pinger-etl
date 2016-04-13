package edu.stanford.slac.pinger.db.loader;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import edu.stanford.slac.pinger.general.Logger;
import edu.stanford.slac.pinger.general.utils.Utils;

public class DBLoader {

	public static void main(String[] args) throws SQLException {
		Connection conn = getConnection();
		//exemplo1(conn);
		
		long t1 = System.currentTimeMillis();
		insert(conn);
		long t2 = System.currentTimeMillis();
		
		Logger.log("Done! It took " + (t2-t1)/1000.0 + " s");
		conn.close();
	}

	public static void insert(Connection conn) throws SQLException {
		conn.setAutoCommit(false);

		Statement st = conn.createStatement();

		String dir = "C:/Users/Renan/Documents/WfC/WfTransform3/exp/_shared/transformedFiles/2014";
		
		long t1 = System.currentTimeMillis(); 
		long n = 0;
		File[] files = Utils.getAllFilePathsInADir(dir);
		long numberOfFiles = files.length;
		for (File file : files) {
			n++;
			long ti = System.currentTimeMillis();
			st.execute(
					"COPY pinger_measurement FROM '"+file.getAbsolutePath()+"' DELIMITER ',' CSV;"
			);
			long tf = System.currentTimeMillis();
			Logger.log("There are are " + (numberOfFiles-n) + " files remaining. It took " + (tf-ti)/1000.0 + " s to copy " + file.getAbsolutePath());
			if (n % 10 == 0) {
				long tii = System.currentTimeMillis();
				//conn.commit();
				long tff = System.currentTimeMillis();
				//Logger.log("It took " + (tff-tii)/1000.0 + " s to commit");	
			}
		}
		long t2 = System.currentTimeMillis();
		Logger.log("Main loop has finished. It took " + (t2-t1)/1000.0 + " s");
		long tii = System.currentTimeMillis();
		conn.commit();
		long tff = System.currentTimeMillis();
		Logger.log("It took " + (tff-tii)/1000.0 + " s to commit ");	
		st.close(); 
	}
	
	public static Connection getConnection() {
		String url = "jdbc:postgresql://localhost:5432/pinger";
		Properties props = new Properties();
		props.setProperty("user","postgres");
		props.setProperty("password","admin");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, props);
			System.out.println("Conectado com sucesso!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	
}
