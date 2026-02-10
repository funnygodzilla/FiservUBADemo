package com.fiserv.uba.account.service;

import com.fiserv.uba.account.domain.Permission;
import com.fiserv.uba.account.domain.Role;
import com.fiserv.uba.account.domain.User;
import com.fiserv.uba.account.dto.*;
import com.fiserv.uba.account.repository.RoleRepository;
import com.fiserv.uba.account.repository.UserRepository;
import com.fiserv.uba.account.security.JwtTokenProvider;
import com.fiserv.uba.account.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /**
     * Authenticate user and generate JWT token with claims
     */
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Update last login time
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT token with claims
        String jwt = tokenProvider.generateToken(authentication);

        // Get user principal
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Extract roles and permissions
        List<String> roles = userPrincipal.getAuthorities().stream()
                .filter(auth -> auth.getAuthority().startsWith("ROLE_"))
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());

        List<String> permissions = userPrincipal.getAuthorities().stream()
                .filter(auth -> !auth.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        log.info("User {} logged in successfully with roles: {} and permissions: {}",
                loginRequest.getUsername(), roles, permissions);

        return LoginResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000) // Convert to seconds
                .username(userPrincipal.getUsername())
                .email(userPrincipal.getEmail())
                .customerId(userPrincipal.getCustomerId())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    /**
     * Register a new user
     */
    @Transactional
    public UserDTO register(RegisterRequest registerRequest) {
        log.info("Registering new user: {}", registerRequest.getUsername());

        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .customerId(registerRequest.getCustomerId())
                .enabled(true)
                .accountLocked(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .build();

        // Assign roles
        Set<Role> roles = new HashSet<>();
        if (registerRequest.getRoles() != null && !registerRequest.getRoles().isEmpty()) {
            for (String roleName : registerRequest.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        } else {
            // Default role
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default USER role not found"));
            roles.add(userRole);
        }
        user.setRoles(roles);

        // Save user
        User savedUser = userRepository.save(user);

        log.info("User {} registered successfully", savedUser.getUsername());

        return convertToDTO(savedUser);
    }

    /**
     * Get current authenticated user
     */
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToDTO(user);
    }

    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToDTO(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .collect(Collectors.toList());

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .customerId(user.getCustomerId())
                .enabled(user.getEnabled())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}

