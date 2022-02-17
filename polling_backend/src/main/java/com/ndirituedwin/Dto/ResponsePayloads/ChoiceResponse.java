package com.ndirituedwin.Dto.ResponsePayloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceResponse {

    private Long id;
    private String text;
    private  long voteCount;
}
