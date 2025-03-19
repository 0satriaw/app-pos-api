package sawi.saas.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sawi.saas.pos.entity.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
     boolean existsCategoryByName(String name);

}
