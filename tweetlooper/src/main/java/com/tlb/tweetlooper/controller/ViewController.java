package com.tlb.tweetlooper.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tlb.tweetlooper.entity.LoopTweet;
import com.tlb.tweetlooper.entity.Setting;
import com.tlb.tweetlooper.entity.TeikiTweet;
import com.tlb.tweetlooper.model.TweetSet;
import com.tlb.tweetlooper.service.LoopTweetService;
import com.tlb.tweetlooper.service.SettingService;
import com.tlb.tweetlooper.service.TeikiTweetService;

@Controller
public class ViewController {
	
	@Autowired
	LoopTweetService loopTweetService;
	
	@Autowired
	TeikiTweetService teikiTweetService;
	
	@Autowired
	SettingService settingService;
	
	//
	// ログイン画面ビュー
	//
    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String login() {
        return "login";
    }

	//
	// ホーム画面ビュー
	//
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String root() {
		return "redirect:/index";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model) {
		//設定を確認、なかったらデフォルト値をデータベースに格納
		settingDefault();
		
		//ループツイート一覧の表示
		List<LoopTweet> looptws = loopTweetService.findAll();
		model.addAttribute("looptws", looptws);
		model.addAttribute("listsize", looptws.size());
		
		return "index";
	}

	@RequestMapping(value = "/teiki", method = RequestMethod.GET)
	public String teiki(Model model) {
		//設定を確認、なかったらデフォルト値をデータベースに格納
		settingDefault();
		
		//定期ツイート一覧の表示
		List<TeikiTweet> teikitws = teikiTweetService.findAll();
		model.addAttribute("teikitws", teikitws);
		model.addAttribute("listsize", teikitws.size());
		
		return "teiki";
	}

	@RequestMapping(value = "/setting", method = RequestMethod.GET)
	public String setting(Model model) {
		//設定を確認、なかったらデフォルト値をデータベースに格納
		settingDefault();
		
		//設定読み出し
		List<Setting> l = settingService.findAll();
		TweetSet ts = new TweetSet();
		Setting setting = null;
		if(l != null && !l.isEmpty()){
			setting = l.get(0);
			ts.twsw = setting.isTwswitch();
			ts.ltmin = setting.getLt_time();
			ts.ttmin = setting.getTt_time();
			ts.accessToken = setting.getAccessToken();
			ts.consumerKey = setting.getConsumerKey();
			ts.consumerSecret = setting.getConsumerSecret();
			ts.accessTokenSecret = setting.getAccessTokenSecret();
		}
		else{
			ts.ltmin = 30;
			ts.ttmin = 120;
		}
		model.addAttribute("twsetting", ts);
		
		return "setting";
	}

	//
	//　管理ユーザ新規登録
	//
	@RequestMapping(value = "makeuser", method = RequestMethod.GET)
	public String makeuser() {
		return "makeuser";
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
