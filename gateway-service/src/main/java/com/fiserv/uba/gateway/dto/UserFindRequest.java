package com.fiserv.uba.gateway.dto;

import javax.validation.constraints.NotBlank;

public class UserFindRequest {

    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
}
