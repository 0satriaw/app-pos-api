package sawi.saas.pos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sawi.saas.pos.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStoreId(UUID storeId);

    List <Order> findByUserId(UUID userId);

    Page<Order> findByStoreId(UUID storeId, Pageable pageable);

    Page<Order> findByUserId(UUID userId, Pageable pageable);

    Page<Order> findByStoreIdAndStatus(UUID storeId, String status, Pageable pageable);
}
