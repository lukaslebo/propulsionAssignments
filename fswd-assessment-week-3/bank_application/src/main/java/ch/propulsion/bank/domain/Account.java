package ch.propulsion.bank.domain;

import java.util.concurrent.atomic.AtomicLong;

public class Account {
	
	// Fields 
	private String accName;
	private Long accID;
	private Long globalID;
	private Long balance;
	private static AtomicLong idGenerator = new AtomicLong(0L);
	
	// Constructor
	public Account(String accName, Long accID) {
		this.accName = accName;
		this.accID = accID;
		this.globalID = idGenerator.incrementAndGet();
		this.balance = 0L;
	}
	
	// Methods
	public void renameAcc(String accName) {
		this.accName = accName;
	}
	public boolean credit(Long credit) {
		if (credit > 0L) {
			this.balance += credit;	
			return true;
		}
		return false;
	}
	public boolean debit(Long debit) {
		if (debit > 0L && this.balance-debit >= 0L) {
			this.balance -= debit;
			return true;
		}
		return false;
	}
	
	/*
	 * GETTER & SETTER
	 */
	
	public String getAccName() {
		return this.accName;
	}
	public Long getBalance() {
		return this.balance;
	}
	public Long getAccID() {
		return this.accID;
	}
	public Long getGlobalID() {
		return this.globalID;
	}
	
}
