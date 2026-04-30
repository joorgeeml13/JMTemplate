package base.template.infrastructure.adapters.in.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import base.template.application.ports.in.auth.command.LoginCommand;
import base.template.application.ports.in.auth.command.RefreshCommand;
import base.template.application.ports.in.auth.command.RegisterCommand;
import base.template.application.ports.in.auth.usecase.LoginAccountUseCase;
import base.template.application.ports.in.auth.usecase.RefreshSessionUseCase;
import base.template.application.ports.in.auth.usecase.RegisterAccountUseCase;
import base.template.application.security.AuthTokens;
import base.template.infrastructure.InfrastructureConstants;
import base.template.infrastructure.adapters.in.web.dto.request.LoginRequestDTO;
import base.template.infrastructure.adapters.in.web.dto.request.RefreshTokenRequestDTO;
import base.template.infrastructure.adapters.in.web.dto.request.RegisterRequestDTO;
import base.template.infrastructure.adapters.in.web.dto.response.AuthTokensResponse;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping(value="/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterAccountUseCase registerAccountUseCase;
    private final LoginAccountUseCase loginAccountUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;

    @Value("${security.jwt.refresh-cookie-name}")
    private String refreshCookieName;

    @Value("${security.jwt.refresh-expiration-days}")
    private long refreshCookieMaxAgeDays;

    @Value("${app.security.jwt.refresh-cookie.secure}")
    private boolean refreshCookieSecure;

    @Value("${security.jwt.refresh-path}")
    private String refreshPath;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO request) {
        RegisterCommand command = new RegisterCommand(request.email(), request.password());

        registerAccountUseCase.execute(command);
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthTokensResponse> login(
        @RequestBody LoginRequestDTO request,
        @RequestHeader(value = "X-Client-Type", defaultValue = "WEB") String clientType
    ) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        
        AuthTokens tokens =  loginAccountUseCase.execute(command);

        if(InfrastructureConstants.CLIENTE_MOBILE.equals(clientType))
            return ResponseEntity.ok(new AuthTokensResponse(tokens.accessToken(), tokens.refreshToken()));

        ResponseCookie cookie = createRefreshCookie(tokens.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthTokensResponse(tokens.accessToken(), null));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthTokensResponse> refresh(
        @CookieValue(value = "${security.jwt.refresh-cookie-name}", required = false) String cookieToken,
        @RequestBody(required = false) RefreshTokenRequestDTO request,
        @RequestHeader(value = "X-Client-Type", defaultValue = InfrastructureConstants.CLIENTE_WEB) String clientType
    ) {
        //Extraer token
        String rawRefreshToken = Optional.ofNullable(request)
            .map(RefreshTokenRequestDTO::refreshToken)
            .orElse(cookieToken);

        if(rawRefreshToken == null || rawRefreshToken.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "error.auth.refresh.token.missing");

        AuthTokens tokens = refreshSessionUseCase.execute(new RefreshCommand(rawRefreshToken));

        ResponseCookie cookie = createRefreshCookie(tokens.refreshToken());

       if(InfrastructureConstants.CLIENTE_MOBILE.equals(clientType))
            return ResponseEntity.ok(new AuthTokensResponse(tokens.accessToken(), tokens.refreshToken()));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthTokensResponse(tokens.accessToken(), null));
    }
    

    private ResponseCookie createRefreshCookie(String token) {
        return ResponseCookie.from(refreshCookieName, token)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .path(refreshPath)                           
                .maxAge(refreshCookieMaxAgeDays * 24 * 60 * 60) 
                .sameSite("Strict")                         
                .build();
    }
}