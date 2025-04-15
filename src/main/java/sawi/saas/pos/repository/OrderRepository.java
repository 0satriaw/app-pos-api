package sawi.saas.pos.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sawi.saas.pos.entity.Order;

import java.time.LocalDateTime;
import java.util.Date;
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

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.store.owner.id = :ownerId AND o.createdAt BETWEEN :from AND :to")
    Double sumTotalPriceByStoreOwnerIdAndCreatedAtBetween(@Param("ownerId") UUID ownerId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    Long countByStore_OwnerIdAndCreatedAtBetween(UUID ownerId, LocalDateTime from, LocalDateTime to);

    Long countByUserIdAndCreatedAtBetween(UUID userId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.user.id = :userId AND o.createdAt BETWEEN :from AND :to")
    Double sumTotalPriceByUserIdAndCreatedAtBetween(@Param("userId") UUID userId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    Page<Order> findByStoreIdAndStatus(UUID storeId, String status, Pageable pageable);

    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Order> findAll(Sort sort);

    List<Order> findByStore_OwnerIdOrderByCreatedAtDesc(UUID ownerId);
}
