package sawi.saas.pos.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sawi.saas.pos.dto.UserRequest;
import sawi.saas.pos.dto.UserResponse;
import sawi.saas.pos.entity.Role;
import sawi.saas.pos.entity.Store;
import sawi.saas.pos.entity.User;
import sawi.saas.pos.repository.RoleRepository;
import sawi.saas.pos.repository.StoreRepository;
import sawi.saas.pos.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private StoreRepository storeRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }


    public UserResponse createUser(UserRequest request){
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        if (role.getName().equalsIgnoreCase("CASHIER")) {
            if (request.getStoreId() == null) {
                throw new IllegalArgumentException("Cashiers must be assigned to a store.");
            }
        } else {
            if (request.getStoreId() != null) {
                throw new IllegalArgumentException("Only cashiers can be assigned to a store.");
            }
        }

        if(request.getStoreId() != null) {
            Store store = storeRepository.findById(UUID.fromString(request.getStoreId()))
                    .orElseThrow(() -> new EntityNotFoundException("Store not found"));
            user.setStore(store);
        }

        user.setRole(role);

         User savedUser = userRepository.save(user);

         return mapToUserResponse(savedUser);
    }

    public UserResponse findByEmail(String email) {
         User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found with email : " + email));

         return mapToUserResponse(user);
    }

    public List<UserResponse> findByRoleName(String roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new EntityNotFoundException("Role not found"));

        List<User> allUsers = userRepository.findByRole(role);


        return allUsers.stream().map(this::mapToUserResponse).toList();
    }

    public List<UserResponse> findAllUsers() {


        List<User> allUsers =  userRepository.findAll();

        return allUsers.stream().map(this::mapToUserResponse).toList();
    }

    public User getUserById(String id) {
         return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("User Not Found with email : " + id));


    }

    @Transactional
    public UserResponse updateUser(String id, UserRequest user) {
        User existingUser = getUserById(id);

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

        Role role = roleRepository.findByName(user.getRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        if (role.getName().equalsIgnoreCase("CASHIER")) {
            if (user.getStoreId() == null) {
                throw new IllegalArgumentException("Cashiers must be assigned to a store.");
            }
        } else {
            if (user.getStoreId() != null) {
                throw new IllegalArgumentException("Only cashiers can be assigned to a store.");
            }
        }

        if(user.getStoreId() != null) {
            Store store = storeRepository.findById(UUID.fromString(user.getStoreId()))
                    .orElseThrow(() -> new EntityNotFoundException("Store not found"));
            existingUser.setStore(store);
        }

        existingUser.setRole(role);

        User updatedUser = userRepository.save(existingUser);

        return mapToUserResponse(updatedUser);
    }

    public void deleteUser(String id) {
        User existingUser = getUserById(id);
        userRepository.delete(existingUser);
    }

    public UserResponse mapToUserResponse(User user) {
        String storeId = (user.getStore() != null) ? user.getStore().getId().toString() : null;
        return new UserResponse(
          user.getId(),
          user.getEmail(),
          user.getName(),
          user.getRole().getId(),
          user.getRole().getName(), storeId
        );
    }
}
