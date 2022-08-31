package io.github.geniot.sayagain.repositories;

import io.github.geniot.sayagain.entities.Recipe;
import io.github.geniot.sayagain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    Optional<Recipe> findByUserAndId(User user, Integer id);
}
