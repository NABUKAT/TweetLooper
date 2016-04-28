package com.tlb.tweetlooper.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Setting {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer setting_id;
	
	@NotNull
	public Integer lt_time;

	@NotNull
	public Integer tt_time;
	
	@NotNull
	public boolean twswitch;
	
	@OneToOne(cascade = CascadeType.ALL)
	public Admin admin;
	
	public String consumerKey;
	public String consumerSecret;
	public String accessToken;
	public String accessTokenSecret;
	
	public Boolean health1;
	public Boolean health2;
	public Boolean autofollow;
}
