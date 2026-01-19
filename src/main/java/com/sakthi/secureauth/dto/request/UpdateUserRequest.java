package com.sakthi.secureauth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @NotBlank
    private String fullName;
}
