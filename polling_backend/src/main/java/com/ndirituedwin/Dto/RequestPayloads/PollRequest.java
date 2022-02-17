package com.ndirituedwin.Dto.RequestPayloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollRequest {
    @NotBlank
    @Size(max = 150)
    private String question;

    @NotNull
    @Size(min = 2,max = 6)
    @Valid
    private HashSet<ChoiceRequest> choices;
    @NotNull
    @Valid
    private PollLength pollLength;

}
