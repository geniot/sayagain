package io.github.geniot.sayagain.services;

import io.github.geniot.sayagain.entities.Recipe;
import io.github.geniot.sayagain.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

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
}
