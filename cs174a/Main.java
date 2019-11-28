package cs174a;                         // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// DO NOT REMOVE THIS IMPORT.
import cs174a.Testable.*;
import java.util.Scanner;
/**
 * This is the class that launches your application.
 * DO NOT CHANGE ITS NAME.
 * DO NOT MOVE TO ANY OTHER (SUB)PACKAGE.
 * There's only one "main" method, it should be defined within this Main class, and its signature should not be changed.
 */
public class Main
{
	/**
	 * Program entry point.
	 * DO NOT CHANGE ITS NAME.
	 * DON'T CHANGE THE //!### TAGS EITHER.  If you delete them your program won't run our tests.
	 * No other function should be enclosed by the //!### tags.
	 */
	//!### COMENZAMOS
	public static void main( String[] args )
	{
		App app = new App();                        // We need the default constructor of your App implementation.  Make sure such
													// constructor exists.
		String r = app.initializeSystem();          // We'll always call this function before testing your system.

		if( r.equals( "0" ) )
		{
			Scanner s = new Scanner(System.in);
			System.out.println("-----------------\n0: Reset DB\n1: Run App\n2: Example Test\n-----------------");
			String input = s.nextLine();
			if(input.equals("0")){
				app.dropTables();
				app.createTables();
			} else if (input.equals("1")){
			 	app.setUpUI();
			} else {
				//app.exampleAccessToDB();                // Example on how to connect to the DB.

				// Example tests.  We'll overwrite your Main.main() function with our final tests.

				// Another example test.
				r = app.setDate(2019, 11, 27);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "12345", 1234.56, "987654321", "Im YoungMing", "Known" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "99999", 1111.11, "999999999", "John Smith", "123 Main" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.listClosedAccounts();
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("54321", "12345", 1229.552, "987654321");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "67890", 1500.75, "123456789", "Tester McTesting", "6565 Segovia" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("09876", "67890", 100.00, "123456789");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.showBalance("67890");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "34567", 20.75, "123456789", "Tester McTesting", "6565 Segovia" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "23456", 1120.75, "123456789", "Tester McTesting", "6565 Segovia" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("99999", "67890", 1000.55, "987654321");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.payFriend("67890", "23456", 500.25);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.listClosedAccounts();
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.setDate(10, 21, 5);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.generateCustomerReport("987654321");
				System.out.println( r );

				System.out.println("----------------------------");

				app.setTaxId("987654321");

				boolean check = app.verifyPIN("1717");
				System.out.println( check );

				System.out.println("----------------------------");

				double num = app.getNumDays();
				System.out.println(Double.toString(num));

				System.out.println("----------------------------");

				//double num2 = app.calculateAverage("67890", 1500.75, 30.0);
				//System.out.println(Double.toString(num2));
			}
			s.close();
		}
	}
	//!### FINALIZAMOS
}
