package org.example.PCOI.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String DEFAULT_GUEST = "游客";
    private final LogService logService;

    public RequestLoggingInterceptor(LogService logService) {
        this.logService = logService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String username = firstNonBlank(
                request.getHeader("username"),
                request.getHeader("Username")
        );
        if (isBlank(username)) {
            username = DEFAULT_GUEST;
        }

        String ip = firstNonBlank(
                request.getHeader("ipaddress"),
                request.getHeader("IpAddress"),
                getClientIpFromProxy(request),
                request.getRemoteAddr()
        );

        String operation = request.getRequestURI();
        if (handler instanceof HandlerMethod hm) {
            String controller = hm.getBeanType().getSimpleName();
            if (controller.endsWith("Controller")) {
                controller = controller.substring(0, controller.length() - "Controller".length());
            }
            controller = lowerFirst(controller);
            operation = controller + ":" + hm.getMethod().getName();
        }

        logService.logMethodExecution(username, ip, operation);
        return true;
    }

    private static String getClientIpFromProxy(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (!isBlank(xff)) {
            int comma = xff.indexOf(',');
            return comma > 0 ? xff.substring(0, comma).trim() : xff.trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        return isBlank(realIp) ? null : realIp.trim();
    }

    private static String lowerFirst(String s) {
        if (isBlank(s)) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String firstNonBlank(String... vals) {
        if (vals == null) return null;
        for (String v : vals) {
            if (!isBlank(v)) return v.trim();
        }
        return null;
    }
}