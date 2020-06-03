package db_trad;

import java.sql.*;
import java.util.Random; 

public class ExternalDb {
	private static String url,db;
	private static long queriesCount = 11915;
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
			for(int i=0;i<1000;i++) {

				startTime = System.nanoTime();
				rs = stmt.executeQuery("select * from person where id = " + i);
				endTime = System.nanoTime();
				totalTime += (endTime - startTime);
				count++;
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}  
		long duration = endTime - startTime;
		System.out.println("Time taken for SELECT statement(" + count + ") - " + duration + " ns");

	}
	public static void main(String[] args) {
		try{  
			
			Class.forName("com.mysql.cj.jdbc.Driver");  
			url = "jdbc:mysql://sql12.freemysqlhosting.net:3306";
			db = "sql12345000";
			
			String url_to_connect = url + "/" + db;
			
			Connection con = DriverManager.getConnection(url_to_connect,"sql12345000","5Vg1apneyU");  
			
			Statement stmt = con.createStatement();  
				
			selectQuery(stmt);
			
			con.close();  
		}catch(Exception e){ 
			System.out.println(e);
		}  
	} 
}
