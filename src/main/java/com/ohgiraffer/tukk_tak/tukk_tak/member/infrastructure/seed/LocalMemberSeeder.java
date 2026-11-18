package com.ohgiraffer.tukk_tak.tukk_tak.member.infrastructure.seed;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.LoginType;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.MemberStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Role;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*
 * comment.
 *  로컬 개발용 더미 회원 시더
 *  SPRING_PROFILES_ACTIVE=local 일 때만 실행되어 서버(EC2)에는 영향이 없다.
 *  이미 같은 이메일이 있으면 건너뛰므로 여러 번 실행해도 중복 생성되지 않는다.
 *  비밀번호는 전부 password1234! 이고, DB에는 BCrypt로 인코딩해서 저장한다.
 * */

@Slf4j
@Component
@Profile("local")   // local 프로필이 켜져 있을 때만 이 클래스가 동작
@RequiredArgsConstructor
public class LocalMemberSeeder implements ApplicationRunner {

    private static final String PASSWORD = "password1234!";   // 더미 공통 평문 비밀번호

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String encoded = passwordEncoder.encode(PASSWORD);   // 한 번만 인코딩해서 재사용

        // 1) 정상 로그인 / 로그아웃 / 토큰 재발급 / rememberMe 테스트용
        seed(local("biz-active@test.local", "더미사업자", "01000000001", encoded,
                MemberStatus.ACTIVE, null));

        // 2) 비밀번호 5회 오류 잠금 테스트 전용 (잠기면 15분간 사용 불가라 정상 계정과 분리)
        seed(local("biz-lock@test.local", "더미잠금", "01000000002", encoded,
                MemberStatus.ACTIVE, null));

        // 3) 탈퇴 회원 로그인 테스트용 (WITHDRAWN + withdrawnAt 세팅)
        seed(local("biz-withdrawn@test.local", "탈퇴_더미", "01000000003", encoded,
                MemberStatus.WITHDRAWN, LocalDateTime.now()));

        // 4) 구글 가입자 테스트용 (비밀번호 null → 비밀번호 로그인 시 SOCIAL_ACCOUNT_NO_PASSWORD)
        seed(new Member(null, "google-user@test.local", null, LoginType.GOOGLE,
                "더미구글", "더미구글", "01000000004", null, null,
                Role.BUSINESS, true, true, true, MemberStatus.ACTIVE, null));
    }

    /** 이메일이 이미 있으면 건너뛰고, 없으면 저장한다 (멱등) */
    private void seed(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) return;
        memberRepository.save(member);
        log.info("[seed] 더미 회원 생성 | email={} status={}", member.getEmail(), member.getStatus());
    }

    /** 일반(LOCAL 가입) 사업자 더미 회원 생성 헬퍼. 닉네임과 이름은 같은 값으로 채운다 */
    private Member local(String email, String nickname, String phone, String encodedPassword,
                         MemberStatus status, LocalDateTime withdrawnAt) {
        return new Member(null, email, encodedPassword, LoginType.LOCAL,
                nickname, nickname, phone, null, null,
                Role.BUSINESS, true, true, true, status, withdrawnAt);
    }

}
