package io.github.geniot.sayagain.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "email")
    @Email(message = "Email is invalid.", groups = FirstConstraintGroup.class)
    @NotEmpty(message = "Email address cannot be empty.", groups = FirstConstraintGroup.class)
    String email;

    @Column(name = "password")
    @Size(min = 2, max = 200, message = "Password should be between 2 and 200 characters.", groups = SecondConstraintGroup.class)
    @NotEmpty(message = "Password cannot be empty.", groups = SecondConstraintGroup.class)
    String password;

    @ElementCollection(fetch = FetchType.EAGER)
    List<Role> roles;
}
