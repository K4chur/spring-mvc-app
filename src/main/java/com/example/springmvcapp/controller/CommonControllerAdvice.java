package com.example.springmvcapp.controller;

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
            model.addAttribute("principal", authentication.getPrincipal());
        }
    }
}
