package org.gymcrm.workload.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class M2mJwtFilterTest {

    @Mock
    private M2mJwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private M2mJwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getRoleFromToken(token)).thenReturn("ROLE_SYSTEM");

        jwtFilter.doFilterInternal(request, response, filterChain);

        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("system-client", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SYSTEM")));
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        
        String token = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateToken(token)).thenReturn(false);

        
        jwtFilter.doFilterInternal(request, response, filterChain);

        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithoutAuthorizationHeader_ShouldNotSetAuthentication() throws ServletException, IOException {
        
        when(request.getHeader("Authorization")).thenReturn(null);

        
        jwtFilter.doFilterInternal(request, response, filterChain);

        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidAuthorizationFormat_ShouldNotSetAuthentication() throws ServletException, IOException {
        
        when(request.getHeader("Authorization")).thenReturn("Basic user:password");

        
        jwtFilter.doFilterInternal(request, response, filterChain);

        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        
        verify(filterChain).doFilter(request, response);
    }
}
