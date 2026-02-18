package com.fiserv.uba.gateway.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;

public final class HeaderUtils {

    public static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String SESSION_HEADER = "X-FISV-SESSION";

    private HeaderUtils() {
    }

    public static Map<String, String> appTokenHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        return headers;
    }

    public static Map<String, String> sessionHeaders(String appToken, String sessionToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, BEARER_PREFIX + appToken);
        headers.put(SESSION_HEADER, sessionToken);
        return headers;
    }
}
