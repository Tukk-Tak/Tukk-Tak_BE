package com.ohgiraffer.tukk_tak.tukk_tak.site.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.site.application.command.SaveSiteCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.site.application.usecase.SiteCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.event.SiteDeletedEvent;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception.SiteNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.Site;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.SiteStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository.SiteContentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SiteCommandService implements SiteCommandUsecase {

    private final SiteRepository siteRepository;
    private final SiteContentRepository siteContentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public SaveSiteResult saveSite(SaveSiteCommand command) {

        // siteId가
        // 널이면 -> 최조 저장 : mysql에 저장 후 생성된 siteId로 몽고 db에 저장
        // 널 아니면 -> 기존에 있던 siteId로 수정
        Site site = command.siteId() == null
                ? Site.create(command.memberId(), command.title())
                : updateSite(command);

        // mysql 저장
        Site saved = siteRepository.save(site);

        // 몽고는 마지막에 저장 - 여기서 예외가 나면 위의 mysql 저장도 롤백
        siteContentRepository.save(saved.getId(), command.theme(), command.rows());

        return new SaveSiteResult(saved.getId(), saved.getLastSavedAt());
    }

    @Override
    public void deleteSite(Long siteId, Long memberId) {

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new SiteNotFoundException("존재하지 않는 사이트예요."));

        site.validateOwner(memberId);   // 본인 사이트가 아니면 403

        // mysql 삭제
        siteRepository.deleteById(siteId);

        // 몽고 삭제 - 여기서 예외가 나면 mysql 삭제도 롤백
        siteContentRepository.deleteBySiteId(siteId);

        // 배포된 사이트면 배포 도메인이 내릴 수 있게 이벤트 발행 (커밋 이후에 처리됨)
        if (site.getStatus() == SiteStatus.PUBLISHED) {
            eventPublisher.publishEvent(new SiteDeletedEvent(siteId, site.getSlug()));
        }
    }

    private Site updateSite(SaveSiteCommand command) {

        Site site = siteRepository.findById(command.siteId())
                .orElseThrow(() -> new SiteNotFoundException("존재하지 않는 사이트예요."));

        site.update(command.memberId(), command.title());   // 본인 사이트가 아니면 403
        return site;
    }
}
