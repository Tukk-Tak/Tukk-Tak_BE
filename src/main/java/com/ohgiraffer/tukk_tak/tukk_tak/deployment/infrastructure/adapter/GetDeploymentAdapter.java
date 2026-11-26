package com.ohgiraffer.tukk_tak.tukk_tak.deployment.infrastructure.adapter;

import com.ohgiraffer.tukk_tak.tukk_tak.deployment.infrastructure.persistence.DeploymentJpaEntity;
import com.ohgiraffer.tukk_tak.tukk_tak.deployment.infrastructure.persistence.DeploymentJpaRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.site.application.port.GetDeploymentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetDeploymentAdapter implements GetDeploymentPort {

    private final DeploymentJpaRepository deploymentJpaRepository;

    @Override
    public Map<Long, DeploymentSummary> getLatestDeployments(List<Long> siteIds) {
        if (siteIds.isEmpty()) {
            return Map.of();
        }

        return deploymentJpaRepository.findBySiteIdInOrderByDeployedAtDescIdDesc(siteIds).stream()
                .collect(Collectors.toMap(
                        DeploymentJpaEntity::getSiteId,
                        d -> new DeploymentSummary(d.getStatus(), d.getUrl(), d.getDeployedAt()),
                        (first, second) -> first
                ));
    }
}
