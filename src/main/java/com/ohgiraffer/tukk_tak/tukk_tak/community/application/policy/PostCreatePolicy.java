package com.ohgiraffer.tukk_tak.tukk_tak.community.application.policy;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.InvalidImageException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.InvalidPostContentException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.InvalidTagException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.MissingImageException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PostCreatePolicy {

    private static final int TITLE_MAX = 25;
    private static final int CONTENT_MAX = 500;
    private static final int TAG_MAX_COUNT = 5;
    private static final int TAG_MAX_LENGTH = 10;
    private static final long IMAGE_MAX_BYTES = 5L * 1024 * 1024;
    private static final Pattern TAG_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]+$");

    public void validate(String title, String content, List<String> tags, MultipartFile image) {
        validateTitle(title);
        validateImage(image);
        validateContent(content);
        validateTags(tags);
    }

    // 제목 검증
    private void validateTitle(String title) {
        if (title.length() > TITLE_MAX) {
            throw new InvalidPostContentException("제목은 공백 포함 25자까지 쓸 수 있어요.");
        }
    }

    // 내용 검증
    private void validateContent(String content) {
        if (content.length() > CONTENT_MAX) {
            throw new InvalidPostContentException("내용은 공백 포함 500자까지 쓸 수 있어요.");
        }
    }

    // 태그 검증
    // 최대 5개 / 개당 최대 10글자 / 한글 영문 숫자 가능 / 중복 불가능
    private void validateTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return;

        if (tags.size() > TAG_MAX_COUNT) {
            throw new InvalidTagException("태그는 최대 5개까지 쓸 수 있어요.");
        }
        Set<String> seen = new HashSet<>();
        for (String tag : tags) {
            if (tag.length() > TAG_MAX_LENGTH) {
                throw new InvalidTagException("태그는 10자까지 쓸 수 있어요.");
            }
            if (!TAG_PATTERN.matcher(tag).matches()) {
                throw new InvalidTagException("태그는 한글, 영문, 숫자만 쓸 수 있어요.");
            }
            if (!seen.add(tag.toLowerCase())) {   // 영문 대소문자는 같은 태그로 취급
                throw new InvalidTagException("이미 추가한 태그예요.");
            }
        }
    }

    // 이미지 검증 - 제출여부/용량/확장자 검증
    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new MissingImageException("대표사진을 1장 올려주세요.");
        }
        if (image.getSize() > IMAGE_MAX_BYTES) {
            throw new InvalidImageException("사진은 최대 5MB까지 올릴 수 있어요.");
        }
        if (!hasValidSignature(image)) {
            throw new InvalidImageException("JPG, PNG 사진만 올릴 수 있어요.");
        }
    }

    // 실제 파일의 바이트로 확인한 확장자랑 제목 뒤에 붙은 확장자가 일치하는지 확인
    // -> 왜냐면 확장자는 이름만 바꾸면 속이기 가능
    private boolean hasValidSignature(MultipartFile image) {
        byte[] header = readHeader(image);
        return switch (extensionOf(image.getOriginalFilename())) {
            case "png" -> header.length >= 4
                    && (header[0] & 0xFF) == 0x89 && header[1] == 'P' && header[2] == 'N' && header[3] == 'G';
            case "jpg", "jpeg" -> header.length >= 3
                    && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;
            default -> false;
        };
    }

    // 실제 파일의 바이트로 타입을 확인
    // 확장자 확인에는 앞 4바이트만 필요해서 파일 전체를 읽지 X
    private byte[] readHeader(MultipartFile image) {
        try (InputStream fileStream = image.getInputStream()) {
            // 업로드된 파일의 내용을 처음부터 순서대로 읽을 수 있는 통로(스트림)를 돌려줌
            return fileStream.readNBytes(4);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // 파일명에 뒤에 붙은 확장자 확인
    private String extensionOf(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot + 1).toLowerCase();
    }
}