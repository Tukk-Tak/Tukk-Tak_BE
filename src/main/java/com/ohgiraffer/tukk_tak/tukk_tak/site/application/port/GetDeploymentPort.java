package com.ohgiraffer.tukk_tak.tukk_tak.site.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.deployment.domain.model.DeploymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface GetDeploymentPort {
    Map<Long, DeploymentSummary> getLatestDeployments(List<Long> siteIds);

    record DeploymentSummary(DeploymentStatus status, String url, LocalDateTime deployedAt) {}
}
