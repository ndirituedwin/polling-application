package com.ndirituedwin.Utils;


import com.ndirituedwin.Dto.ResponsePayloads.ChoiceResponse;
import com.ndirituedwin.Dto.ResponsePayloads.PollResponse;
import com.ndirituedwin.Dto.ResponsePayloads.UserSummary;
import com.ndirituedwin.Entity.Choice;
import com.ndirituedwin.Entity.Poll;
import com.ndirituedwin.Entity.User;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ModelMapper {

    public static PollResponse mapPollToPollResponse(Poll poll, Map<Long,Long> choiVotesMap, User creator,Long userVote){
        PollResponse pollResponse=new PollResponse();
        pollResponse.setId(poll.getId());
        pollResponse.setQuestion(poll.getQuestion());
        pollResponse.setCreationDateTime(poll.getCreatedAt());
        pollResponse.setExpirationDateTime(poll.getExpirationDateTime());
        pollResponse.setIsExpired(poll.getExpirationDateTime().isBefore(Instant.now()));
//        pollResponse.setChoices();
        List<ChoiceResponse> choiceResponses=
                poll.getChoices().stream().map(choice -> {
                    ChoiceResponse choiceResponse=new ChoiceResponse();
                    choiceResponse.setId(choice.getId());
                    choiceResponse.setText(choice.getText());
                    if (choiVotesMap.containsKey(choice.getId())){
                        choiceResponse.setVoteCount(choiVotesMap.get(choice.getId()));
                    }else{
                        choiceResponse.setVoteCount(0);
                    }
                    return choiceResponse;

                }).collect(Collectors.toList());
        pollResponse.setChoices(choiceResponses);
        log.info("logging choiceresponse {}",choiceResponses);
        UserSummary creatorSummary=new UserSummary();
        creatorSummary.setId(creator.getId());
        creatorSummary.setName(creator.getName());
        creatorSummary.setUsername(creator.getEmail());
        pollResponse.setCreatedBy(creatorSummary);
        if (userVote !=null){
            pollResponse.setSelectedChoice(userVote);
        }
        long totalVotes=pollResponse.getChoices().stream().mapToLong(ChoiceResponse::getVoteCount).sum();
        pollResponse.setTotalVotes(totalVotes);
        return pollResponse;

    }

}
