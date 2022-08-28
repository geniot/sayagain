package io.github.geniot.sayagain.services;

import io.github.geniot.sayagain.entities.Recipe;
import io.github.geniot.sayagain.gen.model.IngredientDto;
import io.github.geniot.sayagain.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

    public Recipe getRecipe(Integer recipeId) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
        if (optionalRecipe.isPresent()) {
            return optionalRecipe.get();
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }


    public List<Recipe> getRecipes(Boolean vegetarian,
                                   Integer servings,
                                   List<IngredientDto> includeIngredients,
                                   List<IngredientDto> excludeIngredients,
                                   String fullTextQuery) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Recipe> criteriaQuery = criteriaBuilder.createQuery(Recipe.class);
        Root<Recipe> root = criteriaQuery.from(Recipe.class);
        List<Predicate> predicates = new ArrayList<>();
        if (vegetarian != null) {
            predicates.add(criteriaBuilder.equal(root.get("vegetarian"), vegetarian));
        }
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        TypedQuery<Recipe> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
