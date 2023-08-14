package com.example.springmvcapp.service;

import com.example.springmvcapp.domain.Role;
import com.example.springmvcapp.domain.User;
import com.example.springmvcapp.repos.UserRepo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;

    private final SmtpMailSender smtpMailSender;

    public UserService(UserRepo userRepo, SmtpMailSender smtpMailSender) {
        this.userRepo = userRepo;
        this.smtpMailSender = smtpMailSender;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findUserByUsername(username);
    }

    public void sendMessage(User user){
        if(!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Sweater. PLease, visit next link http://localhost:8080/activate/%s",
                    user.getUsername(),user.getActivationCode()
            );
            smtpMailSender.send(user.getEmail(), "Activation code", message);
        }
    }

    public boolean addUser(User user){
        User userFromDb = userRepo.findUserByUsername(user.getUsername());

        if(userFromDb != null){
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());

        userRepo.save(user);

        sendMessage(user);
        return true;
    }

    public boolean activateUser(String code) {
        User user = userRepo.findUserByActivationCode(code);
        if(user == null){
            return false;
        }

        user.setActivationCode(null);

        userRepo.save(user);
        return true;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void saveUser(User user) {
        userRepo.save(user);
    }

    public void updateUser(User user, String email, String password) {
        String userEmail = user.getEmail();
        boolean isEmailChanged = (!Objects.equals(userEmail, email) && email != null) || (userEmail != null && !Objects.equals(email,userEmail));

        if(isEmailChanged){
            user.setEmail(email);

            if(!StringUtils.isEmpty(email)){
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if(!StringUtils.isEmpty(password)){
            user.setPassword(password);
        }

        userRepo.save(user);

        if(isEmailChanged){
            sendMessage(user);
        }

    }
}
