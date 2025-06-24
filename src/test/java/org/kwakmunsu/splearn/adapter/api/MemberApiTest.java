package org.kwakmunsu.splearn.adapter.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kwakmunsu.splearn.AssertThatUtils.equalsTo;
import static org.kwakmunsu.splearn.AssertThatUtils.notNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.splearn.adapter.api.dto.MemberRegisterResponse;
import org.kwakmunsu.splearn.application.member.provided.MemberRegister;
import org.kwakmunsu.splearn.application.member.required.MemberRepository;
import org.kwakmunsu.splearn.domain.member.Member;
import org.kwakmunsu.splearn.domain.member.MemberFixture;
import org.kwakmunsu.splearn.domain.member.MemberRegisterRequest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;
/*
* API 테스트만 하는 것이 아니라 뒷 단계까지 다 다녀온다.
* API로직이 너무 복잡하거나 인증 같은게 포함돼서 API만 테스트 해보고 싶다면 WebMvcTest를 사용함. */
@RequiredArgsConstructor
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class MemberApiTest {
    final MockMvcTester mockMvcTester;
    final ObjectMapper objectMapper;
    final MemberRepository memberRepository;
    final MemberRegister memberRegister;

    @DisplayName("회원 등록 테스트")
    @Test
    void register() throws JsonProcessingException, UnsupportedEncodingException {
        MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        // exchange로 꺼내 와야지 응답값을 꺼낼 수 있다.
        MvcTestResult result = mockMvcTester.post().uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.memberId", notNull())
                .hasPathSatisfying("$.email", equalsTo(request));
                // 여러 필드일 떄 위에 처럼 사용한다!

        MemberRegisterResponse response =
                objectMapper.readValue(result.getResponse().getContentAsString(), MemberRegisterResponse.class);
        // 실제 DB에 들어왔는지까지 확인하는 작업
        Member member = memberRepository.findById(response.memberId()).orElseThrow();
        assertThat(member.getEmail().address()).isEqualTo(request.email());
    }

    @DisplayName("회원 이메일 중복 테스트")
    @Test
    void duplicateEmail() throws JsonProcessingException {
        memberRegister.register(MemberFixture.createMemberRegisterRequest());

        MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        MvcTestResult result = mockMvcTester.post().uri("/api/members").contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .exchange();

        assertThat(result)
                .apply(print())
                .hasStatus(HttpStatus.CONFLICT);


    }


}