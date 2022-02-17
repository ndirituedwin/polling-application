package com.ndirituedwin.Controller;

import com.ndirituedwin.Dto.ApiResponse;
import com.ndirituedwin.Dto.RequestPayloads.PollRequest;
import com.ndirituedwin.Dto.RequestPayloads.VoteRequest;
import com.ndirituedwin.Dto.ResponsePayloads.PagedResponse;
import com.ndirituedwin.Dto.ResponsePayloads.PollResponse;
import com.ndirituedwin.Entity.Poll;
import com.ndirituedwin.Security.CurrentUser;
import com.ndirituedwin.Security.UserPrincipal;
import com.ndirituedwin.Service.PollService;
import com.ndirituedwin.Utils.AppConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/polls")
public class PollController {

  private PollService pollService;
  @GetMapping("/getall")
    public PagedResponse<PollResponse> getallpolls(@CurrentUser UserPrincipal currentUser
          ,@RequestParam(value = "page",defaultValue=AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                   @RequestParam(value = "size",defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size){
   return pollService.getallpolls(currentUser,page,size);
  }
  @PostMapping("/createpoll")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPoll(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody PollRequest pollRequest){
      Poll poll=pollService.createPoll(pollRequest,currentUser);
      URI location= ServletUriComponentsBuilder.fromCurrentContextPath().path("/{pollId}")
              .buildAndExpand(poll.getId()).toUri();
      return ResponseEntity.created(location).body(new ApiResponse(true,"Poll created by successfully"));

  }
  @GetMapping("/getbypollid/{pollId}")
  public ResponseEntity<PollResponse> getpollbyid(@CurrentUser UserPrincipal currentUser,@PathVariable("pollId") Long pollId){
    return new ResponseEntity(pollService.findByPollId(pollId,currentUser), HttpStatus.OK);
  }

  @PostMapping("/{pollId}/votes")
  @PreAuthorize("hasRole('USER')")
  public PollResponse castvote(@CurrentUser UserPrincipal currentuser, @PathVariable("pollId") Long pollid, @Valid @RequestBody VoteRequest voteRequest){

    return pollService.castvoteandgetupdatedpoll(currentuser,pollid,voteRequest);
  }
}
