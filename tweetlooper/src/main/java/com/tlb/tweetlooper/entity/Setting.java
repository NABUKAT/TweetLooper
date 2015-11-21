package com.tlb.tweetlooper.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Setting {
	@Id
	public Integer setting_id;
	
	@NotNull
	public Integer lt_time;

	@NotNull
	public Integer tt_time;
	
	@NotNull
	public boolean twswitch;
	
	public String consumerKey;
	public String consumerSecret;
	public String accessToken;
	public String accessTokenSecret;
}
