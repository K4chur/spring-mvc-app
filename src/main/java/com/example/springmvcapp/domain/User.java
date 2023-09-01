package com.example.springmvcapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="usr")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr_seq")
    @SequenceGenerator(name = "usr_seq", sequenceName = "usr_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Username can`t be empty")
    private String username;
    @NotBlank(message = "Password can`t be empty")
    private String password;
    @NotBlank(message = "Password confirmation can`t be empty")
    @Transient
    private String passwordConfirmation;

    private boolean active;
    @NotBlank(message = "Email can`t be empty")
    @Email(message = "Email is not correct")
    private String email;
    private String activationCode;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Message> messages;

    @ManyToMany
    @JoinTable(
            name="user_subscriptions",
            joinColumns = { @JoinColumn(name = "subscriber_id")},
            inverseJoinColumns = { @JoinColumn(name = "channel_id")}
    )
    private Set<User> subscribtions = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name="user_subscriptions",
            joinColumns = { @JoinColumn(name = "channel_id")},
            inverseJoinColumns = { @JoinColumn(name = "subscriber_id")}
    )
    private Set<User> subscribers = new HashSet<>();
    public boolean isAdmin(){
        return roles.contains(Role.ADMIN);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
