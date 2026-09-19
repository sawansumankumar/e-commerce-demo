package com.ecommerce.service;


import com.ecommerce.dto.request.CreateProductRequest;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.dto.request.UpdateProductRequest;
import com.ecommerce.exception.DuplicateSkuException;
import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.exception.ProductsNotFoundException;
import com.ecommerce.model.Inventory;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


@Service
public class ProductService {

    private final ProductRepository productRepository;


    public List<ProductResponse> getProducts()
    {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> responses = new ArrayList<>();

        for(Product product : products)
        {
             ProductResponse response = mapToResponse(product);
             responses.add(response);
        }

        return responses;
    }

   public ProductResponse mapToResponse(Product product)
   {
       ProductResponse response = new ProductResponse();
       response.setName(product.getName());
       response.setId(product.getId());
       response.setPrice(product.getPrice());
       response.setDescription(product.getDescription());
       response.setSku(product.getSku());
       response.setCategory(product.getCategory());
       response.setActive(product.getActive());

       return response;
   }

    public ProductResponse getProductById(Long id)
    {
         Product product = productRepository.findById(id).orElseThrow(
                 () -> new ProductNotFoundException("Product with id " + id + " not found"));

         return mapToResponse(product);
    }

    public List<ProductResponse> getProductsByCategory(String category)
    {
        List<Product> products = productRepository.findByCategory(category);
        List<ProductResponse> responses = new ArrayList<>();


        if(products.isEmpty())
        {
            throw new ProductsNotFoundException( "No products found for category " + category);
        }

        for(Product product : products)
        {
            ProductResponse response = mapToResponse(product);
            responses.add(response);
        }

        return responses;
    }


    public ProductService(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(CreateProductRequest request)
    {
        if(productRepository.existsBySku(request.getSku()))
        {
            throw new DuplicateSkuException("Product with SKU " + request.getSku() + " already exists" );
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setCategory(request.getCategory());
        product.setActive(true);

        Inventory inventory = new Inventory();
        inventory.setQuantity(request.getQuantity());
        inventory.setProduct(product);
        product.setInventory(inventory);

        Product savedProduct = productRepository.save(product);

        ProductResponse response = mapToResponse(savedProduct);

        return response;
    }



    public ProductResponse updateProduct(Long id, UpdateProductRequest request)
    {
        Product existingProduct = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product with id " + id + " not found"));


        if(productRepository.existsBySkuAndIdNot(request.getSku(), id))
        {
            throw new DuplicateSkuException("This SKU is already present");
        }


        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setSku(request.getSku());
        existingProduct.setActive(request.getActive());

        Product product = productRepository.save(existingProduct);
        ProductResponse response = mapToResponse(product);

        return response;
    }

    public List<ProductResponse> getProductByActive(boolean active)
    {
        List<Product> products = productRepository.findByActive(active);
        List<ProductResponse> responses = new ArrayList<>();
        if(products.isEmpty())
        {
            throw new ProductsNotFoundException("No products found with active = " + active);
        }

        for(Product product  : products)
        {
            ProductResponse response = mapToResponse(product);
            responses.add(response);
        }

        return responses;
    }

    public void deleteProduct(Long id)
    {
       Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

        product.setActive(false);

        productRepository.save(product);

    }

    public Page<ProductResponse> getProducts(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);
        Page<ProductResponse> responses = products.map(product -> mapToResponse(product));
        return responses;
    }


}
