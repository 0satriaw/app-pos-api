package sawi.saas.pos.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sawi.saas.pos.dto.UserRequest;
import sawi.saas.pos.entity.Role;
import sawi.saas.pos.entity.User;
import sawi.saas.pos.repository.RoleRepository;
import sawi.saas.pos.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }


    public User createUser(UserRequest request){
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        user.setRole(role);

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found with email : " + email));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("User Not Found with email : " + id));
    }

    @Transactional
    public User updateUser(String id, UserRequest user) {
        User existingUser = getUserById(id);

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

        Role role = roleRepository.findByName(user.getRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        existingUser.setRole(role);

        return userRepository.save(existingUser);
    }

    public void deleteUser(String id) {
        User existingUser = getUserById(id);
        userRepository.delete(existingUser);
    }
}
