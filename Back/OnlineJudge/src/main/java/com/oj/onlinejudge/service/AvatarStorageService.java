package com.oj.onlinejudge.service;

// 头像存储服务：校验图片、保存文件并返回访问 URL

import com.oj.onlinejudge.config.StorageProperties;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AvatarStorageService {

    private static final Set<String> SUPPORTED_TYPES = Set.of("png", "jpg", "jpeg", "gif", "webp");

    private final StorageProperties storageProperties;

    public String storeAvatar(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("文件不是有效的图片");
        }
        if (image.getWidth() != image.getHeight()) {
            throw new IllegalArgumentException("头像必须为正方形图片");
        }
        String extension = resolveExtension(file);
        if (!SUPPORTED_TYPES.contains(extension)) {
            throw new IllegalArgumentException("暂不支持的图片格式：" + extension);
        }
        Path avatarDir = Paths.get(storageProperties.getAvatarDir()).toAbsolutePath();
        Files.createDirectories(avatarDir);
        String filename = UUID.randomUUID().toString().replace("-", "");
        Path target = avatarDir.resolve(filename + "." + extension);
        file.transferTo(target);
        String urlPrefix = storageProperties.getAvatarUrlPrefix();
        if (!urlPrefix.startsWith("/")) {
            urlPrefix = "/" + urlPrefix;
        }
        return urlPrefix + "/" + target.getFileName();
    }

    private String resolveExtension(MultipartFile file) {
        String original = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(original);
        if (!StringUtils.hasText(ext)) {
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                ext = contentType.substring("image/".length());
            }
        }
        if (!StringUtils.hasText(ext)) {
            ext = "png";
        }
        return ext.toLowerCase(Locale.ROOT);
    }
}
