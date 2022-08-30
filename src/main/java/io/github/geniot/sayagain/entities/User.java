package io.github.geniot.sayagain.entities;

import lombok.Data;

import javax.persistence.*;
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
    String email;

    @Column(name = "password")
    String password;

    @ElementCollection(fetch = FetchType.EAGER)
    List<Role> roles;
}
