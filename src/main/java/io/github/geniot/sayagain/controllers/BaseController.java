package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.entities.Ingredient;
import io.github.geniot.sayagain.entities.Recipe;
import io.github.geniot.sayagain.entities.User;
import io.github.geniot.sayagain.gen.model.IngredientDto;
import io.github.geniot.sayagain.gen.model.RecipeDto;
import io.github.geniot.sayagain.gen.model.UserDto;
import io.github.geniot.sayagain.services.RecipeService;
import io.github.geniot.sayagain.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Validation;
import javax.validation.Validator;

public class BaseController {

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    IngredientDto convertIngredientToDto(Ingredient recipe) {
        return modelMapper.map(recipe, IngredientDto.class);
    }

    RecipeDto convertRecipeToDto(Recipe recipe) {
        return modelMapper.map(recipe, RecipeDto.class);
    }

    Recipe convertToRecipe(RecipeDto recipeDto) {
        return modelMapper.map(recipeDto, Recipe.class);
    }

    User convertToUser(UserDto userdto) {
        return modelMapper.map(userdto, User.class);
    }
}
