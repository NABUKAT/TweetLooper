package com.tlb.tweetlooper.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.annotation.ScopedProxyMode;
import com.tlb.tweetlooper.entity.Setting;

import twitter4j.IDs;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/*
 * Twitterのフォロー数等取得、検索、フォロー機能
 */
@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = WebApplicationContext.SCOPE_SESSION)
public class TwitterFunctions {

	private Twitter twitter = null;

	// 自分のフォローリストを格納
	private List<Long> myfollowIDs;

	// 検索したユーザリストを格納
	private List<User> userlist = null;
	
	// 設定の一部を保持
	private String accessToken = null;

	// 初期設定
	public void setting(Setting setting) {
		if(accessToken != null && setting.getAccessToken().equals(accessToken)){
			return;
		}
		// 設定
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(setting.getConsumerKey())
				.setOAuthConsumerSecret(setting.getConsumerSecret()).setOAuthAccessToken(setting.getAccessToken())
				.setOAuthAccessTokenSecret(setting.getAccessTokenSecret());
		// Twitterインスタンス取得
		twitter = new TwitterFactory(cb.build()).getInstance();
		// 自分のフォローリストを取得
		getMyFollorIDs();
	}
	
	// アカウント情報を取得
	public MyTwAccount getAccount(){
		if(twitter == null){
			return null;
		}
		MyTwAccount mta = new MyTwAccount();
		try {
			mta.setScreenname(twitter.getScreenName());
			User user = twitter.showUser(twitter.getId());
			mta.setFriendsnum(user.getFriendsCount());
			mta.setFollowersnum(user.getFollowersCount());
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return mta;
	}

	// フォロワーIDを取得
	private void getMyFollorIDs() {
		long cursor = -1L;
		IDs ids;
		myfollowIDs = new ArrayList<Long>();

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
	}

	// ユーザ検索、ユーザネームのリストを返す
	public List<String> findUsers(String keyword) {
		ArrayList<String> rets = new ArrayList<String>();
		int max = 50;
		int cnt = 0;
		Query q = new Query(keyword);
		QueryResult qr;
		
		userlist = null;
		userlist = new ArrayList<User>();
				
		// 検索処理
		try {
			do {
				qr = twitter.search(q);
				List<Status> tweets = qr.getTweets();
				if (tweets != null) {
					for (Status tw : tweets) {
						User tmp = tw.getUser();
						//既に友達なら検索結果から除外
						if(myfollowIDs.contains(tmp.getId())){
							break;
						}
						cnt++;
						userlist.add(tmp);
						rets.add(tw.getUser().getName());
						if (max <= cnt) {
							break;
						}
					}
				}
			} while ((q = qr.nextQuery()) != null && max > cnt);
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		return rets;
	}

	// フォロー
	// 検索したユーザを全員フォローする
	public void followUsers() {
		if(userlist != null){
			for (User user : userlist) {
				try {
					twitter.createFriendship(user.getId());
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Twitterの設定が十分か確認
	public boolean checkTwitterSetting(Setting setting) {
		String accessToken = setting.getAccessToken();
		String accessTokenSecret = setting.getAccessTokenSecret();
		String consumerKey = setting.getConsumerKey();
		String consumerSecret = setting.getConsumerSecret();

		if (accessToken == null || accessToken == "") {
			return false;
		} else if (accessTokenSecret == null || accessTokenSecret == "") {
			return false;
		} else if (consumerKey == null || consumerKey == "") {
			return false;
		} else if (consumerSecret == null || consumerSecret == "") {
			return false;
		} else {
			return true;
		}
	}
}
