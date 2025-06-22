package org.kwakmunsu.splearn.application.provided;

import jakarta.validation.Valid;
import org.kwakmunsu.splearn.domain.Member;
import org.kwakmunsu.splearn.domain.MemberRegisterRequest;

/**
 *  회원 등록과 관련된 기능을 제공합니다.
**/
public interface MemberRegister {

    Member register(@Valid MemberRegisterRequest request);
    Member activate(Long memberId);

}