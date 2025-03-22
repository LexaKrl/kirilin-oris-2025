package com.kirilin.controller;

import com.kirilin.service.HelloService;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final HelloService helloService;

    @GetMapping("/{user-name}")
    @ResponseStatus(HttpStatus.OK)
    public String getUser(@PathVariable("user-name") String userName) {
        return helloService.sayName(userName);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public String hello() {
        return "hello world";
    }
}
