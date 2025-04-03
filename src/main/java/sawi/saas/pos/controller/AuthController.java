package sawi.saas.pos.controller;

import io.lettuce.core.models.role.RedisInstance;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import sawi.saas.pos.dto.ApiResponse;
import sawi.saas.pos.dto.LoginResponse;
import sawi.saas.pos.dto.UserResponse;
import sawi.saas.pos.entity.Role;
import sawi.saas.pos.entity.User;
import sawi.saas.pos.model.JwtResponse;
import sawi.saas.pos.model.LoginRequest;
import sawi.saas.pos.model.RegisterRequest;
import sawi.saas.pos.repository.RoleRepository;
import sawi.saas.pos.repository.UserRepository;
import sawi.saas.pos.security.JWTUtil;
import sawi.saas.pos.service.UserService;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    @Autowired
    @Qualifier("customUserDetailsService")
    private final UserDetailsService userDetailsService;

    @Autowired
    private  UserRepository userRepository ;

    @Autowired
    private RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${token.expired.time}")
    private int timeout;
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Date expiredTime = new Date(System.currentTimeMillis()+timeout);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken(authentication,expiredTime);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        UserResponse user = userService.findByEmail(userDetails.getUsername());

//        return ResponseEntity.ok(new JwtResponse(token));
        return ResponseEntity.ok(new ApiResponse<>(true, "User found", getLoginResponse(token, expiredTime, user)));
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

    public LoginResponse getLoginResponse(String token,Date expiresIn, UserResponse userResponse){
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setExpiresIn(expiresIn);
        loginResponse.setUser(userResponse);

        return loginResponse;
    }

}
