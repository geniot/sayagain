package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.entities.Recipe;
import io.github.geniot.sayagain.gen.model.RecipeDto;
import io.github.geniot.sayagain.gen.model.SearchCriteriaDto;
import io.github.geniot.sayagain.services.RecipeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("${apiPrefix}/recipes")
public class RecipeController {
    Logger logger = LoggerFactory.getLogger(RecipeController.class);

    @Autowired
    RecipeService recipeService;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeDto> getRecipes() {
        return recipeService.getRecipes().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeDto> getRecipes(@RequestBody SearchCriteriaDto searchCriteriaDto) {
        return recipeService.getRecipes(searchCriteriaDto.getVegetarian(),
                        searchCriteriaDto.getServings(),
                        searchCriteriaDto.getIncludeIngredients(),
                        searchCriteriaDto.getExcludeIngredients(),
                        searchCriteriaDto.getFullTextQuery())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeDto getRecipe(@PathVariable Integer recipeId) {
        return convertToDto(recipeService.getRecipe(recipeId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDto postRecipe(@RequestBody RecipeDto recipeDto) {
        if (recipeDto.getId() != null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        return convertToDto(recipeService.createRecipe(convertToRecipe(recipeDto)));
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public RecipeDto patchRecipe(@RequestBody RecipeDto recipeDto) {
        if (recipeDto.getId() == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        return convertToDto(recipeService.createRecipe(convertToRecipe(recipeDto)));
    }

    @DeleteMapping
    public void deleteRecipes() {
        recipeService.deleteAll();
    }

    @DeleteMapping("/{recipeId}")
    public void deleteRecipe(@PathVariable Integer recipeId) {
        recipeService.deleteRecipe(recipeId);
    }

    private RecipeDto convertToDto(Recipe recipe) {
        return modelMapper.map(recipe, RecipeDto.class);
    }

    private Recipe convertToRecipe(RecipeDto recipeDto) {
        return modelMapper.map(recipeDto, Recipe.class);
    }
}