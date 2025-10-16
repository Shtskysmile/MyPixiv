package org.example.PCOI.Entity.RequestStruct;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String identity;
}
