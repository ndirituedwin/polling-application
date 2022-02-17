package com.ndirituedwin.Service;

import com.ndirituedwin.Dto.ResponsePayloads.UserIdentityAvailability;
import com.ndirituedwin.Dto.ResponsePayloads.UserProfile;
import com.ndirituedwin.Dto.ResponsePayloads.UserSummary;
import com.ndirituedwin.Entity.User;
import com.ndirituedwin.Exceptions.ResourceNotFoundException;
import com.ndirituedwin.Repository.PollRepository;
import com.ndirituedwin.Repository.UserRepository;
import com.ndirituedwin.Repository.VoteRepository;
import com.ndirituedwin.Security.UserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
   private final PollRepository pollRepository;
   private final VoteRepository voteRepository;
    public UserSummary getCurrentuser(UserPrincipal currentUser) {
        UserSummary userSummary=new UserSummary();
        userSummary.setId(currentUser.getId());
        userSummary.setName(currentUser.getName());
        userSummary.setUsername(currentUser.getUsername());
        log.info("logging the current user {}",userSummary);
        return userSummary;
    }

    public UserIdentityAvailability existsbyusername(String username) {
     Boolean isAvailabe=!userRepository.existsByUsername(username);
     log.info("logging useridentityavailability {}",isAvailabe);
     return new UserIdentityAvailability(isAvailabe);
    }
    public UserIdentityAvailability existsbyemail(String email) {
        Boolean isAvailabe=!userRepository.existsByEmail(email);
        log.info("logging useridentityavailability {}",isAvailabe);
        return new UserIdentityAvailability(isAvailabe);
    }

    public UserProfile getuseprofile(String username) {
      User user=userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("user","username",username));
      log.info("logging the user {}",user);
      long pollcount=pollRepository.countByCreatedBy(user.getId());
      log.info("logging poll count {}",pollcount);
      long voteCount=voteRepository.countByUserId(user.getId());
      log.info("logging votecount {}",voteCount);
      UserProfile userProfile=new UserProfile();
      userProfile.setId(user.getId());
      userProfile.setName(user.getName());
      userProfile.setUsername(user.getUsername());
      userProfile.setJoinedAt(user.getCreatedAt());
      userProfile.setPollCount(pollcount);
      userProfile.setVoteCount(voteCount);
      log.info("logging user profile {}",userProfile);
      return  userProfile;

    }
}
