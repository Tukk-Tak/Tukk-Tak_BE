package com.ohgiraffer.tukk_tak.tukk_tak.member.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.repository.BusinessVerificationRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.global.exception.ResourceNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.PhoneNumberNormalizer;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.command.MemberUpdateCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.MemberInfoResult;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.usecase.MemberProfileUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.exception.MemberException;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/*
 * comment.
 *  마이페이지 회원 정보 서비스
 *  @Transactional을 클래스에 붙이지 않았다.
 *  저장은 레포지토리가 짧은 트랜잭션으로 처리하고, 닉네임 UNIQUE 위반은 트랜잭션이 끝난 여기서 잡아야 한다.
 * */

@Service
@RequiredArgsConstructor
public class MemberProfileService implements MemberProfileUsecase {

    private final MemberRepository memberRepository;
    private final BusinessVerificationRepository businessVerificationRepository;

    @Override
    public MemberInfoResult getMyInfo(Long memberId) {

        // 1. 회원 조회 후 현재 사업자와 함께 응답으로 만든다
        return toResult(findMember(memberId));
    }

    @Override
    public MemberInfoResult updateMyInfo(MemberUpdateCommand command) {

        // 1. 회원 조회
        Member member = findMember(command.memberId());

        // 2. 바꿀 값 정리: null이면 현재 값을 유지하고, 업장 전화번호와 주소는 빈 문자열이면 삭제(null)한다
        //    (닉네임과 전화번호의 형식은 요청 DTO에서 이미 검사했다. 전화번호는 숫자만 남긴다)
        String nickname = command.nickname() != null ? command.nickname() : member.getNickname();
        String phone = command.phone() != null
                ? PhoneNumberNormalizer.normalize(command.phone()) : member.getPhone();
        String businessPhone = command.businessPhone() != null
                ? PhoneNumberNormalizer.normalize(command.businessPhone()) : member.getBusinessPhone();
        String address = command.address() != null
                ? (StringUtils.hasText(command.address()) ? command.address().trim() : null)
                : member.getAddress();

        // 3. 닉네임이 바뀌었을 때만 중복 확인 (본인 닉네임 그대로면 건너뛴다)
        //    안내용이고, 동시에 같은 닉네임으로 바꾸는 경우는 5번의 UNIQUE 위반 변환이 마지막 방어선
        boolean nicknameChanged = !nickname.equals(member.getNickname());
        if (nicknameChanged && memberRepository.existsByNickname(nickname)) {
            throw MemberException.nicknameAlreadyUsed();
        }

        // 4. 값 반영
        member.updateProfile(nickname, phone, businessPhone, address);
        if (command.aiUsageAgreed() != null) {
            member.changeAiUsageAgreed(command.aiUsageAgreed());
        }

        // 5. 저장: 동시에 같은 닉네임으로 바꿔서 UNIQUE를 위반하면, 트랜잭션이 끝난 여기서 잡아 중복 에러로 바꾼다
        //    닉네임 때문이 아니면 다른 무결성 오류이므로 원래 예외를 그대로 던진다
        try {
            memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            if (nicknameChanged && memberRepository.existsByNickname(nickname)) {
                throw MemberException.nicknameAlreadyUsed();
            }
            throw e;
        }

        return toResult(member);
    }

    // 회원 조회 (로그인한 회원이 DB에 없는 경우는 글로벌 404로 처리)
    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다."));
    }

    // 회원과 현재 사업자(가장 최근 인증 기록)를 응답 결과로 만든다
    private MemberInfoResult toResult(Member member) {
        MemberInfoResult.BusinessInfo business = businessVerificationRepository
                .findLatestByMemberId(member.getId())
                .map(b -> new MemberInfoResult.BusinessInfo(
                        b.getBusinessNumber(), b.getOpenDate(), b.getCeoName(), b.getVerifiedAt()))
                .orElse(null);

        return new MemberInfoResult(member.getEmail(), member.getLoginType(), member.getName(),
                member.getNickname(), member.getPhone(), member.getBusinessPhone(), member.getAddress(),
                member.isPrivacyAgreed(), member.isAiUsageAgreed(), member.getAiUsageAgreedAt(), business);
    }

}
