package sawi.saas.pos.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sawi.saas.pos.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStoreId(UUID storeId);

    List <Order> findByUserId(UUID userId);

@Query(value = "SELECT o.* FROM orders o " +
               "JOIN stores s ON o.store_id = s.id " +
               "WHERE s.owner_id = :ownerId " +
               "ORDER BY CASE WHEN o.status = 'PENDING' THEN 1 ELSE 2 END, o.status ASC",
       nativeQuery = true)
    List<Order> findByOwnerId(@Param("ownerId") UUID ownerId);

    Page<Order> findByStoreId(UUID storeId, Pageable pageable);

    List<Order> findByUserId(UUID userId, Pageable pageable);

    Page<Order> findByStoreIdAndStatus(UUID storeId, String status, Pageable pageable);
}
