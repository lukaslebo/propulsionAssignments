package ch.propulsion.twitter.service;

import java.util.List;

import ch.propulsion.twitter.domain.User;

public interface UserService {
	void initialize();
	void save(User user);
	void deleteById(Integer id);
	User findById(Integer id);
	List<User> findAll();
	List<User> findAllByUsername(String username);
	List<String> findAllUsernames();
}
