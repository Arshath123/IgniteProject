package db_trad;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class DatabaseIgnite {
	private static long queriesCount = 100000;
    private static String getAlphaNumericString(int n) 
    { 
        String AlphaNumericString = "abcdefghijklmnopqrstuvxyz"; 
 
        StringBuilder sb = new StringBuilder(n); 
  
        for (int i = 0; i < n; i++) { 
            int index = (int)(AlphaNumericString.length() * Math.random()); 
            sb.append(AlphaNumericString.charAt(index)); 
        } 
        return sb.toString(); 
    }
	private static void selectQuery(Statement stmt) {
		long startTime = 0,endTime = 0,count = 0,totalTime = 0;
		ResultSet rs = null;
		try {
			for(int i=0;i<queriesCount;i++) {
				startTime = System.nanoTime();
				rs = stmt.executeQuery("select * from records where id = " + i);
				endTime = System.nanoTime();
				totalTime += endTime - startTime;
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}  
		long duration = totalTime;
		System.out.println("Time taken for SELECT statement(" + queriesCount + ") - " + duration + " ns");	
	}
	private static void insertQuery(Connection con) {
		long startTime = 0,endTime = 0,totalTime = 0;
		try {
			totalTime = 0;
			int id = 0;
			String[] dep = new String[4];
			dep[0] = "CSE";dep[1] = "MECH";dep[2] = "ECE";dep[3] = "EEE";
			String[] col = new String[4];
			col[0] = "CEG";col[1] = "MIT"; col[2] = "VIT";col[3] = "IIT";
			for(int i=0;i<queriesCount;i++) {
				String query = "INSERT INTO records (id,name,department,college)" + "VALUES (?,?,?,?)";
				PreparedStatement ps = con.prepareStatement(query);
				ps.setInt(1,id);
				id++;
				int rand1 = new Random().nextInt(4);
				ps.setString(2, getAlphaNumericString(10));
				ps.setString(3,dep[rand1]);
				ps.setString(4,col[rand1]);
				
				startTime = System.nanoTime();
				ps.execute();
				endTime = System.nanoTime();
				
				totalTime += endTime - startTime;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  
		long duration = totalTime;
		System.out.println("Time taken for INSERT statement("+queriesCount+") - " + duration + " ns");
	}
	private static void updateQuery(Connection con) {
		long startTime = 0,endTime = 0,totalTime = 0;
		try {
			int id = 0;
			for(int i=0;i<queriesCount;i++) {
				String query = "UPDATE records set name = ? where id = ?";
				
				PreparedStatement ps = con.prepareStatement(query);
				ps.setString(1,"employeeofciticorp");
				ps.setInt(2, id);
				id++;
				
				startTime = System.nanoTime();
				ps.execute();
				endTime = System.nanoTime();		
				totalTime += endTime - startTime;
			}
	
		} catch (SQLException e) {
			e.printStackTrace();
		}  
		long duration = totalTime;
		System.out.println("Time taken for UPDATE statement("+queriesCount+") - " + duration + " ns");		
	}
    public static void main(String[] args) throws Exception {
        System.out.println("JDBC example started.");

        // Register JDBC driver
        Class.forName("org.apache.ignite.IgniteJdbcDriver");

        // Open JDBC connection
        Connection conn = DriverManager.getConnection(
                "jdbc:ignite:thin://127.0.0.1/");
        
        Statement stmt = conn.createStatement(); 

		//insertQuery(conn);
		
		//updateQuery(conn);
		
		selectQuery(stmt);
		
		conn.close();  
    }
}
