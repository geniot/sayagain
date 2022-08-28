package io.github.geniot.sayagain.services;

import io.github.geniot.sayagain.entities.Recipe;
import io.github.geniot.sayagain.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class RecipeService {
    @Autowired
    RecipeRepository recipeRepository;

    public List<Recipe> getRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe createRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public void deleteAll() {
        recipeRepository.deleteAll();
    }

    public void deleteRecipe(Integer recipeId) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
        if (optionalRecipe.isPresent()) {
            recipeRepository.delete(optionalRecipe.get());
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }
}
