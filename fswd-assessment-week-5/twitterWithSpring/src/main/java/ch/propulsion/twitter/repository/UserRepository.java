package ch.propulsion.twitter.repository;

import java.util.List;
import ch.propulsion.twitter.domain.User;

public interface UserRepository {
	void initialize();
	int count();
	void save(User user);
	void deleteById(Integer id);
	void deleteAll();
	User findById(Integer id);
	List<User> findAll();
	List<User> findAllByUsername(String username);
}
