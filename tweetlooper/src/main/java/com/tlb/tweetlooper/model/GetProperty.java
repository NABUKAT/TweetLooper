package com.tlb.tweetlooper.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@Data
@PropertySource("classpath:application.yml")
public class GetProperty {
	@Value("${server.adminpass}")
	private String adminpass;
}
