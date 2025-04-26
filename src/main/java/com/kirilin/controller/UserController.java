package com.kirilin.controller;

import com.kirilin.entity.User;
import com.kirilin.repository.UserRepository;
import com.kirilin.repository.UserRepositoryHiber;
import com.kirilin.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {


    private final HelloService helloService;
    private final UserRepository userRepository;

/*    @GetMapping(value = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getUsers() {
        return userRepository.findAll();
    }*/

    @GetMapping("/registration")
    @ResponseStatus(HttpStatus.OK)
    public String usersPage() {
        return "registration";
    }

    @GetMapping("/hello")
    public String hello(@RequestParam("name") String name) {
        return helloService.sayHello(name);
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@ModelAttribute("user") User user, BindingResult result) {
        userRepository.save(user);
    }
}
