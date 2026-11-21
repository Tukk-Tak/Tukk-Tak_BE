package com.ohgiraffer.tukk_tak.tukk_tak.member.infrastructure.seed;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/*
 * comment.
 *  서버 시작 시 관리자 계정을 한 번 만들어 주는 초기화 클래스
 *  app.admin.email / app.admin.password 환경변수가 모두 있고 해당 계정이 없을 때만 생성한다.
 * */

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}") private String adminEmail;
    @Value("${app.admin.password:}") private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) return;

        String email = adminEmail.trim().toLowerCase(Locale.ROOT);
        if (memberRepository.existsByEmail(email)) return;

        memberRepository.save(Member.admin(email, passwordEncoder.encode(adminPassword)));
        log.info("[admin] 관리자 계정 생성 완료 | email={}", email);
    }

}
