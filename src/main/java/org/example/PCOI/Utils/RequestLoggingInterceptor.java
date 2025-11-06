package org.example.PCOI.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.DispatcherType;

import java.util.Map;

import static org.example.PCOI.Service.Support.Enum.DEFAULT_GUEST;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private final LogService logService;

    public RequestLoggingInterceptor(LogService logService) {
        this.logService = logService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        System.out.println(("token"+token));
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Map<String, Object> claims = JwtUtil.parseToken(token);
                System.out.println("RequestLoggingInterceptor: " + request.getRequestURI());
                String userId = (String)TokenProcess.getAttributeFromToken(token, "userId");
                String operation = request.getRequestURI();
                if (request.getDispatcherType() != DispatcherType.REQUEST || "/error".equals(operation)) {
                    return true;
                }
                logService.logMethodExecution(userId, operation);
                request.setAttribute("claims", claims);
                return true;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return true;
    }
}