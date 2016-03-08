package com.tlb.tweetlooper.security;

import org.springframework.security.core.authority.AuthorityUtils;

import com.tlb.tweetlooper.entity.Admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AdminDetails extends org.springframework.security.core.userdetails.User {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Admin admin;

    public AdminDetails(Admin admin) {
        super(admin.getAdmin_name(), admin.getAdmin_pass(), AuthorityUtils.createAuthorityList("ROLE_USER"));
        this.admin = admin;
    }
}
