package com.fiserv.uba.gateway.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String scope;
    private boolean otpValidationEnabled = true;
    private String defaultOtp = "123456";
    private Duration connectTimeout = Duration.ofSeconds(5);
    private Duration readTimeout = Duration.ofSeconds(20);
    private Retry retry = new Retry();
    private Endpoints endpoints = new Endpoints();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isOtpValidationEnabled() {
        return otpValidationEnabled;
    }

    public void setOtpValidationEnabled(boolean otpValidationEnabled) {
        this.otpValidationEnabled = otpValidationEnabled;
    }

    public String getDefaultOtp() {
        return defaultOtp;
    }

    public void setDefaultOtp(String defaultOtp) {
        this.defaultOtp = defaultOtp;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    public static class Retry {
        private int maxAttempts = 3;
        private Duration backoff = Duration.ofMillis(300);

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public Duration getBackoff() {
            return backoff;
        }

        public void setBackoff(Duration backoff) {
            this.backoff = backoff;
        }
    }

    public static class Endpoints {
        private String applicationToken = "/oauth2/token";
        private String login = "/authenticate";
        private String logout = "/logoutUser";
        private String verifyToken = "/validateToken";
        private String keepAlive = "/keepAlive";
        private String requestOtp = "/authenticate/otp";
        private String validateOtp = "/authenticate/otp/validate";
        private String resetPassword = "/password";
        private String changePassword = "/password/change";
        private String createUser = "/users";
        private String updateUser = "/users/{userId}";
        private String findUser = "/users/search";

        public String getApplicationToken() {
            return applicationToken;
        }

        public void setApplicationToken(String applicationToken) {
            this.applicationToken = applicationToken;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getLogout() {
            return logout;
        }

        public void setLogout(String logout) {
            this.logout = logout;
        }

        public String getVerifyToken() {
            return verifyToken;
        }

        public void setVerifyToken(String verifyToken) {
            this.verifyToken = verifyToken;
        }

        public String getKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(String keepAlive) {
            this.keepAlive = keepAlive;
        }

        public String getRequestOtp() {
            return requestOtp;
        }

        public void setRequestOtp(String requestOtp) {
            this.requestOtp = requestOtp;
        }

        public String getValidateOtp() {
            return validateOtp;
        }

        public void setValidateOtp(String validateOtp) {
            this.validateOtp = validateOtp;
        }

        public String getResetPassword() {
            return resetPassword;
        }

        public void setResetPassword(String resetPassword) {
            this.resetPassword = resetPassword;
        }

        public String getChangePassword() {
            return changePassword;
        }

        public void setChangePassword(String changePassword) {
            this.changePassword = changePassword;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }

        public String getFindUser() {
            return findUser;
        }

        public void setFindUser(String findUser) {
            this.findUser = findUser;
        }
    }
}
