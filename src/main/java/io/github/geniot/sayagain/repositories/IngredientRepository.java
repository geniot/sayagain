package io.github.geniot.sayagain.repositories;

import io.github.geniot.sayagain.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    Ingredient findIngredientByName(String name);
}
