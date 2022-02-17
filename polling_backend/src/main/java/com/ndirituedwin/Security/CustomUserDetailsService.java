package com.ndirituedwin.Security;

import com.ndirituedwin.Entity.User;
import com.ndirituedwin.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

       @Autowired
        UserRepository userRepository;



    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameorEmail) throws UsernameNotFoundException {
        log.info("reached here load userbyemailorusername {}",usernameorEmail);
        User user=userRepository.findByUsernameOrEmail(usernameorEmail,usernameorEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with provided  email or username  "+usernameorEmail)
                );
        log.info("reached here load userbyemailorusername user {}",user);


        return UserPrincipal.create(user);
    }

    //this method is used byJwtAuthenticationFilter
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("user not found with id "+userId));
        return UserPrincipal.create(user);
    }
}
