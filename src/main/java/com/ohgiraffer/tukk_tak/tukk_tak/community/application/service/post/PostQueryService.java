package com.ohgiraffer.tukk_tak.tukk_tak.community.application.service.post;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostQueryUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService implements PostQueryUsecase {



    @Override
    public List<PostSearchResult> getPosts(String keyword, int page, int size) {
        return List.of();
    }
}
