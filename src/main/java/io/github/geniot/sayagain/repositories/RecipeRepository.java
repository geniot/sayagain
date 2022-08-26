package io.github.geniot.sayagain.repositories;

import io.github.geniot.sayagain.entities.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Integer> {

}
