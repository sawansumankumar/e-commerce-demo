package com.ecommerce.repository;

import com.ecommerce.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long>
{

    Optional<Inventory> findByProductId(Long productId);
    @Modifying
    @Query("""
    UPDATE Inventory i
    SET i.quantity = i.quantity - :quantity
    WHERE i.product.id = :productId
    AND i.quantity >= :quantity
    """)
    int decreaseStock(@Param("productId") Long productId,
                      @Param("quantity") Integer quantity);

}
