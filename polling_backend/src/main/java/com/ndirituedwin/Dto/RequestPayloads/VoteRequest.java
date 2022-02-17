package com.ndirituedwin.Dto.RequestPayloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequest {

    @NotNull
    private Long choiceId;
}
