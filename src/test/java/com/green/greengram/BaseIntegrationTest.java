package com.green.greengram;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
@ActiveProfiles("tdd")
@Import({ CharEncodingConfiguration.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //통합테스트때 쓰이는 어노테이션 모두 객체화~
@AutoConfigureMockMvc //컨트롤러 슬라이스 테스트때 쓴것과 비슷
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//매퍼 테스트때 같은 설정을 했었음
public class BaseIntegrationTest {
    @Autowired protected MockMvc mvc;
    @Autowired protected ObjectMapper om;//스트링이 객체로 객체를 스트링으로 > 그 스트링은 제이슨 형태
//제이슨 형태의 문자열을 객체로...맨날 쓰던거
}
