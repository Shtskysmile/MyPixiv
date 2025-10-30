package org.example.PCOI.Utils;

import org.example.PCOI.Entity.Claims;

import java.util.Map;

public class TokenProcess {
    public static String getAttributeFromToken(String authHeader, String key) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new Exception("无效的授权头");
        }
        String token = authHeader.substring(7);
        Map<String, Object> map = JwtUtil.parseToken(token);
        Claims claims = Claims.fromMap(map);
        String value = switch (key) {
            case "userId" -> claims.userId();
            case "username" -> claims.username();
            case "role" -> claims.role();
            case "type" -> claims.type();
            default -> throw new Exception("无效的属性键");
        };
        if (value == null) {
            throw new Exception("属性值为空: " + key);
        }
        return value;
    }
}
