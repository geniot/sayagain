package io.github.geniot.sayagain.services;

import io.github.geniot.sayagain.entities.Ingredient;
import io.github.geniot.sayagain.entities.Recipe;
import io.github.geniot.sayagain.entities.User;
import io.github.geniot.sayagain.gen.model.IngredientDto;
import io.github.geniot.sayagain.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
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

    public Recipe createRecipe(Recipe recipe) {
        recipe.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return recipeRepository.save(recipe);
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
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        TypedQuery<Recipe> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
