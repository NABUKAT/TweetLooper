package com.tlb.tweetlooper.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class LoopTweet {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer id;

	public String msg;
	
}
