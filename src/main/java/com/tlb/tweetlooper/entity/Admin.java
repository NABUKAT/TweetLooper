package com.tlb.tweetlooper.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Admin{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer admin_id;
	
	@NotNull
	public String admin_name;
	
	@NotNull
	public String admin_pass;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "admin", fetch = FetchType.EAGER)
	public List<LoopTweet> ltlist;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "admin", fetch = FetchType.EAGER)
	public List<TeikiTweet> ttlist;
	
	@OneToOne(cascade = CascadeType.ALL)
	public Setting setting;
}
