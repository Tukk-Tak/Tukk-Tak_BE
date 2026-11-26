ALTER TABLE payment
    ADD COLUMN canceled_at DATETIME NULL COMMENT '취소 일시 (토스 수동 취소 웹훅 반영)' AFTER paid_at;