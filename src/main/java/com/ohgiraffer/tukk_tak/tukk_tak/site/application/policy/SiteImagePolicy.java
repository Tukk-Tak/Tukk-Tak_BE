package com.ohgiraffer.tukk_tak.tukk_tak.site.application.policy;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception.InvalidSiteImageException;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception.MissingSiteImageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;

@Component
public class SiteImagePolicy {

    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private static final int HEADER_SIZE = 4;
    private static final long MAX_PIXELS = 30_000_000L;   // 가로 x 세로 상한


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

        if (!isDecodable(image)) {
            throw new InvalidSiteImageException("손상되었거나 지원하지 않는 사진이에요.");
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


    /**
     * 용량 제한(5MB)만 있으면 파일은 작은데 펼치면 엄청 큰 그림인 파일도 통과함
     * 이런 파일을 열면 메모리가 수 GB까지 차서 서버가 죽을 수 있어서
     * 열기 전에 가로×세로만 먼저 확인하고 넘으면 바로 거절
     *
     * 여기서 read(0)은 이미지를 실제 픽셀 데이터로 복원하는 호출이라
     * 성공하려면 파일 안에 실제 이미지 데이터가 있어야하고
     * 사진이 아닌데 사진처럼 위장하고 있는 것들도 여기서 걸러짐
     */
    // 디코딩이 되는 이미지인지 확인 (헤더만 있는 파일이나 깨진 파일 차단)
    private boolean isDecodable(MultipartFile image) {
        try (InputStream in = image.getInputStream();
             ImageInputStream stream = ImageIO.createImageInputStream(in)) {

            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) {
                return false;
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(stream);
                // 디코딩 전에 해상도부터 확인 - 용량은 작은데 해상도가 큰 파일 방지
                if ((long) reader.getWidth(0) * reader.getHeight(0) > MAX_PIXELS) {
                    return false;
                }
                reader.read(0);
                return true;
            } finally {
                reader.dispose();
            }
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }
}