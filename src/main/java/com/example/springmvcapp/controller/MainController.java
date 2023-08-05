package com.example.springmvcapp.controller;

import com.example.springmvcapp.domain.Message;
import com.example.springmvcapp.domain.User;
import com.example.springmvcapp.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

@Controller
public class MainController {

    @Autowired
    private MessageRepo messageRepo;

    @GetMapping
    public String greeting( Model model) {
        return "greeting";
    }


    @GetMapping("/index")
    public String index(
            @RequestParam(name = "filterValue", required = false) String filterValue,
            Model model
    ){
        Iterable<Message> messages;
        if(!StringUtils.isEmpty(filterValue)){
            messages = messageRepo.findByTag(filterValue);
        } else {
            messages = messageRepo.findAll();
        }

        model.addAttribute("filterValue", filterValue);
        model.addAttribute("message", new Message());
        model.addAttribute("messages", messages);
        return "index";
    }

    @PostMapping("/index")
    public String messageSubmit(
            @AuthenticationPrincipal User user,
            @ModelAttribute Message message,
            Model model
    ){
        message.setAuthor(user);
        messageRepo.save(message);
        return "redirect:/index";
    }
}