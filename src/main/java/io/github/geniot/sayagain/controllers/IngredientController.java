package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.entities.Ingredient;
import io.github.geniot.sayagain.gen.model.IngredientDto;
import io.github.geniot.sayagain.services.IngredientService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${apiPrefix}/ingredients")
public class IngredientController {
    Logger logger = LoggerFactory.getLogger(RecipeController.class);

    @Autowired
    Environment env;

    @Autowired
    IngredientService ingredientService;

    @Autowired
    ModelMapper modelMapper;

    @DeleteMapping
    public void deleteRecipes() {
//        if (Arrays.asList(env.getActiveProfiles()).contains("prod")) {
//            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
//        }
        ingredientService.deleteAll();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientDto> getIngredients() {
        return ingredientService.getIngredients().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private IngredientDto convertToDto(Ingredient recipe) {
        return modelMapper.map(recipe, IngredientDto.class);
    }
}
