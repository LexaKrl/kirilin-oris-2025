package com.kirilin.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String sayName(String name) {
        return "Hello, %s".formatted(name);
    }
}
