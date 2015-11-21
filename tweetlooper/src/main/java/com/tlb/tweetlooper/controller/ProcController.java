package com.tlb.tweetlooper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tlb.tweetlooper.entity.Admin;
import com.tlb.tweetlooper.entity.LoopTweet;
import com.tlb.tweetlooper.entity.Setting;
import com.tlb.tweetlooper.entity.TeikiTweet;
import com.tlb.tweetlooper.model.GetProperty;
import com.tlb.tweetlooper.model.TweetSet;
import com.tlb.tweetlooper.service.AdminService;
import com.tlb.tweetlooper.service.LoopTweetService;
import com.tlb.tweetlooper.service.SettingService;
import com.tlb.tweetlooper.service.TeikiTweetService;

@Controller
public class ProcController {
	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AdminService adminService;
	
	@Autowired
	LoopTweetService loopTweetService;
	
	@Autowired
	TeikiTweetService teikiTweetService;

	@Autowired
	SettingService settingService;
	
	@Autowired
	GetProperty getProperty;
	
	//
	// ループツイート追加処理
	//
	@RequestMapping(value = "ltadd", method = RequestMethod.POST)
	public String addlt(@RequestParam("lt") String lt){
		//追加処理
		LoopTweet loopTweet = new LoopTweet();
		loopTweet.setMsg(lt);
		loopTweetService.save(loopTweet);
		
		return "redirect:/index";
	}
	
	//
	// ループツイート削除処理
	//
	@RequestMapping(value = "lt_delete", method = RequestMethod.GET)
	public String deletelt(@RequestParam("id") Integer id){
		//削除処理
		loopTweetService.delete(id);
		
		return "redirect:/index";
	}

	//
	// ツイート設定処理
	//
	@RequestMapping(value = "twsetting", method = RequestMethod.POST)
	public String ltset(@ModelAttribute("twsetting") TweetSet twsetting){
		//設定
		Setting setting = settingService.find(1);
		setting.setTwswitch(twsetting.isTwsw());
		setting.setLt_time(twsetting.getLtmin());
		setting.setTt_time(twsetting.getTtmin());
		setting.setAccessToken(twsetting.getAccessToken());
		setting.setAccessTokenSecret(twsetting.getAccessTokenSecret());
		setting.setConsumerKey(twsetting.getConsumerKey());
		setting.setConsumerSecret(twsetting.getConsumerSecret());
		settingService.save(setting);
		
		return "redirect:/setting";
	}

	//
	// 定期ツイート追加処理
	//
	@RequestMapping(value = "ttadd", method = RequestMethod.POST)
	public String addtt(@RequestParam("tt") String tt){
		//追加処理
		TeikiTweet teikiTweet = new TeikiTweet();
		teikiTweet.setMsg(tt);
		teikiTweetService.save(teikiTweet);
		
		return "redirect:/teiki";
	}
	
	//
	// 定期ツイート削除処理
	//
	@RequestMapping(value = "tt_delete", method = RequestMethod.GET)
	public String deletett(@RequestParam("id") Integer id){
		//削除処理
		teikiTweetService.delete(id);
		
		return "redirect:/teiki";
	}
	
	//
	// ユーザ登録処理
	//
	@RequestMapping(value = "makeuser", method = RequestMethod.POST)
	public String makeuser(
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			@RequestParam("adminpass") String adminpass) {
		
		String pass = getAdminPass();
		if(adminpass.equals(pass)){
			Admin admin = new Admin();
			admin.setAdmin_id(email);
			admin.setAdmin_pass(passwordEncoder.encode(password));
			adminService.save(admin);
			System.out.println("Adminユーザを登録しました");
		}
		else{
			System.out.println("管理者パスワードが違います");
		}
		
		return "makeuser";
	}
	
	private String getAdminPass(){
		String ret = null;
		try {
			ret = getProperty.getAdminpass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
