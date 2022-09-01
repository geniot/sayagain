package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.exception.ApiError;
import io.github.geniot.sayagain.gen.model.IngredientDto;
import io.github.geniot.sayagain.gen.model.RecipeDto;
import io.github.geniot.sayagain.gen.model.SearchCriteriaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("${apiPrefix}/recipes")
public class RecipeController extends BaseController{
    Logger logger = LoggerFactory.getLogger(RecipeController.class);

    /**
     * CREATE
     *
     * @param recipeDto
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDto postRecipe(@RequestBody RecipeDto recipeDto) {
        if (recipeDto.getId() != null) {
            throw new ApiError(HttpStatus.BAD_REQUEST, "Recipe id is not empty.");
        }
        if (recipeDto.getIngredients() != null) {
            for (IngredientDto ingredientDto : recipeDto.getIngredients()) {
                if (ingredientDto.getId() != null) {
                    throw new ApiError(HttpStatus.BAD_REQUEST, "Ingredient id is not empty.");
                }
            }
        }
        return convertRecipeToDto(recipeService.createRecipe(convertToRecipe(recipeDto)));
    }

    /**
     * READ
     *
     * @param recipeId
     * @return
     */
    @GetMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeDto getRecipe(@PathVariable Integer recipeId) {
        return convertRecipeToDto(recipeService.getRecipe(recipeId));
    }

    /**
     * UPDATE
     *
     * @param recipeDto
     * @return
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public RecipeDto putRecipe(@RequestBody RecipeDto recipeDto) {
        if (recipeDto.getId() == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        return convertRecipeToDto(recipeService.createRecipe(convertToRecipe(recipeDto)));
    }

    /**
     * DELETE
     *
     * @param recipeId
     */
    @DeleteMapping("/{recipeId}")
    public void deleteRecipe(@PathVariable Integer recipeId) {
        recipeService.deleteRecipe(recipeId);
    }

    /**
     * FIND
     *
     * @param searchCriteriaDto
     * @return
     */
    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeDto> getRecipes(@RequestBody SearchCriteriaDto searchCriteriaDto) {
        return recipeService.getRecipes(searchCriteriaDto.getVegetarian(),
                        searchCriteriaDto.getServings(),
                        searchCriteriaDto.getIncludeIngredients(),
                        searchCriteriaDto.getExcludeIngredients(),
                        searchCriteriaDto.getKeyword())
                .stream()
                .map(this::convertRecipeToDto)
                .collect(Collectors.toList());
    }
}