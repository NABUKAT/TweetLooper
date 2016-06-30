package com.tlb.tweetlooper.model;

import lombok.Data;

@Data
public class TweetSet {
	public boolean twsw;
	public int ltmin;
	public int ttmin;
	public String consumerKey;
	public String consumerSecret;
	public String accessToken;
	public String accessTokenSecret;
}
