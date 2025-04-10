package sawi.saas.pos.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sawi.saas.pos.entity.Product;
import sawi.saas.pos.entity.Store;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByStoreId(UUID storeId);

    @Query("SELECT p FROM Product p " +
            "JOIN p.store s " +
            "WHERE s.owner.id = :ownerId")
    List<Product> findByOwnerId(@Param("ownerId") UUID ownerId);
    
    Page<Product> findByStoreId(UUID storeId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.store.id = :storeId AND " +
            "(COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:inStock IS NULL OR (p.stock > 0 AND :inStock = true) OR :inStock = false)")
    Page<Product> findByFilters(
            @Param("storeId") UUID storeId,
            @Param("name") String name,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable);

    UUID store(Store store);
}
