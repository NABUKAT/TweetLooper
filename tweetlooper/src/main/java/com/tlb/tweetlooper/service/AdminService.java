package com.tlb.tweetlooper.service;

import com.tlb.tweetlooper.entity.*;
import com.tlb.tweetlooper.repository.AdminRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService{
	@Autowired
	AdminRepository adminRepository;
	   
	public List<Admin> findAll() {
	  return adminRepository.findAll(new Sort(Sort.Direction.ASC, "id"));
	}
	   
	public Admin save(Admin admin) {
	  return adminRepository.save(admin);
	}
	   
	public void delete(String id) {
	  adminRepository.delete(id);
	}
	   
	public Admin find(String id) {
	  return adminRepository.findOne(id);
	}
}
