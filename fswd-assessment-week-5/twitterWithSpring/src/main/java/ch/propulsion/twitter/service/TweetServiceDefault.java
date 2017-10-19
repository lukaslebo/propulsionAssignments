package ch.propulsion.twitter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ch.propulsion.twitter.domain.Tweet;
import ch.propulsion.twitter.repository.TweetRepository;
import ch.propulsion.twitter.repository.UserRepository;

@Service
public class TweetServiceDefault implements TweetService {
	
	/*
	 * FIELDS
	 */
	
	private final TweetRepository tweetRepo;
	private final UserRepository userRepo;
	
	/*
	 * CONSTRUCTOR
	 */
	
	public TweetServiceDefault(TweetRepository tweetRepo, UserRepository userRepo) {
		this.tweetRepo = tweetRepo;
		this.userRepo = userRepo;
	}
	
	/*
	 * METHODS
	 */
	
	@Override
	public void save(Tweet tweet) {
		tweetRepo.save(tweet);
	}

	@Override
	public void deleteById(Integer id) {
		tweetRepo.deleteById(id);
	}

	@Override
	public Tweet findById(Integer id) {
		return tweetRepo.findById(id);
	}

	@Override
	public List<Tweet> findAll() {
		return tweetRepo.findAll();
	}

	@Override
	public List<Tweet> findAllByUsername(String username) {
		return tweetRepo.findAllByUsername(username);
	}

	@Override
	public List<Tweet> findAllContainingHashTag(String hashTag) {
		return tweetRepo.findAllContaining("#"+hashTag);
	}

	@Override
	public List<Tweet> findAllMentioningUsername(String username) {
		return tweetRepo.findAllContaining("@"+username);
	}

	@Override
	public void initialize() {
		tweetRepo.initialize();		
	}

}
