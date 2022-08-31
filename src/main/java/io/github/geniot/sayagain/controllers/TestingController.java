package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.gen.model.RecipeDto;
import io.github.geniot.sayagain.repositories.IngredientRepository;
import io.github.geniot.sayagain.repositories.RecipeRepository;
import io.github.geniot.sayagain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Profile("!dev")
@RequestMapping("${apiPrefix}/testing")
public class TestingController extends BaseController {

    Logger logger = LoggerFactory.getLogger(TestingController.class);

    @Autowired
    Environment env;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    IngredientRepository ingredientRepository;

    @PostConstruct
    public void postConstruct() {
        if (Arrays.asList(env.getActiveProfiles()).contains("prod")) {
            throw new RuntimeException("Found testing controller in prod.");
        }
    }

    @DeleteMapping("/ingredients")
    public void deleteIngredients() {
        ingredientRepository.deleteAll();
    }

    @DeleteMapping("/recipes")
    public void deleteRecipes() {
        recipeRepository.deleteAll();
    }

    @DeleteMapping("/users")
    public void deleteUsers() {
        userRepository.deleteAll();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeDto> getRecipes() {
        return recipeRepository.findAll().stream()
                .map(this::convertRecipeToDto)
                .collect(Collectors.toList());
    }
}
