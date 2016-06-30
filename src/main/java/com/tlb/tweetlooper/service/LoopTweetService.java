package com.tlb.tweetlooper.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlb.tweetlooper.entity.LoopTweet;
import com.tlb.tweetlooper.repository.LoopTweetRepository;

@Service
@Transactional
public class LoopTweetService {
	@Autowired
	LoopTweetRepository loopTweetRepository;
	
	public List<LoopTweet> findAll() {
		return loopTweetRepository.findAll();
	}
	   
	public LoopTweet save(LoopTweet loopTweet) {
		return loopTweetRepository.save(loopTweet);
	}
	   
	public void delete(Integer id) {
		loopTweetRepository.delete(id);
	}
	   
	public LoopTweet find(Integer id) {
		return loopTweetRepository.findOne(id);
	}
}
