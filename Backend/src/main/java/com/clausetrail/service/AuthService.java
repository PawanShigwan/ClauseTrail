package com.clausetrail.service;

import com.clausetrail.config.JwtUtils;
import com.clausetrail.dto.AuthResponse;
import com.clausetrail.dto.LoginRequest;
import com.clausetrail.dto.RegisterRequest;
import com.clausetrail.dto.UserDTO;
import com.clausetrail.model.AuditAction;
import com.clausetrail.model.Role;
import com.clausetrail.model.User;
import com.clausetrail.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.auditService = auditService;
    }

    public AuthResponse login(LoginRequest request, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));

        String token = jwtUtils.generateToken(user);

        auditService.logAction(
                null,
                null,
                user,
                AuditAction.LOGIN,
                "User logged in successfully with role " + user.getRole(),
                null,
                ipAddress
        );

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .department(user.getDepartment())
                .title(user.getTitle())
                .build();
    }

    public AuthResponse register(RegisterRequest request, String ipAddress) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use: " + request.getEmail());
        }

        Role assignedRole = request.getRole() != null ? request.getRole() : Role.VIEWER;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(assignedRole)
                .department(request.getDepartment())
                .title(request.getTitle())
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        user = userRepository.save(user);

        String token = jwtUtils.generateToken(user);

        auditService.logAction(
                null,
                null,
                user,
                AuditAction.USER_REGISTER,
                "Registered new user with role " + user.getRole(),
                null,
                ipAddress
        );

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .department(user.getDepartment())
                .title(user.getTitle())
                .build();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("No authenticated user in context");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Current user not found: " + authentication.getName()));
    }

    public UserDTO getCurrentUserDTO() {
        User user = getCurrentUser();
        return toDTO(user);
    }

    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .department(user.getDepartment())
                .title(user.getTitle())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
