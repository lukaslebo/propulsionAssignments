package ch.propulsion.twitter.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ch.propulsion.twitter.domain.Tweet;

@Repository
public class TweetRepositoryDefault implements TweetRepository {
	
	/*
	 * SQL TEMPLATES
	 */
	
	private static final String CREATE_TWEET_TABLE = //
			  "CREATE TABLE IF NOT EXISTS tweets ("
			+ "id INTEGER IDENTITY PRIMARY KEY,"
			+ "msg VARCHAR(280) NOT NULL,"
			+ "author_name VARCHAR(64) NOT NULL,"
			+ "date_created TIMESTAMP NOT NULL"
			+ ");"; 
	private static final String COUNT_TWEETS = "SELECT COUNT(*) FROM tweets;";
	private static final String INSERT_TWEET = "INSERT INTO tweets (msg, author_name, date_created) VALUES (?, ?, ?);";
	private static final String DELETE_BY_ID = "DELETE FROM tweets WHERE id = ?;";
	private static final String DELETE_ALL = "DELETE FROM tweets;";
	private static final String FIND_BY_ID = "SELECT * FROM tweets WHERE id = ?;";
	private static final String FIND_ALL = "SELECT * FROM tweets;";
	private static final String FIND_BY_USERNAME = "SELECT * FROM tweets WHERE author_name LIKE CONCAT('%',?,'%');";
	private static final String FIND_BY_CONTAINS = "SELECT * FROM tweets WHERE msg LIKE CONCAT('%',?,'%');";
	private static final String FIND_ALL_USERNAMES = "SELECT DISTINCT author_name FROM tweets;";
	
	/*
	 * FIELDS
	 */
	
	private final JdbcTemplate jdbcTemplate;
	
	// private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	/*
	 * CONSTRUCTOR
	 */
	
	public TweetRepositoryDefault (JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/*
	 * ROWMAPPER
	 */
	
	private final RowMapper<Tweet> tweetMapper = (rs, num) -> {
		return new Tweet(
			rs.getInt("id"),
			rs.getString("msg"),
			rs.getString("author_name"),
			rs.getTimestamp("date_created").toLocalDateTime()
			); 
	};
	
	/*
	 * METHODS
	 */
	
	@Override
	public void initialize() {
		jdbcTemplate.execute(CREATE_TWEET_TABLE);
	}
	
	@Override
	public int count() {
		return jdbcTemplate.queryForObject(COUNT_TWEETS, int.class);
	}

	@Override
	public void save(Tweet tweet) {
		jdbcTemplate.update(INSERT_TWEET, tweet.getMsg(), tweet.getAuthorName(), tweet.getDateCreated());
	}

	@Override
	public void deleteById(Integer id) {
		jdbcTemplate.update(DELETE_BY_ID, id);
	}

	@Override
	public void deleteAll() {
		jdbcTemplate.update(DELETE_ALL);
	}

	@Override
	public Tweet findById(Integer id) {
		return jdbcTemplate.queryForObject(FIND_BY_ID, tweetMapper, id);
	}

	@Override
	public List<Tweet> findAll() {
		return jdbcTemplate.query(FIND_ALL, tweetMapper);
	}

	@Override
	public List<Tweet> findAllByUsername(String username) {
		return jdbcTemplate.query(FIND_BY_USERNAME, tweetMapper, username);
	}

	@Override
	public List<Tweet> findAllContaining(String searchText) {
		return jdbcTemplate.query(FIND_BY_CONTAINS, tweetMapper, searchText);
	}
	
	@Override
	public List<String> findAllUsernames() {
		return jdbcTemplate.queryForList(FIND_ALL_USERNAMES, String.class);
	}

}
