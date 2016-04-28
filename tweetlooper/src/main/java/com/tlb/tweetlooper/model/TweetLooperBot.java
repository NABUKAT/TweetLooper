package com.tlb.tweetlooper.model;

import java.util.ArrayList;
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

import twitter4j.IDs;
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
	
	//健全化実行間隔（1週間）
	long healthtime = 86400 * 7;

	// admin数の保持
	int adminnum = 0;

	long cnt = 0;
	int[] ltcnt;
	int[] ttcnt;

	@Scheduled(initialDelay = 30000, fixedRate = 1000)
	public void tweetbot() {
		// 10分に一回設定読み出し
		if (timehasCome(600)) {
			admins = null;
			admins = adminService.findAll();

			// adminが増えてたらカウントを初期化
			if (adminnum != admins.size()) {
				ltcnt = null;
				ltcnt = new int[admins.size()];
				ttcnt = null;
				ttcnt = new int[admins.size()];
				for (int i = 0; i < admins.size(); i++) {
					ltcnt[i] = 0;
					ttcnt[i] = 0;
				}
				adminnum = admins.size();
			}
		}

		if (admins == null) {
			return;
		}
		for (int i = 0; i < admins.size(); i++) {
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

			// 健全化
			if (timehasCome(healthtime) && (setting.getHealth1() || setting.getHealth2())) {
				doHealth(setting, setting.getHealth1(), setting.getHealth2());
			}
		}

		cnt++;
		if (cnt >= 86400 * 14) {
			cnt = 0;
		}
	}

	// ツイート
	private void doTweet(String tw, Setting setting) {
		try {
			// 設定
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey(setting.getConsumerKey())
					.setOAuthConsumerSecret(setting.getConsumerSecret()).setOAuthAccessToken(setting.getAccessToken())
					.setOAuthAccessTokenSecret(setting.getAccessTokenSecret());
			// 初期化
			Twitter twitter = new TwitterFactory(cb.build()).getInstance();
			// ツイート
			twitter.updateStatus(tw);
		} catch (TwitterException e) {
			System.out.println("ツイートに失敗しました");
			e.printStackTrace();
		}

		// テスト用
		// System.out.println("----------------------------");
		// System.out.println("tw = " + tw);
	}

	// タイマー
	private boolean timehasCome(long sec) {
		if (cnt % sec == 0) {
			return true;
		} else {
			return false;
		}
	}

	// 健全化
	private void doHealth(Setting setting, boolean isHealth1, boolean isHealth2) {
		// Twitter
		Twitter twitter = null;

		// 設定
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(setting.getConsumerKey())
				.setOAuthConsumerSecret(setting.getConsumerSecret()).setOAuthAccessToken(setting.getAccessToken())
				.setOAuthAccessTokenSecret(setting.getAccessTokenSecret());
		// 初期化
		twitter = new TwitterFactory(cb.build()).getInstance();

		// フォロワー、フォローリスト
		ArrayList<Long> myfollowIDs = getMyFollorIDs(twitter);
		ArrayList<Long> myfollowerIDs = getMyFollowerIDs(twitter);

		// health1
		if (isHealth1) {
			System.out.println("health1を実行しました");
			reduceUsers(twitter, myfollowIDs, myfollowerIDs);
		}

		// health2
		if (isHealth2) {
			System.out.println("health2を実行しました");
			plusUsers(twitter, myfollowIDs, myfollowerIDs);
		}
	}

	// 相互フォローでないフォローユーザを解除する
	private void reduceUsers(Twitter twitter, ArrayList<Long> myfollowIDs, ArrayList<Long> myfollowerIDs) {
		for (Long followid : myfollowIDs) {
			if (!myfollowerIDs.contains(followid)) {
				try {
					twitter.destroyFriendship(followid);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 相互フォローでないフォロワーをフォローする
	private void plusUsers(Twitter twitter, ArrayList<Long> myfollowIDs, ArrayList<Long> myfollowerIDs) {
		for (Long followerid : myfollowerIDs) {
			if (!myfollowIDs.contains(followerid)) {
				try {
					twitter.createFriendship(followerid);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// フォローIDを取得
	private ArrayList<Long> getMyFollorIDs(Twitter twitter) {
		long cursor = -1L;
		IDs ids;
		ArrayList<Long> myfollowIDs = new ArrayList<Long>();

		try {
			do {
				ids = twitter.getFriendsIDs(twitter.getScreenName(), cursor);

				for (long id : ids.getIDs()) {
					myfollowIDs.add(id);
				}

				cursor = ids.getNextCursor();
			} while (ids.hasNext());

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return myfollowIDs;
	}

	// フォロワーIDを取得
	private ArrayList<Long> getMyFollowerIDs(Twitter twitter) {
		long cursor = -1L;
		IDs ids;
		ArrayList<Long> myfollowerIDs = new ArrayList<Long>();

		try {
			do {
				ids = twitter.getFollowersIDs(twitter.getScreenName(), cursor);

				for (long id : ids.getIDs()) {
					myfollowerIDs.add(id);
				}

				cursor = ids.getNextCursor();
			} while (ids.hasNext());

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return myfollowerIDs;
	}
}
