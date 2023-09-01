package com.example.springmvcapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_seq")
    @SequenceGenerator(name = "message_seq", sequenceName = "message_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Please fill the message")
    @Length(max = 2048, message = "Message is too long (more then 2kB)")
    private String text;
    @Length(max = 255, message = "Tag is too long (more then 255 literals)")
    private String tag;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    private String filename;
    public String getAuthorName(){
        return author != null ? author.getUsername() : "<none>";
     }
}
