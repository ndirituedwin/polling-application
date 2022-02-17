package com.ndirituedwin.Entity;

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
@Entity
@Table(name = "users",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class User extends DateAudit{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "name may not be blank")
    @Size(max = 40)
    private String name;
    @NotBlank(message = "username may not be blank")
    @Size(max = 15)
    private String username;
    @NaturalId
    @NotBlank(message = "email may not be blank")
    @Size(max=50)
    @Email
    private String email;
    @NotBlank(message = "password may not be blank")
    @Size(max = 100)
    private String password;
    private Boolean isEnabled;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="users_roles",joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles=new HashSet<>();
}
