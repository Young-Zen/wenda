package com.nowcoder;

import com.nowcoder.service.LikeService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LikeServiceTests {
    @Autowired
    LikeService likeService;

    @Before
    public void setUp(){
        //数据初始化
        System.out.println("setUp");
    }

    @After
    public void tearDown(){
        //数据清理
        System.out.println("tearDown");
    }

    @BeforeClass
    public static void beforeClass(){
        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterClass(){
        System.out.println("afterClass");
    }

    @Test
    public void testLike(){
        System.out.println("testLike");
        likeService.like(123,1,1);
        Assert.assertEquals(1,likeService.getLikeStatus(123,1,1));

        likeService.dislike(123,1,1);
        Assert.assertEquals(-1,likeService.getLikeStatus(123,1,1));
    }

    @Test
    public void testXXX(){
        System.out.println("testXXX");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException(){
        System.out.println("testException");
        throw new IllegalArgumentException("异常发生");
    }
}
