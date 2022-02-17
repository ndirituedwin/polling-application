package com.ndirituedwin.Service;

import com.ndirituedwin.Dto.ApiResponse;
import com.ndirituedwin.Dto.JwtAuthenticationResponse;
import com.ndirituedwin.Dto.LoginRequest;
import com.ndirituedwin.Dto.SignupRequest;
import com.ndirituedwin.Entity.Enum.RoleName;
import com.ndirituedwin.Entity.NotificationEmail;
import com.ndirituedwin.Entity.Role;
import com.ndirituedwin.Entity.User;
import com.ndirituedwin.Entity.VerificationToken;
import com.ndirituedwin.Exceptions.AppException;
import com.ndirituedwin.Exceptions.VerificationTokenNotFoundException;
import com.ndirituedwin.Repository.RoleRepository;
import com.ndirituedwin.Repository.UserRepository;
import com.ndirituedwin.Repository.VerificationTokenRepository;
import com.ndirituedwin.Security.JwtTokenProvider;
import com.ndirituedwin.Service.mailservice.MailService;
import com.ndirituedwin.config.AppConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AuthService {
    private static final String subject="Please activate your account";
    private static final String  body="Thank you for signing up to the polling application,click on the link below to activate your account";

    private static final String  Bearer="Bearer";
 private final UserRepository userRepository;
 private final AuthenticationManager authenticationManager;
private final JwtTokenProvider jwtTokenProvider;
private final PasswordEncoder passwordEncoder;
private final RoleRepository roleRepository;
private final MailService mailService;
private final AppConfig appConfig;
private final VerificationTokenRepository verificationTokenRepositpry;


 public JwtAuthenticationResponse authenticate(LoginRequest loginRequest) {
  Authentication authentication=authenticationManager.
          authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail()
                  ,loginRequest.getPassword()));
  log.info("logging authentication {}",authentication);
  SecurityContextHolder.getContext().setAuthentication(authentication);
   String jwt=jwtTokenProvider.generateToken(authentication);
   log.info("logging jwt inside signin {}",jwt);
     log.info("logging the security context after login {}",SecurityContextHolder.getContext().getAuthentication());
     log.info("logging the security context authentication principal after login {}",SecurityContextHolder.getContext().getAuthentication().getPrincipal());
   return new JwtAuthenticationResponse(jwt,Bearer);
 }

 public Object signup(SignupRequest signupRequest) {
  if (userRepository.existsByUsername(signupRequest.getUsername())){
   return new ResponseEntity(new ApiResponse(false,"username  "+signupRequest.getUsername()+" is already taken choose another one"),HttpStatus.BAD_REQUEST);

  }
  if(userRepository.existsByEmail(signupRequest.getEmail())){
   return new ResponseEntity(new ApiResponse(false,"email address "+signupRequest.getEmail()+" is already taken choose another one"),HttpStatus.BAD_REQUEST);
  }
  //registering user
         Role  userRole=roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User Role not set"));
         log.info("logging userrole {}",userRole);
         User user=new User();
         user.setName(signupRequest.getName());
         user.setUsername(signupRequest.getUsername());
         user.setEmail(signupRequest.getEmail());
         user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
         user.setIsEnabled(false);
         user.setRoles(Collections.singleton(userRole));
         User result=userRepository.save(user);
         log.info("saved user {}",result);
         String vtoken=generateverificationToken(user);
         mailService.sendemailactivationlink(new NotificationEmail(subject,user.getEmail(),body+"\n "+appConfig.getUrl()+"api/auth/accountverification/"+vtoken));
     log.info("Logging the token before sending email to the user {} ",vtoken);
     URI location= ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{username}")
               .buildAndExpand(result.getUsername()).toUri();
         log.info("logging location from auth service signup {}",location);
         return ResponseEntity.created(location).body(new ApiResponse(true,"user successfullly registered"));
 }

    private String generateverificationToken(User user) {
    String token= UUID.randomUUID().toString();
        VerificationToken verificationToken=new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepositpry.save(verificationToken);
        return token;

       }

    public void verifyAccount(String vtoken) {
     Optional<VerificationToken> verificationToken=verificationTokenRepositpry.findByToken(vtoken);
             verificationToken.orElseThrow(() -> new VerificationTokenNotFoundException("verification token not found"));
        log.info("checking what verification token.get contains {}",verificationToken.get());
        fetchuserandenable(verificationToken.get());
 }


    @Transactional
    private void fetchuserandenable(VerificationToken verificationToken) {
     String  username=verificationToken.getUser().getUsername();
        log.info("logging the token user {}",verificationToken.getUser());
        User user=userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("username "+username+" not found"));
         user.setIsEnabled(true);
         userRepository.save(user); }
}
