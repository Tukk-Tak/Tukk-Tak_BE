package com.ohgiraffer.tukk_tak.tukk_tak.site.infrastructure.listener;

import com.ohgiraffer.tukk_tak.tukk_tak.global.s3.S3FileStorage;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.event.SiteImageCleanupEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SiteImageCleanupListener {

    private final S3FileStorage s3FileStorage;

    // 사이트 삭제가 커밋된 뒤에만 S3 사진 삭제 - 삭제가 롤백되면 사진은 그대로
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SiteImageCleanupEvent event) {
        event.keys().forEach(s3FileStorage::delete);
    }

    // 만약에 db처리 단계에서 실패해서 롤백하면 db들은 롤백이 되는데 S3는 이미 지워지고 난 뒤일 수도 있어서
    // db 작업들은 다 커밋하고서 S3 삭제
}
