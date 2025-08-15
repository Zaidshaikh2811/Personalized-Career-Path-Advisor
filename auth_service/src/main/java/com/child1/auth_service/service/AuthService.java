package com.child1.auth_service.service;



import com.child1.auth_service.Dto.LoginRequest;
import com.child1.auth_service.Dto.TokenResponse;
import com.child1.auth_service.model.AuthRegisterRequest;
import com.child1.auth_service.model.RegisterRequest;
import com.child1.auth_service.model.User;
import com.child1.auth_service.model.UserRole;
import com.child1.auth_service.repo.UserRepository;
import com.child1.commonsecurity.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final WebClient.Builder webClientBuilder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                       WebClient.Builder webClientBuilder) {
        this.userRepository = userRepository;
        this.webClientBuilder = webClientBuilder;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;

    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return new TokenResponse(token);
    }

    @Transactional
    public String register(AuthRegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Save in Auth DB first
        User authUser = new User();
        authUser.setEmail(request.getEmail());
        authUser.setPassword(passwordEncoder.encode(request.getPassword()));
        authUser.setRole(request.getRole() != null ? request.getRole() : UserRole.USER);
        userRepository.save(authUser);
        System.out.println("User saved in auth service: " + authUser.getEmail());

        try {
            // Prepare DTO for user-service
            RegisterRequest profileRequest = new RegisterRequest();
            profileRequest.setName(request.getName());
            profileRequest.setEmail(request.getEmail());
            profileRequest.setFirstName(request.getFirstName() != null ? request.getFirstName() : "DefaultFirst");
            profileRequest.setLastName(request.getLastName() != null ? request.getLastName() : "DefaultLast");
            System.out.println("Preparing profile request for user-service: " + profileRequest);
            webClientBuilder.build()
                    .post()
                    .uri("http://user-service/api/v1/users/create")
                    .bodyValue(profileRequest)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

        } catch (Exception e) {
            // Rollback in case User Service fails
            userRepository.deleteByEmail(request.getEmail());
            throw new RuntimeException("User creation failed in profile service, rolling back", e);
        }

        return "User registered successfully";
    }

    public   boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }
}