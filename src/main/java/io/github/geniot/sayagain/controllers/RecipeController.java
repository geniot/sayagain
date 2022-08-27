package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.gen.api.RecipesApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RecipeController implements RecipesApi {
    Logger logger = LoggerFactory.getLogger(RecipeController.class);

}