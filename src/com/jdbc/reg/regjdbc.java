package com.jdbc.reg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.PreparedStatement;
import java.sql.*;
import java.sql.ResultSet;

public class regjdbc {
	
	public static String userName = null;
	public static String email = null;
	public static String pass = null;
	public static String action = null;
	private static Connection con = null;
	public static boolean isExistingUser = false;
	public static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demodb?serverTimezone=America/New_York",
					"root","Dasatva@9");
		
			
			System.out.println("Welcome to table demoDb, Please enter your choice :");
			System.out.println("1. Insert into Database ");
			System.out.println("2. Update into Database");
			int ch = Integer.parseInt(sc.nextLine());
			
			switch(ch) {
			case 1:
				System.out.println("This is Insert Case !!!");

				while(true) {
					System.out.println("Please provide your Username: ");
					String userName = sc.nextLine();
					
					if(isValidUsername(userName)) {
						System.out.println("Please provide your Email: ");
						String email = sc.nextLine();
						while(true) {
						System.out.println("Please provide your password:"); 
						String pass = sc.nextLine();
						if(isValidPassword(pass)) {
							
							isExistingUser = checkIfExistingUser(userName, email, con);
							if(!isExistingUser) {
								insertIntoDb(userName,email,pass,con);
							}else {
								System.out.println("This username is already taken, kindly choose a different UserName");
							}
						}
						break;
					 }
					break;
					}else {
		                System.out.println(userName + " is not a valid username. It must contain only alphanumeric characters and no spaces or special characters.");
					}
				break;
				}
			break;
			case 2:
				System.out.println("Entering Update Case !!!");
				updateIntoDb(con);
				break;
			default:
				System.out.println("Invalid Option ! Please select from given options only. ");
				break;
			}
			
		}catch (Exception e) { 
			System.out.println(e);
		}
		
	}
	
	private static boolean isValidPassword(String pass) {
		boolean isValidPass = false;
		String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()]).{8,}$";
		if(pass.matches(regex)) {
			isValidPass = true;
		}else {
			if(pass.length() < 8) {
				System.out.println("Password must be 8 characters long !");
			}if(!pass.matches(".*[a-z].*")) {
				System.out.println("Password must contain at least one lowercase letter.");
			}
			if(!pass.matches(".*[A-Z].*")) {
				System.out.println("Password must contain at least one uppercase letter.");
			}
			if(!pass.matches(".*\\d.*")) {
				System.out.println("Password must contain at least one digit.");
			}
			if(!pass.matches(".*[!@#$%^&*()].*")) {
				System.out.println("Password must contain at least one special character (!@#$%^&*()).");
			}
			isValidPass = false;
			
		}
		return isValidPass;
		
	}
	
	private static boolean isValidUsername(String userName2) {
		boolean isValid = false;
		String regex = "^[a-zA-Z0-9]+$";
		if(userName2 != null && userName2.matches(regex)) {
			isValid = true;
		}
		
		return isValid;
	}
	
	
	// This method is good
	private static boolean checkIfExistingUser(String userName2, String email2, Connection con) {
			
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean isExistingUser = false;
		try {
			String sql = "SELECT * FROM REG_DETAILS WHERE USERNAME like ? and email like ? ";

			stmt = con.prepareStatement(sql);
			stmt.setString(1, userName2);
			stmt.setString(2, email2);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				isExistingUser = true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isExistingUser;
	}

	// This method is good
	public static void insertIntoDb(String user, String email, String pass,Connection con) {

		try {
			
			System.out.println("Inserting records into Database");
			String sql = "INSERT INTO REG_DETAILS(USERNAME,EMAIL,PASSWORD,DOR) VALUES (?,?,?,CURDATE())";
			PreparedStatement stmt = con.prepareStatement(sql);
			
			stmt.setString(1, user);
			stmt.setString(2, email);
			stmt.setString(3, pass);
			
			int set = stmt.executeUpdate();
			
			if(set > 0) {
				System.out.println("Record insert in database is successful");
			}else {
				System.out.println("No records to be updated.");
			}	
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void updateIntoDb(Connection con) throws SQLException, NumberFormatException{
		
		System.out.println("Updating records into Database");
		System.out.println("Enter a Registration ID to update :");
		int reg_id = Integer.parseInt(sc.nextLine());
		
		String sql = " SELECT * FROM REG_DETAILS WHERE REG_ID = ? ";
		PreparedStatement pstatement = con.prepareStatement(sql);
		pstatement.setInt(1, reg_id);
		ResultSet result = pstatement.executeQuery();
	
		if(result.next()) {
			int regid = result.getInt("reg_id");
			String username = result.getString("username");
			String email = result.getString("email");
			String password = result.getString("password");
			Date date = result.getDate("dor");
			
			System.out.println("Registration ID : " + regid);
			System.out.println("Username : " + username);
			System.out.println("Email : " + email);
			System.out.println("Password : " + password);
			System.out.println("Date : " + date);
			
			System.out.println("What do you want to update ? ");
			System.out.println("1. Email");
			System.out.println("2. Password");
			
			int choice = Integer.parseInt(sc.nextLine());
			String sqlQuery = "update reg_details set ";
			
			switch (choice) {
			case 1:
				System.out.println("Enter new Email :");
				String newEmail = sc.nextLine();
				sqlQuery = sqlQuery + "email = ? where reg_id = ?"; 
				PreparedStatement mt = con.prepareStatement(sqlQuery);
				mt.setString(1, newEmail);	
				mt.setInt(2, regid);	

				int rows = mt.executeUpdate();
				if(rows > 0) {
					System.out.println("Email updated successfully");
				}
				break;
			case 2:
				System.out.println("Enter new Password :");
				String newPass = sc.nextLine();
				sqlQuery = sqlQuery + "password = ? where reg_id = ?"; 
				PreparedStatement mt2 = con.prepareStatement(sqlQuery);
				mt2.setString(1, newPass);
				mt2.setInt(2, regid);	
				int rows2 = mt2.executeUpdate();
				if(rows2 > 0) {
					System.out.println("Password updated successfully");
				}
				break;	
			default:
				break;
			}
			
			
		}else {
			System.out.println("Records not found !");
		}
	}
	

}
