package com.example.dochubserver.service.springSecurity;

import com.example.dochubserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserLogin implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        com.example.dochubserver.bean.User user = userService.findByUsername(s);
        if (user==null)
        {
            throw new UsernameNotFoundException("该用户不存在");
        }
        return user;
    }
}
