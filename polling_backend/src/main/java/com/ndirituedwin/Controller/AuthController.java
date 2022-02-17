package com.ndirituedwin.Controller;

import com.ndirituedwin.Dto.ApiResponse;
import com.ndirituedwin.Dto.LoginRequest;
import com.ndirituedwin.Dto.SignupRequest;
import com.ndirituedwin.Entity.User;
import com.ndirituedwin.Service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private  final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequest loginRequest){
        return  ResponseEntity.ok(authService.authenticate(loginRequest));
    }
    @PostMapping("/signup")
    public Object signup(@Valid @RequestBody SignupRequest signupRequest){
//       return new ResponseEntity( authService.signup(signupRequest));
       return authService.signup(signupRequest);
    }
    @GetMapping("/accountverification/{vtoken}")
    public ResponseEntity<String> accountverification(@PathVariable("vtoken") String vtoken){
          authService.verifyAccount(vtoken);
          return new ResponseEntity("Account activation successful",HttpStatus.OK);
    }



}
