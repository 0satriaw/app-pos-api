package sawi.saas.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sawi.saas.pos.entity.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}
