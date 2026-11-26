package com.ohgiraffer.tukk_tak.tukk_tak.site.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.site.application.port.GetDeploymentPort;
import com.ohgiraffer.tukk_tak.tukk_tak.site.application.port.GetDeploymentPort.DeploymentSummary;
import com.ohgiraffer.tukk_tak.tukk_tak.site.application.port.GetMemberPlanPort;
import com.ohgiraffer.tukk_tak.tukk_tak.site.application.usecase.SiteQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception.SiteNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.Site;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.model.SiteStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository.SiteContentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository.SiteContentRepository.SiteContent;
import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.repository.SiteRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanLimit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteQueryService implements SiteQueryUsecase {

    private final SiteRepository siteRepository;
    private final SiteContentRepository siteContentRepository;
    private final GetMemberPlanPort getMemberPlanPort;
    private final GetDeploymentPort getDeploymentPort;

    @Override
    public SiteListResult getSiteList(Long memberId) {

        // 저장한 사이트의 목록
        List<Site> sites = siteRepository.findAllByMemberId(memberId);
        // 구독 플랜에 따라 저장/배포가능한 사이트 수
        PlanLimit limit = PlanLimit.of(getMemberPlanPort.getPlan(memberId));

        // 사이트 전체 id 목록
        List<Long> siteIds = sites.stream().map(Site::getId).toList();
        // 배포된 사이트 목록
        List<Long> publishedIds = sites.stream()
                .filter(site -> site.getStatus() == SiteStatus.PUBLISHED)
                .map(Site::getId)
                .toList();

        Map<Long, SiteContent> contents = siteContentRepository.findAllBySiteIdIn(siteIds);
        Map<Long, DeploymentSummary> deployments = getDeploymentPort.getLatestDeployments(publishedIds);

        // 사이트 정보 + 베포 정보 가져오기
        List<SiteInfo> siteInfos = sites.stream()
                .map(site -> {
                   SiteContent content = contents.getOrDefault(
                            site.getId(), new SiteContent(Map.of(), List.of()));
                    DeploymentSummary deployment = deployments.get(site.getId());

                    return new SiteInfo(
                            site.getId(),
                            site.getTitle(),
                            site.getStatus(),
                            content.theme(),
                            content.rows(),
                            site.getLastSavedAt(),
                            deployment == null ? null : deployment.status(),
                            deployment == null ? null : deployment.url(),
                            deployment == null ? null : deployment.deployedAt()
                    );
                })
                .toList();

        return new SiteListResult(
                sites.size(),
                limit.saveLimit(),
                publishedIds.size(),
                limit.publishLimit(),
                siteInfos
        );
    }

    @Override
    public SiteDetailResult getSite(Long siteId, Long memberId) {

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new SiteNotFoundException("존재하지 않는 사이트예요."));

        site.validateOwner(memberId);   // 본인 사이트가 아니면 403

        // 몽고에 문서가 없으면 빈 테마/빈 rows
        SiteContent content = siteContentRepository.findBySiteId(siteId)
                .orElse(new SiteContentRepository.SiteContent(Map.of(), List.of()));

        return new SiteDetailResult(
                site.getId(),
                site.getTitle(),
                site.getStatus(),
                content.theme(),
                content.rows(),
                site.getLastSavedAt()
        );
    }
}
