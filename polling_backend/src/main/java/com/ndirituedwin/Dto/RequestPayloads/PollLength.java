package com.ndirituedwin.Dto.RequestPayloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollLength {

    @NotNull
    @Max(7)
    private Integer days;
    @NotNull
    @Max(23)
    private Integer hours;

}
