package com.ndirituedwin.Dto;

import com.ndirituedwin.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {


    private Long id;
    @NotBlank
    @Size(min = 3,max = 40)
    private String name;
    @NotBlank
    @Size(min = 3,max = 15)
    private String username;

    @NotBlank
    @Size(min = 3,max=50)
    @Email
    private String email;
    @NotBlank
    @Size(min= 6,max=50)
    private String password;
//    private Set<Role> roles=new HashSet<>();

}
