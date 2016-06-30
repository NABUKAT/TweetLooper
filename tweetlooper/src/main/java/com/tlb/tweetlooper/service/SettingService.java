package com.tlb.tweetlooper.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlb.tweetlooper.entity.Setting;
import com.tlb.tweetlooper.repository.SettingRepository;

@Service
@Transactional
public class SettingService {
	@Autowired
	SettingRepository settingRepository;

	public List<Setting> findAll() {
		return settingRepository.findAll();
	}
	   
	public Setting save(Setting Setting) {
		return settingRepository.save(Setting);
	}
	   
	public void delete(Integer id) {
		settingRepository.delete(id);
	}
	   
	public Setting find(Integer id) {
		return settingRepository.findOne(id);
	}
}
