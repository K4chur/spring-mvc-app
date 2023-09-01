package com.example.springmvcapp.controller;

import com.example.springmvcapp.domain.Role;
import com.example.springmvcapp.domain.User;
import com.example.springmvcapp.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model){
        model.addAttribute("users", userService.findAll());
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model){
        model.addAttribute("user",user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String editUser(@ModelAttribute User user){
        userService.saveUser(user);
        return "redirect:/user";
    }

    @GetMapping("/profile")
    public String userProfile(
            Model model,
            @AuthenticationPrincipal User user
    ){
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String editProfile(
            @AuthenticationPrincipal User user,
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ){
        userService.updateUser(user,email,password);

        return "redirect:/user/profile";
    }

    @GetMapping("subscribe/{user}")
    public String subscribe(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            Model model
    ){

        userService.subscribe(currentUser,user);
        return "redirect:/user-messages/" + user.getId();
    }

    @GetMapping("unsubscribe/{user}")
    public String unsubscribe(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            Model model
    ){

        userService.unsubscribe(currentUser,user);
        return "redirect:/user-messages/" + user.getId();
    }

    @GetMapping("{type}/{user}")
    public String subList(
            Model model,
            @PathVariable User user,
            @PathVariable String type
    ){
        model.addAttribute("userChannel", user);
        model.addAttribute("type", type);

        if("subscriptions".equals(type)){
            model.addAttribute("users", user.getSubscribtions());
        } else {
            model.addAttribute("users", user.getSubscribers());
        }

        return "subscriptions";
    }
}

