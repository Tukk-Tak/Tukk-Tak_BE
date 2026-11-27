package com.ohgiraffer.tukk_tak.tukk_tak.deployment.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.deployment.domain.model.DeploymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "deployment")
public class DeploymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_id", nullable = false)
    private Long siteId;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeploymentStatus status;

    @Column(name = "deployed_at", nullable = false)
    private LocalDateTime deployedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
}
