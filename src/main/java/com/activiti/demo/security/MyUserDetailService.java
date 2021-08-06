package com.activiti.demo.security;

import com.activiti.demo.mapper.UserInfoBeanMapper;
import com.activiti.demo.pojo.UserInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    UserInfoBeanMapper userInfoBeanMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        String password = passwordEncoder().encode("111");
//        return new User(username,password
//        , AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ACTIVITI_USER"));
        UserInfoBean userInfoBean = userInfoBeanMapper.selectByUsername(username);
        if(userInfoBean == null){
            throw new UsernameNotFoundException("数据库无此用户");
        }
        System.out.println(userInfoBean.toString());
        return userInfoBean;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
