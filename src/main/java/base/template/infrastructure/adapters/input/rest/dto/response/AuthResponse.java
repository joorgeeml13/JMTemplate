package base.template.infrastructure.adapters.input.rest.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}