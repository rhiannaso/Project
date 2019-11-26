import java.util.Scanner;

import cs174a.App;

public class ATMApp {
    private App app;

    ATMApp(App app) {
        this.app = app;
    }

    public void displayUI() {
        Scanner s = new Scanner(System.in);
        System.out.println("Welcome to the ATM App Interface. Please enter your pin: ");
        String pin = s.nextLine();
        
        // implement encryption before entering the pin
        boolean pinVerified = app.verifyPIN(pin);

        if(pinVerified == true) {
            System.out.println("Transaction Options:\n");
            System.out.println("0: Deposit\n");
            System.out.println("1: Top-Up\n");
            System.out.println("2: Make a Withdrawal\n");
            System.out.println("3: Purchase\n");
            System.out.println("4: Transfer\n");
            System.out.println("5: Collect\n");
            System.out.println("6: Wire\n");
            System.out.println("7: Pay Friend\n");
            System.out.println("Enter the number associated with the transaction type you'd like to make: ");
            String choice = s.nextLine();

            String aid = "";
            double amount = 0;
            switch(choice) {
                case "0":
                    System.out.println("Enter your account ID: ");
                    aid = s.nextLine();
                    System.out.println("Enter the amount you'd like to deposit: ");
                    amount = Double.parseDouble(s.nextLine());
                    app.deposit(aid, amount);
                    break;
                case "1":

                    break;
                case "2":

                    break;  
                case "3":

                    break;
                case "4":

                    break;
                case "5":

                    break;
                case "6":

                    break;
                case "7":

                    break;
                default:
                    System.out.println("Not a valid transaction number.");
                    break;
            }

        } else {
            System.out.println("Incorrect PIN.");
        }

        s.close();
    }
}