package ch.propulsion.twitter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ch.propulsion.twitter.domain.User;
import ch.propulsion.twitter.repository.TweetRepository;
import ch.propulsion.twitter.repository.UserRepository;

@Service
public class UserServiceDefault implements UserService {
	
	/*
	 * FIELDS
	 */
	
	private final TweetRepository tweetRepo;
	private final UserRepository userRepo;
	
	/*
	 * CONSTRUCTOR
	 */
	
	public UserServiceDefault(TweetRepository tweetRepo, UserRepository userRepo) {
		this.tweetRepo = tweetRepo;
		this.userRepo = userRepo;
	}
	
	/*
	 * METHODS
	 */
	
	@Override
	public List<String> findAllUsernames() {
		return tweetRepo.findAllUsernames();
	}

	@Override
	public void save(User user) {
		userRepo.save(user);
	}

	@Override
	public void deleteById(Integer id) {
		userRepo.deleteById(id);
	}

	@Override
	public User findById(Integer id) {
		return userRepo.findById(id);
	}

	@Override
	public List<User> findAll() {
		return userRepo.findAll();
	}

	@Override
	public List<User> findAllByUsername(String username) {
		return userRepo.findAllByUsername(username);
	}
	
	@Override
	public void initialize() {
		userRepo.initialize();		
	}
	
}
