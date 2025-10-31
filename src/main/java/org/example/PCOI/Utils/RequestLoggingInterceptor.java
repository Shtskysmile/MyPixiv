package org.example.PCOI.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.PCOI.Service.Inter.LogService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String authHeader = request.getHeader("Authorization");
            String userId = (String) TokenProcess.getAttributeFromToken(authHeader, "userId");
            if (isBlank(userId)) {
                userId = DEFAULT_GUEST;
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

            // 依接口签名顺序：username, operationType, ipAddress
            logService.logMethodExecution(userId, operation);
            return true;
        }catch(Exception e){
            log.error("Logging failed: {}", e.getMessage());
            return false;
        }
    }
    private static String lowerFirst(String s) {
        if (isBlank(s)) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

}