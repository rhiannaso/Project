package cs174a;                                             // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// You may have as many imports as you need.
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.lang.Exception;
import java.sql.Statement;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;

import java.util.LinkedHashMap;
import java.util.Calendar;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;


/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
	private OracleConnection _connection;                   // Example connection object to your DB.
	//private ATMApp atmApp;
	//private BankTeller bankTeller;
	//private LinkedHashMap<String, Account> accountsInUse; // ACCOUNT ID: VARCHAR(5) 

	/**
	 * Default constructor.
	 * DO NOT REMOVE.
	 */
	App()
	{
		// atmApp = new ATMApp(this);
		// bankTeller = new BankTeller(this);
	}

	/**
	 * This is an example access operation to the DB.
	 */
	void exampleAccessToDB()
	{
		// Statement and ResultSet are AutoCloseable and closed automatically.
		try( Statement statement = _connection.createStatement() )
		{
			try( ResultSet resultSet = statement.executeQuery( "select * from user_tables" ) )
			{
				while( resultSet.next() )
					System.out.println( resultSet.getString( 1 ) + " " + resultSet.getString( 2 ) + " " );
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
		}
	}

	////////////////////////////// Implement all of the methods given in the interface /////////////////////////////////
	// Check the Testable.java interface for the function signatures and descriptions.

	@Override
	public String initializeSystem()
	{
		// Some constants to connect to your DB.
		final String DB_URL = "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
		final String DB_USER = "c##rhiannaso";
		final String DB_PASSWORD = "5543541";

		// Initialize your system.  Probably setting up the DB connection.
		Properties info = new Properties();
		info.put( OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER );
		info.put( OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD );
		info.put( OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20" );

		try
		{
			OracleDataSource ods = new OracleDataSource();
			ods.setURL( DB_URL );
			ods.setConnectionProperties( info );
			_connection = (OracleConnection) ods.getConnection();

			// Get the JDBC driver name and version.
			DatabaseMetaData dbmd = _connection.getMetaData();
			System.out.println( "Driver Name: " + dbmd.getDriverName() );
			System.out.println( "Driver Version: " + dbmd.getDriverVersion() );

			// Print some connection properties.
			System.out.println( "Default Row Prefetch Value is: " + _connection.getDefaultRowPrefetch() );
			System.out.println( "Database Username is: " + _connection.getUserName() );
			System.out.println();

			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	public void setUpUI() {
		Scanner s = new Scanner(System.in);

		System.out.println("Welcome to your virtual account management system.\n");
		System.out.println("0: ATM App\n1: Bank Teller\n2: Set Date");
		System.out.println("Enter the number associated with the action you'd like to take: ");
		String choice = s.nextLine();
		System.out.println(choice);
		//createTables();
		if(choice.equals("0")) {
			// atmApp.displayUI();
		} else if (choice.equals("1")) {
			// bankTeller.displayUI();
		} else {
			System.out.println("Setting a New Date\n");
			System.out.println("Enter the year: ");
			String year = s.nextLine();
			System.out.println("Enter the month: ");
			String month = s.nextLine();
			System.out.println("Enter the day: ");
			String day = s.nextLine();
			setDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
		}
		s.close();
	}

	@Override
	public String dropTables()
	{
		try( Statement statement = _connection.createStatement() )
		{
			statement.executeUpdate( "DROP TABLE BankDate" );
			statement.executeUpdate( "DROP TABLE Accounts" );
			statement.executeUpdate( "DROP TABLE Customers" );
			statement.executeUpdate( "DROP TABLE Transactions" );
			statement.executeUpdate( "DROP TABLE Involves" );
			statement.executeUpdate( "DROP TABLE Owners" );
			statement.executeUpdate( "DROP TABLE Primary" );
			statement.executeUpdate( "DROP TABLE LinkedTo" );
			statement.executeUpdate( "DROP SEQUENCE transId_seq" );
			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	@Override
	public String createTables()
	{
		String createDate = "CREATE TABLE BankDate(" +
							"year INTEGER, " +
							"month INTEGER, " +
							"day INTEGER, " +
							"PRIMARY KEY(year, month, day))"; 

		String createAccounts = "CREATE TABLE Accounts (" +
								"aid VARCHAR(5), " +
								"branch VARCHAR(15) DEFAULT \'Los Angeles\', " +
								"init_balance DECIMAL(15, 2), " +
								"curr_balance DECIMAL(15, 2), " +
								"interest DECIMAL(4, 2), " +
								"active INTEGER, " +
								"type VARCHAR(15), " +
								"PRIMARY KEY(aid) )"; 

		// NEED TO ENFORCE THAT CUSTOMER ONLY EXISTS IF IT OWNS AT LEAST ONE ACCOUNT IN JAVA
		String createCustomers = "CREATE TABLE Customers (" +
								"tax_id VARCHAR(9), " +
								"aid VARCHAR(5), " +
								"name VARCHAR(20), " +
								"address VARCHAR(50), " +
								"pin VARCHAR(4) NOT NULL, " +
								"PRIMARY KEY (tax_id) )"; 

		String createTransactions = "CREATE TABLE Transactions (" +
									"tid INTEGER, " +
									"year INTEGER, " +
									"month INTEGER, " +
									"day INTEGER, " +
									"amount DECIMAL(15,2), " +
									"type VARCHAR(20), " +
									"fee DECIMAL(15,2), " +
									"check_no INTEGER, " +
									"avg_daily_balance DECIMAL(15,2), " +
									"PRIMARY KEY (tid) )"; 

		String createInvolves = "CREATE TABLE Involves (" +
								"tid INTEGER, " +
								"aid_to VARCHAR(5), " +
								"aid_from VARCHAR(5), " +
								"PRIMARY KEY (aid_to, aid_from, tid) )"; 

		String createOwners = "CREATE TABLE Owners (" +
								"tax_id VARCHAR(9), " +
								"aid VARCHAR(5), " +
								"PRIMARY KEY (tax_id, aid), " +
								"FOREIGN KEY (tax_id) REFERENCES Customers(tax_id), " +
								"FOREIGN KEY (aid) REFERENCES Accounts(aid) )"; 

		String createPrimary = "CREATE TABLE Primary (" +
								"tax_id VARCHAR(9), " +
								"aid VARCHAR(5), " +
								"PRIMARY KEY (tax_id, aid), " +
								"FOREIGN KEY (tax_id) REFERENCES Customers(tax_id), " +
								"FOREIGN KEY (aid) REFERENCES Accounts(aid) )"; 

		String createLinkedTo = "CREATE TABLE LinkedTo (" +
								"aid_main VARCHAR(5), " +
								"aid_pocket VARCHAR(5), " +
								"PRIMARY KEY (aid_main, aid_pocket) )"; 

		String createSequence = "CREATE SEQUENCE transId_seq "+
								"START WITH 1 " +
								"INCREMENT BY 1";

		try( Statement statement = _connection.createStatement() )
		{
			statement.executeUpdate( createDate );
			statement.executeUpdate( createAccounts );
			statement.executeUpdate( createCustomers );
			statement.executeUpdate( createTransactions );
			statement.executeUpdate( createInvolves );
			statement.executeUpdate( createOwners );
			statement.executeUpdate( createPrimary );
			statement.executeUpdate( createLinkedTo );
			statement.executeUpdate( createSequence );
			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	@Override
	public String setDate( int year, int month, int day )
	{
		Calendar c = Calendar.getInstance();
		c.setLenient(false);
		c.set(year, month, day);
		try {
			c.getTime();
		}
		catch (Exception e) {
			System.out.println("Invalid date entered");
			return "1 "+Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(day);
		}
		/*String query = "UPDATE BankDate D " +
						"SET D.year = ? AND D.month = ? AND D.day = ? "+
						"WHERE D.year <> ? OR D.month <> ? OR D.day <> ?"; // UPDATE single row in date table to be new date; check that date is valid
		*/
		try( Statement statement = _connection.createStatement())
		{
			statement.executeUpdate("DELETE FROM BankDate");
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1 "+Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(day);
		}

		String query = "INSERT INTO BankDate(year, month, day) " +
						"VALUES (?, ?, ?) ";
		try( PreparedStatement prepStatement = _connection.prepareStatement(query))
		{
			prepStatement.setInt(1, year);
			prepStatement.setInt(2, month);
			prepStatement.setInt(3, day);
			prepStatement.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1 "+Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(day);
		}

		return "0 "+Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(day);
	}

	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{
		if (Double.compare(initialBalance, 1000) < 0) {
			return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
		} else {
			String createAccount = "INSERT INTO Accounts(aid, branch, init_balance, curr_balance, interest, active, type) " +
									"VALUES (?, ?, ?, ?, ?, ?, ?) ";
			try( PreparedStatement prepStatement2 = _connection.prepareStatement(createAccount))
			{
				prepStatement2.setString(1, id);
				prepStatement2.setString(2, id);
				prepStatement2.setDouble(3, initialBalance);
				prepStatement2.setDouble(4, initialBalance);
				if(accountType == AccountType.STUDENT_CHECKING) {
					prepStatement2.setDouble(5, 0.00);
					prepStatement2.setString(7, "student");
				} else if (accountType == AccountType.INTEREST_CHECKING) {
					prepStatement2.setDouble(5, 3.00);
					prepStatement2.setString(7, "interest");
				} else {
					prepStatement2.setDouble(5, 4.80);
					prepStatement2.setString(7, "savings");
				}
				prepStatement2.setInt(6, 1);
				prepStatement2.executeUpdate();
			}
			catch( SQLException e )
			{
				System.err.println( e.getMessage() );
				return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
			}

			// Check if exists
			String checkCust = checkCustomerExists(tin);
			if(checkCust.equals("0")) {
				createCustomer(id, tin, name, address);
			}

			if(checkCust.equals("-1")) {
				return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
			}

			createOwners(tin, id);

			createPrimary(tin, id);

			createTransaction("deposit", initialBalance, 0.00, 0, 0.00, id, id);
			return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
		}
	}

	public String checkCustomerExists(String tin) {
		String findCustomer = "SELECT * FROM Customers C WHERE C.tax_id = ?";
		try( PreparedStatement prepStatement = _connection.prepareStatement(findCustomer))
		{
			prepStatement.setString(1, tin);
			ResultSet rs = prepStatement.executeQuery();
			// if customer doesn't exist yet
			if(rs.next() == false) {
				return "0";
			} else {
				return "1";
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error with checking customer existence.");
			return "-1";
		}
	}

	@Override
	public String createPocketAccount( String id, String linkedId, double initialTopUp, String tin )
	{
		// Check if customer already has the savings or checkings account
		String findCustomer = "SELECT O.aid FROM Owners O WHERE O.tax_id = ?";
		try( PreparedStatement prepStatement3 = _connection.prepareStatement(findCustomer))
		{
			prepStatement3.setString(1, tin);
			String accId = "";
			ResultSet rs = prepStatement3.executeQuery();
			while(rs.next()) {
				accId = rs.getString(1);
			}
			if (accId.equals(linkedId) == false) {
				System.out.println("The given account is not owned by the customer with the given ID.");
				return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("oops 1");
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		String createAccount = "INSERT INTO Accounts(aid, branch, init_balance, curr_balance, interest, active, type) " +
								"VALUES (?, ?, ?, ?, ?, ?, ?) ";
		try( PreparedStatement prepStatement2 = _connection.prepareStatement(createAccount))
		{
			prepStatement2.setString(1, id);
			prepStatement2.setString(2, id);
			prepStatement2.setDouble(3, initialTopUp); // OR SHOULD WE SET IT AS 0 AND ADD STATEMENT TO UPDATE LIKE CURR BALANCE
			prepStatement2.setDouble(4, 0.00);
			prepStatement2.setDouble(5, 0.00);
			prepStatement2.setInt(6, 1);
			prepStatement2.setString(7, "pocket");
			prepStatement2.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("oops 2");
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		String createLinkedTo = "INSERT INTO LinkedTo(aid_main, aid_pocket) " +
								"VALUES (?, ?) ";
		try( PreparedStatement prepStatement = _connection.prepareStatement(createLinkedTo))
		{
			prepStatement.setString(1, linkedId);
			prepStatement.setString(2, id);
			prepStatement.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("oops 3");
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
		}

		createPrimary(tin, id);

		createOwners(tin, id);

		topUp(id, initialTopUp);
		
		String check = chargeFee(linkedId);
		if(check.equals("1")) {
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin; 
		}

		return "0 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
	}

	public String chargeFee(String id) {
		double balance = 0.00;
		balance = getBalance(id);

		balance = balance - 5;

		if(Double.compare(balance, 0.00) >= 0) {
			String check = "";
			check = updateBalance(balance, id);
			if(check.equals("1")) {
				System.out.println("Error with update balance in charge fee.");
				return "1";
			}
			createTransaction("fee", 5, 0.00, 0, 0.00, id, id);
			return "0";
		} else {
			System.out.println("Error with charge fee");
			return "1";
		}
	}

	@Override
	public String createCustomer( String accountId, String tin, String name, String address )
	{
		String createCustomer = "INSERT INTO Customers(tax_id, aid, name, address, pin) " +
								"VALUES (?, ?, ?, ?, ?) ";
		try( PreparedStatement prepStatement = _connection.prepareStatement(createCustomer))
		{
			prepStatement.setString(1, tin);
			prepStatement.setString(2, accountId);
			prepStatement.setString(3, name);
			prepStatement.setString(4, address);
			prepStatement.setString(5, "1717"); // Do we need to encrypt this?
			prepStatement.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		return "0";
	}

	public void createOwners(String tin, String accountId) {
		String createOwners = "INSERT INTO Owners(tax_id, aid) " +
							"VALUES (?, ?) ";
		try( PreparedStatement prepStatement2 = _connection.prepareStatement(createOwners))
		{
			prepStatement2.setString(1, tin);
			prepStatement2.setString(2, accountId);
			prepStatement2.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not create owner.");
		}
	}

	public void createPrimary(String tin, String id) {
		String createPrimary = "INSERT INTO Primary(tax_id, aid) " +
								"VALUES (?, ?) ";
		try( PreparedStatement prepStatement3 = _connection.prepareStatement(createPrimary))
		{
			prepStatement3.setString(1, tin);
			prepStatement3.setString(2, id);
			prepStatement3.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not create primary owner.");
		}
	}

	public ArrayList<Integer> getDateInfo() {
		ArrayList<Integer> dateInfo = new ArrayList<Integer>(3);

		int year = 0;
		int month = 0;
		int day = 0;
		try (Statement statement = _connection.createStatement()) {
			ResultSet rs_year = statement.executeQuery("SELECT D.year FROM BankDate D");
			while(rs_year.next()) {
				year = rs_year.getInt(1);
				dateInfo.add(year);
			}

			ResultSet rs_month = statement.executeQuery("SELECT D.month FROM BankDate D");
			while(rs_month.next()) {
				month = rs_month.getInt(1);
				dateInfo.add(month);
			}

			ResultSet rs_day = statement.executeQuery("SELECT D.day FROM BankDate D");
			while(rs_day.next()) {
				day = rs_day.getInt(1);
				dateInfo.add(day);
			}
		} catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error with getting date: date likely not set yet.");
		}
		return dateInfo;
	}

	@Override
	public String deposit( String accountId, double amount )
	{
		// WRITE STATEMENTS FOR CREATING ACCOUNT OBJECT, MODIFYING BALANCE, AND RESETTING
		double oldBalance = 0;
		double newBalance = 0;
		boolean shouldClose = false;
		String check = "";
		
		oldBalance = getBalance(accountId);
		if(Double.compare(oldBalance, -1.00) == 0) {
			return "1 " + oldBalance + " " + newBalance;
		}

		newBalance = oldBalance + amount;
		
		if(Double.compare(newBalance, 0.00) >= 0) {
			check = updateBalance(newBalance, accountId);
			if (check.equals(1)) {
				return "1 " + oldBalance + " " + newBalance;
			}

			createTransaction("deposit", amount, 0.00, 0, 0.00, accountId, accountId);
		} else {
			return "1 " + oldBalance + " " + newBalance;
		}

		return "0 " + oldBalance + " " + newBalance;
	}

	@Override
	public String showBalance( String accountId )
	{
		double balance = 0;
		balance = getBalance(accountId);
		if(Double.compare(balance, -1.00) == 0) {
			System.out.println("Could not retrieve balance.");
			return "1 " + balance;
		} else {
			System.out.println(balance);
			return "0 " + balance;
		}
	}

	public double getBalance(String accountId) {
		String getBalance = "SELECT A.curr_balance FROM Accounts A WHERE A.aid = ?";
		double balance = 0.00;
		try(PreparedStatement statement = _connection.prepareStatement(getBalance)) {
			statement.setString(1, accountId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				balance = rs.getDouble(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not retrieve balance");
			return -1.00;
		}
		return balance;
	}

	public String updateBalance(double balance, String aid) {
		String updBalance = "UPDATE Accounts SET curr_balance = ? WHERE aid = ?";
		try(PreparedStatement updateBalance = _connection.prepareStatement(updBalance)) {
			updateBalance.setDouble(1, balance);
			updateBalance.setString(2, aid);
			updateBalance.executeUpdate();
			return "0";
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not update balance.");
			return "1";
		}
	}

	@Override
	public String topUp( String accountId, double amount )
	{
		double pocketOldBalance = 0;
		double mainOldBalance = 0;
		String main_id = "";
		double linkedNewBalance = 0;
		double pocketNewBalance = 0;
		boolean shouldPClose = false;
		boolean shouldMClose = false;

		String getMain = "SELECT L.aid_main FROM LinkedTo L WHERE L.aid_pocket = ?";
		try( PreparedStatement mainStatement = _connection.prepareStatement(getMain) ) {
			mainStatement.setString(1, accountId);
			ResultSet rs_main = mainStatement.executeQuery();
			while(rs_main.next()) {
				main_id = rs_main.getString(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not find linked account ID.");
			return "1 " + linkedNewBalance + " " + pocketNewBalance;
		}

		pocketOldBalance = getBalance(accountId);
		mainOldBalance = getBalance(main_id);
		if(Double.compare(pocketOldBalance, -1.00) == 0 || Double.compare(mainOldBalance, -1.00) == 0) {
			return "1 " + linkedNewBalance + " " + pocketNewBalance;
		}

		linkedNewBalance = mainOldBalance - amount;
		pocketNewBalance = pocketOldBalance + amount;

		if(Double.compare(linkedNewBalance, 0.00) >= 0 && Double.compare(pocketNewBalance, 0.00) >= 0) {
			String check = "";
			String check2 = "";
			check = updateBalance(linkedNewBalance, main_id);
			check2 = updateBalance(pocketNewBalance, accountId);

			if(check.equals("1") || check2.equals("1")) {
				return "1 " + linkedNewBalance + " " + pocketNewBalance;
			}

			createTransaction("topUp", amount, 0.00, 0, 0.00, accountId, main_id);
		} else {
			return "1 " + linkedNewBalance + " " + pocketNewBalance;
		}

		return "0 " + linkedNewBalance + " " + pocketNewBalance;
	}

	@Override
	public String payFriend( String from, String to, double amount )
	{
		double fromOldBalance = 0;
		double toOldBalance = 0;
		double fromNewBalance = 0;
		double toNewBalance = 0;
		boolean shouldFromClose = false;
		boolean shouldToClose = false;

		fromOldBalance = getBalance(from);
		toOldBalance = getBalance(to);

		if(Double.compare(fromOldBalance, -1.00) == 0 || Double.compare(toOldBalance, -1.00) == 0) {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		fromNewBalance = fromOldBalance - amount;
		toNewBalance = toOldBalance + amount;

		if(Double.compare(fromNewBalance, 0.00) >= 0 && Double.compare(toNewBalance, 0.00) >= 0) {
			String check = "";
			String check2 = "";
			check = updateBalance(fromNewBalance, from);
			check2 = updateBalance(toNewBalance, to);

			if(check.equals("1") || check2.equals("1")) {
				return "1 " + fromNewBalance + " " + toNewBalance;
			}

			createTransaction("payFriend", amount, 0.00, 0, 0.00, to, from);
		} else {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		return "0 " + fromNewBalance + " " + toNewBalance;
	}

	@Override
	public String listClosedAccounts()
	{
		String getClosed = "SELECT A.aid FROM Accounts A WHERE A.active = 0";
		try(Statement statement = _connection.createStatement()) {
			ResultSet rs = statement.executeQuery(getClosed);
			ArrayList<String> closedIds = new ArrayList<String>();
			while(rs.next()) {
				closedIds.add(rs.getString("aid"));
			}
			String closedAccs = "";
			for(int i = 0; i < closedIds.size(); i++) {
				System.out.println(closedIds.get(i));
				closedAccs += closedAccs + " " + closedIds.get(i);
			}
			return "0" + closedAccs;
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	// return true if needs to be closed; false if okay
	public boolean checkAccountBalance(double balance) {
		if(Double.compare(balance, 0.01) == 0 || Double.compare(balance, 0.00) == 0) {
			System.out.println("Account balance less than $0.02. Account should close.");
			return true;
		} else {
			System.out.println("Account is okay.");
			return false;
		}
	}

	public void closeAccount(String accountId) {
		String updActive = "UPDATE Accounts SET active = 0 WHERE aid = ?";
		try(PreparedStatement updateActive = _connection.prepareStatement(updActive)) {
			updateActive.setString(1, accountId);
			updateActive.executeUpdate();
			System.out.println("Closed account.");
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Could not close account.");
		}
	}

	public String createTransaction( String type, double amount, double fee, int check_no, double avg_daily_balance, String aid_to, String aid_from) {
		ArrayList<Integer> dateInfo = new ArrayList<Integer>(getDateInfo());

		String createTransactions = "INSERT INTO Transactions(tid, year, month, day, amount, type, fee, check_no, avg_daily_balance) " +
									"VALUES (transId_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?) ";
		try( PreparedStatement prepStatement3 = _connection.prepareStatement(createTransactions))
		{
			prepStatement3.setInt(1, dateInfo.get(0));
			prepStatement3.setInt(2, dateInfo.get(1));
			prepStatement3.setInt(3, dateInfo.get(2));
			prepStatement3.setDouble(4, amount);
			prepStatement3.setString(5, type);
			prepStatement3.setDouble(6, fee);
			prepStatement3.setInt(7, check_no);
			prepStatement3.setDouble(8, avg_daily_balance);
			prepStatement3.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		String createInvolves = "INSERT INTO Involves(aid_to, aid_from, tid) " +
								"VALUES (?, ?, transId_seq.currval) ";
		try( PreparedStatement prepStatement3 = _connection.prepareStatement(createInvolves))
		{
			prepStatement3.setString(1, aid_to);
			prepStatement3.setString(2, aid_from);
			prepStatement3.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		// Check if account should be closed
		boolean shouldToClose = checkAccountBalance(getBalance(aid_to));
		
		if(shouldToClose == true) {
			closeAccount(aid_to);
		}

		if(aid_from.equals(aid_to) == false) {
			boolean shouldFromClose = checkAccountBalance(getBalance(aid_from));

			if(shouldFromClose == true) {
				closeAccount(aid_from);
			}
		}

		return "0";
	}

	public boolean verifyPIN(String pin) {
		// Implement encryption and check
		return true;
	}

	public void setPIN(String OldPIN, String NewPIN) {
		// Implement encryption and push into database
		return;
	}

}
