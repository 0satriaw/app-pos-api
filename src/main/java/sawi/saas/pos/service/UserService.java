package sawi.saas.pos.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sawi.saas.pos.entity.User;
import sawi.saas.pos.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;

    public User createUser(User user){
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
    public User updateUser(String id, User user) {
        User existingUser = getUserById(id);

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());

        return userRepository.save(existingUser);
    }

    public void deleteUser(String id) {
        User existingUser = getUserById(id);
        userRepository.delete(existingUser);
    }
}
