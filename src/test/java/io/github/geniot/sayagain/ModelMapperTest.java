package io.github.geniot.sayagain;

import io.github.geniot.sayagain.entities.Recipe;
import io.github.geniot.sayagain.gen.model.RecipeDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ModelMapperTest {
    @Test
    public void testNull() {
        WebConfig webConfig = new WebConfig();
        ModelMapper modelMapper = webConfig.modelMapper();
        Recipe recipe = new Recipe();
        recipe.setIngredients(new HashSet<>());
        RecipeDto recipeDto = modelMapper.map(recipe, RecipeDto.class);
        assertNotNull(recipeDto.getIngredients());
    }
}
