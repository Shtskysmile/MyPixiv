package org.example.PCOI.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;



import static org.example.PCOI.Service.Support.Enum.DEFAULT_GUEST;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    @Autowired
    private LogService logService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler){
        String userId = DEFAULT_GUEST;
        String authHeader = request.getHeader("Authorization");
        if (!isBlank(authHeader)) {
            try {
                Object uid = TokenProcess.getAttributeFromToken(authHeader, "userId");
                if (uid instanceof String s && !isBlank(s)) {
                    userId = s;
                }
            } catch (Exception e) {
                // 仅记录调试信息，不中断请求
                log.debug("RequestLoggingInterceptor token parse skipped: {}", e.getMessage());
            }
        }
        String operation = request.getRequestURI();
        if (handler instanceof HandlerMethod hm) {
            String controller = hm.getBeanType().getSimpleName();
            if (controller.endsWith("Controller")) {
                controller = controller.substring(0, controller.length() - "Controller".length());
            }
            controller = lowerFirst(controller);
            operation = controller + ":" + hm.getMethod().getName();
        }
        logService.logMethodExecution(userId, operation);
        return true;
    }
    private static String lowerFirst(String s) {
        if (isBlank(s)) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

}