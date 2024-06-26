package com.green.greengram.feed;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.feed.model.*;
import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import com.green.greengram.user.UserServiceImpl;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@Import({ FeedServiceImpl.class })//얘를 객체화 시켜줄거임
@TestPropertySource(
        properties = {
                "file.directory=D:/2024-01/download/greengram_tdd/"
        }
)
class FeedServiceTest {
    @Value("${file.directory}") String uploadPath;
    @Autowired
    FeedService service;//피드서비스 객체화 후 di받기를 해야함
                        //근데 서비스를 객체화하려면 매퍼도 객체화 시켜야함
    //@RequiredArgsConstructor가 붙어있고 final 매퍼가 있어서
    // 파라미터가 필요한 생성자가 생성됨 mapper랑 customFileUtils가 필요함
    //부모는 자식의 주소값을 받을 수 있다 FeedServiceImpl가 FeedService의자식임
    @MockBean
    FeedMapper mapper;//얘까지 객체화 시켜도 되는데 목표가 슬라이스 테스트라 안함
    @MockBean
    CustomFileUtils customFileUtils;

    @Test
    void postFeed() throws Exception {
        FeedPostReq p = new FeedPostReq();
        //feedId 값 주입시켜줘야한다.
        //실제때는 mapper.postFeed(p)때 feedId를 받을 수있지만 (마이바티스)
        //지금은 못쓰니까 직접 ㅈ넣어줌
        p.setFeedId(10);
        List<MultipartFile> pics = new ArrayList();
        MultipartFile fm1 = new MockMultipartFile(
                "pic", "1.jpg", "image/jpg",//위에걸로 저장
                new FileInputStream(String.format("%stest/1.jpg",uploadPath))//테스트 폴더에 있는걸 가져와서
        );// new 생성자  -- 생성할 때만 호출 가능한 메소드

        MultipartFile fm2 = new MockMultipartFile(
                "pic", "2.jpg", "image/jpg",//위에걸로 저장
                new FileInputStream(String.format("%stest/2.jpg",uploadPath))//테스트 폴더에 있는걸 가져와서
        );
        pics.add(fm1);
        pics.add(fm2);
        String randomFileNm1 = "a1.jpg";
        String randomFileNm2 = "a2.jpg";
        String[] randomFileArr = { randomFileNm1 , randomFileNm2};
        given(customFileUtils.makeRandomFileName(fm1)).willReturn(randomFileNm1);
        given(customFileUtils.makeRandomFileName(fm2)).willReturn(randomFileNm2);
        FeedPostRes res = service.postFeed(pics , p);
        verify(mapper).postFeed(p);
        String path = String.format("feed/%d", p.getFeedId());
        verify(customFileUtils).makeFolders(path);
        FeedPicPostDto dto = FeedPicPostDto.builder()
                            .feedId(p.getFeedId())
                            .build();
        for(int i = 0 ; i< pics.size() ;  i++){
            MultipartFile pic = pics.get(i);
            verify(customFileUtils).makeRandomFileName(pic);
            String target = String.format("%s/%s",path,randomFileArr[i]);
            dto.getFileNames().add(randomFileArr[i]);
            verify(customFileUtils).transferTo(pic,target);

        }
        verify(mapper).postFeedPics(dto);
        FeedPostRes watedRes = FeedPostRes.builder()
                .feedId(dto.getFeedId())
                .pics(dto.getFileNames()).build();

        assertEquals(watedRes,res,"리턴값이 다름");
    }

    @Test
    void getFeed() {
        FeedGetReq p = new FeedGetReq(1, 10 , 2L, 3L);

        List<FeedGetRes> list = new ArrayList();
        FeedGetRes fgr1 = new FeedGetRes();
        FeedGetRes fgr2 = new FeedGetRes();
        list.add(fgr1);
        list.add(fgr2);
        fgr1.setContents("내용1");
        fgr1.setFeedId(1);
        fgr2.setContents("내용2");
        fgr2.setFeedId(2);

        given(mapper.getFeed(p)).willReturn(list);

        List<String> picsA = new ArrayList();
        picsA.add("a1.jpg");
        picsA.add("a2.jpg");

        List<String> picsB = new ArrayList();
        picsB.add("b1.jpg");
        picsB.add("b2.jpg");
        picsB.add("b3.jpg");

        given(mapper.getFeedPicsByFeedId(fgr1.getFeedId())).willReturn(picsA);
        given(mapper.getFeedPicsByFeedId(fgr2.getFeedId())).willReturn(picsB);

        List<FeedCommentGetRes> commentsA = new ArrayList();
        FeedCommentGetRes fcgrA1 = new FeedCommentGetRes();
        FeedCommentGetRes fcgrA2 = new FeedCommentGetRes();
        commentsA.add(fcgrA1);
        commentsA.add(fcgrA2);
        fcgrA1.setComment("a1");
        fcgrA2.setComment("a2");

        List<FeedCommentGetRes> commentsB = new ArrayList();
        FeedCommentGetRes fcgrB1 = new FeedCommentGetRes();
        FeedCommentGetRes fcgrB2 = new FeedCommentGetRes();
        FeedCommentGetRes fcgrB3 = new FeedCommentGetRes();
        FeedCommentGetRes fcgrB4 = new FeedCommentGetRes();
        commentsB.add(fcgrB1);
        commentsB.add(fcgrB2);
        commentsB.add(fcgrB3);
        commentsB.add(fcgrB4);
        fcgrB1.setComment("b1");
        fcgrB2.setComment("b2");
        fcgrB3.setComment("b3");
        fcgrB4.setComment("b4");

        given(mapper.getFeedComment(fgr1.getFeedId())).willReturn(commentsA);
        given(mapper.getFeedComment(fgr2.getFeedId())).willReturn(commentsB);

        List<FeedGetRes> res = service.getFeed(p);
        verify(mapper).getFeed(p);

        assertEquals(list.size(), res.size(), "리턴값이 다름");

        for(FeedGetRes item : list ){
            verify(mapper).getFeedPicsByFeedId(item.getFeedId());
            verify(mapper).getFeedComment(item.getFeedId());
        }

        
//        verify(mapper.getFeedPicsByFeedId(fgr1.getFeedId()));
//        verify(mapper.getFeedPicsByFeedId(fgr2.getFeedId()));
//        verify(mapper.getFeedComment(fgr1.getFeedId()));
//        verify(mapper.getFeedComment(fgr2.getFeedId())); for로 대신함
        FeedGetRes actualItemA = res.get(0);
        assertEquals(picsA, actualItemA.getPics(),"fgr1의 이미지가 다름");
        assertEquals(commentsA, actualItemA.getComments(), "fg1의 댓글이 다름");
        assertEquals(0,actualItemA.getIsMoreComment());

        FeedGetRes actualItemB = res.get(1);
        assertEquals(picsB, actualItemB.getPics(),"fgr2의 이미지가 다름");
        assertEquals(commentsB, actualItemB.getComments(), "fg2의 댓글이 다름");
        assertEquals(3, commentsB.size());
        assertEquals(1,actualItemB.getIsMoreComment());

    }
}