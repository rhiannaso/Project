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
import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;


/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
	private OracleConnection _connection;                   // Example connection object to your DB.
	private ATMApp atmApp;
	private BankTeller bankTeller;
	private LinkedHashMap<String, String> d;
	private String taxId;
	//private LinkedHashMap<String, Account> accountsInUse; // ACCOUNT ID: VARCHAR(5) 

	/**
	 * Default constructor.
	 * DO NOT REMOVE.
	 */
	App()
	{
		atmApp = new ATMApp(this);
		bankTeller = new BankTeller(this);
		setUpEncryption();
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
		String choice = "";

		while(choice.equals("3") == false) {
			System.out.println("Welcome to your virtual account management system.\n");
			System.out.println("0: ATM App\n1: Bank Teller\n2: Set Date\n3: Exit");
			System.out.println("Enter the number associated with the action you'd like to take: ");
			choice = s.nextLine();
			
			if(choice.equals("0")) {
				if(checkDate() == false) {
					System.out.println("No date set yet. Please set a date first.");
					continue;
				}
				atmApp.displayUI(s, _connection);
			} else if (choice.equals("1")) {
				if(checkDate() == false) {
					System.out.println("No date set yet. Please set a date first.");
					continue;
				}
				bankTeller.displayUI();
			} else if (choice.equals("2")){
				System.out.println("Setting a New Date\n");
				System.out.println("Enter the year: ");
				String year = s.nextLine();
				System.out.println("Enter the month: ");
				String month = s.nextLine();
				System.out.println("Enter the day: ");
				String day = s.nextLine();
				setDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
			} else {
				break;
			}
		}
		s.close();
	}

	@Override
	public String dropTables()
	{
		try( Statement statement = _connection.createStatement() )
		{
			statement.executeUpdate( "DROP TABLE BankDate" );
			statement.executeUpdate( "DROP TABLE Involves" );
			statement.executeUpdate( "DROP TABLE Owners" );
			statement.executeUpdate( "DROP TABLE Primary" );
			statement.executeUpdate( "DROP TABLE LinkedTo" );
			statement.executeUpdate( "DROP TABLE Accounts" );
			statement.executeUpdate( "DROP TABLE Customers" );
			statement.executeUpdate( "DROP TABLE Transactions" );
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
							"d DATE, " +
							"PRIMARY KEY(d))"; 

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
									"d DATE, " +
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
		String newDate = Integer.toString(year) + "-" + Integer.toString(month)  + "-" + Integer.toString(day);
		c.setLenient(false);
		c.set(year, month, day);
		try {
			c.getTime();
		}
		catch (Exception e) {
			System.out.println("Invalid date entered");
			return "1 "+newDate;
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
			return "1 "+newDate;
		}

		String query = "INSERT INTO BankDate(d) " +
						"VALUES (TO_DATE(?, \'YYYY-MM-DD\')) ";
		try( PreparedStatement prepStatement = _connection.prepareStatement(query))
		{
			prepStatement.setString(1, newDate);
			prepStatement.executeUpdate();
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1 "+newDate;
		}

		return "0 "+newDate;
	}

	public boolean checkDate() {
		String query = "SELECT COUNT(*) FROM BankDate";
		try (Statement s = _connection.createStatement() ) {
			ResultSet rs = s.executeQuery(query);
			int count = -1;
			while(rs.next()) {
				count = rs.getInt(1);
				System.out.println("COUNT "+ count);
			}
			if(count == 0) {
				return false;
			} else if (count == 1) {
				return true;
			}
		} catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return false;
		}
		return false;
	}

	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{
		if (Double.compare(initialBalance, 1000) < 0) {
			return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
		} else {
			String createAccount = "INSERT INTO Accounts(aid, init_balance, curr_balance, interest, active, type) " +
									"VALUES (?, ?, ?, ?, ?, ?) ";
			try( PreparedStatement prepStatement2 = _connection.prepareStatement(createAccount))
			{
				prepStatement2.setString(1, id);
				prepStatement2.setDouble(2, initialBalance);
				prepStatement2.setDouble(3, 0);
				if(accountType == AccountType.STUDENT_CHECKING) {
					prepStatement2.setDouble(4, 0.00);
					prepStatement2.setString(6, "student");
				} else if (accountType == AccountType.INTEREST_CHECKING) {
					prepStatement2.setDouble(4, 3.00);
					prepStatement2.setString(6, "interest");
				} else {
					prepStatement2.setDouble(4, 4.80);
					prepStatement2.setString(6, "savings");
				}
				prepStatement2.setInt(5, 1);
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

			String checkTrans = createTransaction("deposit", initialBalance, 0.00, 0, 0.00, id, id, initialBalance, initialBalance);
			if(checkTrans.equals("1")) {
				return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
			}
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

		String createAccount = "INSERT INTO Accounts(aid, init_balance, curr_balance, interest, active, type) " +
								"VALUES (?, ?, ?, ?, ?, ?) ";
		try( PreparedStatement prepStatement2 = _connection.prepareStatement(createAccount))
		{
			prepStatement2.setString(1, id);
			prepStatement2.setDouble(2, initialTopUp); // OR SHOULD WE SET IT AS 0 AND ADD STATEMENT TO UPDATE LIKE CURR BALANCE
			prepStatement2.setDouble(3, 0.00);
			prepStatement2.setDouble(4, 0.00);
			prepStatement2.setInt(5, 1);
			prepStatement2.setString(6, "pocket");
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
		
		String check = chargeFee(linkedId, 5.00);
		if(check.equals("1")) {
			return "1 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin; 
		}

		return "0 " + id + " " + AccountType.POCKET + " " + initialTopUp + " " + tin;
	}

	public String chargeFee(String id, double amount) {
		double balance = 0.00;
		balance = getBalance(id);

		balance = balance - amount;

		if(Double.compare(balance, 0.00) >= 0) {
			String isValid = createTransaction("fee", amount, 0.00, 0, 0.00, id, id, balance, balance); 
			if(isValid.equals("1")) {
				System.out.println("Charging fee failed.");
				return "1";
			}
			return "0";
		} else {
			System.out.println("Error with charge fee");
			return "1";
		}
	}

	@Override
	public String createCustomer( String accountId, String tin, String name, String address )
	{
		boolean checkAccount = checkClosed(accountId);
		if(checkAccount == true) {
			System.out.println("Attempting to link customer to a closed account. Customer creation failed.");
			return "1";
		}

		String createCustomer = "INSERT INTO Customers(tax_id, aid, name, address, pin) " +
								"VALUES (?, ?, ?, ?, ?) ";
		try( PreparedStatement prepStatement = _connection.prepareStatement(createCustomer))
		{
			prepStatement.setString(1, tin);
			prepStatement.setString(2, accountId);
			prepStatement.setString(3, name);
			prepStatement.setString(4, address);
			String pin = encrypt("1717");
			prepStatement.setString(5, pin); 
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

	public String getDateInfo() {
		String bankDate = "";
		try (Statement statement = _connection.createStatement()) {
			ResultSet rs = statement.executeQuery("SELECT D.d FROM BankDate D");
			while(rs.next()) {
				bankDate = rs.getString(1);
				String splitDateTime[] = bankDate.split(" ");
				return splitDateTime[0];
			}
		} catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error with getting date.");
			return "1";
		}
		return bankDate;
	}

	@Override
	public String deposit( String accountId, double amount )
	{
		// WRITE STATEMENTS FOR CREATING ACCOUNT OBJECT, MODIFYING BALANCE, AND RESETTING
		double oldBalance = 0;
		double newBalance = 0;

		boolean checkType = isCheckingOrSavings(accountId);
		
		if (checkType == false) {
			System.out.println("The involved account must be a checking/savings account.");
			return "1 " + oldBalance + " " + newBalance; 
		}
		
		oldBalance = getBalance(accountId);
		if(Double.compare(oldBalance, -1.00) == 0) {
			return "1 " + oldBalance + " " + newBalance;
		}

		newBalance = oldBalance + amount;
		
		if(Double.compare(newBalance, 0.00) >= 0) {
			String isValid = createTransaction("deposit", amount, 0.00, 0, 0.00, accountId, accountId, newBalance, newBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + oldBalance + " " + newBalance;
			}
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

		boolean checkType = checkType(accountId, "pocket");
		boolean checkType2 = isCheckingOrSavings(main_id);
		
		if (checkType == false || checkType2 == false) {
			System.out.println("The involved accounts must be a pocket account and a checking/savings account.");
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
			String isValid = createTransaction("topUp", amount, 0.00, 0, 0.00, accountId, main_id, pocketNewBalance, linkedNewBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + linkedNewBalance + " " + pocketNewBalance;
			}
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
		boolean checkType = checkType(from, "pocket");
		boolean checkType2 = checkType(to, "pocket");

		if (checkType == false || checkType2 == false) {
			System.out.println("One or more involved accounts are not pocket accounts.");
			return "1 " + fromNewBalance + " " + toNewBalance; 
		}

		fromOldBalance = getBalance(from);
		toOldBalance = getBalance(to);

		if(Double.compare(fromOldBalance, -1.00) == 0 || Double.compare(toOldBalance, -1.00) == 0) {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		fromNewBalance = fromOldBalance - amount;
		toNewBalance = toOldBalance + amount;

		if(Double.compare(fromNewBalance, 0.00) >= 0 && Double.compare(toNewBalance, 0.00) >= 0) {
			String isValid = createTransaction("payFriend", amount, 0.00, 0, 0.00, to, from, toNewBalance, fromNewBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + fromNewBalance + " " + toNewBalance;
			}
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
			String printClosed = "";
			for(int i = 0; i < closedIds.size(); i++) {
				closedAccs = closedAccs + " " + closedIds.get(i);
				printClosed = printClosed + closedIds.get(i) + "\n";
			}
			System.out.println("Closed Accounts:");
			System.out.print(printClosed);

			return "0" + closedAccs;
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	public boolean checkType(String accountId, String type) {
		String typeCheck = "SELECT A.type FROM Accounts A WHERE A.aid = ?";
		try(PreparedStatement statement = _connection.prepareStatement(typeCheck)) {
			statement.setString(1, accountId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				if((rs.getString(1)).equals(type)) {
					return true;
				}
			}
			return false;
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return false;
		}
	}

	public boolean isCheckingOrSavings(String accountId) {
		String typeCheck = "SELECT A.type FROM Accounts A WHERE A.aid = ?";
		try(PreparedStatement statement = _connection.prepareStatement(typeCheck)) {
			statement.setString(1, accountId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				if((rs.getString(1)).equals("pocket") == false) {
					return true;
				}
			}
			return false;
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return false;
		}
	}

	// use to check if performing transaction on a closed account
	public boolean checkClosed(String accountId) {
		String getClosed = "SELECT A.aid FROM Accounts A WHERE A.active = 0";
		try(Statement statement = _connection.createStatement()) {
			ResultSet rs = statement.executeQuery(getClosed);
			ArrayList<String> closedIds = new ArrayList<String>();
			while(rs.next()) {
				if(accountId.equals(rs.getString(1))) {
					return true;
				}
			}
			return false;
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return true;
		}
	}

	// return true if needs to be closed; false if okay
	public boolean checkAccountBalance(double balance) {
		if(Double.compare(balance, 0.01) == 0 || Double.compare(balance, 0.00) == 0) {
			System.out.println("Account balance less than $0.02. Account should close.");
			return true;
		} else {
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

	public String createTransaction( String type, double amount, double fee, int check_no, double avg_daily_balance, String aid_to, String aid_from, double newTo, double newFrom) {
		String dateInfo = getDateInfo();

		boolean checkClosed = checkClosed(aid_to);
		if(checkClosed) {
			System.out.println("An account involved in the transaction is already closed. Transaction failed.");
			return "1";
		}
		checkClosed = checkClosed(aid_from);
		if(checkClosed) {
			System.out.println("An account involved in the transaction is already closed. Transaction failed.");
			return "1";
		}

		String createTransactions = "INSERT INTO Transactions(tid, d, amount, type, fee, check_no, avg_daily_balance) " +
									"VALUES (transId_seq.nextval, TO_DATE(?, \'YYYY-MM-DD\'), ?, ?, ?, ?, ?) ";
		try( PreparedStatement prepStatement3 = _connection.prepareStatement(createTransactions))
		{
			prepStatement3.setString(1, dateInfo);
			prepStatement3.setDouble(2, amount);
			prepStatement3.setString(3, type);
			prepStatement3.setDouble(4, fee);
			prepStatement3.setInt(5, check_no);
			prepStatement3.setDouble(6, avg_daily_balance);
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

		String check = updateBalance(newTo, aid_to);

		if(check.equals("1")) {
			return "1";
		}

		// Check if account should be closed
		boolean shouldToClose = checkAccountBalance(getBalance(aid_to));
		
		if(shouldToClose == true) {
			closeAccount(aid_to);
		}

		if(aid_from.equals(aid_to) == false) {
			String check2 = updateBalance(newFrom, aid_from);
			if(check2.equals("1")) {
				return "1";
			}

			boolean shouldFromClose = checkAccountBalance(getBalance(aid_from));

			if(shouldFromClose == true) {
				closeAccount(aid_from);
			}
		}

		return "0";
	}

	public String withdrawal(String accountId, double amount) {
		double oldBalance = 0;
		double newBalance = 0;
		boolean shouldClose = false;

		boolean checkType = isCheckingOrSavings(accountId);
		
		if (checkType == false) {
			System.out.println("The involved account must be a checking/savings account.");
			return "1 " + oldBalance + " " + newBalance; 
		}
		
		oldBalance = getBalance(accountId);
		if(Double.compare(oldBalance, -1.00) == 0) {
			return "1 " + oldBalance + " " + newBalance;
		}

		newBalance = oldBalance - amount;
		
		if(Double.compare(newBalance, 0.00) >= 0) {
			String isValid = createTransaction("withdrawal", amount, 0.00, 0, 0.00, accountId, accountId, newBalance, newBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + oldBalance + " " + newBalance;
			}
		} else {
			return "1 " + oldBalance + " " + newBalance;
		}

		return "0 " + oldBalance + " " + newBalance;
	}

	public String purchase(String accountId, double amount) {
		double oldBalance = 0;
		double newBalance = 0;
		boolean shouldClose = false;

		boolean checkType = checkType(accountId, "pocket");
		
		if (checkType == false) {
			System.out.println("The involved account must be a pocket account.");
			return "1 " + oldBalance + " " + newBalance; 
		}
		
		oldBalance = getBalance(accountId);
		if(Double.compare(oldBalance, -1.00) == 0) {
			return "1 " + oldBalance + " " + newBalance;
		}

		newBalance = oldBalance - amount;
		
		if(Double.compare(newBalance, 0.00) >= 0) {
			String isValid = createTransaction("purchase", amount, 0.00, 0, 0.00, accountId, accountId, newBalance, newBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + oldBalance + " " + newBalance;
			}
		} else {
			return "1 " + oldBalance + " " + newBalance;
		}

		return "0 " + oldBalance + " " + newBalance;
	}

	public boolean isOwner(String accountId) {
		String checkOwner = "";
		String check = "SELECT O.tax_id FROM Owners O WHERE O.aid = ?";
		try(PreparedStatement statement = _connection.prepareStatement(check)) {
			statement.setString(1, accountId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				checkOwner = rs.getString("tax_id");
			}
			if(checkOwner.equals(this.taxId) == false) {
				return false;
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return false;
		}
		return true;
	}

	public String transfer(String from, String to, double amount ) {
		double fromOldBalance = 0;
		double toOldBalance = 0;
		double fromNewBalance = 0;
		double toNewBalance = 0;

		if(Double.compare(amount, 2000) > 0) {
			System.out.println("Transfer amount is too large");
			return "1 " + fromNewBalance + " " + toNewBalance; 
		}

		boolean check = isOwner(from);
		if(check == false) {
			System.out.println("Customer initiating transfer is not an owner of one or more of the accounts involved.");
			return "1 " + fromNewBalance + " " + toNewBalance; 
		}

		check = isOwner(to);
		if(check == false) {
			System.out.println("Customer initiating transfer is not an owner of one or more of the accounts involved.");
			return "1 " + fromNewBalance + " " + toNewBalance; 
		}

		boolean checkType = isCheckingOrSavings(from);
		boolean checkType2 = isCheckingOrSavings(to);
		
		if (checkType == false || checkType2 == false) {
			System.out.println("The involved accounts must be checking/savings accounts.");
			return "1 " + fromNewBalance + " " + toNewBalance; 
		}

		fromOldBalance = getBalance(from);
		toOldBalance = getBalance(to);

		if(Double.compare(fromOldBalance, -1.00) == 0 || Double.compare(toOldBalance, -1.00) == 0) {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		fromNewBalance = fromOldBalance - amount;
		toNewBalance = toOldBalance + amount;

		if(Double.compare(fromNewBalance, 0.00) >= 0 && Double.compare(toNewBalance, 0.00) >= 0) {
			String isValid = createTransaction("transfer", amount, 0.00, 0, 0.00, to, from, toNewBalance, fromNewBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + fromNewBalance + " " + toNewBalance;
			}
		} else {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		return "0 "+ fromNewBalance + " " + toNewBalance;
	}

	public String collect(String pocketId, String linkedId, double amount) {
		double fee = amount * 0.03;
		double pocketNewBalance = 0.00;
		double linkedNewBalance = 0.00;

		boolean checkType = checkType(pocketId, "pocket");
		boolean checkType2 = isCheckingOrSavings(linkedId);
		
		if (checkType == false || checkType2 == false) {
			System.out.println("The involved accounts must be a pocket account and a checking/savings account.");
			return "1 " + pocketNewBalance + " " + linkedNewBalance; 
		}

		double pocketOldBalance = getBalance(pocketId);
		double linkedOldBalance = getBalance(linkedId);

		if(Double.compare(pocketOldBalance, -1.00) == 0 || Double.compare(linkedOldBalance, -1.00) == 0) {
			return "1 " + pocketNewBalance + " " + linkedNewBalance;
		}

		pocketNewBalance = pocketOldBalance - amount;
		linkedNewBalance = linkedOldBalance + amount;

		if(Double.compare(pocketNewBalance, 0.00) >= 0 && Double.compare(linkedNewBalance, 0.00) >= 0) {
			String isValid = createTransaction("collect", amount, 0.00, 0, 0.00, linkedId, pocketId, linkedNewBalance, pocketNewBalance); 
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + pocketNewBalance + " " + linkedNewBalance;
			}
		} else {
			return "1 " + pocketNewBalance + " " + linkedNewBalance;
		}

		String check = chargeFee(pocketId, fee);
		if(check.equals("1")) {
			return "1 " + pocketNewBalance + " " + linkedNewBalance;
		}

		return "0 " + pocketNewBalance + " " + linkedNewBalance;
	}

	public String wire( String from, String to, double amount, String tin )
	{
		double fromOldBalance = 0;
		double toOldBalance = 0;
		double fromNewBalance = 0;
		double toNewBalance = 0;
		double fee = amount * 0.02;

		boolean checkOwner = isOwner(from);
		if(checkOwner == false) {
			System.out.println("Customer initiating wire is not the owner of the account.");
			return "1 " + fromNewBalance + " " + toNewBalance; 
		}

		boolean checkType = isCheckingOrSavings(from);
		boolean checkType2 = isCheckingOrSavings(to);
		
		if (checkType == false || checkType2 == false) {
			System.out.println("The involved accounts must be checking/savings accounts.");
			return "1 " + fromNewBalance + " " + toNewBalance; 
		}

		fromOldBalance = getBalance(from);
		toOldBalance = getBalance(to);

		if(Double.compare(fromOldBalance, -1.00) == 0 || Double.compare(toOldBalance, -1.00) == 0) {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		fromNewBalance = fromOldBalance - amount;
		toNewBalance = toOldBalance + amount;

		if(Double.compare(fromNewBalance, 0.00) >= 0 && Double.compare(toNewBalance, 0.00) >= 0) {
			String isValid = createTransaction("wire", amount, 0.00, 0, 0.00, to, from, toNewBalance, fromNewBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + fromNewBalance + " " + toNewBalance;
			}
		} else {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}
		String check = chargeFee(from, fee);
		if(check.equals("1")) {
			return "1 " + fromNewBalance + " " + toNewBalance;
		}

		return "0 " + fromNewBalance + " " + toNewBalance;
	}

	public String writeCheck(String accountId, double amount) {
		double oldBalance = 0;
		double newBalance = 0;
		boolean shouldClose = false;
		int check_no = 0; // FIND A WAY TO RANDOMIZE THIS AND KEEP UNIQUE

		boolean checkType = checkType(accountId, "student");
		boolean checkType2 = checkType(accountId, "interest");
		
		if (checkType == false && checkType2 == false) {
			System.out.println("The involved accounts must be a checking account.");
			return "1 " + oldBalance + " " + newBalance; 
		}
		
		oldBalance = getBalance(accountId);
		if(Double.compare(oldBalance, -1.00) == 0) {
			return "1 " + oldBalance + " " + newBalance;
		}

		newBalance = oldBalance - amount;
		
		if(Double.compare(newBalance, 0.00) >= 0) {
			String isValid = createTransaction("writeCheck", amount, 0.00, check_no, 0.00, accountId, accountId, newBalance, newBalance);
			if(isValid.equals("1")) {
				System.out.println("Transaction failed.");
				return "1 " + oldBalance + " " + newBalance;
			}
		} else {
			return "1 " + oldBalance + " " + newBalance;
		}

		return "0 " + oldBalance + " " + newBalance;
	}

	public double getNumDays() {
		String bankDate = getDateInfo();
		String getMonth = "SELECT EXTRACT( MONTH FROM TO_DATE(?, \'YYYY-MM-DD\') ) MONTH FROM DUAL";
		int month = 0;
		try(PreparedStatement monthStatement = _connection.prepareStatement(getMonth)) {
			monthStatement.setString(1, bankDate);
			ResultSet rs = monthStatement.executeQuery();
			while(rs.next()) {
				month = rs.getInt(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return -1;
		}

		if (month == 2) {
			return 28.0;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30.0;
		} else {
			return 31.0;
		}
	}

	public double calculateAverage(String accountId, double initBalance, double numDays) {
		// repeat process for tids_from and when tids_to = tids_from? then add all together?
		// also need to subtract last amount of days (from end of month to whenever last transaction occurs) and use that to calculate avg
		ArrayList<String> tids_to = new ArrayList<String>();

		String getTidsTo = "SELECT I.tid FROM Involves I WHERE I.aid_to = ? AND I.aid_to <> I.aid_from";
		try(PreparedStatement tidToStatement = _connection.prepareStatement(getTidsTo)) {
			tidToStatement.setString(1, accountId);
			ResultSet rs = tidToStatement.executeQuery();
			while(rs.next()) {
				tids_to.add(rs.getString("tid"));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return -1.0;
		}

		System.out.println(tids_to);

		ArrayList<String> tids_from = new ArrayList<String>();

		String getTidsFrom = "SELECT I.tid FROM Involves I WHERE I.aid_from = ? AND I.aid_to <> I.aid_from";
		try(PreparedStatement tidFromStatement = _connection.prepareStatement(getTidsFrom)) {
			tidFromStatement.setString(1, accountId);
			ResultSet rs2 = tidFromStatement.executeQuery();
			while(rs2.next()) {
				tids_from.add(rs2.getString("tid"));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return -1.0;
		}

		System.out.println(tids_from);

		ArrayList<String> tids = new ArrayList<String>();

		String getTids = "SELECT I.tid FROM Involves I WHERE I.aid_to = ? AND I.aid_to = I.aid_from";
		try(PreparedStatement tidStatement = _connection.prepareStatement(getTids)) {
			tidStatement.setString(1, accountId);
			ResultSet rs3 = tidStatement.executeQuery();
			while(rs3.next()) {
				tids.add(rs3.getString("tid"));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return -1.0;
		}

		System.out.println(tids);

		double avg_daily_balance = 0;
		double tempBalance = initBalance;

		for(int i = 0; i < tids_to.size(); i++) {
			int day = 0;
			String getDay = "SELECT T.d FROM Transactions T WHERE T.tid = ?";
			try(PreparedStatement dayStatement = _connection.prepareStatement(getDay)) {
				dayStatement.setString(1, tids_to.get(i));
				ResultSet rs_to = dayStatement.executeQuery();
				while(rs_to.next()) {
					Date d = rs_to.getDate("d");
					Calendar cal = Calendar.getInstance();
					cal.setTime(d);
					day = cal.get(Calendar.DAY_OF_MONTH);
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return -1.0;
			}

			double amount = 0;
			String getAmount = "SELECT T.amount FROM Transactions T WHERE T.tid = ?";
			try(PreparedStatement amtStatement = _connection.prepareStatement(getAmount)) {
				amtStatement.setString(1, tids_to.get(i));
				ResultSet rs_to2 = amtStatement.executeQuery();
				while(rs_to2.next()) {
					amount = rs_to2.getDouble("amount");
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return -1.0;
			}
			tempBalance = tempBalance + amount;

			double temp = (tempBalance*((day-1)/numDays));
			System.out.println(Double.toString(temp));
			avg_daily_balance = avg_daily_balance + (tempBalance*((day-1)/numDays));
		}
		System.out.println(avg_daily_balance);

		for(int i = 0; i < tids_from.size(); i++) {
			int day = 0;
			String getDay = "SELECT T.d FROM Transactions T WHERE T.tid = ?";
			try(PreparedStatement dayStatement = _connection.prepareStatement(getDay)) {
				dayStatement.setString(1, tids_from.get(i));
				ResultSet rs_from = dayStatement.executeQuery();
				while(rs_from.next()) {
					Date d = rs_from.getDate("d");
					Calendar cal = Calendar.getInstance();
					cal.setTime(d);
					day = cal.get(Calendar.DAY_OF_MONTH);
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return -1.0;
			}

			double amount = 0;
			String getAmount = "SELECT T.amount FROM Transactions T WHERE T.tid = ?";
			try(PreparedStatement amtStatement = _connection.prepareStatement(getAmount)) {
				amtStatement.setString(1, tids_from.get(i));
				ResultSet rs_from2 = amtStatement.executeQuery();
				while(rs_from2.next()) {
					amount = rs_from2.getDouble("amount");
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return -1.0;
			}
			tempBalance = tempBalance - amount;

			double temp = (tempBalance*((day-1)/numDays));
			System.out.println(Double.toString(temp));
			avg_daily_balance = avg_daily_balance + (tempBalance*((day-1)/numDays));
		}

		System.out.println(avg_daily_balance);

		for(int i = 0; i < tids.size(); i++) {
			int day = 0;
			String getDay = "SELECT T.d FROM Transactions T WHERE T.tid = ?";
			try(PreparedStatement dayStatement = _connection.prepareStatement(getDay)) {
				dayStatement.setString(1, tids.get(i));
				ResultSet rs_tid = dayStatement.executeQuery();
				while(rs_tid.next()) {
					Date d = rs_tid.getDate("d");
					Calendar cal = Calendar.getInstance();
					cal.setTime(d);
					day = cal.get(Calendar.DAY_OF_MONTH);
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return -1.0;
			}

			double amount = 0;
			String getAmount = "SELECT T.amount FROM Transactions T WHERE T.tid = ?";
			try(PreparedStatement amtStatement = _connection.prepareStatement(getAmount)) {
				amtStatement.setString(1, tids.get(i));
				ResultSet rs_tid2 = amtStatement.executeQuery();
				while(rs_tid2.next()) {
					amount = rs_tid2.getDouble("amount");
				}
			} catch ( SQLException e )
			{
				System.err.println( e.getMessage() );
				return -1.0;
			}
			tempBalance = tempBalance - amount;

			double temp = (tempBalance*((day-1)/numDays));
			System.out.println(Double.toString(temp));
			avg_daily_balance = avg_daily_balance + (tempBalance*((day-1)/numDays));
		}

		System.out.println(avg_daily_balance);

		return avg_daily_balance;
	}

	public String accrueInterest(String accountId) {
		// NEED TO CHECK THAT ACCOUNT ISN'T CLOSED 
		// can only happen at end of month
		// need to use involves to retrieve transaction IDs where the account is involved
		// then for each transaction ID get the date and amount
		// then for the account get initial balance and do the math
		// need to get number of days for the month
		// ALSO NEED TO KNOW WHETHER YOU'RE ADDING OR SUBTRACTING????
			// NEED TO DISTINGUISH IF TO OR FROM FOR THE MULTIPLE ACCOUNT ONES???
			// Find TIDs whenever the account is a to (and to != from) = +
			// Find TIDs whenver the account is a from (and to != from) = -
		boolean checkType = isCheckingOrSavings(accountId);
		
		if (checkType == false) {
			System.out.println("The involved accounts must be a savings/checking account.");
			//return "1 " + oldBalance + " " + newBalance; 
		}

		double numDays = getNumDays();

		double initBalance = 0;

		String getInitial = "SELECT A.init_balance FROM Accounts A WHERE A.aid = ?";
		try(PreparedStatement initStatement = _connection.prepareStatement(getInitial)) {
			initStatement.setString(1, accountId);
			ResultSet rs_init = initStatement.executeQuery();
			while(rs_init.next()) {
				initBalance = rs_init.getDouble(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		double avg_daily_balance = calculateAverage(accountId, initBalance, numDays);

		return "0";
	}

	public String generateMonthly() {
		// Pull month from BankDate. Use MONTH() to make a query to pull all transactions with this month for the given customer
		// query for names and addresses of all customers in Owners with that account id.
		// Pull initial balance and current balance from accounts involved. 
		// Find all accounts where customer is primary owner (from Primary) and add up current balances - if > 100000 print warning
		return "0";
	}

	public String generateDTER() {
		return "0";
	}

	public String generateCustomerReport(String tin) {
		String findAccounts = "SELECT O.aid FROM Owners O WHERE O.tax_id = ?";
		ArrayList<String> accounts = new ArrayList<String>();
		//String accounts = "";
		try(PreparedStatement accStatement = _connection.prepareStatement(findAccounts)) {
			accStatement.setString(1, tin);
			ResultSet rs = accStatement.executeQuery();

			while(rs.next()) {
				accounts.add(rs.getString("aid"));
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		System.out.println("Accounts for Customer with Tax ID "+tin);
		System.out.println("----------------------");
		
		String findAccountInfo = "SELECT * FROM Accounts WHERE aid IN (";
		try(Statement infoStatement = _connection.createStatement()) {
			for(int i = 0; i < accounts.size(); i++) {
				findAccountInfo = findAccountInfo + "\'" + accounts.get(i) + "\',";
			}
			if(findAccountInfo.length() > 1) {
                findAccountInfo = findAccountInfo.substring(0, findAccountInfo.length()-1);
			}
			findAccountInfo = findAccountInfo + ")";
			ResultSet rs2 = infoStatement.executeQuery(findAccountInfo);

			while(rs2.next()) {
				if(rs2.getString("active").equals("1")) {
					System.out.println(rs2.getString("aid")+ "\t" + "active");
				} else {
					System.out.println(rs2.getString("aid")+ "\t" + "inactive");
				}
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		return "0";
	}

	public String addInterest() {
		// Select all aids for accounts with active = 1 and put in arraylist. iterate through arraylist and call accrueinterest
		// find a way to note that this has been done for the month
		return "0";
	}

	public String deleteClosed() {
		return "0";
	}

	public String deleteTransactions() {
		return "0";
	}

	public String setNewInterest() {
		return "0";
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public String encrypt(String pin) {
		String encrypted = "";
		for (int i = 0; i < pin.length(); i++) {
			String temp = d.get(Character.toString(pin.charAt(i)));
			encrypted = encrypted + temp;
		}
		return encrypted;
	}

	public boolean verifyPIN(String pin) {
		String encrypted = encrypt(pin);
		String userPin = "";

		String checkPin = "SELECT C.pin FROM Customers C WHERE C.tax_id = ?";
		try(PreparedStatement statement = _connection.prepareStatement(checkPin)) {
			statement.setString(1, this.taxId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				userPin = rs.getString(1);
			}
			if(encrypted.equals(userPin)) {
				return true;
			} else {
				return false;
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			return false;
		}
	}

	public void setPIN(String OldPIN, String NewPIN) {
		String checkPin = "SELECT C.cid FROM Customers C WHERE C.pin = ?"; // NEED TO DISTNGUISH THE CUSTOMER bc some may have same pin
		String tin = "";
		String encryptedOld = encrypt(OldPIN);

		try(PreparedStatement statement = _connection.prepareStatement(checkPin)) {
			statement.setString(1, encryptedOld);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				tin = rs.getString(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error getting customer with old pin number.");
		}

		String encryptedNew = encrypt(NewPIN);
		
		String setNewPin = "UPDATE Customers SET pin = ? WHERE tax_id = ?";
		try(PreparedStatement statement2 = _connection.prepareStatement(setNewPin)) {
			statement2.setString(1, encryptedNew);
			statement2.setString(2, tin);
			ResultSet rs2 = statement2.executeQuery();
			while(rs2.next()) {
				tin = rs2.getString(1);
			}
		} catch ( SQLException e )
		{
			System.err.println( e.getMessage() );
			System.out.println("Error setting new pin number.");
		}
	}

	public void setUpEncryption() {
		d = new LinkedHashMap<String, String>(10);
		d.put("1", "a");
		d.put("2", "b");
		d.put("3", "c");
		d.put("4", "d");
		d.put("5", "e");
		d.put("6", "f");
		d.put("7", "g");
		d.put("8", "h");
		d.put("9", "i");
		d.put("0", "j");
	}
}