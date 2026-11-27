package com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model;

public record PlanLimit(
        int saveLimit,
        int publishLimit,
        int aiLimit
) {

    private static final PlanLimit FREE = new PlanLimit(3, 0, 1);       // AI는 평생 1회
    private static final PlanLimit BASIC = new PlanLimit(5, 1, 3);      // AI는 월 3회
    private static final PlanLimit PREMIUM = new PlanLimit(10, 3, 7);   // AI는 월 7회

    public static PlanLimit of(PlanName plan) {
        return switch (plan) {
            case FREE -> FREE;
            case BASIC -> BASIC;
            case PREMIUM -> PREMIUM;
        };
    }
}
