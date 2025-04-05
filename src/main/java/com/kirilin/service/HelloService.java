package com.kirilin.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class HelloService {
    public String sayHello(String name) {
        HashMap<String, String> map = new HashMap<>();
        return "Hello, %s".formatted(name);
    }
}
