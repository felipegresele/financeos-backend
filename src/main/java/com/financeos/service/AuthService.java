package com.financeos.service;

import com.financeos.dto.AuthDTOs.*;
import com.financeos.entity.Category;
import com.financeos.entity.User;
import com.financeos.exception.BusinessException;
import com.financeos.repository.CategoryRepository;
import com.financeos.repository.UserRepository;
import com.financeos.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use");
        }
        var user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();
        user = userRepository.save(user);
        seedDefaultCategories(user);

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("User not found"));

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        var user = userRepository.findByRefreshToken(request.getRefreshToken())
            .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (jwtService.isTokenExpired(request.getRefreshToken())) {
            throw new BusinessException("Refresh token expired");
        }
        var accessToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return buildAuthResponse(accessToken, newRefreshToken, user);
    }

    public void logout(String userId) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setRefreshToken(null);
            userRepository.save(u);
        });
    }

    private AuthResponse buildAuthResponse(String access, String refresh, User user) {
        return AuthResponse.builder()
            .accessToken(access)
            .refreshToken(refresh)
            .tokenType("Bearer")
            .expiresIn(jwtService.getExpiration())
            .user(new com.financeos.dto.UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole().name()))
            .build();
    }

    private void seedDefaultCategories(User user) {
        List<Category> defaults = List.of(
            Category.builder().name("Salário").color("#00e5a0").icon("💼").type(Category.CategoryType.INCOME).isDefault(true).user(user).build(),
            Category.builder().name("Freelance").color("#00d4ff").icon("💻").type(Category.CategoryType.INCOME).isDefault(true).user(user).build(),
            Category.builder().name("Investimentos").color("#6c63ff").icon("📈").type(Category.CategoryType.INCOME).isDefault(true).user(user).build(),
            Category.builder().name("Alimentação").color("#ff4d6d").icon("🍔").type(Category.CategoryType.EXPENSE).isDefault(true).user(user).build(),
            Category.builder().name("Moradia").color("#ffd166").icon("🏠").type(Category.CategoryType.EXPENSE).isDefault(true).user(user).build(),
            Category.builder().name("Transporte").color("#ff9a3c").icon("🚗").type(Category.CategoryType.EXPENSE).isDefault(true).user(user).build(),
            Category.builder().name("Saúde").color("#ff6b9d").icon("❤️").type(Category.CategoryType.EXPENSE).isDefault(true).user(user).build(),
            Category.builder().name("Lazer").color("#c77dff").icon("🎮").type(Category.CategoryType.EXPENSE).isDefault(true).user(user).build(),
            Category.builder().name("Educação").color("#4cc9f0").icon("📚").type(Category.CategoryType.EXPENSE).isDefault(true).user(user).build()
        );
        categoryRepository.saveAll(defaults);
    }
}
