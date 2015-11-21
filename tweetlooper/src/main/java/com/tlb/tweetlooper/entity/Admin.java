package com.tlb.tweetlooper.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Admin{
	@Id
	public String admin_id;
	
	@NotNull
	public String admin_pass;
}
