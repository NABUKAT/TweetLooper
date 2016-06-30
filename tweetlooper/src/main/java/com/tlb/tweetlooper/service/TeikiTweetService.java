package com.tlb.tweetlooper.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlb.tweetlooper.entity.TeikiTweet;
import com.tlb.tweetlooper.repository.TeikiTweetRepository;

@Service
@Transactional
public class TeikiTweetService {
	@Autowired
	TeikiTweetRepository teikiTweetRepository;

	public List<TeikiTweet> findAll() {
		return teikiTweetRepository.findAll();
	}
	   
	public TeikiTweet save(TeikiTweet TeikiTweet) {
		return teikiTweetRepository.save(TeikiTweet);
	}
	   
	public void delete(Integer id) {
		teikiTweetRepository.delete(id);
	}
	   
	public TeikiTweet find(Integer id) {
		return teikiTweetRepository.findOne(id);
	}
}
