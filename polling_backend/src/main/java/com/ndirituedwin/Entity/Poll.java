package com.ndirituedwin.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "polls",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"question"}),
})
public class Poll extends UserDateAudit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "question may not be blank")
    @Size(max = 150)
    private String question;
    @OneToMany(mappedBy = "poll",cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @Size(min = 2, max = 50)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 80)
    private List<Choice> choices=new ArrayList<>();
    @NotNull
    private Instant expirationDateTime;


    public  void addChoice(Choice choice){
        choices.add(choice);
        choice.setPoll(this);
    }
    public void removeChoice(Choice choice){
        choices.remove(choice);
        choice.setPoll(null);
    }
}
