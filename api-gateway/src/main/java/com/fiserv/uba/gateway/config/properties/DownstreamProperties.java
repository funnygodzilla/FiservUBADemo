package com.fiserv.uba.gateway.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.downstream")
public class DownstreamProperties {

    private String userManagementBaseUrl = "http://localhost:8082";
    private String esfBaseUrl = "http://localhost:8084";

    public String getUserManagementBaseUrl() {
        return userManagementBaseUrl;
    }

    public void setUserManagementBaseUrl(String userManagementBaseUrl) {
        this.userManagementBaseUrl = userManagementBaseUrl;
    }

    public String getEsfBaseUrl() {
        return esfBaseUrl;
    }

    public void setEsfBaseUrl(String esfBaseUrl) {
        this.esfBaseUrl = esfBaseUrl;
    }
}
