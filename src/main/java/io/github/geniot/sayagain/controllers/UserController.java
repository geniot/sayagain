package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.entities.FirstConstraintGroup;
import io.github.geniot.sayagain.entities.SecondConstraintGroup;
import io.github.geniot.sayagain.entities.User;
import io.github.geniot.sayagain.exception.ApiError;
import io.github.geniot.sayagain.gen.model.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("${apiPrefix}/users")
public class UserController extends BaseController {

    @PostMapping("/signin")
    public String login(@RequestBody UserDto userDto) {
        validate(userDto);
        return userService.signin(userDto.getEmail(), userDto.getPassword());
    }

    @PostMapping("/signup")
    public String signup(@RequestBody UserDto userDto) {
        validate(userDto);
        return userService.signup(userDto.getEmail(), userDto.getPassword());
    }

    private void validate(UserDto userDto) {
        User user = convertToUser(userDto);

        List<String> validationErrors = new ArrayList<>();
        getViolations(user, FirstConstraintGroup.class, validationErrors);
        getViolations(user, SecondConstraintGroup.class, validationErrors);

        if (!validationErrors.isEmpty()) {
            throw new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Some fields are invalid.", validationErrors);
        }
    }

    private void getViolations(User user, Class clazz, List<String> validationErrors) {
        Set<ConstraintViolation<User>> violations = validator.validate(user, clazz);
        for (ConstraintViolation<User> violation : violations) {
            validationErrors.add(violation.getMessage());
        }
    }
}
