package org.kwakmunsu.splearn.adapter.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.splearn.adapter.api.dto.MemberRegisterResponse;
import org.kwakmunsu.splearn.application.member.provided.MemberRegister;
import org.kwakmunsu.splearn.domain.member.Member;
import org.kwakmunsu.splearn.domain.member.MemberRegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberApi {

    private final MemberRegister memberRegister;

    @PostMapping("/api/members")
    public ResponseEntity<MemberRegisterResponse> registerMember(@RequestBody @Valid MemberRegisterRequest request) {
        Member member = memberRegister.register(request);

        return ResponseEntity.ok(MemberRegisterResponse.of(member));
    }

}