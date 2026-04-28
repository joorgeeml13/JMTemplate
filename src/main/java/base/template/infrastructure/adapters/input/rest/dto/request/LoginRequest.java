package base.template.infrastructure.adapters.input.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "error.validation.email.empty")
    @Email(message = "error.validation.email.invalid") 
    String email,

    @NotBlank(message = "error.validation.password.empty")
    String password
) {}
