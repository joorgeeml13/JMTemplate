package base.template.infrastructure.adapters.input.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "error.validation.email.empty")
    @Email(message = "error.validation.email.invalid")
    String email,

    @NotBlank(message = "error.validation.password.empty")
    @Size(min = 6, message = "error.validation.password.short")
    String password
) {} 