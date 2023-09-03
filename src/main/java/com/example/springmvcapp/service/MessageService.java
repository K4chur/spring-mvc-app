package com.example.springmvcapp.service;

import com.example.springmvcapp.domain.Message;
import com.example.springmvcapp.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class MessageService {
    private MessageRepo messageRepo;
    public MessageService(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    public Page<Message> findPaginated(Pageable pageable, String type, Set<Message> userMessages){
        List<Message> messages;
        if(userMessages==null){
            if(type == null){
                messages  = (List<Message>) messageRepo.findAll();
            } else if (type.isEmpty()) {
                messages  = (List<Message>) messageRepo.findAll();
            } else {
                messages = (List<Message>) messageRepo.findByTag(type);

            }
        } else {
            messages = new ArrayList<>(userMessages);
        }

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Message> list;

        if (messages.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, messages.size());
            list = messages.subList(startItem, toIndex);
        }

        Page<Message> messagePage
                = new PageImpl<Message>(list, PageRequest.of(currentPage, pageSize), messages.size());

        return messagePage;
    }
}
