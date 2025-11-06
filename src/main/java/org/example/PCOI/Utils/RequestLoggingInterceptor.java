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
        // 跳过错误派发与错误控制器，避免误判为业务接口被拦截
        String uri = request.getRequestURI();
        if (request.getDispatcherType() != DispatcherType.REQUEST || "/error".equals(uri)) {
            return true;
        }
        String userId = DEFAULT_GUEST;
        String authHeader = request.getHeader("Authorization");
        System.out.println(("authHeader: " + authHeader));
        System.out.println("abc" + uri);
        if (!isBlank(authHeader)) {
            userId = (String)TokenProcess.getAttributeFromToken(authHeader, "userId");
            System.out.println("Logging request for userId: " + userId);
        }
        String operation = uri;
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