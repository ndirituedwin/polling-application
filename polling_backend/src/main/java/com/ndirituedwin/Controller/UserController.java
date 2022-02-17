package com.ndirituedwin.Controller;

import com.ndirituedwin.Dto.ResponsePayloads.*;
import com.ndirituedwin.Security.CurrentUser;
import com.ndirituedwin.Security.UserPrincipal;
import com.ndirituedwin.Service.PollService;
import com.ndirituedwin.Service.UserService;
import com.ndirituedwin.Utils.AppConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {
     private final UserService userService;
      private final PollService pollService;

     @GetMapping("/currentuser")
     @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentuser(@CurrentUser UserPrincipal currentUser){
         return userService.getCurrentuser(currentUser);
     }
     @GetMapping("/checkUsernameAvailability")
    public UserIdentityAvailability userIdentityAvailability(@RequestParam(value = "username") String username){
       return userService.existsbyusername(username);
     }
    @GetMapping("/checkEmailAvailability")
    public UserIdentityAvailability useremailIdentityAvailability(@RequestParam(value = "email") String email){
        return userService.existsbyemail(email);
    }

    @GetMapping("/profie/{username}")
    public UserProfile userProfile(@PathVariable("username") String username){
         return userService.getuseprofile(username);
    }
    @GetMapping("/{username}/polls")
    public PagedResponse<PollResponse> getpollscreatedBy(@PathVariable("username") String username,@CurrentUser UserPrincipal currentUser,
                                                         @RequestParam(value = "page",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER)int page,
                                                         @RequestParam(value = "size",defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size){
         return pollService.getpollscreatedBy(username,currentUser,page,size);
    }
    @GetMapping("/{username}/votes")
    public PagedResponse<PollResponse> getpollsvotedby(@PathVariable("username") String username, @CurrentUser UserPrincipal currentUser,
         @RequestParam(value = "page",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
         @RequestParam(value = "size",defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size){
         return pollService.getpollsvotedBy(currentUser,username,page,size);
    }
}
