package base.template.shared;



import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class CookieUtil {
    
    @Value("${security.jwt.refresh-cookie-name}")
    private String refreshTokenCookieName;

    @Value("${security.jwt.refresh-expiration-days}")
    private long expiresInDays;

    public HttpCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(refreshTokenCookieName, token)
            .httpOnly(true)
            .secure(false) // Set to true in production with HTTPS
            .path("/")
            .maxAge(expiresInDays * 24 * 60 * 60)
            .sameSite("Strict")
            .build();
    }

    public HttpCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(refreshTokenCookieName, "")
            .httpOnly(true)
            .secure(false) // Set to true in production with HTTPS
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();
    }
}