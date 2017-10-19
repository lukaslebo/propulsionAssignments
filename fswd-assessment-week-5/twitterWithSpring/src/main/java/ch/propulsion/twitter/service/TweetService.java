package ch.propulsion.twitter.service;

import java.util.List;
import ch.propulsion.twitter.domain.Tweet;

public interface TweetService {
	void initialize();
	void save(Tweet tweet);
	void deleteById(Integer id);
	Tweet findById(Integer id);
	List<Tweet> findAll();
	List<Tweet> findAllByUsername(String username);
	List<Tweet> findAllContainingHashTag(String hashTag);
	List<Tweet> findAllMentioningUsername(String username);
}
