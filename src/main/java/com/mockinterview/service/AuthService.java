package com.mockinterview.service;

import com.mockinterview.dto.auth.*;
import com.mockinterview.entity.EmailVerificationToken;
import com.mockinterview.entity.PasswordResetToken;
import com.mockinterview.entity.RefreshToken;
import com.mockinterview.entity.User;
import com.mockinterview.exception.BadRequestException;
import com.mockinterview.exception.DuplicateResourceException;
import com.mockinterview.exception.ResourceNotFoundException;
import com.mockinterview.exception.TokenException;
import com.mockinterview.repository.EmailVerificationTokenRepository;
import com.mockinterview.repository.PasswordResetTokenRepository;
import com.mockinterview.repository.RefreshTokenRepository;
import com.mockinterview.repository.UserRepository;
import com.mockinterview.security.JwtTokenProvider;
import com.mockinterview.util.TokenGenerator;
import com.mockinterview.security.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;

    @Value("${app.token.email-verification-expiry}")
    private int emailVerificationExpiryHours;

    @Value("${app.token.password-reset-expiry}")
    private int passwordResetExpiryHours;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .status(User.UserStatus.ACTIVE)
                .googleCalendarConnected(false)
                .calendarSyncEnabled(false)
                .build();

        user = userRepository.save(user);

        // Generate and send email verification token
        String token = TokenGenerator.generateToken();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plus(emailVerificationExpiryHours, ChronoUnit.HOURS))
                .used(false)
                .build();

        emailVerificationTokenRepository.save(verificationToken);

        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), token);

        log.info("User registered successfully: {}", user.getEmail());

        return RegisterResponse.builder()
                .message("Registration successful. Please check your email to verify your account.")
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        // Update last login
        user.setLastLogin(Instant.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(userPrincipal.getId());

        // Save refresh token
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiresAt(Instant.now().plus(tokenProvider.getRefreshTokenExpiration(), ChronoUnit.MILLIS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        log.info("User logged in successfully: {}", user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiration() / 1000) // in seconds
                .user(UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .emailVerified(user.getEmailVerified())
                        .profileCompleted(user.getProfile() != null)
                        .lastLogin(user.getLastLogin())
                        .build())
                .build();
    }

    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new TokenException("Invalid refresh token");
        }

        // Find refresh token in database
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new TokenException("Refresh token not found"));

        if (storedToken.getRevoked()) {
            throw new TokenException("Refresh token has been revoked");
        }

        if (storedToken.isExpired()) {
            throw new TokenException("Refresh token has expired");
        }

        // Generate new access token
        User user = storedToken.getUser();
        String newAccessToken = tokenProvider.generateAccessTokenFromUserId(
                user.getId(),
                user.getEmail()
        );

        // Optionally generate new refresh token (refresh token rotation)
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getId());

        // Revoke old refresh token
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        // Save new refresh token
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .user(user)
                .expiresAt(Instant.now().plus(tokenProvider.getRefreshTokenExpiration(), ChronoUnit.MILLIS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newRefreshTokenEntity);

        log.info("Token refreshed for user: {}", user.getEmail());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiration() / 1000)
                .build();
    }

    @Transactional
    public void logout(Long userId) {
        // Revoke all refresh tokens for user
        refreshTokenRepository.revokeAllByUserId(userId);
        log.info("User logged out: userId={}", userId);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        EmailVerificationToken token = emailVerificationTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new TokenException("Invalid verification token"));

        if (token.getUsed()) {
            throw new TokenException("Verification token has already been used");
        }

        if (token.isExpired()) {
            throw new TokenException("Verification token has expired");
        }

        User user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        token.setUsed(true);
        emailVerificationTokenRepository.save(token);

        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    @Transactional
    public void resendVerificationEmail(ResendVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if (user.getEmailVerified()) {
            throw new BadRequestException("Email is already verified");
        }

        // Delete existing unused tokens
        emailVerificationTokenRepository.deleteByUserId(user.getId());

        // Generate new token
        String token = TokenGenerator.generateToken();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plus(emailVerificationExpiryHours, ChronoUnit.HOURS))
                .used(false)
                .build();

        emailVerificationTokenRepository.save(verificationToken);

        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), token);

        log.info("Verification email resent to: {}", user.getEmail());
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // Generate password reset token
        String token = TokenGenerator.generateToken();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plus(passwordResetExpiryHours, ChronoUnit.HOURS))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Send password reset email
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        log.info("Password reset email sent to: {}", user.getEmail());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new TokenException("Invalid password reset token"));

        if (token.getUsed()) {
            throw new TokenException("Password reset token has already been used");
        }

        if (token.isExpired()) {
            throw new TokenException("Password reset token has expired");
        }

        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        passwordResetTokenRepository.save(token);

        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllByUserId(user.getId());

        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Revoke all refresh tokens (force re-login)
        refreshTokenRepository.revokeAllByUserId(userId);

        log.info("Password changed successfully for user: {}", user.getEmail());
    }
}