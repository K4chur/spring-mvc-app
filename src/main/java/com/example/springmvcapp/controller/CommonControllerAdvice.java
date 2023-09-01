package com.example.springmvcapp.controller;

import com.example.springmvcapp.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonControllerAdvice {
    @ModelAttribute
    public void addAuthenticationInfo(Authentication authentication, Model model){
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("authentication", authentication);
            model.addAttribute("username", authentication.getName());
            User user = (User) authentication.getPrincipal();
            model.addAttribute("authUser", user);
            model.addAttribute("authId", user.getId());
        }
    }
}
