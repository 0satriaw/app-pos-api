package sawi.saas.pos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sawi.saas.pos.entity.Store;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

    Page<Store> findByOwnerId(UUID owner, Pageable pageable);

    boolean existsByNameAndOwnerId(String name, UUID ownerId);

    Long countByOwnerId(UUID ownerId);
}
