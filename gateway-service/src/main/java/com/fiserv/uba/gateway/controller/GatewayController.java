package com.fiserv.uba.gateway.controller;

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
import com.fiserv.uba.gateway.service.GatewayService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<LoginResponse>> login(@RequestBody LoginRequest request) {
        return gatewayService.login(request);
    }

    @PostMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<UserResponse>> createUser(
        @RequestHeader("X-FISV-SESSION") String sessionToken,
        @RequestBody UserCreateRequest request
    ) {
        return gatewayService.createUser(request, sessionToken);
    }

    @PutMapping(path = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<UserResponse>> updateUser(
        @PathVariable String userId,
        @RequestHeader("X-FISV-SESSION") String sessionToken,
        @RequestBody UserUpdateRequest request
    ) {
        return gatewayService.updateUser(userId, request, sessionToken);
    }

    @PostMapping(path = "/users/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<UserResponse>> findUser(
        @RequestHeader("X-FISV-SESSION") String sessionToken,
        @RequestBody UserFindRequest request
    ) {
        return gatewayService.findUser(request, sessionToken);
    }

    @PostMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<LogoutResponse>> logout(@RequestHeader("X-FISV-SESSION") String sessionToken) {
        return gatewayService.logout(sessionToken);
    }

    @PostMapping(path = "/session/keep-alive", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<KeepAliveResponse>> keepAlive(@RequestHeader("X-FISV-SESSION") String sessionToken) {
        return gatewayService.keepAlive(sessionToken);
    }

    @PostMapping(path = "/otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<OtpResponse>> requestOtp(@RequestBody OtpRequest request) {
        return gatewayService.requestOtp(request);
    }

    @PostMapping(path = "/otp/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<OtpValidateResponse>> validateOtp(@RequestBody OtpValidateRequest request) {
        return gatewayService.validateOtp(request);
    }

    @PostMapping(path = "/password/reset", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<PasswordResetResponse>> resetPassword(@RequestBody PasswordResetRequest request) {
        return gatewayService.resetPassword(request);
    }

    @PostMapping(path = "/password/change", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<PasswordChangeResponse>> changePassword(
        @RequestHeader("X-FISV-SESSION") String sessionToken,
        @RequestBody PasswordChangeRequest request
    ) {
        return gatewayService.changePassword(request, sessionToken);
    }

    @PostMapping(path = "/token/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseDTO<VerifyTokenResponse>> verifyToken(@RequestHeader("X-FISV-SESSION") String sessionToken) {
        return gatewayService.verifyToken(sessionToken);
    }
}
