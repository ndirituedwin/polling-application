package com.ndirituedwin.Service;

import com.ndirituedwin.Dto.RequestPayloads.PollRequest;
import com.ndirituedwin.Dto.RequestPayloads.VoteRequest;
import com.ndirituedwin.Dto.ResponsePayloads.PagedResponse;
import com.ndirituedwin.Dto.ResponsePayloads.PollResponse;
import com.ndirituedwin.Entity.*;
import com.ndirituedwin.Exceptions.BadRequestException;
import com.ndirituedwin.Exceptions.ResourceNotFoundException;
import com.ndirituedwin.Repository.PollRepository;
import com.ndirituedwin.Repository.UserRepository;
import com.ndirituedwin.Repository.VoteRepository;
import com.ndirituedwin.Security.UserPrincipal;
import com.ndirituedwin.Utils.AppConstants;
import com.ndirituedwin.Utils.ModelMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class PollService {
   private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    public PagedResponse<PollResponse> getallpolls(UserPrincipal currentUser, int page, int size) {
        vaidatePageNumberandsize(page,size);
           //time to get polls;
        Pageable pageable= PageRequest.of(page,size, Sort.Direction.DESC,"createdAt");
        log.info("logging pageable {}");
        Page<Poll> polls=pollRepository.findAll(pageable);
        log.info("loging polls from database {}",polls);
        polls.forEach(poll -> {
            log.info("logging pageable poll {}",poll);
            System.out.println("Printing poll pageable "+poll);
        });
        if (polls.getNumberOfElements()==0){
            return new PagedResponse<>(Collections.emptyList(),polls.getNumber(), polls.getSize(), polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
        }
        //map polls to poll responses containing vote count and poll creator details
        List<Long> pollIds=polls.map(poll -> poll.getId()).getContent();
        log.info("logging pollIds {}",pollIds);
        System.out.println("printing pollesponses "+pollIds);

        Map<Long,Long>  choicevotecountmap=getChoiceVoteCountmap(pollIds);
          log.info("choice vote ount map {}",choicevotecountmap);
        Map<Long,Long> pollUserVotemap=getPollUserVoteMap(currentUser,pollIds);
         log.info("poll user vote map {}",pollUserVotemap);
         Map<Long,User> creatormap=getPollCreatorMap(polls.getContent());
         log.info("logging poll creator map {}",creatormap);
         List<PollResponse> pollResponses=polls.map(poll -> {
             return ModelMapper.mapPollToPollResponse(poll,choicevotecountmap,creatormap.get(poll.getCreatedBy()),
                     pollUserVotemap==null? null :
                     pollUserVotemap.getOrDefault(poll.getId(),null));
         }).getContent();
//         return new PagedResponse<>(pollResponses,polls.getNumber()
//         ,polls.getTotalPages(),polls.isLast());

        return new PagedResponse<>(pollResponses, polls.getNumber(),
                polls.getSize(), polls.getTotalElements(), polls.getTotalPages(), polls.isLast());

    }

    private Map<Long, User> getPollCreatorMap(List<Poll> polls) {
    //get poll creator detais of a given list of polls;
        List<Long> creatorIds=polls.stream().map(poll -> poll.getCreatedBy()).distinct().collect(Collectors.toList());
        log.info("creator Ids {}",creatorIds);
        List<User> creators=userRepository.findByIdIn(creatorIds);
        log.info("logging creators {}",creators);
        Map<Long,User> creatorMap=creators.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        return creatorMap;
    }

    private Map<Long,Long> getPollUserVoteMap(UserPrincipal currentUser, List<Long> pollIds) {
      //Retrieve Votes done by the currently logged in user to the given pollIds
        Map<Long,Long> pollUserVoteMap=null;
        if(currentUser !=null){
            List<Vote> userVotes=voteRepository.findByUserIdAndPollIdIn(currentUser.getId(),pollIds);
               log.info("logging user votes {}",userVotes);
               System.out.println("printing user votes"+userVotes);
            pollUserVoteMap=userVotes.stream().collect(Collectors.toMap(vote -> vote.getPoll().getId(),vote -> vote.getChoice().getId()));
        }
        return pollUserVoteMap;
    }

    private Map<Long, Long> getChoiceVoteCountmap(List<Long> pollIds) {
    //retrieve vote counts for every choice beongingto given pollIds;
        List<ChoiceVoteCount> votes=voteRepository.countByPollIdInGroupByChoiceId(pollIds);
        log.info("votes {}",votes);
        Map<Long,Long> choicevotesmap=votes.stream().collect(Collectors.toMap(ChoiceVoteCount::getChoiceId,ChoiceVoteCount::getVoteCount));
         log.info("choice votes map {}",choicevotesmap);

        return choicevotesmap;
    }

    private void vaidatePageNumberandsize(int page, int size) {

         if (page<0){
             throw new BadRequestException("page number may not be less than zero ");
         }
         if (size> AppConstants.MAX_PAGE_SIZE){
             throw new BadRequestException("page size may not be greater than "+AppConstants.MAX_PAGE_SIZE);
         }
    }

    public Poll createPoll(PollRequest pollRequest, UserPrincipal currentUser) {
        log.info("logging the curent user {}",currentUser);
        if (currentUser !=null) {
            User user=userRepository.findByUsername(currentUser.getUsername()).orElseThrow(() -> new UsernameNotFoundException("username "+currentUser.getUsername()+" could not be found"));
            if (user.getIsEnabled()){
                Poll poll = new Poll();
            poll.setQuestion(pollRequest.getQuestion());
            pollRequest.getChoices().forEach(choiceRequest -> {
                poll.addChoice(new Choice(choiceRequest.getText()));
                log.info("choice request {}", choiceRequest);
            });
            Instant expirationDateTime = Instant.now().plus(Duration.ofDays(pollRequest.getPollLength().getDays())).plus(Duration.ofHours(pollRequest.getPollLength().getHours()));
            log.info("expirytiondateTime {}", expirationDateTime);
            poll.setExpirationDateTime(expirationDateTime);
            poll.setCreatedBy(currentUser.getId());
            poll.setUpdatedBy(currentUser.getId());
            log.info("saved poll {}", poll);
            log.info("poll request {}", pollRequest);
            return pollRepository.save(poll);
            }
            throw new RuntimeException("Your account must be activated to create polls");

        }
        throw new RuntimeException("an exception occurred");
    }

    public PollResponse findByPollId(Long pollId, UserPrincipal currentUser) {
        Poll pol=pollRepository.findById(pollId).orElseThrow(() -> new ResourceNotFoundException("poll with  ","id->",pollId+"  could not be found"));
            log.info("logging poll {}",pol);
            log.info("poll created by {}",pol.getCreatedBy());
        //Retrieve vote counts for every choice belonging to the current poll
        List<ChoiceVoteCount> votes=voteRepository.countByPollIdGroupByChoiceId(pollId);
        log.info("logging votes {}",votes);
        Map<Long,Long> choiceVotesMap=votes.stream().collect(Collectors.toMap(ChoiceVoteCount::getChoiceId,ChoiceVoteCount::getVoteCount));
         log.info("choicesvotemap {}",choiceVotesMap);
        //retrieve poll creatordetails
        User creator=userRepository.findById(pol.getCreatedBy()).orElseThrow(() -> new ResourceNotFoundException("user with ","id->",pol.getCreatedBy()+" not found"));
         log.info("creator {}",creator);
        //retrieve post done by logged in user
        Vote uservote=null;
        if(currentUser !=null){
            uservote=voteRepository.findByUserIdAndPollId(currentUser.getId(),pollId);
            log.info("logging current user votes {}",uservote);

        }
        return ModelMapper.mapPollToPollResponse(pol,choiceVotesMap,creator,
                uservote  !=null? uservote.getChoice().getId():null);
    }

    public PollResponse castvoteandgetupdatedpoll(UserPrincipal currentuser, Long pollid, VoteRequest voteRequest) {

        //checking whether poll exists
        Poll poll=pollRepository.findById(pollid).orElseThrow(() -> new ResourceNotFoundException("poll","id no found",pollid));
        log.info("logging the retrieved poll {}",poll);
        if(poll.getExpirationDateTime().isBefore(Instant.now())){
            throw new BadRequestException("Sorry,this poll has aready exipred");
        }
        User user=userRepository.findById(currentuser.getId()).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        log.info("logging the user {}",user);
        Choice selectedchoice=poll.getChoices().stream()
                .filter(choice -> choice.getId().equals(voteRequest.getChoiceId())).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("choice ","with id ->",voteRequest.getChoiceId()+" not found"));
         log.info("selected choice {}",selectedchoice);
         Vote vote=new Vote();
         vote.setPoll(poll);
         vote.setUser(user);
         vote.setChoice(selectedchoice);

         try {
              vote=voteRepository.save(vote);
              log.info("logging the saved vote {}",vote);
         }catch (DataIntegrityViolationException ex){
             log.info("user{} has aready voted {}",currentuser.getId(),pollid);
             throw new BadRequestException("sorry bt yuve aready voted in this poll");
         }
         //vote saved time to return the updated poll Response now
        //retrieve the vote counts of every choice belonging to the current poll
        List<ChoiceVoteCount> votes=voteRepository.countByPollIdGroupByChoiceId(pollid);
         log.info("logging the votes {}",votes);
         Map<Long,Long> choicevotesmap=votes.stream()
                 .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId,ChoiceVoteCount::getVoteCount));
         log.info("logging choicevotesmap {}",choicevotesmap);
         //retrieve poll creator poll details
        User creator=userRepository.findById(poll.getCreatedBy()).orElseThrow(() -> new ResourceNotFoundException("user with","id ->",poll.getCreatedBy()+" not found"));
         log.info("logging creator {}",creator);
        return ModelMapper.mapPollToPollResponse(poll,choicevotesmap,creator,vote.getChoice().getId());
    }

    public PagedResponse<PollResponse> getpollscreatedBy(String username, UserPrincipal currentUser, int page, int size) {

        vaidatePageNumberandsize(page, size);
        User user=userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("user","username",username));
        //Retrieve all polls created By the given username;
        Pageable pageable=PageRequest.of(page, size,Sort.Direction.DESC,"createdAt");
        log.info("logging pageable {}",pageable);
        Page<Poll> polls=pollRepository.findByCreatedBy(user.getId(),pageable);
        log.info("logging polls {}",polls);
        if (polls.getNumberOfElements()==0){
            return new PagedResponse<>(Collections.emptyList(), polls.getNumber(), polls.getSize(), polls.getTotalElements(), polls.getTotalPages(), polls.isLast());

        }
        //map polls to pollresponses containig vote count and poll creator details
        List<Long> pollIds=polls.map(Poll::getId).getContent();
        List<Long> pollIdss=polls.map(Poll::getId).toList();
        log.info("logging poll ids {}",pollIds);
        log.info("logging poll pollIdss {}",pollIdss);
        Map<Long,Long> choicecountvotemap=getChoiceVoteCountmap(pollIds);
        log.info("logging choicecountvotemap {}",choicecountvotemap);
        Map<Long,Long>  polluservotemap=getPollUserVoteMap(currentUser,pollIds);
        log.info("logging polluservotemap {}",polluservotemap);
        List<PollResponse> pollResponses=polls.map(poll -> {
            return ModelMapper.mapPollToPollResponse(poll,choicecountvotemap,user,polluservotemap==null ? null:
                    polluservotemap.getOrDefault(poll.getId(),null));
        }).getContent();
        log.info(" logging pollResponses {}",pollResponses);
        return new PagedResponse<>(pollResponses,polls.getNumber(),polls.getSize(),polls.getTotalElements(),polls.getTotalPages(),polls.isLast());
    }

    public PagedResponse<PollResponse> getpollsvotedBy(UserPrincipal currentUser, String username, int page, int size) {

       vaidatePageNumberandsize(page, size);
       User user=userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("user","username",username));
     log.info("logging user {}",user);
       //retrieve all pollids in which the given username has voted
        Pageable pageable=PageRequest.of(page,size,Sort.Direction.DESC,"createdAt");
        log.info("ogging pageable {}",pageable);
        Page<Long> uservotedpollIds=voteRepository.findVotedPollIdsByUserId(user.getId(),pageable);
        log.info("logging uservoted pollids {}",uservotedpollIds);
        if (uservotedpollIds.getNumberOfElements()==0){
            return new PagedResponse<>(Collections.emptyList(), uservotedpollIds.getNumber(), uservotedpollIds.getSize(),
                    uservotedpollIds.getTotalElements(), uservotedpollIds.getTotalPages(), uservotedpollIds.isLast());
        }
        //retrievea all pol details from the voted pollIds;
        List<Long> pollIds=uservotedpollIds.getContent();
          log.info("logging pollIds {}",pollIds);
          Sort sort=Sort.by(Sort.Direction.DESC,"createdAt");
          List<Poll> polls=pollRepository.findByIdIn(pollIds,sort);
          log.info("logging sort {}",sort);
          log.info("logging polls {}",polls);
          Map<Long,Long> choiceVotecountmap=getChoiceVoteCountmap(pollIds);
            log.info("choicevotecountmap {}",choiceVotecountmap);
          Map<Long,Long> pollUserVotemap=getPollUserVoteMap(currentUser,pollIds);
         log.info("polluservotemap {}",pollUserVotemap);
         Map<Long,User> creatorMap=getPollCreatorMap(polls);
           log.info("creator map {}",creatorMap);
           List<PollResponse> pollResponses=polls.stream().map(poll -> {
               return ModelMapper.mapPollToPollResponse(poll,choiceVotecountmap,
                       creatorMap.get(poll.getCreatedBy()),
                       pollUserVotemap==null?null:
                       pollUserVotemap.getOrDefault(poll.getId(),null));
           }).collect(Collectors.toList());
           return new PagedResponse<>(pollResponses, uservotedpollIds.getNumber(), uservotedpollIds.getSize(),
                   uservotedpollIds.getTotalElements(), uservotedpollIds.getTotalPages(), uservotedpollIds.isLast());
    }
}
