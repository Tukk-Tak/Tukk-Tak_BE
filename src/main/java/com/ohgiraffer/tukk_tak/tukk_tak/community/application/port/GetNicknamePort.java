package com.ohgiraffer.tukk_tak.tukk_tak.community.application.port;

import java.util.Map;
import java.util.Set;

public interface GetNicknamePort {
    String getNickname(Long memberId);

    Map<Long, String> getNicknames(Set<Long> memberIds);

}
