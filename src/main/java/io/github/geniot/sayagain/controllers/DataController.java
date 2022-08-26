package io.github.geniot.sayagain.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DataController {
    Logger logger = LoggerFactory.getLogger(DataController.class);


    @GetMapping("/welcome")
    public String handleWelcome() {
        return "Branch works";
    }
}

