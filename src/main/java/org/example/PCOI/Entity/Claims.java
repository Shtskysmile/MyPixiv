package org.example.PCOI.Entity;
import java.util.Map;

public record Claims(String username, String userId, String role, String type) {
    public Map<String, Object> toMap() {
        return Map.of(
                "username", username,
                "userId", userId,
                "role", role,
                "type", type
        );
    }

    public static Claims fromMap(Map<String, ?> m) {
        if (m == null) return null;
        String username = (String) m.get("username");
        String userId = (String) m.get("userId");
        String role = (String) m.get("role");
        String type = (String) m.get("type");
        return new Claims(username, userId, role, type);
    }
}