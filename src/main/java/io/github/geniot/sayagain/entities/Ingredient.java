package io.github.geniot.sayagain.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    Integer id;

    @Column(name = "ingredient_name", unique = true)
    String name;

}
