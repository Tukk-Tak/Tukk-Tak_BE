package com.ohgiraffer.tukk_tak.tukk_tak.site.application.policy;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception.InvalidSiteImageException;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception.MissingSiteImageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@Component
public class SiteImagePolicy {

    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private static final int HEADER_SIZE = 4;

    public void validate(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new MissingSiteImageException("업로드할 사진을 선택해주세요.");
        }

        if (image.getSize() > MAX_SIZE) {
            throw new InvalidSiteImageException("사진은 5MB 이하만 올릴 수 있어요.");
        }

        String extension = extensionOf(image.getOriginalFilename());
        if (!hasValidSignature(extension, readHeader(image))) {
            throw new InvalidSiteImageException("JPG, PNG 사진만 올릴 수 있어요.");
        }
    }

    private boolean hasValidSignature(String extension, byte[] header) {
        return switch (extension) {
            case "png" -> header.length >= 4
                    && (header[0] & 0xFF) == 0x89
                    && header[1] == 0x50
                    && header[2] == 0x4E
                    && header[3] == 0x47;
            case "jpg", "jpeg" -> header.length >= 3
                    && (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF;
            default -> false;
        };
    }

    private byte[] readHeader(MultipartFile image) {
        try (InputStream in = image.getInputStream()) {
            return in.readNBytes(HEADER_SIZE);
        } catch (IOException e) {
            throw new InvalidSiteImageException("사진을 읽을 수 없어요.");
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}