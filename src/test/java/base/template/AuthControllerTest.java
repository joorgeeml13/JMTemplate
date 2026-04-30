package base.template;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import base.template.application.ports.in.auth.usecase.LoginAccountUseCase;
import base.template.application.ports.in.auth.usecase.RefreshSessionUseCase;
import base.template.application.ports.in.auth.usecase.RegisterAccountUseCase;
import base.template.application.security.AuthTokens;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterAccountUseCase registerAccountUseCase;

    @MockBean
    private LoginAccountUseCase loginAccountUseCase;

    @MockBean
    private RefreshSessionUseCase refreshSessionUseCase;

    @Test
    void testRegister() throws Exception {
        // Given
        String requestJson = "{\"email\":\"test@example.com\",\"password\":\"password123\"}";

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated());
    }

    @Test
    void testLoginWeb() throws Exception {
        // Given
        String requestJson = "{\"email\":\"test@example.com\",\"password\":\"password123\"}";
        AuthTokens tokens = new AuthTokens("access-token", "refresh-token");
        when(loginAccountUseCase.execute(any())).thenReturn(tokens);

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .header("X-Client-Type", "WEB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void testLoginMobile() throws Exception {
        // Given
        String requestJson = "{\"email\":\"test@example.com\",\"password\":\"password123\"}";
        AuthTokens tokens = new AuthTokens("access-token", "refresh-token");
        when(loginAccountUseCase.execute(any())).thenReturn(tokens);

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .header("X-Client-Type", "MOBILE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    @Test
    void testRefreshWebWithCookie() throws Exception {
        // Given
        AuthTokens tokens = new AuthTokens("new-access-token", "new-refresh-token");
        when(refreshSessionUseCase.execute(any())).thenReturn(tokens);

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                .cookie(new Cookie("refresh_token", "old-refresh-token"))
                .header("X-Client-Type", "WEB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void testRefreshMobileWithBody() throws Exception {
        // Given
        String requestJson = "{\"refreshToken\":\"old-refresh-token\"}";
        AuthTokens tokens = new AuthTokens("new-access-token", "new-refresh-token");
        when(refreshSessionUseCase.execute(any())).thenReturn(tokens);

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .header("X-Client-Type", "MOBILE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    @Test
    void testRefreshMissingToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/refresh")
                .header("X-Client-Type", "WEB"))
                .andExpect(status().isUnauthorized());
    }
}