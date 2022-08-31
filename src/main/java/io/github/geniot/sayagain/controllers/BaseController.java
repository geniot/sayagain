package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.entities.Ingredient;
import io.github.geniot.sayagain.entities.Recipe;
import io.github.geniot.sayagain.gen.model.IngredientDto;
import io.github.geniot.sayagain.gen.model.RecipeDto;
import io.github.geniot.sayagain.services.RecipeService;
import io.github.geniot.sayagain.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    IngredientDto convertIngredientToDto(Ingredient recipe) {
        return modelMapper.map(recipe, IngredientDto.class);
    }

    RecipeDto convertRecipeToDto(Recipe recipe) {
        return modelMapper.map(recipe, RecipeDto.class);
    }

    Recipe convertToRecipe(RecipeDto recipeDto) {
        return modelMapper.map(recipeDto, Recipe.class);
    }
}
