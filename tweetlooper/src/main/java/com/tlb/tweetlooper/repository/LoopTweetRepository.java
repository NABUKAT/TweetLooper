package com.tlb.tweetlooper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tlb.tweetlooper.entity.LoopTweet;

public interface LoopTweetRepository extends JpaRepository<LoopTweet, Integer>{

}
