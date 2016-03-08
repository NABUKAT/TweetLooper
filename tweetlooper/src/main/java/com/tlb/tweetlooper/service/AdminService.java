package com.tlb.tweetlooper.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlb.tweetlooper.entity.Admin;
import com.tlb.tweetlooper.repository.AdminRepository;

@Service
@Transactional
public class AdminService{
	@Autowired
	AdminRepository adminRepository;
	   
	public List<Admin> findAll() {
	  return adminRepository.findAll();
	}
	   
	public Admin save(Admin admin) {
	  return adminRepository.save(admin);
	}
	   
	public void delete(Long id) {
	  adminRepository.delete(id);
	}
	   
	public Admin find(Long id) {
	  return adminRepository.findOne(id);
	}
}
