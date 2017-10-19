package ch.propulsion.twitter.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import ch.propulsion.twitter.domain.User;

@Component
public class UserRepositoryDefault implements UserRepository {
	
	/*
	 * SQL TEMPLATES
	 */
	
	private static final String CREATE_USER_TABLE = //
			  "CREATE TABLE IF NOT EXISTS users ("
			+ "id INTEGER IDENTITY PRIMARY KEY,"
			+ "user_name VARCHAR(64) NOT NULL UNIQUE,"
			+ "email VARCHAR(86) NOT NULL UNIQUE,"
			+ "password VARCHAR(64) NOT NULL,"
			+ "date_created TIMESTAMP NOT NULL"
			+ ");";
	private static final String COUNT_USERS = "SELECT COUNT(*) FROM users;";
	private static final String INSERT_USER = "INSERT INTO users (user_name, email, password, date_created) VALUES (?, ?, ?, ?);";
	private static final String DELETE_BY_ID = "DELETE FROM users WHERE id = ?;";
	private static final String DELETE_ALL = "DELETE FROM users;";
	private static final String FIND_BY_ID = "SELECT * FROM users WHERE id = ?;";
	private static final String FIND_ALL = "SELECT * FROM users;";
	private static final String FIND_BY_USERNAME = "SELECT * FROM users WHERE user_name LIKE CONCAT('%',?,'%');";
	
	/*
	 * FIELDS
	 */
	
	private final JdbcTemplate jdbcTemplate;
	
	/*
	 * CONSTRUCTOR
	 */
	
	public UserRepositoryDefault (JdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/*
	 * ROWMAPPER
	 */
	
	private final RowMapper<User> userMapper = (rs, num) -> {
		return new User(
			rs.getInt("id"),
			rs.getString("user_name"),
			rs.getString("email"),
			rs.getString("password"),
			rs.getTimestamp("date_created").toLocalDateTime()
			); 
	};
	
	/*
	 * METHODS
	 */
	
	@Override
	public void initialize() {
		jdbcTemplate.execute(CREATE_USER_TABLE);
	}

	@Override
	public int count() {
		return jdbcTemplate.queryForObject(COUNT_USERS, int.class);
	}

	@Override
	public void save(User user) {
		jdbcTemplate.update(INSERT_USER, user.getUserName(), user.getEmail(), user.getPassword(), user.getDateCreated());
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
	public User findById(Integer id) {
		return jdbcTemplate.queryForObject(FIND_BY_ID, userMapper, id);
	}

	@Override
	public List<User> findAll() {
		return jdbcTemplate.query(FIND_ALL, userMapper);
	}

	@Override
	public List<User> findAllByUsername(String username) {
		return jdbcTemplate.query(FIND_BY_USERNAME, userMapper, username);
	}

}
