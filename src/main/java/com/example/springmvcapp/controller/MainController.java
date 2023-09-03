package com.example.springmvcapp.controller;

import com.example.springmvcapp.domain.Message;
import com.example.springmvcapp.domain.User;
import com.example.springmvcapp.repos.MessageRepo;
import com.example.springmvcapp.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class MainController {

    private final MessageRepo messageRepo;
    private final MessageService messageService;
    public MainController(MessageRepo messageRepo,MessageService messageService) {
        this.messageRepo = messageRepo;
        this.messageService = messageService;
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
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model
    ){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Message> messagePage = messageService.findPaginated(PageRequest.of(currentPage-1,pageSize),filterValue, null);

        int totalPages = messagePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("filterValue", filterValue);
        model.addAttribute("message", new Message());
        model.addAttribute("messagePage", messagePage);
        model.addAttribute("url", "/index");
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
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model
    ){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Set<Message> messages = user.getMessages();
        List<Message> sortedMessages = new ArrayList<>(messages);
        Collections.sort(sortedMessages, Comparator.comparingLong(Message::getId));
        messages = new LinkedHashSet<>(sortedMessages);

        Page<Message> messagePage = messageService.findPaginated(PageRequest.of(currentPage-1,pageSize ),null, messages);

        int totalPages = messagePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("messagePage", messagePage);

        model.addAttribute("messages", messages);
        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount", user.getSubscribtions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("isCurrentUser", currentUser.equals(user));
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        model.addAttribute("url", "/user-messages/"+user.getId());

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