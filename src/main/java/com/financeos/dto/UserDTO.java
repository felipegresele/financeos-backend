package com.financeos.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String role;
}
