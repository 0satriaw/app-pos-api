package sawi.saas.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sawi.saas.pos.entity.Role;
import sawi.saas.pos.entity.User;

import java.util.Optional;
import java.util.UUID;

import java.util.List;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

}
