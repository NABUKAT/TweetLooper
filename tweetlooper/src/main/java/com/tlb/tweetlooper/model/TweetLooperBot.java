package com.tlb.tweetlooper.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tlb.tweetlooper.entity.Admin;
import com.tlb.tweetlooper.entity.LoopTweet;
import com.tlb.tweetlooper.entity.Setting;
import com.tlb.tweetlooper.entity.TeikiTweet;
import com.tlb.tweetlooper.service.AdminService;
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

	@Autowired
	AdminService adminService;

	List<Admin> admins;

	//admin数の保持
	int adminnum = 0;
	
	int cnt = 0;
	int[] ltcnt;
	int[] ttcnt;
	
	@Scheduled(initialDelay = 30000, fixedRate = 1000)
	public void tweetbot() {
		// 10分に一回設定読み出し
		if (timehasCome(600)) {
			admins = null;
			admins = adminService.findAll();
			
			// adminが増えてたらカウントを初期化
			if(adminnum != admins.size()){
				ltcnt = null;
				ltcnt = new int[admins.size()];
				ttcnt = null;
				ttcnt = new int[admins.size()];
				for(int i=0; i<admins.size(); i++){
					ltcnt[i] = 0;
					ttcnt[i] = 0;
				}
				adminnum = admins.size();
			}
		}

		if(admins == null){
			return;
		}
		for (int i=0; i<admins.size(); i++) {
			// adminの情報を取得
			Setting setting = admins.get(i).getSetting();
			List<LoopTweet> ltlist = admins.get(i).getLtlist();
			List<TeikiTweet> ttlist = admins.get(i).getTtlist();
			
			if (!setting.isTwswitch()) {
				continue;
			}

			// 設定どおりにツイート
			// ループ
			if (timehasCome(setting.getLt_time() * 60) && ltlist != null && ltlist.size() != 0) {
				LoopTweet lt = ltlist.get(ltcnt[i]);
				doTweet(lt.getMsg(), setting);
				ltcnt[i]++;
				if (ltcnt[i] >= ltlist.size()) {
					ltcnt[i] = 0;
				}
			}

			// 定期
			if (timehasCome(setting.getTt_time() * 60) && ttlist != null && ttlist.size() != 0) {
				TeikiTweet tt = ttlist.get(ttcnt[i]);
				doTweet(tt.getMsg(), setting);
				ttcnt[i]++;
				if (ttcnt[i] >= ttlist.size()) {
					ttcnt[i] = 0;
				}
			}
		}

		cnt++;
		if (cnt >= 86400) {
			cnt = 0;
		}
	}

	// ツイート
	public void doTweet(String tw, Setting setting) {
		try {
			// 設定
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey(setting.getConsumerKey()).setOAuthConsumerSecret(setting.getConsumerSecret())
					.setOAuthAccessToken(setting.getAccessToken()).setOAuthAccessTokenSecret(setting.getAccessTokenSecret());
			// 初期化
			Twitter twitter = new TwitterFactory(cb.build()).getInstance();
			// ツイート
			twitter.updateStatus(tw);
		} catch (TwitterException e) {
			System.out.println("ツイートに失敗しました");
			e.printStackTrace();
		}

		// テスト用
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
}
