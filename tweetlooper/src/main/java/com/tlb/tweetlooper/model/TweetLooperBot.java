package com.tlb.tweetlooper.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tlb.tweetlooper.entity.LoopTweet;
import com.tlb.tweetlooper.entity.Setting;
import com.tlb.tweetlooper.entity.TeikiTweet;
import com.tlb.tweetlooper.service.LoopTweetService;
import com.tlb.tweetlooper.service.SettingService;
import com.tlb.tweetlooper.service.TeikiTweetService;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Component
public class TweetLooperBot {
	@Autowired
	SettingService settingService;
	
	@Autowired
	LoopTweetService loopTweetService;
	
	@Autowired
	TeikiTweetService teikiTweetService;
	
	boolean twswitch = false;
	int lt_min;
	int tt_min;
	int cnt = 0;
	int ltcnt = 0;
	int ttcnt = 0;
	List<String> ltlist = null;
	List<String> ttlist = null;
	String consumerKey = "";
	String consumerSecret = "";
	String accessToken = "";
	String accessTokenSecret = "";

	@Scheduled(initialDelay = 30000, fixedRate = 1000)
	public void tweetbot() {
		// 10分に一回設定、ツイート読み出し
		if (timehasCome(600)) {
			getSetting();
			getLoopTweet();
			getTeikiTweet();
		}
		
		if(!twswitch){
			return;
		}

		// 設定どおりにツイート
		// ループ
		if (timehasCome(lt_min * 60) && ltlist != null && ltlist.size() != 0) {
			String tw = ltlist.get(ltcnt);
			doTweet(tw);
			ltcnt++;
			if (ltcnt >= ltlist.size()) {
				ltcnt = 0;
			}
		}

		// 定期
		if (timehasCome(tt_min * 60) && ttlist != null && ttlist.size() != 0) {
			String tw = ttlist.get(ttcnt);
			doTweet(tw);
			ttcnt++;
			if (ttcnt >= ttlist.size()) {
				ttcnt = 0;
			}
		}
		cnt++;
		if (cnt >= 86400) {
			cnt = 0;
		}
	}

	// 設定を読み出して更新
	public void getSetting() {
		//設定を確認、なかったらデフォルト値をデータベースに格納
		settingDefault();
		
		List<Setting> sets = settingService.findAll();
		Setting set = sets.get(0);
		lt_min = set.getLt_time();
		tt_min = set.getTt_time();
		twswitch = set.isTwswitch();
		accessToken = set.getAccessToken();
		accessTokenSecret = set.getAccessTokenSecret();
		consumerKey = set.getConsumerKey();
		consumerSecret = set.getConsumerSecret();
	}
	
	// ループツイートの読み出し
	public void getLoopTweet() {
		List<LoopTweet> lts = loopTweetService.findAll();
		if(lts == null || lts.size() == 0){
			return;
		}
		ltlist = null;
		ltlist = new ArrayList<String>();
		for (LoopTweet l : lts) {
			ltlist.add(l.getMsg());
		}
	}

	// 定期ツイートの読み出し
	public void getTeikiTweet() {
		List<TeikiTweet> tts = teikiTweetService.findAll();
		if(tts == null || tts.size() == 0){
			return;
		}
		ttlist = null;
		ttlist = new ArrayList<String>();
		for (TeikiTweet l : tts) {
			ttlist.add(l.getMsg());
		}
	}
	
	// ツイート
	public void doTweet(String tw) {
        try {
        	//設定
        	ConfigurationBuilder cb = new ConfigurationBuilder();
        	cb.setDebugEnabled(true)
        	  .setOAuthConsumerKey(consumerKey)
        	  .setOAuthConsumerSecret(consumerSecret)
        	  .setOAuthAccessToken(accessToken)
        	  .setOAuthAccessTokenSecret(accessTokenSecret);
    		//初期化
    		Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        	//ツイート
			twitter.updateStatus(tw);
		} catch (TwitterException e) {
			System.out.println("ツイートに失敗しました");
			e.printStackTrace();
		}

		//テスト用
    	//System.out.println("----------------------------");
    	//System.out.println("tw = " + tw);
	}
	
	// タイマー
	public boolean timehasCome(int sec) {
		if (cnt % sec == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	//
	//　デフォルト値をDBに格納
	//
	private void settingDefault(){
		List<Setting> l = settingService.findAll();
		if(l == null || l.isEmpty()){
			Setting setting = new Setting();
			setting.setSetting_id(1);
			setting.setLt_time(30);
			setting.setTt_time(120);
			setting.setTwswitch(false);
			settingService.save(setting);
		}
	}
}
