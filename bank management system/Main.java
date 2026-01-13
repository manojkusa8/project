import java.util.*;
import java.time.LocalDateTime;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final BankManager manager = new BankManager();

    public static void main(String[] args) {
        seedDemoData();
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose: ");
            try {
                switch (choice) {
                    case 1 -> createCustomer();
                    case 2 -> openAccount();
                    case 3 -> deposit();
                    case 4 -> withdraw();
                    case 5 -> transfer();
                    case 6 -> accountSummary();
                    case 7 -> transactionHistory();
                    case 8 -> closeAccount();
                    case 9 -> listCustomers();
                    case 0 -> { running = false; System.out.println("Goodbye!"); }
                    default -> System.out.println("Invalid option");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private static void seedDemoData() {
        Customer c1 = manager.createCustomer("Rahul Sharma", "rahul@example.com");
        Customer c2 = manager.createCustomer("Neha Gupta", "neha@example.com");
        manager.openAccount(c1.getId(), AccountType.SAVINGS, 5000);
        manager.openAccount(c2.getId(), AccountType.CURRENT, 10000);
    }

    private static void printMenu() {
        System.out.println("=== Bank Management System (OOP Demo) ===");
        System.out.println("1. Create Customer");
        System.out.println("2. Open Account");
        System.out.println("3. Deposit");
        System.out.println("4. Withdraw");
        System.out.println("5. Transfer");
        System.out.println("6. Account Summary");
        System.out.println("7. Transaction History");
        System.out.println("8. Close Account");
        System.out.println("9. List Customers");
        System.out.println("0. Exit");
    }

    private static void createCustomer() {
        String name = readString("Customer name: ");
        String email = readString("Email: ");
        Customer c = manager.createCustomer(name, email);
        System.out.println("Created: " + c);
    }

    private static void openAccount() {
        listCustomers();
        long cid = readLong("Customer id: ");
        AccountType type = AccountType.valueOf(readString("Account type (SAVINGS/CURRENT): ").toUpperCase());
        double initial = readDouble("Initial deposit: ");
        Account a = manager.openAccount(cid, type, initial);
        System.out.println("Opened: " + a);
    }

    private static void deposit() {
        long accId = readLong("Account id: ");
        double amt = readDouble("Amount to deposit: ");
        manager.deposit(accId, amt);
        System.out.println("Deposit successful. New balance: " + manager.getBalance(accId));
    }

    private static void withdraw() {
        long accId = readLong("Account id: ");
        double amt = readDouble("Amount to withdraw: ");
        manager.withdraw(accId, amt);
        System.out.println("Withdrawal successful. New balance: " + manager.getBalance(accId));
    }

    private static void transfer() {
        long from = readLong("From account id: ");
        long to = readLong("To account id: ");
        double amt = readDouble("Amount to transfer: ");
        manager.transfer(from, to, amt);
        System.out.println("Transfer successful.");
        System.out.println("From balance: " + manager.getBalance(from));
        System.out.println("To balance: " + manager.getBalance(to));
    }

    private static void accountSummary() {
        long accId = readLong("Account id: ");
        System.out.println(manager.getAccountDetails(accId));
    }

    private static void transactionHistory() {
        long accId = readLong("Account id: ");
        List<Transaction> txs = manager.getTransactions(accId);
        if (txs.isEmpty()) System.out.println("No transactions found.");
        else txs.forEach(System.out::println);
    }

    private static void closeAccount() {
        long accId = readLong("Account id to close: ");
        boolean ok = manager.closeAccount(accId);
        System.out.println(ok ? "Account closed." : "Account not found or balance not zero.");
    }

    private static void listCustomers() { manager.getAllCustomers().forEach(System.out::println); }

    // helpers
    private static int readInt(String prompt) { System.out.print(prompt); return Integer.parseInt(sc.nextLine().trim()); }
    private static long readLong(String prompt) { System.out.print(prompt); return Long.parseLong(sc.nextLine().trim()); }
    private static double readDouble(String prompt) { System.out.print(prompt); return Double.parseDouble(sc.nextLine().trim()); }
    private static String readString(String prompt) { System.out.print(prompt); return sc.nextLine().trim(); }
}
