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

@Service
public class FileStorageService {

    @Value("${pcoi.upload.base-dir}")
    private String uploadBaseDir;
    @Value("${pcoi.upload.url-prefix}")
    private String uploadUrlPrefix;
    @Value("${pcoi.upload.avatar-subdir:avatars}")
    private String avatarSubdir;

    public String saveAvatar(MultipartFile avatar) {
        return saveImage(avatar, avatarSubdir);
    }

    public String saveImage(MultipartFile file, String categorySubdir) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("仅支持图片类型文件");
        }
        LocalDate today = LocalDate.now();
        Path dir = Paths.get(ensureTrailingSlash(uploadBaseDir), categorySubdir,
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
                categorySubdir,
                String.valueOf(today.getYear()),
                String.format("%02d", today.getMonthValue()),
                String.format("%02d", today.getDayOfMonth()),
                filename);
        return ensureTrailingSlash(uploadUrlPrefix) + relPath;
    }

    private static String ensureTrailingSlash(String s) {
        if (s == null || s.isEmpty()) return "/";
        return s.endsWith("/") ? s : s + "/";
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
            default -> null;
        };
    }
}

