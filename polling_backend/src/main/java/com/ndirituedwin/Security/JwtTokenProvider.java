package com.ndirituedwin.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String JwtSecret;
    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public  String generateToken(Authentication authentication){
        log.info("logging the authentication inside jwtTokenProvider {}",authentication);
        UserPrincipal userPrincipal= (UserPrincipal) authentication.getPrincipal();
        Date now=new Date();
        Date expiryDate=new Date(new Date().getTime()+jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256,JwtSecret)
                .compact();

    }
public  Long getUserIdFromJwt(String authToken){
        Claims claims=Jwts.parser().setSigningKey(JwtSecret)
                .parseClaimsJws(authToken).getBody();

      return Long.parseLong(claims.getSubject());
    }
 public boolean validateToken(String authToken){
                try {
                    Jwts.parser().setSigningKey(JwtSecret).parseClaimsJws(authToken);
                return true;
                }catch(SignatureException exception){
                  log.error("invaid JWT signature {}",exception.getMessage());
                }catch (MalformedJwtException ex){
                    log.error("Invalid jwt token {}",ex.getMessage());
                }catch (ExpiredJwtException ex){
                    log.error("Expired jwt token {}",ex.getMessage());
                }catch (UnsupportedJwtException exception){
                    log.error("Unsupported Jwt token {}",exception.getMessage());
                }catch (IllegalArgumentException e){
                    log.error("Jwt claims string is empty {}",e.getMessage());

                }
                return false;
                 }
}
