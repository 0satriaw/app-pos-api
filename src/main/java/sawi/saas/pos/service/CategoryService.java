package sawi.saas.pos.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sawi.saas.pos.dto.ApiResponse;
import sawi.saas.pos.dto.CategoryRequest;
import sawi.saas.pos.dto.CategoryResponse;
import sawi.saas.pos.dto.StoreResponse;
import sawi.saas.pos.entity.Category;
import sawi.saas.pos.entity.Store;
import sawi.saas.pos.entity.User;
import sawi.saas.pos.repository.CategoryRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryResponse createCategory(CategoryRequest request) {
        User currentUser = userService.getCurrentUser();

        if (!currentUser.getRole().getName().equals("ADMIN")) {
            throw new AccessDeniedException("Only admins can create categories");
        }


        if(categoryRepository.existsCategoryByName(request.getName())) {
            throw new IllegalArgumentException("You already have a categories with this name");
        }

        Category category = new Category();
        category.setName(request.getName());
        Category savedCategory = categoryRepository.save(category);

        return mapToCategory(savedCategory);
    }

    public CategoryResponse updateCategory(CategoryRequest request, UUID categoryId) {
        User currentUser = userService.getCurrentUser();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new EntityNotFoundException("Category not found"));

        if(currentUser.getRole().getName().equals("ADMIN")) {
            throw new AccessDeniedException("You are not authorized to view this store");
        }

        category.setName(request.getName());

        Category updatedCategory = categoryRepository.save(category);

        return mapToCategory(updatedCategory);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        User currentUser = userService.getCurrentUser();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if (!currentUser.getRole().getName().equals("ADMIN")) {
            throw new AccessDeniedException("You are not authorized to delete this store");
        }

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Category not found"));

        User currentUser = userService.getCurrentUser();

        if(currentUser.getRole().getName().equals("ADMIN")) {
            throw new AccessDeniedException("You are not authorized to view this store");
        }

        return mapToCategory(category);
    }

    @Transactional(readOnly = true )
    public List<CategoryResponse> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(this::mapToCategory).toList();
    }

    private CategoryResponse mapToCategory(Category category) {
        return new CategoryResponse(
                category.getId().toString(),
                category.getName(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
