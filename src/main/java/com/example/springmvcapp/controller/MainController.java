package com.example.springmvcapp.controller;

import com.example.springmvcapp.domain.Message;
import com.example.springmvcapp.domain.User;
import com.example.springmvcapp.repos.MessageRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final MessageRepo messageRepo;

    public MainController(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    @GetMapping
    public String greeting(Model model) {
        return "greeting";
    }

    @Value("${upload.path}")
    private String uploadPath;

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
            @RequestParam(name = "file", required = false) MultipartFile file ,
            @ModelAttribute @Valid  Message message,
            BindingResult bindingResult,
            Model model
    ) throws IOException {

        if(bindingResult.hasErrors()){
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("show", true);
            model.addAttribute("message",message);
        } else{
            if(file != null && !file.getOriginalFilename().isEmpty()){
                File uploadDir = new File(uploadPath);

                if(!uploadDir.exists()){
                    uploadDir.mkdir();
                }
                String uuidFile = UUID.randomUUID().toString();
                String resultFileName = uuidFile + "." + file.getOriginalFilename();
                file.transferTo(new File(uploadPath + "/" + resultFileName));
                message.setFilename(resultFileName);
            }

            message.setAuthor(user);
            messageRepo.save(message);
        }

        model.addAttribute("messages",messageRepo.findAll());
        return "redirect:/index";
    }

    @GetMapping("/user-messages/{user}")
    public String userMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            @RequestParam(name = "message", required = false) Long messageId,
            Model model
    ){
        Set<Message> messages = user.getMessages();
        model.addAttribute("messages", messages);
        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount", user.getSubscribtions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("isCurrentUser", currentUser.equals(user));
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        if(messageId == null){
            model.addAttribute("message", new Message());
        } else {
            model.addAttribute("show", true);
            Optional<Message> messageOptional = messageRepo.findById(messageId);
            if (messageOptional.isPresent()) {
                model.addAttribute("message", messageOptional.get());
            }
        }

        return "userMessages";
    }



}