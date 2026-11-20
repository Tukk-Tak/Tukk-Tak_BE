package com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.comment;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.command.CreateCommentCommand;

public interface CommentCommandUsecase {
    void createComment(CreateCommentCommand createCommentCommand);
}
