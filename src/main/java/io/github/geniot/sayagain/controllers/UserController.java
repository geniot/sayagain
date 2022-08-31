package io.github.geniot.sayagain.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${apiPrefix}/users")
public class UserController extends BaseController {

    @GetMapping("/signin")
    public String login(@RequestHeader("X-email") String email,
                        @RequestHeader("X-password") String password) {
        return userService.signin(email, password);
    }

    @GetMapping("/signup")
    public String signup(@RequestHeader("X-email") String email,
                         @RequestHeader("X-password") String password) {
        return userService.signup(email, password);
    }
}
