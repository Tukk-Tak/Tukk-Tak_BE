ALTER TABLE post
    ADD COLUMN tags VARCHAR(100) NULL COMMENT '태그, 콤마 구분, # 제외, 최대 5개';