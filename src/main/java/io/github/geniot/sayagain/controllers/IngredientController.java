package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.gen.model.IngredientDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${apiPrefix}/ingredients")
public class IngredientController extends BaseController {
    Logger logger = LoggerFactory.getLogger(RecipeController.class);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientDto> getIngredients() {
        return ingredientService.getIngredients().stream()
                .map(this::convertIngredientToDto)
                .collect(Collectors.toList());
    }


}
