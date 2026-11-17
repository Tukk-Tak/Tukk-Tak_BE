-- FAQ
CREATE TABLE faq (
                     id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                     question    VARCHAR(255) NOT NULL COMMENT '질문',
                     answer      TEXT NOT NULL COMMENT '답변',
                     created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                     updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);