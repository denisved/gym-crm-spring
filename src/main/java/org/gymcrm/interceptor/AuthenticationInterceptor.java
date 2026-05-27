package org.gymcrm.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.gymcrm.facade.GymFacade;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

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

        if (authHeader != null && authHeader.startsWith("Basic ")) {
            try {
                String base64Credentials = authHeader.substring("Basic ".length());
                byte[] decoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(decoded, StandardCharsets.UTF_8);

                String[] values = credentials.split(":", 2);
                if (values.length == 2) {
                    String authUsername = values[0];
                    String authPassword = values[1];

                    if (gymFacade.authenticate(authUsername, authPassword)) {
                        Map<String, String> pathVariables = (Map<String, String>) request
                                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

                        if (pathVariables != null && pathVariables.containsKey("username")) {
                            String targetUsername = pathVariables.get("username");

                            if (!authUsername.equals(targetUsername)) {
                                response.setStatus(HttpStatus.FORBIDDEN.value());
                                response.getWriter().write("Forbidden: You can only access/modify your own profile.");
                                return false;
                            }
                        }

                        return true;
                    }
                }
            } catch (Exception e) {
            }
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("Unauthorized: Invalid username or password");
        return false;
    }
}