package org.example.PCOI.Service.Support;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

public class SimpleMultipartFile implements MultipartFile {
    private final File file;
    private final String contentType;

    public SimpleMultipartFile(File file) throws IOException {
        this.file = Objects.requireNonNull(file, "file");
        this.contentType = Files.probeContentType(file.toPath());
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return file.getName();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return file.length() == 0;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    @Override
    public void transferTo(File dest) throws IOException {
        Files.copy(file.toPath(), dest.toPath());
    }
}

