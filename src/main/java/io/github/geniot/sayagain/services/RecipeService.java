package io.github.geniot.sayagain.services;

import io.github.geniot.sayagain.entities.Ingredient;
import io.github.geniot.sayagain.entities.Recipe;
import io.github.geniot.sayagain.gen.model.IngredientDto;
import io.github.geniot.sayagain.repositories.IngredientRepository;
import io.github.geniot.sayagain.repositories.RecipeRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.*;

@Component
@Transactional
public class RecipeService {

    @Autowired
    UserService userService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Recipe createRecipe(Recipe recipe) {
        recipe.setUser(userService.getCurrentUser());
        Set<Ingredient> newSet = new HashSet<>();
        for (Ingredient ingredient : CollectionUtils.emptyIfNull(recipe.getIngredients())) {
            Ingredient dbIngredient = ingredientRepository.findIngredientByName(ingredient.getName());
            newSet.add(dbIngredient == null ? ingredient : dbIngredient);
        }
        recipe.setIngredients(newSet);
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Integer recipeId) {
        Optional<Recipe> optionalRecipe = recipeRepository.findByUserAndId(userService.getCurrentUser(), recipeId);
        if (optionalRecipe.isPresent()) {
            recipeRepository.delete(optionalRecipe.get());
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    public Recipe getRecipe(Integer recipeId) {
        Optional<Recipe> optionalRecipe = recipeRepository.findByUserAndId(userService.getCurrentUser(), recipeId);
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
        if (servings != null) {
            predicates.add(criteriaBuilder.equal(root.get("servings"), servings));
        }
        if (includeIngredients != null) {
            Join<Recipe, Ingredient> join = root.join("ingredients", JoinType.INNER);
            for (IngredientDto ingredientDto : includeIngredients) {
                predicates.add(criteriaBuilder.like(join.get("name").as(String.class), ingredientDto.getName()));
            }
        }
        if (excludeIngredients != null) {
            Subquery<Recipe> subQuery = criteriaQuery.subquery(Recipe.class);
            Root<Recipe> subQueryRoot = subQuery.from(Recipe.class);
            List<Predicate> excludePredicates = new ArrayList<>();
            Join<Recipe, Ingredient> join = subQueryRoot.join("ingredients", JoinType.INNER);
            for (IngredientDto ingredientDto : excludeIngredients) {
                excludePredicates.add(criteriaBuilder.like(join.get("name").as(String.class), ingredientDto.getName()));
            }
            subQuery.select(subQueryRoot.get("id")).where(excludePredicates.toArray(new Predicate[0]));
            predicates.add(criteriaBuilder.notEqual(root.get("id"), subQuery));
        }
        if (fullTextQuery != null) {
            predicates.add(criteriaBuilder.like(root.get("description"), "%" + fullTextQuery + "%"));
        }

        predicates.add(criteriaBuilder.equal(root.get("user"), userService.getCurrentUser()));

        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        TypedQuery<Recipe> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
