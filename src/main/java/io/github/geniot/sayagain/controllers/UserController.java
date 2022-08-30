package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${apiPrefix}/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/signin")
    public String login(@RequestHeader("X-email") String email,
                        @RequestHeader("X-password") String password) {
        return userService.signin(email, password);
    }
}
