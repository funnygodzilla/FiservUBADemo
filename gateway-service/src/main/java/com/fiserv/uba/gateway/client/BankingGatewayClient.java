package com.fiserv.uba.gateway.client;

import com.fiserv.uba.gateway.config.GatewayProperties;
import com.fiserv.uba.gateway.dto.ApplicationTokenRequest;
import com.fiserv.uba.gateway.dto.ApplicationTokenResponse;
import com.fiserv.uba.gateway.dto.LoginRequest;
import com.fiserv.uba.gateway.dto.LoginResponse;
import com.fiserv.uba.gateway.dto.KeepAliveResponse;
import com.fiserv.uba.gateway.dto.LogoutResponse;
import com.fiserv.uba.gateway.dto.OtpRequest;
import com.fiserv.uba.gateway.dto.OtpResponse;
import com.fiserv.uba.gateway.dto.OtpValidateRequest;
import com.fiserv.uba.gateway.dto.OtpValidateResponse;
import com.fiserv.uba.gateway.dto.PasswordChangeRequest;
import com.fiserv.uba.gateway.dto.PasswordChangeResponse;
import com.fiserv.uba.gateway.dto.PasswordResetRequest;
import com.fiserv.uba.gateway.dto.PasswordResetResponse;
import com.fiserv.uba.gateway.dto.UserCreateRequest;
import com.fiserv.uba.gateway.dto.UserFindRequest;
import com.fiserv.uba.gateway.dto.UserResponse;
import com.fiserv.uba.gateway.dto.UserUpdateRequest;
import com.fiserv.uba.gateway.dto.VerifyTokenResponse;
import com.fiserv.uba.gateway.util.HeaderUtils;
import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class BankingGatewayClient extends BaseGatewayClient {

    private final GatewayProperties properties;

    public BankingGatewayClient(WebClient gatewayWebClient, GatewayProperties properties) {
        super(gatewayWebClient);
        this.properties = properties;
    }

    public Mono<ApplicationTokenResponse> requestApplicationToken(ApplicationTokenRequest request) {
        return post(
            properties.getEndpoints().getApplicationToken(),
            request,
            Collections.emptyMap(),
            ApplicationTokenResponse.class
        )
            .retryWhen(retrySpec());
    }

    public Mono<LoginResponse> login(String appToken, LoginRequest request) {
        return webClient.post()
            .uri(properties.getEndpoints().getLogin())
            .headers(headers -> headers.setAll(HeaderUtils.appTokenHeaders(appToken)))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchangeToMono(this::handleLoginResponse)
            .retryWhen(retrySpec());
    }

    public Mono<UserResponse> createUser(String appToken, String sessionToken, UserCreateRequest request) {
        return post(
            properties.getEndpoints().getCreateUser(),
            request,
            HeaderUtils.sessionHeaders(appToken, sessionToken),
            UserResponse.class
        ).retryWhen(retrySpec());
    }

    public Mono<UserResponse> updateUser(
        String appToken,
        String sessionToken,
        String userId,
        UserUpdateRequest request
    ) {
        String uri = properties.getEndpoints().getUpdateUser().replace("{userId}", userId);
        return put(uri, request, HeaderUtils.sessionHeaders(appToken, sessionToken), UserResponse.class)
            .retryWhen(retrySpec());
    }

    public Mono<UserResponse> findUser(String appToken, String sessionToken, UserFindRequest request) {
        return post(
            properties.getEndpoints().getFindUser(),
            request,
            HeaderUtils.sessionHeaders(appToken, sessionToken),
            UserResponse.class
        ).retryWhen(retrySpec());
    }

    public Mono<LogoutResponse> logout(String appToken, String sessionToken) {
        return post(
            properties.getEndpoints().getLogout(),
            Collections.emptyMap(),
            HeaderUtils.sessionHeaders(appToken, sessionToken),
            LogoutResponse.class
        ).retryWhen(retrySpec());
    }

    public Mono<KeepAliveResponse> keepAlive(String appToken, String sessionToken) {
        return get(
            properties.getEndpoints().getKeepAlive(),
            HeaderUtils.sessionHeaders(appToken, sessionToken),
            KeepAliveResponse.class
        ).retryWhen(retrySpec());
    }

    public Mono<OtpResponse> requestOtp(String appToken, OtpRequest request) {
        return post(
            properties.getEndpoints().getRequestOtp(),
            request,
            HeaderUtils.appTokenHeaders(appToken),
            OtpResponse.class
        ).retryWhen(retrySpec());
    }

    public Mono<OtpValidateResponse> validateOtp(String appToken, OtpValidateRequest request) {
        return post(
            properties.getEndpoints().getValidateOtp(),
            request,
            HeaderUtils.appTokenHeaders(appToken),
            OtpValidateResponse.class
        ).retryWhen(retrySpec());
    }

    public Mono<PasswordResetResponse> resetPassword(String appToken, PasswordResetRequest request) {
        return post(
            properties.getEndpoints().getResetPassword(),
            request,
            HeaderUtils.appTokenHeaders(appToken),
            PasswordResetResponse.class
        ).retryWhen(retrySpec());
    }

    public Mono<PasswordChangeResponse> changePassword(
        String appToken,
        String sessionToken,
        PasswordChangeRequest request
    ) {
        return post(
            properties.getEndpoints().getChangePassword(),
            request,
            HeaderUtils.sessionHeaders(appToken, sessionToken),
            PasswordChangeResponse.class
        ).retryWhen(retrySpec());
    }

    public Mono<VerifyTokenResponse> verifyToken(String appToken, String sessionToken) {
        return get(
            properties.getEndpoints().getVerifyToken(),
            HeaderUtils.sessionHeaders(appToken, sessionToken),
            VerifyTokenResponse.class
        ).retryWhen(retrySpec());
    }

    private Mono<LoginResponse> handleLoginResponse(ClientResponse response) {
        if (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError()) {
            return response.bodyToMono(String.class)
                .flatMap(message -> Mono.error(mapLoginError(response.statusCode(), message)));
        }

        HttpHeaders headers = response.headers().asHttpHeaders();
        String sessionToken = headers.getFirst(HeaderUtils.SESSION_HEADER);
        return response.bodyToMono(LoginResponse.class)
            .defaultIfEmpty(new LoginResponse())
            .map(body -> {
                body.setSessionToken(sessionToken);
                return body;
            });
    }

    private RuntimeException mapLoginError(HttpStatus status, String message) {
        return status.is4xxClientError()
            ? new com.fiserv.uba.gateway.exception.GatewayClientException(message)
            : new com.fiserv.uba.gateway.exception.GatewayServerException(message);
    }

    private Retry retrySpec() {
        return Retry.backoff(properties.getRetry().getMaxAttempts(), properties.getRetry().getBackoff())
            .filter(throwable -> !(throwable instanceof com.fiserv.uba.gateway.exception.GatewayClientException));
    }
}
