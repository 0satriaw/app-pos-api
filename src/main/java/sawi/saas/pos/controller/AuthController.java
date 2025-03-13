package sawi.saas.pos.controller;

import io.lettuce.core.models.role.RedisInstance;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sawi.saas.pos.entity.Role;
import sawi.saas.pos.entity.User;
import sawi.saas.pos.model.JwtResponse;
import sawi.saas.pos.model.LoginRequest;
import sawi.saas.pos.model.RegisterRequest;
import sawi.saas.pos.repository.RoleRepository;
import sawi.saas.pos.repository.UserRepository;
import sawi.saas.pos.security.JWTUtil;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    private  UserRepository userRepository ;

    @Autowired
    private RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JWTUtil jwtUtil, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken(authentication);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest){
        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());

        if(existingUser.isPresent()){
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());

        Role role = roleRepository.findByName(registerRequest.getRole())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        user.setRole(role);

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

}
