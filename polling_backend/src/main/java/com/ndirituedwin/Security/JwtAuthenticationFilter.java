package com.ndirituedwin.Security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt=getJwtfromRequest(request);
            log.info("logging the jwt inside jwtauthenticationfiter dofilter method {}",jwt);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                Long userId = jwtTokenProvider.getUserIdFromJwt(jwt);
                log.info("getting userId from jwtTokenProvider {}",userId);
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                log.info("loading userDetails {}",userDetails);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                  log.info("logging authentication {}",authentication);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                 log.info("logging authentication still {}",authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
             log.info("logging the security contextholder {} ",SecurityContextHolder.getContext().getAuthentication());
            }
            }catch (Exception exception) {
            logger.error("Could not set user authentication in security context {}", exception);
        }
        filterChain.doFilter(request,response);
        }

    private String getJwtfromRequest(HttpServletRequest request) {
     String bearerToken=request.getHeader("Authorization");
     log.info("getting the headers {}",bearerToken);
     if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
         return bearerToken.substring(7,bearerToken.length());
     }
     return null;
    }
}
