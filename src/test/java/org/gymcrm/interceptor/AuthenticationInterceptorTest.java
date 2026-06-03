package org.gymcrm.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gymcrm.facade.GymFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerMapping;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationInterceptorTest {

    @Mock
    private GymFacade gymFacade;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationInterceptor interceptor;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        responseWriter = new StringWriter();
    }

    @Test
    void preHandle_OptionsMethod_ReturnsTrue() throws Exception {
        when(request.getMethod()).thenReturn("OPTIONS");
        assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_NoAuthHeader_ReturnsFalse() throws Exception {
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn(null);

        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void preHandle_ValidCredentials_NoPathVariables_ReturnsTrue() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes());
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(gymFacade.authenticate("user", "pass")).thenReturn(true);

        assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_ValidCredentials_MatchingPathVariables_ReturnsTrue() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes());
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(gymFacade.authenticate("user", "pass")).thenReturn(true);

        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("username", "user");
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVars);

        assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_ValidCredentials_MismatchingPathVariables_ReturnsFalse() throws Exception {
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getMethod()).thenReturn("GET");
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes());
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(gymFacade.authenticate("user", "pass")).thenReturn(true);

        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("username", "otherUser");
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVars);

        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).setStatus(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void preHandle_InvalidCredentials_ReturnsFalse() throws Exception {
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getMethod()).thenReturn("GET");
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("user:wrong".getBytes());
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(gymFacade.authenticate("user", "wrong")).thenReturn(false);

        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
