package io.github.geniot.sayagain.services;

import io.github.geniot.sayagain.entities.Ingredient;
import io.github.geniot.sayagain.repositories.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@Transactional
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    public void deleteAll() {
        ingredientRepository.deleteAll();
    }

    public List<Ingredient> getIngredients() {
        return ingredientRepository.findAll();
    }
}
