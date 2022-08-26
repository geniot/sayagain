package io.github.geniot.sayagain.controllers;

import com.google.gson.Gson;
import io.github.geniot.sayagain.Model;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.util.SortedMap;
import java.util.TreeMap;


@RestController
public class DataController {
    Logger logger = LoggerFactory.getLogger(DataController.class);
    Gson gson = new Gson();

    @PostMapping("/data")
    public String handle(@RequestBody String payload) {
        try {
            Model model = gson.fromJson(payload, Model.class);
            String res = gson.toJson(model);
            return res;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return ExceptionUtils.getStackTrace(ex);
        }
    }

    @GetMapping("/welcome")
    public String handleWelcome() {
        return "Branch works";
    }
}

