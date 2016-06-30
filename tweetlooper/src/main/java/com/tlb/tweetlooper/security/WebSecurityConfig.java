package com.tlb.tweetlooper.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/bot/**");
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
//                認証を除外する場合は以下のように記載
//                .antMatchers("/country/**").permitAll()
        		.antMatchers("/login", "/bot/**", "/makeuser").permitAll()
                .anyRequest().authenticated();
        http.formLogin()
        		.loginPage("/login")
                .defaultSuccessUrl("/index")
                .failureUrl("/login")
                .usernameParameter("email").passwordParameter("password")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll();
	}
	
    @Configuration
    protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {
        @Autowired
        AdminDetailsService adminDetailsService;
        
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        
        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
        	auth.userDetailsService(adminDetailsService).passwordEncoder(passwordEncoder());
        }
    }
}
