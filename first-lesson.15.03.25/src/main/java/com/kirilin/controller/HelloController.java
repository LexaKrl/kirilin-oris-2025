package com.kirilin.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class HelloController {

    @GetMapping("/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public String getUser(@PathVariable("user-id") String userId) {
        return "/template/user";
    }
}
