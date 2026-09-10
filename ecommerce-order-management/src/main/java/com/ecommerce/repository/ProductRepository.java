package com.ecommerce.repository;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.model.Product;


public interface ProductRepository extends JpaRepository<Product, Long>
{
    boolean existsBySku(String sku);
    List<Product> findByCategory(String category);
    List<Product> findByActive(boolean active);
    boolean existsBySkuAndIdNot(String sku, Long id);

}
