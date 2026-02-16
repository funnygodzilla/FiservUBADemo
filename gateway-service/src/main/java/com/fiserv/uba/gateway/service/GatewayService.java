package com.fiserv.uba.gateway.service;

import com.fiserv.uba.gateway.client.BankingGatewayClient;
import com.fiserv.uba.gateway.dto.LoginRequest;
import com.fiserv.uba.gateway.dto.LoginResponse;
import com.fiserv.uba.gateway.dto.LogoutResponse;
import com.fiserv.uba.gateway.dto.OtpRequest;
import com.fiserv.uba.gateway.dto.OtpResponse;
import com.fiserv.uba.gateway.dto.OtpValidateRequest;
import com.fiserv.uba.gateway.dto.OtpValidateResponse;
import com.fiserv.uba.gateway.dto.PasswordChangeRequest;
import com.fiserv.uba.gateway.dto.PasswordChangeResponse;
import com.fiserv.uba.gateway.dto.PasswordResetRequest;
import com.fiserv.uba.gateway.dto.PasswordResetResponse;
import com.fiserv.uba.gateway.dto.KeepAliveResponse;
import com.fiserv.uba.gateway.dto.ResponseDTO;
import com.fiserv.uba.gateway.dto.UserCreateRequest;
import com.fiserv.uba.gateway.dto.UserFindRequest;
import com.fiserv.uba.gateway.dto.UserResponse;
import com.fiserv.uba.gateway.dto.UserUpdateRequest;
import com.fiserv.uba.gateway.dto.VerifyTokenResponse;
import com.fiserv.uba.gateway.config.GatewayProperties;
import com.fiserv.uba.gateway.exception.GatewayClientException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GatewayService {

    private final TokenService tokenService;
    private final BankingGatewayClient gatewayClient;
    private final GatewayProperties properties;

    public GatewayService(
        TokenService tokenService,
        BankingGatewayClient gatewayClient,
        GatewayProperties properties
    ) {
        this.tokenService = tokenService;
        this.gatewayClient = gatewayClient;
        this.properties = properties;
    }

    public Mono<ResponseDTO<LoginResponse>> login(LoginRequest request) {
        return tokenService.getApplicationToken()
            .flatMap(token -> gatewayClient.login(token.getAccessToken(), request))
            .map(response -> new ResponseDTO<>(HttpStatus.OK.value(), "Login successful", response));
    }

    public Mono<ResponseDTO<UserResponse>> createUser(UserCreateRequest request, String sessionToken) {
        return tokenService.getApplicationToken()
            .flatMap(token -> gatewayClient.createUser(token.getAccessToken(), sessionToken, request))
            .map(response -> new ResponseDTO<>(HttpStatus.CREATED.value(), "User created", response));
    }

    public Mono<ResponseDTO<UserResponse>> updateUser(
        String userId,
        UserUpdateRequest request,
        String sessionToken
    ) {
        return tokenService.getApplicationToken()
            .flatMap(token -> gatewayClient.updateUser(token.getAccessToken(), sessionToken, userId, request))
            .map(response -> new ResponseDTO<>(HttpStatus.OK.value(), "User updated", response));
    }

    public Mono<ResponseDTO<UserResponse>> findUser(UserFindRequest request, String sessionToken) {
        return tokenService.getApplicationToken()
            .flatMap(token -> gatewayClient.findUser(token.getAccessToken(), sessionToken, request))
            .map(response -> new ResponseDTO<>(HttpStatus.OK.value(), "User found", response));
    }

    public Mono<ResponseDTO<LogoutResponse>> logout(String sessionToken) {
        return tokenService.getApplicationToken()
            .flatMap(token -> gatewayClient.logout(token.getAccessToken(), sessionToken))
            .map(response -> new ResponseDTO<>(HttpStatus.OK.value(), "Logout successful", response));
    }

    public Mono<ResponseDTO<KeepAliveResponse>> keepAlive(String sessionToken) {
        return tokenService.getApplicationToken()
            .flatMap(token -> gatewayClient.keepAlive(token.getAccessToken(), sessionToken))
            .map(response -> new ResponseDTO<>(HttpStatus.OK.value(), "Session kept alive", response));
    }

    public Mono<ResponseDTO<OtpResponse>> requestOtp(OtpRequest request) {
        return tokenService.getApplicationToken()
            .flatMap(token -> gatewayClient.requestOtp(token.getAccessToken(), request))
            .map(response -> new ResponseDTO<>(HttpStatus.OK.value(), "OTP requested", response));
    }

    public Mono<ResponseDTO<OtpValidateResponse>> validateOtp(OtpValidateRequest request) {
        if (!properties.isOtpValidationEnabled()) {
            if (properties.getDefaultOtp().equals(request.getOtp())) {
                OtpValidateResponse response = new OtpValidateResponse();
                response.setStatus("SUCCESS");
                response.setMessage("OTP validation bypassed");
                return Mono.just(new ResponseDTO<>(HttpStatus.OK.value(), "OTP validated", response));
            }
            return Mono.error(new GatewayClientException(
                "Invalid OTP (validation disabled - use default OTP)."
            ));
        }

        return tokenService.getApplicationToken()
            .flatMap(token -> gatewayClient.validateOtp(token.getAccessToken(), request))
            .map(response -> new ResponseDTO<>(HttpStatus.OK.value(), "OTP validated", response));
    }

    public Mono<ResponseDTO<PasswordResetResponse>> resetPassword(PasswordResetRequest request) {
        return tokenService.getApplicationToken()
            .flatMap(token -> gatewayClient.resetPassword(token.getAccessToken(), request))
            .map(response -> new ResponseDTO<>(HttpStatus.OK.value(), "Password reset", response));
    }

    public Mono<ResponseDTO<PasswordChangeResponse>> changePassword(
        PasswordChangeRequest request,
        String sessionToken
    ) {
        return tokenService.getApplicationToken()
            .flatMap(token -> gatewayClient.changePassword(token.getAccessToken(), sessionToken, request))
            .map(response -> new ResponseDTO<>(HttpStatus.OK.value(), "Password changed", response));
    }

    public Mono<ResponseDTO<VerifyTokenResponse>> verifyToken(String sessionToken) {
        return tokenService.getApplicationToken()
            .flatMap(token -> gatewayClient.verifyToken(token.getAccessToken(), sessionToken))
            .map(response -> new ResponseDTO<>(HttpStatus.OK.value(), "Token verified", response));
    }
}
