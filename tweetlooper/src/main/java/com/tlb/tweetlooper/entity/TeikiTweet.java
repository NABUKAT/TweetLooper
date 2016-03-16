package com.tlb.tweetlooper.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class TeikiTweet {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer tt_id;

	public String msg;

	@ManyToOne(targetEntity=Admin.class, fetch = FetchType.LAZY)
	@JoinColumn(name="admin_id")
	public Admin admin;
}
