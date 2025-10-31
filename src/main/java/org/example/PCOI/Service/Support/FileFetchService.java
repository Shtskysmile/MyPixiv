package org.example.PCOI.Service.Support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class FileFetchService {

    @Value("${pcoi.upload.base-dir}")
    private String uploadBaseDir;
    @Value("${pcoi.upload.url-prefix}")
    private String uploadUrlPrefix;

    public List<MultipartFile> loadImages(String path) throws IOException {
        List<MultipartFile> result = new ArrayList<>();
        String localPath = toLocalPath(path);
        if (localPath == null || localPath.isBlank()) return result;
        File f = new File(localPath);
        if (!f.exists()) return result;
        if (f.isFile()) {
            if (isImage(f.getName())) result.add(new SimpleMultipartFile(f));
            return result;
        }
        File[] files = f.listFiles();
        if (files == null) return result;
        for (File child : files) {
            if (child.isFile() && isImage(child.getName())) {
                result.add(new SimpleMultipartFile(child));
            }
        }
        return result;
    }

    private String toLocalPath(String input) {
        if (input == null || input.isBlank()) return input;
        String prefix = ensureTrailingSlash(uploadUrlPrefix);
        int idx = input.indexOf(prefix);
        if (idx >= 0) {
            String rel = input.substring(idx + prefix.length());
            return Paths.get(ensureTrailingSlash(uploadBaseDir), rel).toString();
        }
        return input;
    }

    private static boolean isImage(String name) {
        if (name == null) return false;
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
                || lower.endsWith(".gif") || lower.endsWith(".webp") || lower.endsWith(".bmp");
    }

    private static String ensureTrailingSlash(String s) {
        if (s == null || s.isEmpty()) return "/";
        return s.endsWith("/") ? s : s + "/";
    }
}
