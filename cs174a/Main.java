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
			System.out.println("-----------------\n0: Reset DB\n1: Run App\n2: Example Test\n3: Accrue Interest Test\n4: Example Test (Long)\n-----------------");
			String input = s.nextLine();
			if(input.equals("0")){
				app.dropTables();
				app.createTables();
			} else if (input.equals("1")){
				app.setUpUI();
			} else if (input.equals("2")){
				r = app.setDate(2019, 11, 30);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "12345", 1000, "123456789", "John Smith", "100 Main St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("54321", "12345", 100, "123456789");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "13579", 1000, "123456789", "John Smith", "100 Main St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("97531", "13579", 50, "123456789");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "55555", 1000, "555555555", "John Smith", "555 Main St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("55557", "55555", 30, "555555555");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "55556", 1000, "555555555", "John Smith", "100 Main St" );
				System.out.println( r );

				System.out.println("----------------------------");
			} else if( input.equals("3") ) {
				r = app.setDate(2019, 11, 5);
				System.out.println( r );

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "17431", 1000, "344151573", "Joe Pepsi", "3210 State St" );
				System.out.println( r );

				app.setTaxId("344151573");

				r = app.createCheckingSavingsAccount(AccountType.SAVINGS, "12345", 5000, "122219876", "Elizabeth Sailor", "4321 State St");
				System.out.println( r );

				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "41725", 15000, "201674933", "George Brush", "5346 Foothill Av" );
				System.out.println( r );

				r = app.createOwners("344151573", "41725");
				System.out.println( r );

				r = app.setDate(2019, 11, 10);
				System.out.println( r );

				r = app.transfer("41725", "17431", 25);
				System.out.println( r );

				r = app.withdrawal("17431", 100);
				System.out.println( r );

				r = app.setDate(2019, 11, 15);
				System.out.println( r );

				r = app.writeCheck("17431", 200);
				System.out.println( r );

				r = app.setDate(2019, 11, 25);
				System.out.println( r );

				app.setTaxId("344151573");

				r = app.wire("17431", "12345", 50);
				System.out.println( r );

				r = app.deposit("17431", 300);
				System.out.println( r );

				r = app.setDate(2019, 11, 30);
				System.out.println( r );

				r = app.addInterest();
				System.out.println( r );

				// should not go through
				r = app.addInterest();
				System.out.println( r );
			} else {
				//app.exampleAccessToDB();                // Example on how to connect to the DB.

				// Example tests.  We'll overwrite your Main.main() function with our final tests.

				// Another example test.
				r = app.setDate(2019, 11, 30);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "17431", 1000, "344151573", "Joe Pepsi", "3210 State St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "54321", 21000, "212431965", "Hurryson Ford", "678 State St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "12121", 1200, "207843218", "David Copperfill", "1357 State St" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "41725", 15000, "201674933", "George Brush", "5346 Foothill Av" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "76543", 8456, "212116070", "Li Kung", "2 People's Rd Beijing" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "93156", 2000000, "209378521", "Kelvin Costner", "Santa Cruz #3579" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "43942", 1289, "361721022", "Alfred Hitchcock", "6667 El Colegio #40" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "29107", 34000, "209378521", "Kelvin Costner", "Santa Cruz #3579" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "19023", 2300, "412231856", "Cindy Laugher", "7000 Hollister" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCustomer("43942", "400651982", "Pit Wilson", "911 State St");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("60413", "43942", 20, "400651982");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "32156", 1000, "188212217", "Michael Jordon", "3852 Court Rd" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createOwners("212116070", "29107");
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.createOwners("212116070", "60413");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.listClosedAccounts();
				System.out.println( r );

				System.out.println("----------------------------");
				
				r = app.showBalance("19023");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createPocketAccount("43947", "29107", 30, "212116070");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "34567", 20.75, "123456789", "Tester McTesting", "6565 Segovia" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount( AccountType.SAVINGS, "23456", 1120.75, "123456789", "Tester McTesting", "6565 Segovia" );
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCustomer("54321", "122219876", "Elizabeth Sailor", "4321 State St");
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.createCustomer("54321", "122219876", "Elizabeth Sailor", "4321 State St");
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.createCustomer("00000", "987654321", "Just Testing", "4321 Testable St");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.createCheckingSavingsAccount(AccountType.SAVINGS, "12345", 5000, "122219876", "Elizabeth Sailor", "4321 State St");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.withdrawal("54321", 3000);
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.purchase("54321", 3000);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.purchase("43947", 10);
				System.out.println( r );

				System.out.println("----------------------------");

				app.setTaxId("212116070");

				r = app.wire("76543", "12345", 7309.80);
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.wire("76543", "29107", 5);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.showBalance("76543");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.transfer("76543", "29107", 999.99);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.payFriend("43947", "60413", 5);
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.payFriend("43947", "43947", 5);
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.payFriend("43942", "17431", 289);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.listClosedAccounts();
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.writeCheck("93156", 1000000);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.showBalance("93156");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.showBalance("29107");
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail, insufficient funds
				r = app.wire("29107", "93156", 34964.99);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.setDate(10, 21, 5);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.generateCustomerReport("212116070");
				System.out.println( r );

				System.out.println("----------------------------");

				boolean check = app.verifyPIN("1717");
				System.out.println( check );

				System.out.println("----------------------------");

				double num = app.getNumDays();
				System.out.println(Double.toString(num));

				System.out.println("----------------------------");

				boolean end = app.checkEndOfMonth();
				System.out.println(end);

				System.out.println("----------------------------");

				r = app.setNewInterest(2.5, "student");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.withdrawal("19023", 2300);
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.withdrawal("29107", 10000000);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.writeCheck("17431", 10);
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.generateMonthly("212116070");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.generateMonthly("209378521");
				System.out.println( r );

				System.out.println("----------------------------");

				r = app.generateDTER();
				System.out.println( r );

				System.out.println("----------------------------");

				//r = app.deleteClosed();
				//System.out.println( r );

				System.out.println("----------------------------");

				//r = app.deleteTransactions();
				//System.out.println( r );

				System.out.println("----------------------------");

				r = app.generateCustomerReport("212116070");
				System.out.println( r );

				System.out.println("----------------------------");

				// should fail
				r = app.generateCustomerReport("000000000");
				System.out.println( r );

				System.out.println("----------------------------");

				app.setPIN("1717", "9999");

				System.out.println("----------------------------");

				r = app.setDate(2019, 12, 31);
				System.out.println( r );

				//double num2 = app.calculateAverage("67890", 1500.75, 30.0);
				//System.out.println(Double.toString(num2));
			}
			s.close();
		}
	}
	//!### FINALIZAMOS
}
