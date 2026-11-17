package com.ohgiraffer.tukk_tak.tukk_tak.global.notification;

public enum NotificationType {
    COMMENT, // 글에 댓글 달렸을 때
    LIKE, // 글에 좋아요 눌렸을 때
    PAYMENT_EXPIRE, // 결제 만료일 다가올 때
    AI_COMPLETE, // AI 초안 백그라운드 생성 완료됐을 때
    DEPLOY_COMPLETE // 백그라운드 배포 완료됐을 때
}
