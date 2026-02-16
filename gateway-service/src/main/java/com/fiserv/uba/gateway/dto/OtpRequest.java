package com.fiserv.uba.gateway.dto;

import javax.validation.constraints.NotBlank;

public class OtpRequest {

    @NotBlank(message = "Username is required")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
