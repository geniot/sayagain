package io.github.geniot.sayagain.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${apiPrefix}/ingredients")
public class IngredientController extends BaseController {
    Logger logger = LoggerFactory.getLogger(RecipeController.class);




}
