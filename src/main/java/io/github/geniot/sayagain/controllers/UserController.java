package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.gen.model.UserDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${apiPrefix}/users")
public class UserController extends BaseController {

    @PostMapping("/signin")
    public String login(@RequestBody UserDto userDto) {
        return userService.signin(userDto.getEmail(), userDto.getPassword());
    }

    @PostMapping("/signup")
    public String signup(@RequestBody UserDto userDto) {
        return userService.signup(userDto.getEmail(), userDto.getPassword());
    }
}
