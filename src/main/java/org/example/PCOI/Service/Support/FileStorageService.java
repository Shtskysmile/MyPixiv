package org.example.PCOI.Service.Support;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static org.example.PCOI.Service.Support.Enum.illustration;

@Service
public class FileStorageService {

    @Value("${pcoi.upload.base-dir}")
    private String uploadBaseDir;
    @Value("${pcoi.upload.url-prefix}")
    private String uploadUrlPrefix;
    @Value("${pcoi.upload.avatar-subdir:avatars}")
    private String avatarSubdir;
    @Value("${pcoi.upload.work-subdir:works}")
    private String workSubdir;
    @Value("${pcoi.upload.illustration-subdir:illustration}")
    private String illustrationSubdir;
    @Value("${pcoi.upload.manga-subdir:manga}")
    private String mangaSubdir;

    // 新签名：头像保存到 avatars/{userId}/YYYY/MM/DD
    public String saveAvatar(MultipartFile avatar, String userId) {
        if (avatar == null || avatar.isEmpty()) return null;
        String subdir = Paths.get(avatarSubdir, sanitize(userId)).toString();
        return saveImageInternal(avatar, subdir);
    }

    // 保存作品图片到 works/{illustration|manga}/YYYY/MM/DD
    public String saveWorkImage(MultipartFile file, Integer type) {
        if (file == null || file.isEmpty() || type == null) return null;
        String typeDir = type.equals(illustration) ? illustrationSubdir : mangaSubdir;
        String subdir = Paths.get(workSubdir, typeDir).toString();
        return saveImageInternal(file, subdir);
    }

    private String saveImageInternal(MultipartFile file, String categorySubdir) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("仅支持图片类型文件");
        }
        LocalDate today = LocalDate.now();
        Path dir = Paths.get(ensureTrailingSlash(uploadBaseDir),
                normalize(categorySubdir),
                String.valueOf(today.getYear()), String.format("%02d", today.getMonthValue()), String.format("%02d", today.getDayOfMonth()));
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("创建目录失败", e);
        }
        String ext = getFileExtension(Objects.requireNonNullElse(file.getOriginalFilename(), ""));
        if (ext == null && contentType != null) {
            ext = mimeToExt(contentType);
        }
        String filename = UUID.randomUUID().toString().replace("-", "");
        filename = (ext == null || ext.isEmpty()) ? filename : filename + "." + ext;
        Path dest = dir.resolve(filename);
        try {
            file.transferTo(dest.toFile());
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败", e);
        }
        String relPath = String.join("/",
                normalize(categorySubdir),
                String.valueOf(today.getYear()),
                String.format("%02d", today.getMonthValue()),
                String.format("%02d", today.getDayOfMonth()),
                filename);
        return buildPublicUrl(relPath);
    }

    private String buildPublicUrl(String relPath) {
        return ensureTrailingSlash(uploadUrlPrefix) + (relPath.startsWith("/") ? relPath.substring(1) : relPath);
    }

    private static String ensureTrailingSlash(String s) {
        if (s == null || s.isEmpty()) return "/";
        return s.endsWith("/") ? s : s + "/";
    }

    private static String normalize(String s) {
        if (s == null) return "";
        // 去掉开头斜杠，避免 Paths 拼接吞掉上级目录
        return s.startsWith("/") ? s.substring(1) : s;
    }

    private static String sanitize(String s) {
        if (s == null || s.isBlank()) return "unknown";
        return s.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    private static String getFileExtension(String filename) {
        if (filename == null) return null;
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) return null;
        return filename.substring(idx + 1).toLowerCase();
    }

    private static String mimeToExt(String mime) {
        if (mime == null) return null;
        return switch (mime) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            case "image/bmp" -> "bmp";
            default -> null;
        };
    }
}
