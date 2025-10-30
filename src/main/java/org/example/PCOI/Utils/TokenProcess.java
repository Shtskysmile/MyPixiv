package org.example.PCOI.Utils;

import org.example.PCOI.Entity.Claims;

import java.util.Map;

public class TokenProcess {
    public <T> T getAttributeFromToken(String authHeader, String key, Class<T> type) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new Exception("无效的授权头");
        }
        String token = authHeader.substring(7);
        Map<String, Object> map = JwtUtil.parseToken(token);
        Claims claims = Claims.fromMap(map);
        Object value = switch (key) {
            case "userId" -> claims.userId();
            case "username" -> claims.username();
            case "role" -> claims.role();
            case "type" -> claims.type();
            default -> throw new Exception("无效的属性键");
        };
        if (value == null) {
            throw new Exception("属性值为空: " + key);
        }
        if (!type.isInstance(value)) {
            throw new Exception("类型不匹配: 需要 " + type.getSimpleName() + " 实际为 " + value.getClass().getSimpleName());
        }
        return type.cast(value);
    }
}
