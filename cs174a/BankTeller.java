package cs174a;

import java.util.Scanner;

public class BankTeller {
    private App app;

    BankTeller(App app) {
        this.app = app;
    }

    public void displayUI() {
        Scanner s = new Scanner(System.in);
        System.out.println("Welcome to the Bank Teller Interface, through which customer accounts are managed. These are your options for action: \n");
        
        System.out.println("0: Enter Check Transaction");
        System.out.println("1: Generate Monthly Statement");
        System.out.println("2: List Closed Accounts");
        System.out.println("3: Generate Government Drug and Tax Evasion Report");
        System.out.println("4: Generate Customer Report");
        System.out.println("5: Add Interest");
        System.out.println("6: Create Account");
        System.out.println("7: Delete Closed Accounts and Customers");
        System.out.println("8: Delete Transactions");
        System.out.println("Enter the number associated with the action you'd like to take: ");
        String choice = s.nextLine();

        switch(choice) {
            case "0":
                
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

                break;
            default:
                System.out.println("Not a valid transaction number.");
                break;
        }

        s.close();
    }
}