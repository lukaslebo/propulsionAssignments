package ch.propulsion.twitter.app;

import java.util.List;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ch.propulsion.twitter.config.DataAccessConfig;
import ch.propulsion.twitter.domain.Tweet;
import ch.propulsion.twitter.domain.User;
import ch.propulsion.twitter.repository.RepositoryConfig;
import ch.propulsion.twitter.service.ServiceConfig;
import ch.propulsion.twitter.service.TweetService;
import ch.propulsion.twitter.service.UserService;
import ch.propulsion.twitter.util.H2Server;

// @ContextConfiguration(classes = {UserServiceDefault.class, TweetRepositoryDefault.class,})
public class CommandLineApp {
	
	/*
	 * FIELDS
	 */
	
	private static final ApplicationContext context = new AnnotationConfigApplicationContext(RepositoryConfig.class, ServiceConfig.class, DataAccessConfig.class);
	
	private static final UserService userService = context.getBean(UserService.class);
	private static final TweetService tweetService = context.getBean(TweetService.class);
	
	private static final Scanner scanner = new Scanner(System.in);
	
	/*
	 * MAIN
	 */
	
	public static void main(String[] args) {
		initialize();
		while (true) {
			Operation operation = getOperationFromUser();
			operationHandler(operation);
		}
	}

	/*
	 * METHODS
	 */
	
	private static void initialize() {
		H2Server.getAndStartServer();
		userService.initialize();
		tweetService.initialize();
	}

	private static void operationHandler(Operation operation) {
		switch (operation) {
			case EXIT: {
				banner("Exiting... 👋");
				H2Server.stopServer();
				System.exit(0);
			}
			case NEW_USER: {
				registerNewUser();
				break;
			}
			case DELETE_USER: {
				deleteUser();
				break;
			}
			case FIND_USER_BY_ID: {
				findUserById();
				break;
			}
			case FIND_USER_BY_USERNAME: {
				findUserByUsername();
				break;
			}
			case FIND_ALL_USERS: {
				findAllUsers();
				break;
			}
			case NEW_TWEET: {
				createNewTweet();
				break;
			}
			case DELETE_TWEET: {
				deleteTweet();
				break;
			}
			case FIND_TWEET_BY_ID: {
				findTweet();
				break;
			}
			case FIND_ALL_TWEETS: {
				findAllTweets();
				break;
			}
			case FIND_TWEETS_BY_USER_ID: {
				findAllTweetsByUserId();
				break;
			}
			case FIND_TWEETS_BY_USERNAME: {
				findAllTweetsByUsername();
				break;
			}
			default:
				System.err.printf("Unsupported operation: %s%n%n", operation.ordinal());
				break;
		}
	}
	
	private static void printTweet(Tweet tweet) {
//		User author = tweet.getAuthor();
//		LocalDate tweetDate = tweet.getDateCreated().toLocalDate();
//		System.out.printf("%s%n\tOn %s, @%s tweeted: %s%n", tweet.getId(), tweetDate, author.getUserName(),
//			tweet.getText());
		System.out.printf("Tweet ID: %s%n\t On %s tweeted: %s%n", tweet.getId(), tweet.getDateCreated(), tweet.getAuthorName());
	}

	private static void printUser(User user) {
//		System.out.println("@" + user.getUserName());
//		System.out.println("  ID:            " + user.getId());
//		System.out.println("  Member since:  " + user.getDateCreated().toLocalDate());
//		System.out.println("  Tweets:        ");
//		user.getTweets().forEach(TwitterApp::printTweet);
		System.out.printf("@%s%nID: %s%nMember since: %s%n", user.getUserName(), user.getId(), user.getDateCreated());
	}

	private static void findAllUsers() {
		banner("Users");
		System.out.println();

		List<User> users = userService.findAll();
		if (users.isEmpty()) {
			System.out.println("No Tweeters!");
		}
		else {
			users.forEach(CommandLineApp::printUser);
		}
	}

	private static void registerNewUser() {
		banner("Register New User");

		prompt("Email Address");
		String email = scanner.nextLine();

		prompt("Username");
		String username = scanner.nextLine();

		prompt("Password");
		String password = scanner.nextLine();

		User user = new User(username, email, password);
		userService.save(user);
	}

	private static void findUserById() {
		banner("Find User By ID");

		prompt("User ID");
		String id = scanner.nextLine();

		System.out.println();
		User user = userService.findById(Integer.parseInt(id));
		if (user == null) {
			System.err.println("Could not find user with ID: " + id);
		}
		else {
			printUser(user);
		}
	}

	private static void findUserByUsername() {
		banner("Find User By Username");

		prompt("User Username");
		String username = scanner.nextLine();

		System.out.println();
		List<User> users = userService.findAllByUsername(username);
		if (users == null || users.size() == 0) {
			System.err.println("Could not find a user with username: " + username);
		}
		else {
			for (User user : users) {
				printUser(user);				
			}
		}
	}

	private static void deleteUser() {
		banner("Delete User");

		prompt("User ID");
		String id = scanner.nextLine();

		userService.deleteById(Integer.parseInt(id));

		// We assume the user got deleted, but we don't
		// actually know if the operation succeeded.
		// For example, if there is no such user, the
		// UserService doesn't inform us of that. :(
		// TODO: fix this...
		System.out.println("Deleted user with ID: " + id);
	}

	private static void createNewTweet() {
		banner("New Tweet");

		prompt("User ID");
		String id = scanner.nextLine();
		User author = userService.findById(Integer.parseInt(id));

		if (author == null) {
			System.err.println("Could not find user with ID: " + id);
		}
		else {
			prompt("Tweet");
			String text = scanner.nextLine();
			Tweet tweet = new Tweet(text, author.getUserName());
//			author.addTweet(tweet);
			tweetService.save(tweet);

			printUser(author);
		}
	}

	private static void deleteTweet() {
		banner("Delete Tweet");

		prompt("Tweet ID");
		String id = scanner.nextLine();

		tweetService.deleteById(Integer.parseInt(id));

		// We assume the tweet got deleted, but we don't
		// actually know if the operation succeeded.
		// For example, if there is no such tweet, the
		// TweetService doesn't inform us of that. :(
		System.out.println("Deleted tweet with ID: " + id);
	}

	private static void findTweet() {
		banner("Find Tweet");

		prompt("Tweet ID");
		String id = scanner.nextLine();

		System.out.println();
		Tweet tweet = tweetService.findById(Integer.parseInt(id));
		if (tweet == null) {
			System.err.println("Could not find tweet with ID: " + id);
		}
		else {
			printTweet(tweet);
		}
	}

	private static void findAllTweets() {
		banner("Tweets");
		System.out.println();

		List<Tweet> tweets = tweetService.findAll();
		if (tweets.isEmpty()) {
			System.out.println("No Tweets!");
		}
		else {
			tweets.forEach(CommandLineApp::printTweet);
		}
	}

	private static void findAllTweetsByUserId() {
//		banner("Tweets by User ID");
//		System.out.println();
//
//		prompt("User ID");
//		String id = scanner.nextLine();
//
//		List<Tweet> tweets = tweetService.findAllTweetsByUserId(id);
//		if (tweets.isEmpty()) {
//			System.out.println("No tweets for user with ID: " + id);
//		}
//		else {
//			tweets.forEach(TwitterApp::printTweet);
//		}
	}

	private static void findAllTweetsByUsername() {
		banner("Tweets by Username");
		System.out.println();

		prompt("Username");
		String username = scanner.nextLine();

		List<Tweet> tweets = tweetService.findAllByUsername(username);
		if (tweets.isEmpty()) {
			System.out.println("No tweets for @" + username);
		}
		else {
			tweets.forEach(CommandLineApp::printTweet);
		}
	}

	// =========================================================================

	private static Operation getOperationFromUser() {
		banner("Menu");

		while (true) {
			System.out.println();
			for (Operation current : Operation.values()) {
				System.out.println(current);
			}

			prompt("Operation");
			if (scanner.hasNextInt()) {
				int input = scanner.nextInt();
				scanner.nextLine();
				Operation operation = Operation.fromOrdinal(input);
				if (operation == null) {
					System.err.println("Invalid operation: " + input);
				}
				else {
					return operation;
				}
			}
			else {
				if (scanner.hasNextLine()) {
					System.err.println("Invalid operation: " + scanner.nextLine());
				}
			}
		}
	}

	private static void prompt(String label) {
		System.out.printf("%nEnter %s: ", label);
	}

	private static void banner(String header) {
		System.out.println();
		System.out.println("=================================================");
		System.out.println("=== " + header);
		System.out.println("=================================================");
	}
	
}
