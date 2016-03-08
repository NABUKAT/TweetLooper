package com.tlb.tweetlooper.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class LoopTweet {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer lt_id;

	public String msg;
	
	@ManyToOne(targetEntity=Admin.class)
	@JoinColumn(name="admin_id")
	public Admin admin;
}
