-- 회원
CREATE TABLE member (
                        id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                        email               VARCHAR(100) NOT NULL UNIQUE,
                        password            VARCHAR(255) NULL COMMENT '소셜 로그인 시 NULL',
                        login_type          VARCHAR(20) NOT NULL COMMENT 'LOCAL, GOOGLE',
                        nickname            VARCHAR(15) NOT NULL UNIQUE COMMENT '닉네임/사업장명, 공백 불가',
                        name                VARCHAR(50) NOT NULL,
                        phone               VARCHAR(20) NOT NULL COMMENT '개인 전화번호',
                        business_phone      VARCHAR(20) NULL COMMENT '업장 전화번호',
                        address             VARCHAR(255) NULL COMMENT '업장 실물주소',
                        role                VARCHAR(20) NOT NULL DEFAULT 'BUSINESS' COMMENT 'BUSINESS, ADMIN',
                        email_verified      BOOLEAN NOT NULL DEFAULT FALSE,
                        privacy_agreed      BOOLEAN NOT NULL DEFAULT FALSE COMMENT '개인정보 이용 동의',
                        ai_usage_agreed     BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'AI 사용 동의',
                        status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, WITHDRAWN',
                        withdrawn_at        DATETIME NULL,
                        created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 사업자 인증
CREATE TABLE business_verification (
                                       id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       member_id           BIGINT NOT NULL,
                                       business_number     VARCHAR(20) NOT NULL COMMENT '사업자 등록번호',
                                       open_date           DATE NOT NULL COMMENT '개업일자',
                                       ceo_name            VARCHAR(50) NOT NULL COMMENT '대표자명, 회원 이름과 일치해야 함',
                                       document_url        VARCHAR(500) NULL COMMENT '사업자 등록증 이미지',
                                       verify_method       VARCHAR(20) NOT NULL COMMENT 'AUTO(국세청API), MANUAL',
                                       status              VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED, ERROR',
                                       reject_reason       VARCHAR(255) NULL COMMENT '반려 사유 (불일치 등)',
                                       error_reason        VARCHAR(255) NULL COMMENT 'API 요청 실패 등',
                                       verified_at         DATETIME NULL,
                                       created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       CONSTRAINT fk_business_verification_member FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 회원 구독 (플랜은 enum, 한도는 애플리케이션 PlanPolicyGuard에서 관리)
CREATE TABLE subscription (
                              id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                              member_id           BIGINT NOT NULL,
                              plan_name           VARCHAR(20) NOT NULL COMMENT 'FREE, BASIC, PREMIUM',
                              ai_usage_count      INT NOT NULL DEFAULT 0 COMMENT '이번 주기 AI 사용 횟수',
                              status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, CANCELED, EXPIRED',
                              start_date          DATE NOT NULL,
                              end_date            DATE NOT NULL,
                              canceled_at         DATETIME NULL COMMENT '해지 신청 시각 (환불 아님, 다음 갱신 중단)',
                              created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_subscription_member FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 결제 (환불 없음 - 성공/실패만 존재)
CREATE TABLE payment (
                         id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                         subscription_id     BIGINT NOT NULL,
                         member_id           BIGINT NOT NULL,
                         amount              INT NOT NULL,
                         method              VARCHAR(20) NOT NULL COMMENT 'TOSS, KAKAOPAY',
                         status              VARCHAR(20) NOT NULL DEFAULT 'PAID' COMMENT 'PAID, FAILED',
                         paid_at             DATETIME NOT NULL,
                         CONSTRAINT fk_payment_subscription FOREIGN KEY (subscription_id) REFERENCES subscription(id),
                         CONSTRAINT fk_payment_member FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 사이트(웹사이트)
CREATE TABLE site (
                      id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                      member_id           BIGINT NOT NULL,
                      title               VARCHAR(100) NOT NULL COMMENT '상호명',
                      category            VARCHAR(20) NOT NULL DEFAULT 'RESTAURANT' COMMENT '업종, 현재는 RESTAURANT만 존재',
                      location            VARCHAR(255) NULL,
                      mood_keywords       VARCHAR(255) NULL COMMENT '분위기 키워드, 다중선택 콤마구분',
                      color_palette       VARCHAR(255) NULL,
                      description         VARCHAR(150) NULL COMMENT '보완 설명',
                      status              VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT, PUBLISHED',
                      slug                VARCHAR(30) NULL UNIQUE COMMENT '배포 링크명',
                      last_saved_at       DATETIME NULL,
                      published_at        DATETIME NULL,
                      created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      CONSTRAINT fk_site_member FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 사이트 블록 (헤더/히어로/줄글/메뉴판/목록/갤러리/푸터)
-- order_index: 전체 블록의 표시 순서. 세로 2단(2줄) 구성 시 같은 행에 있는
--              두 블록 중 왼쪽 블록이 더 작은 order_index 값을 가짐
--              (예: 1행 좌=10, 1행 우=11, 2행=20)
-- content: 블록 타입별로 필요한 실제 데이터. 타입마다 스키마가 다름
--   - HEADER: { "logoText": string, "menuLinks": [{ "label": string, "target": string }] }
--   - HERO: { "headline": string, "subText": string, "backgroundImageUrl": string, "ctaText": string }
--   - TEXT: { "body": string }
--   - MENU: { "items": [{ "name": string, "price": number, "imageUrl": string, "description": string }] }
--   - LIST: { "items": [{ "label": string, "value": string }] } (예: 영업시간, 오시는 길 등)
--   - GALLERY: { "imageUrls": [string] }
--   - FOOTER: { "snsLinks": [{ "type": string, "url": string }], "phone": string, "address": string }
-- style: 블록 공통으로 적용되는 시각적 속성 (색상/테두리/그림자/글자크기/둥글기)
--   { "backgroundColor": string, "textColor": string, "borderWidth": number,
--     "borderColor": string, "borderRadius": number, "shadow": boolean, "fontSize": number }
-- CREATE TABLE site_block (
--                             id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
--                             site_id             BIGINT NOT NULL,
--                             block_type          VARCHAR(30) NOT NULL COMMENT 'HEADER, HERO, TEXT, MENU, LIST, GALLERY, FOOTER',
--                             order_index         INT NOT NULL COMMENT '왼쪽/위쪽일수록 작은 값',
--                             content             JSON NOT NULL COMMENT '블록별 콘텐츠 데이터, 타입별 스키마는 위 주석 참고',
--                             style               JSON NULL COMMENT '색상/테두리/그림자/글자크기/둥글기, 모든 블록 공통 스키마',
--                             created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--                             updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--                             CONSTRAINT fk_site_block_site FOREIGN KEY (site_id) REFERENCES site(id) ON DELETE CASCADE
-- );

-- 배포 이력 (성공/실패/다운됨)
CREATE TABLE deployment (
                            id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                            site_id             BIGINT NOT NULL,
                            url                 VARCHAR(255) NOT NULL,
                            status              VARCHAR(20) NOT NULL DEFAULT 'SUCCESS' COMMENT 'SUCCESS, FAILED, CLOSED(다운됨)',
                            deployed_at         DATETIME NOT NULL,
                            closed_at           DATETIME NULL COMMENT '다운(CLOSED) 처리된 시각',
                            CONSTRAINT fk_deployment_site FOREIGN KEY (site_id) REFERENCES site(id) ON DELETE CASCADE
);

-- 배포 오류
CREATE TABLE deployment_error (
                                  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  site_id             BIGINT NOT NULL,
                                  deployment_id       BIGINT NULL,
                                  error_reason        VARCHAR(255) NOT NULL,
                                  occurred_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  CONSTRAINT fk_deployment_error_site FOREIGN KEY (site_id) REFERENCES site(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_deployment_error_deployment FOREIGN KEY (deployment_id) REFERENCES deployment(id) ON DELETE SET NULL
);

-- 게시물
-- category = STORE_INTRO인 경우 image_url 필수, FREE(자유게시판)인 경우 선택
-- (CHECK 제약은 MySQL 8.0.16+ 부터 강제되므로, 버전 낮으면 애플리케이션 레벨 검증 필요)
CREATE TABLE post (
                      id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                      member_id           BIGINT NOT NULL,
                      category            VARCHAR(20) NOT NULL COMMENT 'STORE_INTRO, FREE',
                      title               VARCHAR(25) NOT NULL,
                      content             VARCHAR(500) NOT NULL,
                      image_url           VARCHAR(500) NULL COMMENT 'STORE_INTRO는 필수, FREE는 선택',
                      like_count          INT NOT NULL DEFAULT 0,
                      comment_count       INT NOT NULL DEFAULT 0,
                      is_deleted          BOOLEAN NOT NULL DEFAULT FALSE,
                      created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      deleted_at          DATETIME NULL,
                      CONSTRAINT fk_post_member FOREIGN KEY (member_id) REFERENCES member(id),
                      CONSTRAINT chk_post_image_required CHECK (
                          category <> 'STORE_INTRO' OR image_url IS NOT NULL
                          )
);

-- 댓글/대댓글
-- 부모 댓글 삭제 시 자식(대댓글)도 함께 삭제 (ON DELETE CASCADE)
-- 게시물 삭제 시 댓글도 함께 삭제 (ON DELETE CASCADE)
CREATE TABLE comment (
                         id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                         post_id             BIGINT NOT NULL,
                         member_id           BIGINT NOT NULL,
                         parent_id           BIGINT NULL COMMENT '대댓글인 경우 부모 댓글 id',
                         content             VARCHAR(100) NOT NULL,
                         is_private          BOOLEAN NOT NULL DEFAULT FALSE,
                         is_deleted          BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         deleted_at          DATETIME NULL,
                         CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                         CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES member(id),
                         CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comment(id) ON DELETE CASCADE
);

-- 좋아요 (게시물 삭제 시 함께 삭제)
CREATE TABLE post_like (
                           id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                           post_id             BIGINT NOT NULL,
                           member_id           BIGINT NOT NULL,
                           created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT fk_post_like_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                           CONSTRAINT fk_post_like_member FOREIGN KEY (member_id) REFERENCES member(id),
                           CONSTRAINT uq_post_like UNIQUE (post_id, member_id)
);

-- 북마크 (게시물 삭제 시 함께 삭제 → 북마킹 해둔 사람의 목록에서도 자동으로 사라짐)
CREATE TABLE bookmark (
                          id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                          post_id             BIGINT NOT NULL,
                          member_id           BIGINT NOT NULL,
                          created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_bookmark_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                          CONSTRAINT fk_bookmark_member FOREIGN KEY (member_id) REFERENCES member(id),
                          CONSTRAINT uq_bookmark UNIQUE (post_id, member_id)
);

-- 알림
CREATE TABLE notification (
                              id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                              member_id           BIGINT NOT NULL,
                              type                VARCHAR(30) NOT NULL COMMENT 'COMMENT, LIKE, PAYMENT_EXPIRE, AI_COMPLETE, DEPLOY_COMPLETE',
                              target_id           BIGINT NULL COMMENT '연결된 게시물/사이트 등의 id',
                              message             VARCHAR(255) NOT NULL,
                              is_read             BOOLEAN NOT NULL DEFAULT FALSE,
                              created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_notification_member FOREIGN KEY (member_id) REFERENCES member(id)
);