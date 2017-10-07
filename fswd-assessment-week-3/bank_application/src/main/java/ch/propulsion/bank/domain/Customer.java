package ch.propulsion.bank.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Customer {
	
	// Fields
	private String firstName;
	private String lastName;
	private Long ID;
	private Long totalBalance;
	private Map<Long, Account> accounts;
	private static AtomicLong idGenerator = new AtomicLong(0L);
	private AtomicLong accIdGenererator = new AtomicLong(0L); 
	
	// Constructor
	public Customer(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.ID = idGenerator.getAndIncrement();
		this.totalBalance = 0L;
		this.accounts = new HashMap<>();
		Long accID = accIdGenererator.getAndIncrement();
		this.accounts.put(accID, new Account("debitAcc",accID));
	}
	
	// Methods
	public boolean credit(Long accID, Long credit) {
		boolean check = accounts.get(accID).credit(credit);
		if (check) {
			this.totalBalance += credit;
		}
		return check;
	}
	public boolean debit(Long accID, Long debit) {
		boolean check = accounts.get(accID).debit(debit);
		if (check) {
			this.totalBalance -= debit;
		}
		return check;
	}
	public List<Account> getAccounts() {
		return new ArrayList<>(accounts.values());
	}
	public Account getAccountByID(Long accID) {
		return accounts.get(accID);
	}
	public void addNewAcc(String accName) {
		Long accID = accIdGenererator.getAndIncrement();
		this.accounts.put(accID, new Account("accName",accID));
	}
	public boolean deleteAcc(Long accID) {
		if (this.accounts.get(accID).getBalance() == 0L && this.accounts.size() > 1) {
			this.accounts.remove(accID);
			return true;
		}
		return false;
	}
	public boolean internalTransfer(Long accIDfrom, Long accIDto, Long amount) {
		boolean check = accounts.get(accIDfrom).debit(amount);
		if (check) {
			return accounts.get(accIDto).credit(amount);
		}
		return false;
	}

	
	/*
	 * GETTER & SETTER
	 */
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Long getID() {
		return this.ID;
	}
	public Long getTotalBalance() {
		return this.totalBalance;
	}
	
}
