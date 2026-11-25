package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.PasswordChangeCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.PasswordChangeUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.exception.AuthException;
import com.ohgiraffer.tukk_tak.tukk_tak.global.exception.ResourceNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.FailureCountGuard;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.token.AccessTokenRevoker;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.token.RefreshTokenService;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

/*
 * comment.
 *  비밀번호 변경 서비스
 *  @Transactional을 클래스에 붙이지 않았다.
 *  비밀번호 저장은 레포지토리가 짧은 트랜잭션으로 처리하고, 그 뒤의 Redis 작업까지 DB 커넥션을 붙잡지 않게 하려는 것이다.
 *  로그인 중인 회원이 현재 비밀번호만으로 바꾸는 API라서, 현재 비밀번호를 무차별로 대입하지 못하게 실패 횟수를 센다.
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordChangeService implements PasswordChangeUsecase {

    // 현재 비밀번호 확인 제한: 10분 안에 5번 틀리면 15분간 잠금 (로그인 실패 제한과 같은 값, 따로 센다)
    private static final String FAILURE_SCOPE = "pw-change";
    private static final int MAX_FAILURE = 5;
    private static final Duration FAILURE_WINDOW = Duration.ofMinutes(10);
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FailureCountGuard failureCountGuard;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenRevoker accessTokenRevoker;

    @Override
    public void changePassword(PasswordChangeCommand command) {

        String identifier = String.valueOf(command.memberId());

        // 1. 잠겨 있으면 비밀번호를 비교하지 않고 바로 거절 (잠금 중에는 맞는 비밀번호도 시도하지 못하게 한다)
        if (failureCountGuard.isLocked(FAILURE_SCOPE, identifier)) {
            throw AuthException.passwordChangeLocked();
        }

        // 2. 회원 조회 (토큰은 유효한데 DB에 회원이 없는 경우는 글로벌 404로 처리)
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다."));

        // 3. 비밀번호가 없는 구글 회원은 바꿀 수 없다 (화면에서도 막지만 서버에서 한 번 더 막는다)
        if (!member.hasPassword()) {
            throw AuthException.passwordChangeSocialAccount();
        }

        // 4. 현재 비밀번호 확인: 틀리면 실패 횟수를 올리고, 이번에 한도에 걸려 잠겼다면 바로 잠김 응답
        if (!passwordEncoder.matches(command.currentPassword(), member.getPassword())) {
            failureCountGuard.recordFailure(FAILURE_SCOPE, identifier,
                    MAX_FAILURE, FAILURE_WINDOW, LOCK_DURATION);
            if (failureCountGuard.isLocked(FAILURE_SCOPE, identifier)) {
                throw AuthException.passwordChangeLocked();
            }
            throw AuthException.currentPasswordMismatch();
        }

        // 5. 새 비밀번호가 현재 비밀번호와 같으면 거절 (바뀐 게 없는데 모든 기기가 로그아웃되는 일을 막는다)
        //    현재 비밀번호가 방금 확인되었으므로 평문끼리 비교해도 같은 결과이고, 암호화 비교를 한 번 줄일 수 있다
        if (command.currentPassword().equals(command.newPassword())) {
            throw AuthException.passwordSameAsCurrent();
        }

        // 6. 새 비밀번호를 암호화해서 저장
        member.changePassword(passwordEncoder.encode(command.newPassword()));
        memberRepository.save(member);

        // 7. 이 회원의 refresh token을 지우고, 이미 발급된 access token도 모두 무효로 만든다 (지금 쓴 토큰 포함)
        //    (비밀번호를 저장한 뒤에 처리해야, 그 직후 옛 비밀번호로 다시 로그인하는 틈이 생기지 않는다)
        refreshTokenService.delete(member.getId());
        accessTokenRevoker.revokeAll(member.getId());

        // 8. 틀린 횟수를 지운다
        //    여기서 실패해도 비밀번호는 이미 바뀌었으므로, 경고만 남기고 성공으로 응답한다
        try {
            failureCountGuard.resetFailure(FAILURE_SCOPE, identifier);
        } catch (RuntimeException e) {
            log.warn("[password-change] 실패 횟수 초기화 실패 | type={}", e.getClass().getSimpleName());
        }

        log.info("[password-change] 변경 완료 | memberId={}", member.getId());
    }

}
