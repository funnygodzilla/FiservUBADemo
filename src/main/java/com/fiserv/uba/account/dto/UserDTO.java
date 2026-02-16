package com.fiserv.uba.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Long customerId;
    private Boolean enabled;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private List<String> roles;
    private List<String> permissions;
}

