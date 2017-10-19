package ch.propulsion.twitter.app;

public enum Operation {
	
	EXIT("Exit"),

	NEW_USER("Register new user"),

	DELETE_USER("Delete user"),

	FIND_ALL_USERS("Find all users"),

	FIND_USER_BY_ID("Find user by ID"),

	FIND_USER_BY_USERNAME("Find user by username"),

	NEW_TWEET("Create new tweet"),

	DELETE_TWEET("Delete tweet"),

	FIND_ALL_TWEETS("Find all tweets"),

	FIND_TWEET_BY_ID("Find tweet by ID"),

	FIND_TWEETS_BY_USER_ID("Find all tweets by user ID"),

	FIND_TWEETS_BY_USERNAME("Find all tweets by username");

	private final String displayText;

	private Operation(String displayText) {
		this.displayText = displayText;
	}

	@Override
	public String toString() {
		return String.format("%s: %s", ordinal(), this.displayText);
	}

	static Operation fromOrdinal(int ordinal) {
		for (Operation operation : values()) {
			if (operation.ordinal() == ordinal) {
				return operation;
			}
		}
		return null;
	}
	
}
