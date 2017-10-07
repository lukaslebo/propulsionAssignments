package ch.propulsion.bank.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ch.propulsion.bank.domain.Customer;

public class BankDB {
	
	// Fields
	private Map<Long, Customer> customers = new HashMap<Long, Customer>();
	
	// Constructor
	public BankDB() {
		// default
	}
	
	// Methods
	public Customer addNewCustomer(String firstName, String lastName) {
		Customer newCustomer = new Customer(firstName, lastName);
		this.customers.put(newCustomer.getID(), newCustomer);
		return newCustomer;
	}
	public boolean deleteCustomerByID(Long ID) {
		try {
			if (this.customers.get(ID).getTotalBalance() == 0) {
				this.customers.remove(ID);
				return true;
			}
			return false;
		}
		catch (Exception e) {
			return false;
		}
	}
	public Customer getByID(Long ID) {
		return this.customers.get(ID);
	}
	public List<Customer> getAllCustomers() {
		return new ArrayList<>(this.customers.values());
	}
	public List<Customer> getByName(String word) {
		List<Customer> matches = new ArrayList<>();
		List<Customer> allCustomers = this.getAllCustomers();
		for (Customer customer : allCustomers) {
			String wholeName = customer.getFirstName().toLowerCase()+" "+customer.getLastName().toLowerCase();
			if (wholeName.contains(word)) {
				matches.add(customer);
			}
		}
		return matches;
	}
	public List<Customer> getByBalance(Long min, Long max) {
		List<Customer> matches = new ArrayList<>();
		List<Customer> allCustomers = this.getAllCustomers();
		for (Customer customer : allCustomers) {
			Long total = customer.getTotalBalance();
			if (total >= min && total <= max) {
				matches.add(customer);
			}
		}
		return matches;
	}

}
