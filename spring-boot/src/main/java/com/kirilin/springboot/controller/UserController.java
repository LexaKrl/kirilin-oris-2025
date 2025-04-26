package com.kirilin.springboot.controller;


import com.kirilin.springboot.dto.CreateUserDto;
import com.kirilin.springboot.dto.UserDto;
import com.kirilin.springboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/{username}/verification")
    public String verify(@RequestParam String code, @PathVariable String username) {
        return userService.verify(username, code) ? "verification_success" : "verification_failure";
    }

    @PostMapping("/user")
    public String createUser(@RequestBody CreateUserDto userDto, HttpServletRequest request) {
        String url = request.getRequestURL().toString().replace(request.getServletPath(), "");
        userService.create(userDto, url);
        return "sign_up_success";
    }
}
