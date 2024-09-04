package com.jdbc.reg;

import java.util.Scanner;
import java.sql.*;


public class regjdbc {
	
	public static String userName = null;
	public static String email = null;
	public static String pass = null;	
	private static Connection con = null;
	public static boolean isExistingUser = false;
	public static Scanner sc = new Scanner(System.in);
	public static java.lang.String sqlQuery = "update reg_details set ";
	
	public static void main(String[] args) {
		

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demodb?serverTimezone=America/New_York",
					"root", "Dasatva@9");

			System.out.println("Welcome to table demoDb, Please enter your choice :");
			System.out.println("1. Insert into Database ");
			System.out.println("2. Update into Database");
			System.out.println("3. Delete from Database");
			int ch = Integer.parseInt(sc.nextLine());

			switch (ch) {
			case 1:
				dataInsert(con); // This will Validate Data and perform insert
				break; // break out of the case

			case 2:
				updateIntoDb(con); // This will update the existing record
				break; // break out of the case
			
			case 3:
				deleteFromDb(con);
				break;
			default:
				System.out.println("Invalid Option ! Please select from given options only. ");
				break; // break out of the case
			}

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	

	private static void dataInsert(Connection con) {
		System.out.println("Please Enter your details:");

		boolean insertionSuccessful = false; // Flag to indicate successful insertion

		while (!insertionSuccessful) { // Outer loop for user input
			System.out.println("Please provide your Username: ");
			String userName = sc.nextLine();

			if (isValidUsername(userName)) {

				while (true) { // Loop for email input
					System.out.println("Please provide your Email: ");
					String email = sc.nextLine();

					if (isValidEmail(email)) {

						while (true) { // Loop for password input
							System.out.println("Please provide your password:");
							String pass = sc.nextLine();

							if (isValidPassword(pass)) {

								isExistingUser = checkIfExistingUser(userName, email, con);
								if (!isExistingUser) {
									insertIntoDb(userName, email, pass, con);
									insertionSuccessful = true; // Mark as successful
									break; // Break out of the password loop after successful insertion
								} else {
									System.out.println(
											"This username is already taken, kindly choose a different UserName");
									break; // Break out of the password loop to allow the user to re-enter details
								}
							} else {
								System.out.println("Invalid password. Please try again.");
							}
						}

						if (insertionSuccessful) {
							break; // Break out of the email loop
						}

					} else {
						System.out.println("Kindly provide a valid emailId");
					}
				}

			} else {
				System.out.println("Kindly provide a valid Username");
			}
		}

		System.out.println("User successfully registered.");
	}
	
	private static boolean isValidEmail(String email2) {
		boolean isValidEmail = false;
		String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

	      if(email2!= null && email2.matches(emailRegex)){
	    	  isValidEmail = true;
		}
		
		return isValidEmail;
	}

	private static boolean isValidPassword(String pass) {
		boolean isValidPass = false;
		String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()]).{8,}$";
		if (pass.matches(regex)) {
			isValidPass = true;
		} else {
			if (pass.length() < 8) {
				System.out.println("Password must be 8 characters long !");
			}
			if (!pass.matches(".*[a-z].*")) {
				System.out.println("Password must contain at least one lowercase letter.");
			}
			if (!pass.matches(".*[A-Z].*")) {
				System.out.println("Password must contain at least one uppercase letter.");
			}
			if (!pass.matches(".*\\d.*")) {
				System.out.println("Password must contain at least one digit.");
			}
			if (!pass.matches(".*[!@#$%^&*()].*")) {
				System.out.println("Password must contain at least one special character (!@#$%^&*()).");
			}
			isValidPass = false;

		}
		return isValidPass;

	}
	
	private static boolean isValidUsername(String userName2) {
		boolean isValid = false;
		String regex = "^[a-zA-Z0-9]+$";
		if (userName2 != null && userName2.matches(regex)) {
			isValid = true;
		}

		return isValid;
	}

	
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

			if (rs.next()) {
				isExistingUser = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isExistingUser;
	}

	public static void insertIntoDb(String user, String email, String pass, Connection con) {

		try {

			System.out.println("Inserting records into Database");
			String sql = "INSERT INTO REG_DETAILS(USERNAME,EMAIL,PASSWORD,DOR) VALUES (?,?,?,CURDATE())";
			PreparedStatement stmt = con.prepareStatement(sql);

			stmt.setString(1, user);
			stmt.setString(2, email);
			stmt.setString(3, pass);

			int set = stmt.executeUpdate();

			if (set > 0) {
				System.out.println("Record insert in database is successful");
				
			} else {
				System.out.println("No records to be updated.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void updateIntoDb(Connection con) throws SQLException, NumberFormatException {

		System.out.println("Updating records into Database");
		System.out.println("Enter a Registration ID to update :");
		int reg_id = Integer.parseInt(sc.nextLine());

		ResultSet result = fetchDetailsFromDB(con, reg_id);

		if (result.next()) {
			int regid = result.getInt("reg_id");
			printDetailsToBeUpdated(result, regid);

			System.out.println("What do you want to update ? ");
			System.out.println("1. Email");
			System.out.println("2. Password");
			int choice = Integer.parseInt(sc.nextLine());
			

			switch (choice) {
			case 1:
				updateToNewEmail(con, regid); // Accepts new email and updates it
				break;
			case 2:
				updateToNewPassword(con, regid); // Accepts new password and updates it
				break;
			default:
				break;
			}

		} else {
			System.out.println("Records not found !");
		}
	}

	public static ResultSet fetchDetailsFromDB(Connection con, int reg_id) throws SQLException {
		String sql = " SELECT * FROM REG_DETAILS WHERE REG_ID = ? ";
		PreparedStatement pstatement = con.prepareStatement(sql);
		pstatement.setInt(1, reg_id);
		ResultSet result = pstatement.executeQuery();
		return result;
	}

	public static void printDetailsToBeUpdated(ResultSet result, int regid) throws SQLException {
		
		String username = result.getString("username");
		String email = result.getString("email");
		String password = result.getString("password");
		Date date = result.getDate("dor");

		System.out.println("Registration ID : " + regid);
		System.out.println("Username : " + username);
		System.out.println("Email : " + email);
		System.out.println("Password : " + password);
		System.out.println("Date : " + date);

	}

	public static void updateToNewPassword(Connection con, int regid) throws SQLException {
		while(true) {	
			System.out.println("Enter new Password :");
			String newPass = sc.nextLine();
			
			if(isValidPassword(newPass)) {
				sqlQuery = sqlQuery + "password = ? where reg_id = ?";
				PreparedStatement mt2 = con.prepareStatement(sqlQuery);
				mt2.setString(1, newPass);
				mt2.setInt(2, regid);
				int rows2 = mt2.executeUpdate();
				if (rows2 > 0) {
					System.out.println("Password updated successfully ! Updated details are as follows :");
					ResultSet result = fetchDetailsFromDB(con,regid);
					if(result.next()) {
						String username = result.getString("username");
						String email = result.getString("email");
						String password = result.getString("password");
						Date date = result.getDate("dor");
						System.out.println("Registration ID : " + regid);
						System.out.println("Username : " + username);
						System.out.println("Email ID : " + email);
						System.out.println("Previous Password : " + password + "  Updated Password : " + newPass);
						System.out.println("Date : " + date);
						break;
					}
				}
			}else {
				System.out.println("Invalid Password.Please try entering a valid password");
			}
		}
	}

	public static void updateToNewEmail(Connection con, int regid) throws SQLException {
		while(true) {
			System.out.println("Enter new Email :");
			String newEmail = sc.nextLine();
			
			if(isValidEmail(newEmail)) {
				sqlQuery = sqlQuery + "email = ? where reg_id = ?";
				PreparedStatement mt = con.prepareStatement(sqlQuery);
				mt.setString(1, newEmail);
				mt.setInt(2, regid);
				
				int rows = mt.executeUpdate();
				if (rows > 0) {
					System.out.println("Email updated successfully ! Updated details are as follows : ");
					ResultSet result = fetchDetailsFromDB(con, regid);
					if (result.next()) {
						String username = result.getString("username");
						String email = result.getString("email");
						String password = result.getString("password");
						Date date = result.getDate("dor");
						System.out.println("Registration ID : " + regid);
						System.out.println("Username : " + username);
						System.out.println("Previous Email ID : " + email + "  Updated Email ID : " + newEmail );
						System.out.println("Password : " + password);
						System.out.println("Date : " + date);
						break;
					}
				}
			}else {
				System.out.println("Invalid Email ! Please try entering a valid Email");
			}
		}
	}
	
	
	private static void deleteFromDb(Connection con) throws SQLException {
		// TODO Auto-generated method stub
		System.out.println("Deleting records from DB :");
		System.out.println("Enter a registration Id you want to delete :");
		int reg_id = Integer.parseInt(sc.nextLine());
		ResultSet result = fetchDetailsFromDB(con, reg_id);
		if(result.next()) {
			int regid  = result.getInt("reg_id");
			String username = result.getString("username");
			String email = result.getString("email");
			String password = result.getString("password");
			Date date = result.getDate("dor");
			System.out.println("Registration ID : " + regid);
			System.out.println("Username : " + username);
			System.out.println("Email ID : " + email );
			System.out.println("Password : " + password);
			System.out.println("Date : " + date);
		}
		System.out.println("Are you sure ? ");
		String ans = sc.nextLine();
		if(("yes".equalsIgnoreCase(ans)) || ("YES".equalsIgnoreCase(ans)) || ("Yes".equalsIgnoreCase(ans))) {
			String sql = "delete from reg_details where reg_id = ?";
			PreparedStatement smt = con.prepareStatement(sql);
			smt.setInt(1, reg_id);
			int rowsd = smt.executeUpdate();
			if(rowsd > 0) {
				System.out.println("Record Deleted successfully from table reg_details");
			}
			
		}else if (("no".equalsIgnoreCase(ans)) || ("NO".equalsIgnoreCase(ans)) || ("No".equalsIgnoreCase(ans)) ) {
			System.out.println("Relax !! we got you, record not deleted yet !! ");
		}
		
			
	}

}
