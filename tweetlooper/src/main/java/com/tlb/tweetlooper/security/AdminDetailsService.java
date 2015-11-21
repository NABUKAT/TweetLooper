package com.tlb.tweetlooper.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tlb.tweetlooper.entity.Admin;
import com.tlb.tweetlooper.service.AdminService;

@Service
public class AdminDetailsService implements UserDetailsService {
	@Autowired
	AdminService adminService;
	
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		Admin admin = adminService.find(id);
		if(admin != null){
			return new AdminDetails(admin);
		}
		else{
			throw new UsernameNotFoundException("ユーザが見つかりません");
		}
	}
}
