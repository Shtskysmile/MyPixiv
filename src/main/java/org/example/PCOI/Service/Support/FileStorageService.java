package org.example.PCOI.Service.Support;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.example.PCOI.Service.Support.Enum.illustration;

@Service
public class FileStorageService {

    @Value("${pcoi.upload.base-dir}")
    private String uploadBaseDir;
    @Value("${pcoi.upload.url-prefix}")
    private String uploadUrlPrefix;
    // 每个用户目录下的三个固定子目录名
    @Value("${pcoi.upload.avatar-subdir:avatar}")
    private String avatarSubdir;
    @Value("${pcoi.upload.illustration-subdir:illustration}")
    private String illustrationSubdir;
    @Value("${pcoi.upload.manga-subdir:manga}")
    private String mangaSubdir;

    // 头像：保存到 baseDir/{userId}/avatar/ 下，更新时清空旧头像；返回该文件夹 URL
    public String saveAvatar(MultipartFile avatar, String userId) {
        if (avatar == null || avatar.isEmpty()) return null;
        String relFolder = Paths.get(sanitize(userId), avatarSubdir).toString();
        Path dir = Paths.get(uploadBaseDir, relFolder);
        try {
            Files.createDirectories(dir);
            // 清空旧头像，保证唯一
            clearDirectory(dir);
            saveOneFile(avatar, dir);
        } catch (IOException e) {
            throw new RuntimeException("保存头像失败", e);
        }
        return buildFolderUrl(relFolder);
    }

    // 作品：统一按 List 存至 baseDir/{userId}/{illustration|manga}/{workId}/，返回作品文件夹 URL
    public String saveWorkImages(List<MultipartFile> files, Integer type, String userId) {
        if (files == null || files.isEmpty() || type == null) return null;
        String typeDir = type.equals(illustration) ? illustrationSubdir : mangaSubdir;
        String workId = UUID.randomUUID().toString().replace("-", "");
        String relFolder = Paths.get(sanitize(userId), typeDir, workId).toString();
        Path dir = Paths.get(uploadBaseDir, relFolder);
        List<MultipartFile> list = new ArrayList<>();
        for (MultipartFile f : files) {
            if (f != null && !f.isEmpty()) list.add(f);
        }
        if (list.isEmpty()) return null;
        try {
            Files.createDirectories(dir);
            for (MultipartFile f : list) {
                saveOneFile(f, dir);
            }
        } catch (IOException e) {
            throw new RuntimeException("保存作品失败", e);
        }
        return buildFolderUrl(relFolder);
    }

    // 保存单文件到指定目录（校验为图片类型；文件名使用 UUID+扩展名）
    private void saveOneFile(MultipartFile file, Path targetDir) throws IOException {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("仅支持图片类型文件");
        }
        String ext = getFileExtension(Objects.requireNonNullElse(file.getOriginalFilename(), ""));
        if (ext == null && contentType != null) {
            ext = mimeToExt(contentType);
        }
        String filename = UUID.randomUUID().toString().replace("-", "");
        filename = (ext == null || ext.isEmpty()) ? filename : filename + "." + ext;
        Path dest = targetDir.resolve(filename);
        file.transferTo(dest.toFile());
    }

    // 清空目录内容（删除文件与子目录）
    private static void clearDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        try (Stream<Path> paths = Files.list(dir)) {
            for (Path p : (Iterable<Path>) paths::iterator) {
                deleteRecursively(p);
            }
        }
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) return;
        if (Files.isDirectory(path)) {
            try (Stream<Path> children = Files.list(path)) {
                for (Path c : (Iterable<Path>) children::iterator) {
                    deleteRecursively(c);
                }
            }
        }
        Files.deleteIfExists(path);
    }

    // 生成以 URL 前缀开头且以 / 结尾的文件夹 URL
    private String buildFolderUrl(String relFolder) {
        String url = ensureTrailingSlash(uploadUrlPrefix) + (relFolder.startsWith("/") ? relFolder.substring(1) : relFolder);
        return ensureTrailingSlash(url);
    }

    private static String ensureTrailingSlash(String s) {
        if (s == null || s.isEmpty()) return "/";
        return s.endsWith("/") ? s : s + "/";
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
