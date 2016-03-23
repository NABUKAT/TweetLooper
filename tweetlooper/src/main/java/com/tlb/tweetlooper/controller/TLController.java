package com.tlb.tweetlooper.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tlb.tweetlooper.entity.Admin;
import com.tlb.tweetlooper.entity.LoopTweet;
import com.tlb.tweetlooper.entity.Setting;
import com.tlb.tweetlooper.entity.TeikiTweet;
import com.tlb.tweetlooper.model.GetProperty;
import com.tlb.tweetlooper.model.MyTwAccount;
import com.tlb.tweetlooper.model.TweetSet;
import com.tlb.tweetlooper.model.TwitterFunctions;
import com.tlb.tweetlooper.service.AdminService;
import com.tlb.tweetlooper.service.LoopTweetService;
import com.tlb.tweetlooper.service.SettingService;
import com.tlb.tweetlooper.service.TeikiTweetService;

@Controller
public class TLController {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	LoopTweetService loopTweetService;

	@Autowired
	TeikiTweetService teikiTweetService;

	@Autowired
	SettingService settingService;

	@Autowired
	AdminService adminService;

	@Autowired
	TwitterFunctions twitterFunctions;

	@Autowired
	GetProperty getProperty;

	//
	// ログイン画面ビュー
	//
	@RequestMapping(value = "/login", method = RequestMethod.GET)
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
	public String index(Model model, Principal principal) {
		// ログインユーザの取得
		Authentication auth = (Authentication) principal;
		User user = (User) auth.getPrincipal();
		List<Admin> admins = adminService.findAll();
		Admin admin = null;
		for (Admin a : admins) {
			if (a.getAdmin_name().equals(user.getUsername())) {
				admin = a;
			}
		}
		model.addAttribute("username", user.getUsername());
		
		// アカウント情報の取得
		if (twitterFunctions.checkTwitterSetting(admin.getSetting())) {
			twitterFunctions.setting(admin.getSetting());
			MyTwAccount mta = twitterFunctions.getAccount();
			model.addAttribute("mta", mta);
		}

		// ループツイート一覧の表示
		List<LoopTweet> looptws = admin.getLtlist();
		model.addAttribute("looptws", looptws);
		model.addAttribute("listsize", looptws.size());

		return "index";
	}

	@RequestMapping(value = "/teiki", method = RequestMethod.GET)
	public String teiki(Model model, Principal principal) {
		// ログインユーザの取得
		Authentication auth = (Authentication) principal;
		User user = (User) auth.getPrincipal();
		List<Admin> admins = adminService.findAll();
		Admin admin = null;
		for (Admin a : admins) {
			if (a.getAdmin_name().equals(user.getUsername())) {
				admin = a;
			}
		}
		model.addAttribute("username", user.getUsername());

		// アカウント情報の取得
		if (twitterFunctions.checkTwitterSetting(admin.getSetting())) {
			twitterFunctions.setting(admin.getSetting());
			MyTwAccount mta = twitterFunctions.getAccount();
			model.addAttribute("mta", mta);
		}
		
		// 定期ツイート一覧の表示
		List<TeikiTweet> teikitws = admin.getTtlist();
		model.addAttribute("teikitws", teikitws);
		model.addAttribute("listsize", teikitws.size());

		return "teiki";
	}

	@RequestMapping(value = "search", method = RequestMethod.GET)
	public String search(Model model, Principal principal) {
		// ログインユーザの取得
		Authentication auth = (Authentication) principal;
		User user = (User) auth.getPrincipal();
		List<Admin> admins = adminService.findAll();
		Admin admin = null;
		for (Admin a : admins) {
			if (a.getAdmin_name().equals(user.getUsername())) {
				admin = a;
			}
		}
		model.addAttribute("username", user.getUsername());

		// アカウント情報の取得
		if (twitterFunctions.checkTwitterSetting(admin.getSetting())) {
			twitterFunctions.setting(admin.getSetting());
			MyTwAccount mta = twitterFunctions.getAccount();
			model.addAttribute("mta", mta);
		}
		
		return "search";
	}

	@RequestMapping(value = "/setting", method = RequestMethod.GET)
	public String setting(Model model, Principal principal) {
		// ログインユーザの取得
		Authentication auth = (Authentication) principal;
		User user = (User) auth.getPrincipal();
		List<Admin> admins = adminService.findAll();
		Admin admin = null;
		for (Admin a : admins) {
			if (a.getAdmin_name().equals(user.getUsername())) {
				admin = a;
			}
		}
		model.addAttribute("username", user.getUsername());

		// アカウント情報の取得
		if (twitterFunctions.checkTwitterSetting(admin.getSetting())) {
			twitterFunctions.setting(admin.getSetting());
			MyTwAccount mta = twitterFunctions.getAccount();
			model.addAttribute("mta", mta);
		}
		
		// 設定読み出し
		Setting setting = admin.getSetting();
		TweetSet ts = new TweetSet();
		ts.twsw = setting.isTwswitch();
		ts.ltmin = setting.getLt_time();
		ts.ttmin = setting.getTt_time();
		ts.accessToken = setting.getAccessToken();
		ts.consumerKey = setting.getConsumerKey();
		ts.consumerSecret = setting.getConsumerSecret();
		ts.accessTokenSecret = setting.getAccessTokenSecret();
		model.addAttribute("twsetting", ts);

		return "setting";
	}

	//
	// ループツイート追加処理
	//
	@RequestMapping(value = "ltadd", method = RequestMethod.POST)
	public String addlt(Principal principal, @RequestParam("lt") String lt) {
		// ログインユーザの取得
		Authentication auth = (Authentication) principal;
		User user = (User) auth.getPrincipal();
		List<Admin> admins = adminService.findAll();
		Admin admin = null;
		for (Admin a : admins) {
			if (a.getAdmin_name().equals(user.getUsername())) {
				admin = a;
			}
		}
		
		// 追加処理
		LoopTweet loopTweet = new LoopTweet();
		loopTweet.setMsg(lt);
		loopTweet.setAdmin(admin);
		loopTweetService.save(loopTweet);

		return "redirect:/index";
	}

	//
	// ループツイート削除処理
	//
	@RequestMapping(value = "lt_delete", method = RequestMethod.GET)
	public String deletelt(@RequestParam("id") Integer id) {
		// 削除処理
		loopTweetService.delete(id);

		return "redirect:/index";
	}

	//
	// ツイート設定処理
	//
	@RequestMapping(value = "twsetting", method = RequestMethod.POST)
	public String ltset(Principal principal, @ModelAttribute("twsetting") TweetSet twsetting) {
		// ログインユーザの取得
		Authentication auth = (Authentication) principal;
		User user = (User) auth.getPrincipal();
		List<Admin> admins = adminService.findAll();
		Admin admin = null;
		for (Admin a : admins) {
			if (a.getAdmin_name().equals(user.getUsername())) {
				admin = a;
			}
		}
		
		// 設定
		Setting setting = admin.getSetting();
		setting.setTwswitch(twsetting.isTwsw());
		setting.setLt_time(twsetting.getLtmin());
		setting.setTt_time(twsetting.getTtmin());
		setting.setAccessToken(twsetting.getAccessToken());
		setting.setAccessTokenSecret(twsetting.getAccessTokenSecret());
		setting.setConsumerKey(twsetting.getConsumerKey());
		setting.setConsumerSecret(twsetting.getConsumerSecret());
		Setting savedset = settingService.save(setting);
		admin.setSetting(savedset);

		return "redirect:/setting";
	}

	//
	// 定期ツイート追加処理
	//
	@RequestMapping(value = "ttadd", method = RequestMethod.POST)
	public String addtt(Principal principal, @RequestParam("tt") String tt) {
		// ログインユーザの取得
		Authentication auth = (Authentication) principal;
		User user = (User) auth.getPrincipal();
		List<Admin> admins = adminService.findAll();
		Admin admin = null;
		for (Admin a : admins) {
			if (a.getAdmin_name().equals(user.getUsername())) {
				admin = a;
			}
		}
		
		// 追加処理
		TeikiTweet teikiTweet = new TeikiTweet();
		teikiTweet.setMsg(tt);
		teikiTweet.setAdmin(admin);
		teikiTweetService.save(teikiTweet);

		return "redirect:/teiki";
	}

	//
	// 定期ツイート削除処理
	//
	@RequestMapping(value = "tt_delete", method = RequestMethod.GET)
	public String deletett(@RequestParam("id") Integer id) {
		// 削除処理
		teikiTweetService.delete(id);

		return "redirect:/teiki";
	}

	//
	// 検索処理
	//
	@RequestMapping(value = "searchproc", method = RequestMethod.POST)
	public String searchproc(Model model, Principal principal, @RequestParam("word") String word) {
		// ログインユーザの取得
		Authentication auth = (Authentication) principal;
		User user = (User) auth.getPrincipal();
		List<Admin> admins = adminService.findAll();
		Admin admin = null;
		for (Admin a : admins) {
			if (a.getAdmin_name().equals(user.getUsername())) {
				admin = a;
			}
		}
		model.addAttribute("username", user.getUsername());

		// アカウント情報の取得
		if (twitterFunctions.checkTwitterSetting(admin.getSetting())) {
			twitterFunctions.setting(admin.getSetting());
			MyTwAccount mta = twitterFunctions.getAccount();
			model.addAttribute("mta", mta);
		}
		// Twitterの設定が不十分なら検索しない
		else{
			model.addAttribute("setting", "notenough");
			return "search";
		}

		model.addAttribute("setting", "enough");
		// 検索結果を取得
		List<String> list = twitterFunctions.findUsers(word);
		model.addAttribute("searchlist", list);
		model.addAttribute("listsize", list.size());

		return "search";
	}
	
	//
	// ユーザフォロー処理
	//
	@RequestMapping(value = "followproc", method = RequestMethod.POST)
	public String followproc(Model model, Principal principal) {
		// ログインユーザの取得
		Authentication auth = (Authentication) principal;
		User user = (User) auth.getPrincipal();
		List<Admin> admins = adminService.findAll();
		Admin admin = null;
		for (Admin a : admins) {
			if (a.getAdmin_name().equals(user.getUsername())) {
				admin = a;
			}
		}
		model.addAttribute("username", user.getUsername());

		// アカウント情報の取得
		if (twitterFunctions.checkTwitterSetting(admin.getSetting())) {
			twitterFunctions.setting(admin.getSetting());
			MyTwAccount mta = twitterFunctions.getAccount();
			model.addAttribute("mta", mta);
		}
		// アカウント情報が不十分なら何もしない
		else{
			return "search";
		}
		
		// フォロー処理
		twitterFunctions.followUsers();
		
		return "search";
	}

	//
	// 管理ユーザ新規登録View
	//
	@RequestMapping(value = "makeuser", method = RequestMethod.GET)
	public String makeuser() {
		return "makeuser";
	}

	//
	// ユーザ登録処理
	//
	@RequestMapping(value = "makeuser", method = RequestMethod.POST)
	public String makeuser(@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("adminpass") String adminpass) {

		String pass = getAdminPass();
		if (adminpass.equals(pass)) {
			Admin admin = new Admin();
			admin.setAdmin_name(email);
			admin.setAdmin_pass(passwordEncoder.encode(password));
			// デフォルトのセッティング
			Setting setting = new Setting();
			setting.setLt_time(30);
			setting.setTt_time(120);
			setting.setTwswitch(false);
			Setting savedset = settingService.save(setting);
			admin.setSetting(savedset);

			adminService.save(admin);
			System.out.println("Adminユーザを登録しました");
		} else {
			System.out.println("管理者パスワードが違います");
		}

		return "makeuser";
	}

	private String getAdminPass() {
		String ret = null;
		try {
			ret = getProperty.getAdminpass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
