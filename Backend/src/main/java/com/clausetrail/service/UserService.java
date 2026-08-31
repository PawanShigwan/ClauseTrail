package com.clausetrail.service;

import com.clausetrail.dto.RoleUpdateRequest;
import com.clausetrail.dto.UserDTO;
import com.clausetrail.model.AuditAction;
import com.clausetrail.model.Role;
import com.clausetrail.model.User;
import com.clausetrail.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AuthService authService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public UserService(UserRepository userRepository, AuthService authService, AuditService auditService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(authService::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return authService.toDTO(user);
    }

    public UserDTO updateUserRole(String id, RoleUpdateRequest request, String ipAddress) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        User currentUser = authService.getCurrentUser();
        Role previousRole = targetUser.getRole();
        targetUser.setRole(request.getRole());
        targetUser.setUpdatedAt(Instant.now());

        User saved = userRepository.save(targetUser);

        auditService.logAction(
                null,
                null,
                currentUser,
                AuditAction.ROLE_CHANGE,
                String.format("Updated role for user '%s' (%s) from %s to %s",
                        saved.getName(), saved.getEmail(), previousRole, saved.getRole()),
                null,
                ipAddress
        );

        notificationService.notifyUser(
                saved.getId(),
                "Role Updated",
                "Your role has been updated to " + saved.getRole() + " by " + currentUser.getName(),
                null,
                null,
                null
        );

        return authService.toDTO(saved);
    }

    public List<User> getReviewersAndAdmins() {
        return userRepository.findByRoleIn(List.of(Role.REVIEWER, Role.ADMIN));
    }
}
