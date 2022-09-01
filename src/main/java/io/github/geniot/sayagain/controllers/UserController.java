package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.entities.SecondConstraintGroup;
import io.github.geniot.sayagain.entities.FirstConstraintGroup;
import io.github.geniot.sayagain.entities.User;
import io.github.geniot.sayagain.exception.CustomException;
import io.github.geniot.sayagain.gen.model.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
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

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getViolations(user, FirstConstraintGroup.class));
        stringBuilder.append(getViolations(user, SecondConstraintGroup.class));

        if (!StringUtils.isEmpty(stringBuilder.toString())) {
            throw new CustomException(stringBuilder.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private String getViolations(User user, Class clazz) {
        StringBuilder stringBuilder = new StringBuilder();
        Set<ConstraintViolation<User>> violations = validator.validate(user, clazz);
        for (ConstraintViolation<User> violation : violations) {
            stringBuilder.append(violation.getMessage());
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}
