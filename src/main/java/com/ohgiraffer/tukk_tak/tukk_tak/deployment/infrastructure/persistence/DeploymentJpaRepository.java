package com.ohgiraffer.tukk_tak.tukk_tak.deployment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeploymentJpaRepository extends JpaRepository<DeploymentJpaEntity, Long> {

    List<DeploymentJpaEntity> findBySiteIdInOrderByDeployedAtDescIdDesc(List<Long> siteIds);
}
