package com.tlb.tweetlooper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tlb.tweetlooper.entity.Setting;

public interface SettingRepository extends JpaRepository<Setting, Integer> {

}
