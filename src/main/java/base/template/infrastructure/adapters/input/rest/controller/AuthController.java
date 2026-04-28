package base.template.infrastructure.adapters.input.rest.controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import base.template.application.usercases.AuthService;
import base.template.infrastructure.adapters.input.rest.dto.request.LoginRequest;
import base.template.infrastructure.adapters.input.rest.dto.request.RefreshTokenRequest;
import base.template.infrastructure.adapters.input.rest.dto.request.RegisterRequest;
import base.template.infrastructure.adapters.input.rest.dto.response.AuthResponse;
import base.template.shared.Constants;
import base.template.shared.CookieUtil;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    public AuthController(AuthService authService, CookieUtil cookieUtil) {
        this.authService = authService;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request.email(), request.password());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @Valid @RequestBody LoginRequest request,
        @RequestHeader(value = "X-Client-Type", defaultValue = Constants.CLIENT_TYPE_DEFAULT) String clientType) {
        
            var tokens = authService.login(request.email(), request.password(), clientType);

            if(Constants.CLIENT_TYPE_MOBILE.equals(clientType))
                return ResponseEntity.ok(new AuthResponse(tokens.accessToken(), tokens.refreshToken()));

            var cookie = cookieUtil.createRefreshTokenCookie(tokens.refreshToken());

            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponse(tokens.accessToken(), null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
        @CookieValue(value = "jwt-token", required = false) String cookieToken,
        @RequestBody(required = false) RefreshTokenRequest refreshTokenRequest,
        @RequestHeader(value = "X-Client-Type", defaultValue = Constants.CLIENT_TYPE_DEFAULT) String clientType
    ) {
        
        String refreshToken = Constants.CLIENT_TYPE_MOBILE.equals(clientType)
            ? refreshTokenRequest != null ? refreshTokenRequest.refreshToken() : null
            : cookieToken;
        
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        var tokens = authService.refreshToken(refreshToken);

        if (Constants.CLIENT_TYPE_MOBILE.equals(clientType)) {
            return ResponseEntity.ok(new AuthResponse(tokens.accessToken(), tokens.refreshToken()));
        }

        var cookie = cookieUtil.createRefreshTokenCookie(tokens.refreshToken());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new AuthResponse(tokens.accessToken(), null));
    }
}
