ALTER TABLE member
    ADD COLUMN privacy_agreed_at DATETIME NULL COMMENT '개인정보 이용 동의 시각',
    ADD COLUMN ai_usage_agreed_at DATETIME NULL COMMENT 'AI 사용 동의 시각 (동의하지 않은 상태면 NULL)';

-- 기존 회원은 동의한 시각을 알 수 없어서 가입 시각으로 채운다
UPDATE member SET privacy_agreed_at = created_at WHERE privacy_agreed = TRUE;
UPDATE member SET ai_usage_agreed_at = created_at WHERE ai_usage_agreed = TRUE;