package com.fiserv.uba.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String username;
    private String email;
    private Long customerId;
    private List<String> roles;
    private List<String> permissions;
}

