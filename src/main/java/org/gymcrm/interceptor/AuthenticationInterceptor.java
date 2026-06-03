package org.gymcrm.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.gymcrm.facade.GymFacade;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final GymFacade gymFacade;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return rejectUnauthorized(response);
        }

        try {
            String[] credentials = decodeCredentials(authHeader);

            if (credentials.length != 2) {
                return rejectUnauthorized(response);
            }

            String authUsername = credentials[0];
            String authPassword = credentials[1];

            if (!gymFacade.authenticate(authUsername, authPassword)) {
                return rejectUnauthorized(response);
            }

            if (!hasAccessToTargetProfile(request, authUsername)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Forbidden: You can only access/modify your own profile.");
                return false;
            }

            return true;

        } catch (IllegalArgumentException e) {
            return rejectUnauthorized(response);
        }
    }

    private String[] decodeCredentials(String authHeader) {
        String base64Credentials = authHeader.substring("Basic ".length());
        byte[] decoded = Base64.getDecoder().decode(base64Credentials);
        String decodedString = new String(decoded, StandardCharsets.UTF_8);
        return decodedString.split(":", 2);
    }

    private boolean hasAccessToTargetProfile(HttpServletRequest request, String authUsername) {
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables == null || !pathVariables.containsKey("username")) {
            return true;
        }

        String targetUsername = pathVariables.get("username");
        return authUsername.equals(targetUsername);
    }

    private boolean rejectUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("Unauthorized: Invalid username or password");
        return false;
    }
}