package com.example.springmvcapp.repos;

import com.example.springmvcapp.domain.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepo extends CrudRepository<Message, Long> {
    Iterable<Message> findByTag(String filterValue);
}
