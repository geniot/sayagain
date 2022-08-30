package io.github.geniot.sayagain.repositories;

import io.github.geniot.sayagain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String username);
}
