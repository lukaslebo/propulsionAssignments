package ch.propulsion.bank.presentation;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.propulsion.bank.service.BankService;

public class BankApp2 {
	
	// Fields
	private BankService service = new BankService();
	private boolean isInitialized = false;
	private String currentStage = "main";
	private boolean runApp = true;
	private int nLinesOld = 0;
	private String bufferFirstName = "";
	private String bufferLastName = "";
	
	// Methods
	public void runApp() {
		String inputStr;
		Scanner sc = new Scanner(System.in);
		System.out.println(""); // print empty line
		while (this.runApp) {
			this.printMenu();
			inputStr = sc.nextLine();
			this.processInput(inputStr);
		}
		sc.close();
	}
	
	private void printMenu() {
		int nLinesNew = 0;
		StringBuilder screen = new StringBuilder();
		String output = this.getOutput();
		String menu = this.getMenu();
		if (output.length() > 0) {
			screen.append(output);
			nLinesNew += this.countLines(output);
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
		return nLines;
	}
	
	public void processInput(String inputStr) {
		if (inputStr.matches("[qQ]")) {
			this.runApp = false;
			return;
		}
		else if (inputStr.matches("[cC]")) {
			this.currentStage = "main";
		}
		if (this.currentStage.matches("main")) {
			if (inputStr.matches("new")) {
				this.currentStage = "new0";
			}
			else if (this.currentStage.matches("new0")) {
				this.currentStage = "new1";
				this.bufferFirstName = inputStr;
			}
			else if (this.currentStage.matches("new1")) {
				this.currentStage = "new1";
				this.bufferFirstName = inputStr;
			}
		}
		else if (this.currentStage.matches("new")) {
			
		}
	}
	
	private String getOutput() {
		StringBuilder output = new StringBuilder();
		
		return output.toString();
	}
	
	private String getMenu() {
		StringBuilder menu = new StringBuilder();
		if (this.currentStage.matches("main")) {
			menu.append("\nCommands:\n");
			menu.append("< new >          - initiates customer creation\n");
			menu.append("< find >         - initiates customer search\n");
			menu.append("< transaction >  - initiates transaction process\n");
			menu.append("< help >         - displays more commands \n");
			menu.append("     \n");
			menu.append("Enter command:");
		}
		else if (this.currentStage.matches("new0")) {
			menu.append("Creating new customer\n");
			menu.append("< C > Cancel current process.\n");
			menu.append("Please enter first name:\n");
		}
		else if (this.currentStage.matches("new1")) {
			menu.append("Creating new customer\n");
			menu.append("< C > Cancel current process.\n");
			menu.append("First Name: "+this.bufferFirstName);
			menu.append("Please enter last name:\n");
		}
		else if (this.currentStage.matches("new2")) {
			
		}
		else {
			menu.append("oops");
		}
		return menu.toString();
	}
	
	/*
	 * Main
	 */
	
	public static void main(String[] args) {
		BankApp2 myBank = new BankApp2();
		myBank.runApp();
	}
	
}
