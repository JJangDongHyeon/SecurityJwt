package com.green.greengram.user;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.user.model.UserProfilePatchReq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@Import({ UserServiceImpl.class })
@TestPropertySource(
        properties = {
                "file.directory=D:/2024-01/download/greengram_tdd/"
        }
)
public class UserService2Test {
    @Value("${file.directory}") String uploadPath;
    @MockBean
    UserMapper mapper;
    @Autowired
    UserService service;
    @MockBean CustomFileUtils customFileUtils;
    //커스텀파일 유틸즈를 진짜로 쓸거냐 가짜로 쓸거냐 (이건 안쓰는거)


    @Test
    void patchProfilePicMe() throws Exception {
        //커스텀파일 유틸즈를 목빈으로 하면 똑같은 메소드를 갖고 있으나 내용은 비어있다고 보면 도미
        long signedUserId1 = 500;
        UserProfilePatchReq p1 = new UserProfilePatchReq();
        p1.setSignedUserId(signedUserId1);
        MultipartFile fm1 = new MockMultipartFile(
                "pic", "1.jpg", "image/jpg",//위에걸로 저장
                new FileInputStream(String.format("%stest/1.jpg",uploadPath))//테스트 폴더에 있는걸 가져와서
        );
        p1.setPic(fm1);
        String cheackFileNm1 = "a1b2.jpg";
        given(customFileUtils.makeRandomFileName(fm1)).willReturn(cheackFileNm1);
        //원래 목빈을하면 null을 리턴해주기 떄문에 fm1을 넣을때 a1b2.jpg를 뱉으라고 임무를 주는 것

        String fileNm1 = service.patchProfilePic(p1); //
        assertEquals(cheackFileNm1,fileNm1);
        verify(mapper).updProfilePic(p1);//호출이 됐는지 확인
//        verify(customFileUtils).deleteFolder(String.format("%suser/%d",uploadPath,signedUserId1));
        verify(customFileUtils).deleteFolder(String.format("%suser/%d",customFileUtils.uploadPath,signedUserId1));
        verify(customFileUtils).makeFolders(String.format("user/%d",signedUserId1));
        verify(customFileUtils).transferTo(p1.getPic(), String.format("user/%d/%s",
                                                                        signedUserId1,p1.getPicName()));
    }
}
