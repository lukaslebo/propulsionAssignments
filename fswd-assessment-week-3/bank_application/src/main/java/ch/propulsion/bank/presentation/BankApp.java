package ch.propulsion.bank.presentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.propulsion.bank.domain.Account;
import ch.propulsion.bank.domain.Customer;
import ch.propulsion.bank.service.BankService;

public class BankApp {
	
	// Fields
	private BankService service = new BankService();
	private boolean isInitialized = false;
	private boolean runApp = true;
	private int nLinesOld = 0;
	private String output = "";

	
	// Methods
	public void runApp() {
		String inputStr;
		Scanner sc = new Scanner(System.in);
		System.out.println(""); // print empty line for initial buffer
		while (this.runApp) {
			this.printMenu();
			inputStr = sc.nextLine();
			this.processInput(inputStr, sc);
		}
		sc.close();
	}
	
	private void printMenu() {
		int nLinesNew = 1;
		StringBuilder screen = new StringBuilder();
		String output = this.getOutput();
		String menu = this.getMenu();
		if (output.length() > 0) {
			screen.append(output);
			nLinesNew += this.countLines(output)-2;
		}
		if (menu.length() > 0) {
			screen.append(menu);
			nLinesNew += this.countLines(menu);
		}
		clrscr(this.nLinesOld);
		this.nLinesOld = nLinesNew;
		System.out.println(screen);
	}
	
	private void clrscr(int n) {
		if (!isInitialized) {
			isInitialized = true;
			return;
		}
		try {
		    if (System.getProperty("os.name").contains("Windows")) {
		    		new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			}
			else {
				System.out.println(String.format("\033[%dA",n) + "\r\033[J");
			}
		} catch (IOException | InterruptedException ex) {}
	}
	
	private int countLines(String str) {
		int nLines = 1;
		Matcher m = Pattern.compile("\r\n|\r|\n").matcher(str);
		while (m.find()) {
		    ++nLines;
		}
		++nLines;
		return nLines;
	}
	
	public void processInput(String inputStr, Scanner sc) {
		this.output = "";
		if (inputStr.length() == 0) {
			return;
		}
		else if (inputStr.matches("[qQ]")) {
			this.runApp = false;
			return;
		}
		else if (inputStr.matches("new")) {
			this.newCustomer(sc);
		}
		else if (inputStr.matches("print[aA]ll")) {
			this.printAll();
		}
		else if (inputStr.matches("find\\([0-9]{1,}\\)")) {
			Long ID = Long.parseLong(inputStr.substring(5,inputStr.length()-1));
			this.printFindByID(ID);
		}
		else if (inputStr.matches("find\\(-?[0-9]{1,}.?[0-9]{0,},[ ]?-?[0-9]{1,}.?[0-9]{0,}\\)")) {
			int separator = inputStr.indexOf(','); 
			Long min = this.moneyToLong(Double.parseDouble(inputStr.substring(5,separator)));
			Long max = this.moneyToLong(Double.parseDouble(inputStr.substring(separator+1,inputStr.length()-1)));
			this.printFindByBalance(min, max);
		}
		else if (inputStr.matches("find\\(.{1,}\\)")) {
			String word = inputStr.substring(5,inputStr.length()-1);
			this.printFindByMatch(word);
		}
		else if (inputStr.matches("credit\\([0-9]{1,},[ ]?[0-9]{1,}.?[0-9]{0,}\\)")) {
			int separator = inputStr.indexOf(',');
			Long ID = Long.parseLong(inputStr.substring(7,separator));
			Long amount = this.moneyToLong(Double.parseDouble(inputStr.substring(separator+1,inputStr.length()-1)));
			this.credit(ID, amount, sc);
		}
		else if (inputStr.matches("debit\\([0-9]{1,},[ ]?[0-9]{1,}.?[0-9]{0,}\\)")) {
			int separator = inputStr.indexOf(','); 
			Long ID = Long.parseLong(inputStr.substring(6,separator));
			Long amount = this.moneyToLong(Double.parseDouble(inputStr.substring(separator+1,inputStr.length()-1)));
			this.debit(ID, amount, sc);
		}
		else if (inputStr.matches("delete\\([0-9]{1,}\\)")) {
			Long ID = Long.parseLong(inputStr.substring(7,inputStr.length()-1));
			this.deleteCustomer(ID);
		}
		else {
			this.output += "Invalid command or invalid argument.\n";
		}
	}

	private String getMenu() {
		StringBuilder menu = new StringBuilder();
		menu.append("\nCommands:\n");
		menu.append("      < new >          - initiates customer creation\n");
		menu.append("    < printAll >       - prints all customers\n");
		menu.append("    < find(ID) >       - find customer by ID\n");
		menu.append("   < find(name) >      - find customers by name\n");
		menu.append(" < find(min, max) >    - find customers by balance\n");
		menu.append("   < delete(ID) >      - delete customer by ID\n");
		menu.append("< credit(ID, amount) > - credit customer with ID specified amount\n");
		menu.append(" < debit(ID, amount) > - debit customer with ID specified amount\n");
		menu.append("       < Q >           - Quit BankApp\n");
		menu.append("     \n");
		menu.append("Enter command:");
		return menu.toString();
	}
	
	private String getOutput() {
		return this.output;
	}
	
	private void newCustomer(Scanner sc) {
		System.out.println("Creating new customer\n");
		System.out.println("< C > Cancel current process.\n");
		System.out.println("Please enter first name:");
		this.nLinesOld += 5;
		String subinput;
		String firstName = "";
		while (firstName.length() == 0) {
			subinput = sc.nextLine();
			++this.nLinesOld;
			if (subinput.matches("[cC]")) {
				this.output += "Process aborted.\n";
				return;
			}
			else if (subinput.matches("[qQ]")) {
				this.runApp = false;
				return;
			}
			else if (subinput.length() < 2 || subinput.replaceAll("[^0-9]", "").length()>0) {
				System.out.println("Invalid input. Name must be at least two characters and can not contain numbers.\nTry again:");
				this.nLinesOld += 2;
			}
			else {
				firstName = subinput;
			}
		}
		System.out.println("Please enter last name:");
		++this.nLinesOld;
		String lastName = "";
		while (lastName.length() == 0) {
			subinput = sc.nextLine();
			++this.nLinesOld;
			if (subinput.matches("[cC]")) {
				this.output += "Process aborted.\n";
				return;
			}
			else if (subinput.matches("[qQ]")) {
				this.runApp = false;
				return;
			}
			else if (subinput.length() < 2 || subinput.replaceAll("[^0-9]", "").length()>0) {
				System.out.println("Invalid input. Name must be at least two characters and can not contain numbers.\nTry again:");
				this.nLinesOld += 2;
			}
			else {
				lastName = subinput;
			}
		}
		Customer customer = service.addNewCustomer(firstName, lastName);
		String who = customer.getFirstName() + " " + customer.getLastName() + " (ID: " + customer.getID() + ")";
		this.output += "Succcessfully created new Customer:\n" + who + "\n";
	}
	
	private void ouputCustomerList(List<Customer> customers, String error) {
		if (customers.size() == 0 || customers.get(0) == null) {
			this.output += error;
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (Customer customer : customers) {
			sb.append("Customer ID: " + customer.getID() + "   |   ");
			sb.append(customer.getFirstName() + " " + customer.getLastName());
			sb.append("   |  Total: " + this.longToMoney(customer.getTotalBalance()) + "\n");
			List<Account> accounts = customer.getAccounts();
			if (accounts.size() > 1) {
				for (Account account : accounts) {
					sb.append("      accID: " + account.getAccID() + "   |   ");
					sb.append(account.getAccName() + "  |  " + this.longToMoney(account.getBalance()) + "\n");
				}
			}
		}
		this.output += sb.toString();
	}
	
	private void printAccountList(List<Account> accounts) {
		StringBuilder sb = new StringBuilder();
		for (Account account : accounts) {
			sb.append("     accID: " + account.getAccID() + "  |  ");
			sb.append(account.getAccName() + "  |  " + this.longToMoney(account.getBalance()) + "\n");
			++this.nLinesOld;
		}
		System.out.println(sb.toString());
		++this.nLinesOld;
	}
	
	private void printAll() {
		List<Customer> customers = service.getAllCustomers();
		ouputCustomerList(customers, "No customers in database.\n");
	}
	
	private void printFindByID(Long ID) {
		this.output = "Find Customer by ID:\n\n";
		List<Customer> customer = new ArrayList<Customer>();
		customer.add(service.find(ID));
		ouputCustomerList(customer, "No customer with ID: "+ID+"\n");
	}
	
	private void printFindByBalance(Long min, Long max) {
		this.output = "Find Customer by Balance:\n\n";
		if (min > max) {
			this.output += "Invalid input for Min and Max Balance.\n";
			return;
		}
		List<Customer> customers = service.find(min, max);
		ouputCustomerList(customers, "No customer matching this range.\n");
	}
	
	private void printFindByMatch(String word) {
		this.output = "Find Customers by Name:\n";
		String[] wordList = word.toLowerCase().split(" ");
		boolean check;
		List<Customer> customers = new ArrayList<>();
		for (String currentWord : wordList) {
			List<Customer> matches = service.find(currentWord);
			for (Customer match : matches) {
				check = true;
				for (Customer customer : customers) {
					if (customer.getID() == match.getID()) {
						check = false;
						break;
					}
				}
				if (check) {
					customers.add(match);
				}
			}
		}
		ouputCustomerList(customers, "No customers matching \"" + word + "\".\n");	
	}
	
	private void debit(Long ID, Long amount, Scanner sc) {
		this.output = "Transaction (debit)\n\n";
		Customer customer = service.find(ID);
		if (customer == null) {
			this.output += "No Customer with ID " + ID + ".\n";
		}
		else {
			String who = customer.getFirstName() + " " +  customer.getLastName() + " (ID: " + customer.getID() + ")"; 
			List<Account> accounts = customer.getAccounts();
			boolean check = false;
			if (accounts.size() > 1) {
				System.out.println("Customer has multiple accounts: \n");
				printAccountList(accounts);
				System.out.println("Which account do you want to debit?\n");
				System.out.println("< C > Cancel current process.\n");
				System.out.println("Enter accID:");
				String subinput = "";
				String accIDStr = "";
				while (accIDStr.length() == 0) {
					subinput = sc.nextLine();
					if (subinput.matches("[cC]")) {
						this.output += "Process aborted.\n";
						return;
					}
					else if (subinput.matches("[qQ]")) {
						this.runApp = false;
						return;
					}
					else {
						for (Account account : accounts) {
							if (account.getAccID() == Long.parseLong(subinput)) {
								accIDStr = subinput;	
							}
						}
					}
					if (accIDStr.length() == 0) {
						System.out.println("Invalid input. Try again:");
					}
				}
				check = service.debit(ID, Long.parseLong(accIDStr), amount);
			}
			else {
				check = service.debit(ID, accounts.get(0).getAccID(), amount);
			}
			if (check) {
				this.output += "Debited " + who + " " + this.longToMoney(amount) + ".\n";
			}
			else {
				this.output += "Debit failed.\nCan only debit existing customers / accounts with sufficient balance.\n";
			}
		}
	}
	
	private void credit(Long ID, Long amount, Scanner sc) {
		this.output = "Transaction (credit)\n\n";
		Customer customer = service.find(ID);
		if (customer == null) {
			this.output += "No Customer with ID " + ID + ".\n";
		}
		else {
			String who = customer.getFirstName() + " " +  customer.getLastName() + " (ID: " + customer.getID() + ")";
			List<Account> accounts = customer.getAccounts();
			boolean check = false;
			if (accounts.size() > 1) {
				System.out.println("Customer has multiple accounts: \n");
				printAccountList(accounts);
				System.out.println("Which account do you want to credit?\n");
				System.out.println("< C > Cancel current process.\n");
				System.out.println("Enter accID:");
				String subinput = "";
				String accIDStr = "";
				while (accIDStr.length() == 0) {
					subinput = sc.nextLine();
					if (subinput.matches("[cC]")) {
						this.output += "Process aborted.\n";
						return;
					}
					else if (subinput.matches("[qQ]")) {
						this.runApp = false;
						return;
					}
					else {
						for (Account account : accounts) {
							if (account.getAccID() == Long.parseLong(subinput)) {
								accIDStr = subinput;	
							}
						}
					}
					if (accIDStr.length() == 0) {
						System.out.println("Invalid input. Try again:");
					}
				}
				check = service.credit(ID, Long.parseLong(accIDStr), amount);
			}
			else {
				check = service.credit(ID, accounts.get(0).getAccID(), amount);
			}
			if (check) {
				this.output += "Credited " + who + " " + this.longToMoney(amount) + ".\n";
			}
			else {
				this.output += "Credit failed.\nCan only credit existing customers / accounts.\n";
			}
		}
	}
	
	private void deleteCustomer(Long ID) {
		Customer customer = service.find(ID);
		String who = "";
		if (customer != null) {
			who = customer.getFirstName() + " " + customer.getLastName() + " (ID: " + customer.getID() + ")";
		}
		boolean check = service.deleteCustomer(ID);
		if (check) {
			this.output += "Deletion successfull.\nRemoved: " +  who + "\n";
		}
		else {
			this.output += "Deletion failed.\nCan only delete existing customers with a balance of 0$.\n";
		}
	}
	
	private Long moneyToLong(double money) {
		return (Long) Math.round(money*100);
	}
	
	private String longToMoney(Long money) {
		double d = ((double) money)/100;
		if (d%1 == 0) {
			int i = (int) d;
			return i+" $";
		}
		else {
			String s = "" + (((double) Math.round(d*100))/100);
			int index = s.indexOf('.');
			if (s.length()-1-index <2) {
				return s+"0 $";
			}
			else {
				return s+" $";
			}
		}
	}
	
	
	private void testInit() {
		service.addNewCustomer("Lukas David", "Lebovitz");
		service.addNewCustomer("Laurent", "Hoxhaj");
		service.addNewCustomer("Jeremy", "Savor");
		service.addNewCustomer("Julya", "Savina");
		service.addNewCustomer("test", "test");
		service.credit(0L, 0L, 8_224_17L);
		service.credit(1L, 0L, 1_20L);
		service.credit(2L, 0L, 70_420_04L);
		service.credit(3L, 0L, 164_123_67L);
	}
	
	/*
	 * Main
	 */
	
	public static void main(String[] args) {
		BankApp myBank = new BankApp();
		myBank.testInit();
		myBank.runApp();
	}
	
}
