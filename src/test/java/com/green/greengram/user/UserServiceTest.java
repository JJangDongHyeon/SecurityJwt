package com.green.greengram.user;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.user.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({ CustomFileUtils.class, UserServiceImpl.class })
@TestPropertySource(
     properties = {
             "file.directory=D:/2024-01/download/greengram_tdd/"
     }
)
class UserServiceTest {

    @Value("${file.directory}") String uploadPath;
    @MockBean UserMapper mapper;
    @Autowired UserService service;
    @Autowired CustomFileUtils customFileUtils;


    @Test
    void signUpPostReq() throws IOException {
        String p1Upw = "abc";
        SignUpPostReq p1 = new SignUpPostReq();
        p1.setUserId(100);
        p1.setUpw(p1Upw);

        given(mapper.signUpPostReq(p1)).willReturn(1);

        String p2Upw = "def";
        SignUpPostReq p2 = new SignUpPostReq();
        p2.setUserId(200);
        p2.setUpw("def");

        given(mapper.signUpPostReq(p2)).willReturn(2);

        MultipartFile fm1 = new MockMultipartFile(
                "pic", "3ef374e3-22bf-4ac6-9fa8-581f696dea4b.png", "image/png",
                new FileInputStream("D:/2024-01/download/greengram_tdd/user/3/3ef374e3-22bf-4ac6-9fa8-581f696dea4b.png")

        );

        int result1 = service.signUpPostReq(fm1, p1);
        assertEquals(1, result1);

        File savedFile1 = new File(uploadPath
                , String.format("%s/%d/%s", "user", p1.getUserId(), p1.getPic()));
        assertTrue(savedFile1.exists(), "1. 파일이 만들어지지 않음");
        savedFile1.delete();

        assertNotEquals(p1Upw, p1.getUpw());


        int result2 = service.signUpPostReq(fm1, p2);
        assertEquals(2, result2);

        File savedFile2 = new File(uploadPath
                , String.format("%s/%d/%s", "user", p2.getUserId(), p2.getPic()));
        assertTrue(savedFile2.exists(), "2. 파일이 만들어지지 않음");
        savedFile2.delete();

        assertNotEquals(p2Upw, p2.getUpw());
    }


    @Test
    void signInPost() {
        SignInPostReq req1 = new SignInPostReq();
        req1.setUid("id1");
        req1.setUpw("1212");
        String hashedUpw1 = BCrypt.hashpw(req1.getUpw(), BCrypt.gensalt());

        SignInPostReq req2 = new SignInPostReq();
        req2.setUid("id2");
        req2.setUpw("2323");
        String hashedUpw2 = BCrypt.hashpw(req2.getUpw(), BCrypt.gensalt());

        User user1 = new User(10, req1.getUid(), hashedUpw1, "홍길동1", "사진1.jpg", null, null);
        given(mapper.signInPost(req1.getUid())).willReturn(user1);

        User user2 = new User(20, req2.getUid(), hashedUpw2, "홍길동2", "사진2.jpg", null, null);
        given(mapper.signInPost(req2.getUid())).willReturn(user2);


        try(MockedStatic<BCrypt> mockedStatic = mockStatic(BCrypt.class)) {

            mockedStatic.when(() -> BCrypt.checkpw(req1.getUpw(), user1.getUpw())).thenReturn(true);
            mockedStatic.when(() -> BCrypt.checkpw(req2.getUpw(), user2.getUpw())).thenReturn(true);

            SignInPostRes res1 = service.signInPost(req1);
            assertEquals(user1.getUserId(), res1.getUserId(), "1. userId 다름");
            assertEquals(user1.getNm(), res1.getNm(), "1. nm 다름");
            assertEquals(user1.getPic(), res1.getPic(), "1. pic 다름");

            mockedStatic.verify(() -> BCrypt.checkpw(req1.getUpw(), user1.getUpw()));

            SignInPostRes res2 = service.signInPost(req2);
            assertEquals(user2.getUserId(), res2.getUserId(), "2. userId 다름");
            assertEquals(user2.getNm(), res2.getNm(), "2. nm 다름");
            assertEquals(user2.getPic(), res2.getPic(), "2. pic 다름");

            mockedStatic.verify(() -> BCrypt.checkpw(req2.getUpw(), user2.getUpw()));
        }

        SignInPostReq req3 = new SignInPostReq();
        req3.setUid("id3");
        given(mapper.signInPost(req3.getUid())).willReturn(null);

        Throwable ex1 = assertThrows(RuntimeException.class, () -> {
           service.signInPost(req3);
        }, "아이디 없음 예외 처리 안 함");
        assertEquals("아이디를 확인해주세요.", ex1.getMessage(), "아이디 없음 에러 메시지 다름");

        SignInPostReq req4 = new SignInPostReq();
        req4.setUid("id4");
        req4.setUpw("6666");

        String hashedUpw4 = BCrypt.hashpw("7777", BCrypt.gensalt());
        User user4 = new User(100, req4.getUid(), hashedUpw4, "홍길동4", "사진4.jpg", null, null);

        given(mapper.signInPost(user4.getUid())).willReturn(user4);
        Throwable ex2 = assertThrows(RuntimeException.class, () -> {
            service.signInPost(req4);
        }, "비밀번호 다름 예외 처리 안 함");
        assertEquals("비밀번호를 확인해주세요.", ex2.getMessage(), "비밀번호 다름, 에러 메시지 다름");

    }

    @Test
    void getUserInfo() {
        UserInfoGetReq p1 = new UserInfoGetReq(2, 1);
        UserInfoGetRes result1 = new UserInfoGetRes();
        result1.setNm("test1");
        given(mapper.selProfileUserInfo(p1)).willReturn(result1);

        UserInfoGetReq p2 = new UserInfoGetReq(3, 1);
        UserInfoGetRes result2 = new UserInfoGetRes();
        result2.setNm("test2");
        given(mapper.selProfileUserInfo(p2)).willReturn(result2);

        UserInfoGetRes res1 = service.getUserInfo(p1);
        assertEquals(result1, res1);

        UserInfoGetRes res2 = service.getUserInfo(p2);
        assertEquals(result2, res2);

    }

    @Test
    void patchProfilePicMe() throws Exception {
        long signedUserId1 = 500;
        String fileName = "1.jpg";
        String midPath = String.format("%s/user/%d/%s",uploadPath, signedUserId1,fileName);
        String copyFile = String.format("%s/test/%s",uploadPath,fileName);
        File dic1 = new File(midPath);
        File copy = new File(copyFile);
        if (!dic1.exists()){ //dic1이 있다면 트루 없다면 폴스
            dic1.mkdirs();//500이라는 폴더를 만들고
            try {
                Files.copy(copy.toPath(), dic1.toPath() , StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException("카피오류");
            }
        }

        UserProfilePatchReq p1 = new UserProfilePatchReq();
        p1.setSignedUserId(signedUserId1);

    }

    @Test
    void patchProfilePicTeacher() throws Exception{//서비스만 테스트 할거임 새로운 파일이 올라오면 기존에 있던 거 지울거임
        //사진이랑 pk값이 들어오는데 pk로 기존에 있던 폴더를 지우고 유저 폴더 들어가서 pk값으로 지움
        // >>> 암튼 폴더 제대로 만들고 지우는지 확인할거임
        long signedUserId1 = 500;
        final String ORIGIN_FILE_PATH = String.format("%s/test/%s",uploadPath,"1.jpg");
        File originFile = new File(ORIGIN_FILE_PATH);
        String midPath1 = String.format("%s/user/%d",uploadPath,signedUserId1);
        File copyFile1 = new File(midPath1 , "1.jpg");


        customFileUtils.deleteFolder(midPath1);//딜리트는 절대경로
        customFileUtils.makeFolders("user/" + signedUserId1); // 얘는 상대경로 (업로드패스 이후에 값만 보내면됨)
        Files.copy(originFile.toPath(), copyFile1.toPath() , StandardCopyOption.REPLACE_EXISTING);

        UserProfilePatchReq p1 = new UserProfilePatchReq();
        p1.setSignedUserId(signedUserId1);
        MultipartFile fm1 = new MockMultipartFile(
                "pic", "1.jpg", "image/jpg",//위에걸로 저장
                new FileInputStream(String.format("%stest/1.jpg",uploadPath))//테스트 폴더에 있는걸 가져와서
        );
        p1.setPic(fm1);
        String fileNm1 = service.patchProfilePic(p1);//한개 있는 파일의 이름이 이거랑 같아야함
        assertNotNull(fileNm1, "1. 파일이 null로 넘어왔음");
        //1.midPath1 폴더가 존재해야함
        //2.해당 폴더의 파일은 한개만 존재해야함
        //3. 해당 파일의 이름은 fileNm1과 같아야함
        File midPath1Folder = new File(midPath1);
        assertEquals(true, midPath1Folder.exists());

        assertEquals(1, midPath1Folder.listFiles().length);

        File file1 = midPath1Folder.listFiles()[0];
        assertEquals(fileNm1, file1.getName());

        //매퍼가 호출을 했는가 체크
        //무조건 목빈이어야함 (mapper)
        verify(mapper).updProfilePic(p1);
        //서비스도 목빈으로 할 예정
    }
}