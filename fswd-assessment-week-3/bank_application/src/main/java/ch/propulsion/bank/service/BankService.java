package ch.propulsion.bank.service;

import java.util.List;

import ch.propulsion.bank.domain.Customer;
import ch.propulsion.bank.infrastructure.BankDB;

public class BankService {
	
	// Fields
	private BankDB _db_ = new BankDB();
	
	// Methods
	public Customer addNewCustomer(String firstName, String lastName) {
		return _db_.addNewCustomer(firstName, lastName);
	}
	public List<Customer> getAllCustomers() {
		return _db_.getAllCustomers();
	}
	public Customer find(Long ID) {
		return _db_.getByID(ID);
	}
	public List<Customer> find(String word) {
		return _db_.getByName(word);
	}
	public List<Customer> find(Long min, Long max) {
		return _db_.getByBalance(min, max);
	}
	public boolean credit(Long ID, Long accID, Long amount) {
		return _db_.getByID(ID).credit(accID, amount);
	}
	public boolean debit(Long ID, Long accID, Long amount) {
		return _db_.getByID(ID).debit(accID, amount);
	}
	public boolean deleteCustomer(Long ID) {
		return _db_.deleteCustomerByID(ID);
	}
}
