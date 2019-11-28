package cs174a;

import java.util.Scanner;

import cs174a.App;

public class ATMApp {
    private App app;

    ATMApp(App app) {
        this.app = app;
    }

    public void displayUI() {
        Scanner s = new Scanner(System.in);
        // CHECK IF CUSTOMER EXISTS YET OR NOT (EITHER HERE OR IN APP) OR ELSE NO CUSTOMER WILL BE FOUND
        boolean customerExists = false;
        while (customerExists == false) {
            System.out.println("Welcome to the ATM App Interface. Please enter your tax ID: ");
            String taxId = s.nextLine();
            if ((app.checkCustomerExists(taxId)).equals("0")) {
                System.out.println("No customer exists with the given tax ID. Try again.");
            } else {
                customerExists = true;
                app.setTaxId(taxId);
            }
        }
        System.out.println("Please enter your PIN: ");
        String pin = s.nextLine();
        
        // implement encryption before entering the pin
        boolean pinVerified = app.verifyPIN(pin);

        if(pinVerified == true) {
            System.out.println("Transaction Options:");
            System.out.println("0: Deposit");
            System.out.println("1: Top-Up");
            System.out.println("2: Make a Withdrawal");
            System.out.println("3: Purchase");
            System.out.println("4: Transfer");
            System.out.println("5: Collect");
            System.out.println("6: Wire");
            System.out.println("7: Pay Friend");
            System.out.println("8: Set PIN");
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
                case "8":
                    System.out.println("Setting a new PIN");
                    System.out.print("Enter your old PIN: ");
                    String oldPin = s.nextLine();
                    System.out.print("Enter your new PIN: ");
                    String newPin = s.nextLine();
                    app.setPIN(oldPin, newPin);
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