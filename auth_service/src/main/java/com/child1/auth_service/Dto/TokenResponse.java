package com.child1.auth_service.Dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class TokenResponse {
    private String token;
    private String user;

    public TokenResponse(String token, String user) {
        this.token = token;
        this.user = user;
    }
}
