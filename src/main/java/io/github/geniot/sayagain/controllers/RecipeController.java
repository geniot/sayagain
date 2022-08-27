package io.github.geniot.sayagain.controllers;

import io.github.geniot.sayagain.entities.Recipe;
import io.github.geniot.sayagain.gen.model.RecipeDto;
import io.github.geniot.sayagain.services.RecipeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("${apiPrefix}/recipes")
public class RecipeController {
    Logger logger = LoggerFactory.getLogger(RecipeController.class);

    @Autowired
    RecipeService recipeService;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping
    public List<RecipeDto> getRecipes() {
        return recipeService.getRecipes().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private RecipeDto convertToDto(Recipe recipe) {
        RecipeDto postDto = modelMapper.map(recipe, RecipeDto.class);
        postDto.setId(recipe.getId());
        postDto.setTitle(recipe.getTitle());
        postDto.setDescription(recipe.getDescription());
        postDto.setServings(recipe.getServings());
        postDto.setVegetarian(recipe.getVegetarian());
        return postDto;
    }
}