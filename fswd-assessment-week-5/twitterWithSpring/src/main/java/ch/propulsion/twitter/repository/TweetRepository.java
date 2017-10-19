package ch.propulsion.twitter.repository;

import java.util.List;
import ch.propulsion.twitter.domain.Tweet;

public interface TweetRepository {
	void initialize();
	int count();
	void save(Tweet tweet);
	void deleteById(Integer id);
	void deleteAll();
	Tweet findById(Integer id);
	List<Tweet> findAll();
	List<Tweet> findAllByUsername(String username);
	List<Tweet> findAllContaining(String searchText);
	List<String> findAllUsernames();
}
