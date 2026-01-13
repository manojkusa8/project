import java.time.LocalDateTime;
import java.util.*;

enum AccountType { SAVINGS, CURRENT }

class Customer {
    private final long id;
    private final String name;
    private final String email;

    public Customer(long id, String name, String email) {
        this.id = id; this.name = name; this.email = email;
    }
    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    @Override
    public String toString() { return String.format("Customer[id=%d, name=%s, email=%s]", id, name, email); }
}

class Account {
    private final long id;
    private final Customer owner;
    private final AccountType type;
    private double balance;
    private boolean active = true;

    public Account(long id, Customer owner, AccountType type, double initial) {
        this.id = id; this.owner = owner; this.type = type; this.balance = initial;
    }
    public long getId() { return id; }
    public Customer getOwner() { return owner; }
    public AccountType getType() { return type; }
    public double getBalance() { return balance; }
    public boolean isActive() { return active; }

    public void deposit(double amt) {
        if (amt <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance += amt;
    }

    public void withdraw(double amt) {
        if (amt <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amt > balance) throw new IllegalStateException("Insufficient funds");
        balance -= amt;
    }

    public void close() {
        if (balance != 0) throw new IllegalStateException("Balance must be zero to close account");
        active = false;
    }

    @Override
    public String toString() {
        return String.format("Account[id=%d, owner=%s, type=%s, balance=%.2f, active=%s]", id, owner.getName(), type, balance, active);
    }
}

class Transaction {
    private final long id;
    private final long accountId;
    private final String type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String note;

    public Transaction(long id, long accountId, String type, double amount, String note) {
        this.id = id; this.accountId = accountId; this.type = type; this.amount = amount; this.timestamp = LocalDateTime.now(); this.note = note;
    }

    public long getId() { return id; }
    public long getAccountId() { return accountId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        return String.format("Transaction[id=%d, accId=%d, type=%s, amount=%.2f, time=%s, note=%s]", id, accountId, type, amount, timestamp, note);
    }
}

class BankManager {
    private final Map<Long, Customer> customers = new HashMap<>();
    private final Map<Long, Account> accounts = new HashMap<>();
    private final Map<Long, List<Transaction>> transactions = new HashMap<>();

    private long custSeq = 1, accSeq = 1001, txSeq = 1;

    public Customer createCustomer(String name, String email) {
        Customer c = new Customer(custSeq++, name, email);
        customers.put(c.getId(), c);
        return c;
    }

    public Account openAccount(long customerId, AccountType type, double initial) {
        Customer c = customers.get(customerId);
        if (c == null) throw new IllegalArgumentException("Customer not found");
        Account a = new Account(accSeq++, c, type, 0);
        accounts.put(a.getId(), a);
        transactions.put(a.getId(), new ArrayList<>());
        if (initial > 0) {
            a.deposit(initial);
            addTransaction(a.getId(), "DEPOSIT", initial, "Initial deposit");
        }
        return a;
    }

    public void deposit(long accountId, double amount) {
        Account a = accounts.get(accountId);
        if (a == null || !a.isActive()) throw new IllegalArgumentException("Account not found or inactive");
        a.deposit(amount);
        addTransaction(accountId, "DEPOSIT", amount, "Deposit");
    }

    public void withdraw(long accountId, double amount) {
        Account a = accounts.get(accountId);
        if (a == null || !a.isActive()) throw new IllegalArgumentException("Account not found or inactive");
        a.withdraw(amount);
        addTransaction(accountId, "WITHDRAW", amount, "Withdrawal");
    }

    public void transfer(long fromAccId, long toAccId, double amount) {
        if (fromAccId == toAccId) throw new IllegalArgumentException("Cannot transfer to same account");
        Account from = accounts.get(fromAccId);
        Account to = accounts.get(toAccId);
        if (from == null || to == null) throw new IllegalArgumentException("Account not found");
        if (!from.isActive() || !to.isActive()) throw new IllegalArgumentException("One of accounts is inactive");
        from.withdraw(amount);
        to.deposit(amount);
        addTransaction(fromAccId, "TRANSFER_OUT", amount, "Transfer to " + toAccId);
        addTransaction(toAccId, "TRANSFER_IN", amount, "Transfer from " + fromAccId);
    }

    public double getBalance(long accountId) {
        Account a = accounts.get(accountId);
        if (a == null) throw new IllegalArgumentException("Account not found");
        return a.getBalance();
    }

    public Account getAccountDetails(long accountId) {
        Account a = accounts.get(accountId);
        if (a == null) throw new IllegalArgumentException("Account not found");
        return a;
    }

    public List<Transaction> getTransactions(long accountId) {
        return transactions.getOrDefault(accountId, Collections.emptyList());
    }

    public boolean closeAccount(long accountId) {
        Account a = accounts.get(accountId);
        if (a == null) return false;
        if (a.getBalance() != 0) return false;
        a.close();
        return true;
    }

    private void addTransaction(long accId, String type, double amt, String note) {
        Transaction tx = new Transaction(txSeq++, accId, type, amt, note);
        transactions.get(accId).add(tx);
    }

    public Collection<Customer> getAllCustomers() { return customers.values(); }
}
