package com.ndirituedwin.Dto.RequestPayloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceRequest {

    @NotBlank
    private String text;

}
