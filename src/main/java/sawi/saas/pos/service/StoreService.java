package sawi.saas.pos.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sawi.saas.pos.dto.StoreRequest;
import sawi.saas.pos.dto.StoreResponse;
import sawi.saas.pos.entity.Store;
import sawi.saas.pos.entity.User;
import sawi.saas.pos.repository.StoreRepository;
import sawi.saas.pos.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public StoreResponse createStore(StoreRequest storeRequest) {
        User currentUser = userService.getCurrentUser();

        if (!currentUser.getRole().getName().equals("OWNER") &&
                !currentUser.getRole().getName().equals("ADMIN")) {
            throw new AccessDeniedException("Only owners and admins can create stores");
        }

        if(storeRepository.existsByNameAndOwnerId(storeRequest.getName(), currentUser.getId() )) {
            throw new IllegalArgumentException("You already have a store with this name");
        }

        Store store = new Store();
        store.setName(storeRequest.getName());
        store.setAddress(storeRequest.getAddress());
        store.setOwner(currentUser);
        Store savedStore = storeRepository.save(store);

        return mapToStoreResponse(savedStore);
    }

    @Transactional(readOnly = true)
    public Page<StoreResponse> getAllStores(Pageable pageable) {
        User currentUser = userService.getCurrentUser();

        Page<Store> stores;
        if(currentUser.getRole().getName().equals("ADMIN")) {
            stores = storeRepository.findAll(pageable);
        }else{
            stores = storeRepository.findByOwnerId(currentUser.getId(), pageable);
        }

        return stores.map(this::mapToStoreResponse);
    }

    @Transactional(readOnly = true )
    public StoreResponse getStoreById(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new EntityNotFoundException("Store not found"));

        User currentUser = userService.getCurrentUser();

        if(currentUser.getRole().getName().equals("ADMIN")
            && store.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to view this store");
        }

        return mapToStoreResponse(store);
    }

    @Transactional
    public StoreResponse updateStore(UUID storeId, StoreRequest storeRequest) {
        User currentUser = userService.getCurrentUser();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new EntityNotFoundException("Store not found"));

        if(currentUser.getRole().getName().equals("ADMIN")
                && store.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to view this store");
        }

        store.setName(storeRequest.getName());
        store.setAddress(storeRequest.getAddress());

        Store updatedStore = storeRepository.save(store);

        return mapToStoreResponse(updatedStore);
    }

    @Transactional
    public void deleteStore(UUID storeId) {
        User currentUser = userService.getCurrentUser();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        if (!currentUser.getRole().getName().equals("ADMIN") &&
                !store.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this store");
        }

        storeRepository.delete(store);
    }

    private StoreResponse mapToStoreResponse(Store store) {
        return new StoreResponse(
          store.getId(),
          store.getName(),
          store.getAddress(),
          store.getOwner().getId(),
          store.getOwner().getName(),
          store.getCreatedAt(),
          store.getUpdatedAt()
        );
    }
}
