package sawi.saas.pos.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sawi.saas.pos.dto.ProductRequest;
import sawi.saas.pos.dto.ProductResponse;
import sawi.saas.pos.entity.Category;
import sawi.saas.pos.entity.Product;
import sawi.saas.pos.entity.Store;
import sawi.saas.pos.entity.User;
import sawi.saas.pos.repository.CategoryRepository;
import sawi.saas.pos.repository.ProductRepository;
import sawi.saas.pos.repository.StoreRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Transactional
    public ProductResponse createProduct(UUID storeId, ProductRequest productRequest) {
        User currentUser = userService.getCurrentUser();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        Category category = categoryRepository.findById(UUID.fromString(productRequest.getCategoryId()))
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if(!currentUser.getRole().getName().equals("ADMIN")
        && !store.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to add products to this store");
        }

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setStore(store);
        product.setCategory(category);

        if(productRequest.getImageUrl() != null) {
            product.setImageUrl(productRequest.getImageUrl());
        }else{
            product.setImageUrl(null);
        }

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(UUID storeId, String name, BigDecimal minPrice,
                                                BigDecimal maxPrice, Boolean inStock, Pageable pageable){

        storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        Page<Product> products = productRepository.findByFilters(storeId, name, minPrice, maxPrice, inStock, pageable);

        return products.map(this::mapToProductResponse);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllOwnerProduct(UUID ownerId){
        User currentUser = userService.getCurrentUser();

        if (!currentUser.getRole().getName().equals("OWNER") &&
                !currentUser.getId().equals(ownerId)) {
            throw new AccessDeniedException("Only real owners and admins can get products");
        }

        List<Product> products = productRepository.findByOwnerId(ownerId);

        return products.stream().map(this::mapToProductResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllStoreProducts(Pageable pageable){

        Page<Product> products = productRepository.findAll(pageable);

        if(products.getTotalElements() < 1) {
            throw new EntityNotFoundException("Product not found");
        }

        return products.map(this::mapToProductResponse);
    }



    @Transactional
    public ProductResponse getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new EntityNotFoundException("Product not found"));

        return mapToProductResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, ProductRequest productRequest) {
        User currentUser = userService.getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new EntityNotFoundException("Product not found"));

        Category category = categoryRepository.findById(UUID.fromString(productRequest.getCategoryId()))
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Store store = storeRepository.findById(UUID.fromString(productRequest.getStoreId()))
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        if(!currentUser.getRole().getName().equals("ADMIN")
                && !product.getStore().getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to add products to this store");
        }

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setCategory(category);
        product.setStore(store);
        if(productRequest.getImageUrl() != null) {
            product.setImageUrl(productRequest.getImageUrl());
        }else{
            product.setImageUrl(null);
        }

        Product updatedProduct = productRepository.save(product);

        return mapToProductResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        User currentUser = userService.getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new EntityNotFoundException("Product not found"));

        if(!currentUser.getRole().getName().equals("ADMIN")
                && !product.getStore().getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to add products to this store");
        }

        productRepository.delete(product);
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPrice(),
                product.getStock(),
                product.getStore().getId(),
                product.getStore().getName(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

}
