ALTER TABLE payment
    ADD COLUMN order_id       VARCHAR(64)  NOT NULL COMMENT '가맹점 주문번호 (서버 생성, 중복 결제 방지)' AFTER status,
    ADD COLUMN pg_payment_key VARCHAR(200) NOT NULL COMMENT '토스 paymentKey / 카카오 tid' AFTER order_id,
    ADD COLUMN months         INT          NOT NULL COMMENT '결제한 개월 수' AFTER pg_payment_key,
    ADD CONSTRAINT uk_payment_order_id UNIQUE (order_id),
    ADD CONSTRAINT uk_payment_pg_key UNIQUE (method, pg_payment_key),
    ADD INDEX idx_payment_member_paid (member_id, paid_at DESC);