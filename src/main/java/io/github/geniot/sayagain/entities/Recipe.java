package io.github.geniot.sayagain.entities;


import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@Data
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    Integer id;

    @Column(name = "title", unique = true)
    String title;

    @Column
    String description;

    @Column(columnDefinition = "boolean default false")
    Boolean vegetarian;

    @Column(columnDefinition = "int default 1")
    Integer servings;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(
            name = "xref_recipe_ingredient",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    Set<Ingredient> ingredients;
}
