package base.template.infrastructure.adapters.out.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import base.template.application.security.AuthTokens;
import base.template.application.security.TokenUtils;
import base.template.domain.model.auth.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenAdapter implements TokenUtils{
    
    @Value("${security.jwt.secret-key}")
    private String secretKeyString;

    @Value("${security.jwt.access-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshTokenExpirationMs;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public AuthTokens issue(Account account) {
        String roles = account.getRoles().stream()
            .map(role -> role.name())
            .collect(Collectors.joining(","));

        String accessToken = Jwts.builder()
            .subject(account.getId().toString())
            .claim("email", account.getEmail().getValue())
            .claim("roles", roles)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
            .signWith(secretKey)
            .compact();

        String refreshToken = UUID.randomUUID().toString();

        return new AuthTokens(accessToken, refreshToken);
    }

    @Override
    public String extractId(String token){
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isValidFormmat(String token){
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    @Override
    public boolean isTokenValid(String token, String userId) {
        try {
            final String subject = extractId(token);
            return subject != null && subject.equals(userId);
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
