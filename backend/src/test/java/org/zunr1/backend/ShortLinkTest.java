package org.zunr1.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zunr1.backend.entity.Link;
import org.zunr1.backend.mapper.LinkMapper;
import org.zunr1.backend.service.LinkService;

import java.util.List;

@SpringBootTest
public class ShortLinkTest {

    @Autowired
    private LinkMapper linkMapper;

    @Autowired
    private LinkService linkService;

    // 1. 测试数据库插入
    @Test
    public void testInsert() {
        Link link = new Link();
        link.setLongUrl("https://www.baidu.com");
        link.setShortCode("test001");
        link.setUserId(1);
        // expireAt 可以为 null，表示永久有效

        int result = linkMapper.insertLink(link);
        System.out.println("插入结果: " + result);
        System.out.println("生成的ID: " + link.getId());
    }

    // 2. 测试根据短码查询
    @Test
    public void testSelectByShortCode() {
        // 先插入一条
        Link link = new Link();
        link.setLongUrl("https://www.google.com");
        link.setShortCode("test002");
        linkMapper.insertLink(link);

        // 再查询
        Link found = linkMapper.selectLinkByShortCode("test002");
        System.out.println("查询结果: " + (found != null ? found.getLongUrl() : "未找到"));
    }

    // 3. 测试根据长链接查询（查重）
    @Test
    public void testSelectByLongUrl() {
        Link found = linkMapper.selectLinkByLongUrl("https://www.baidu.com");
        System.out.println("百度短链: " + (found != null ? found.getShortCode() : "未找到"));
    }

    // 4. 测试根据用户ID查询（返回列表）
    @Test
    public void testSelectByUserId() {
        List<Link> links = linkMapper.selectLinksByUserId(1);
        System.out.println("用户1的短链数量: " + links.size());
        for (Link link : links) {
            System.out.println("  - " + link.getShortCode() + " -> " + link.getLongUrl());
        }
    }

    // 5. 测试Service - 创建短链
    @Test
    public void testSwitchUrl() {
        String longUrl = "https://www.github.com";
        String shortCode = linkService.switchUrl(longUrl);
        System.out.println("短链创建成功: " + shortCode);

        // 第二次调用应该返回相同的短码（查重）
        String shortCode2 = linkService.switchUrl(longUrl);
        System.out.println("再次创建: " + shortCode2);
        System.out.println("是否相同: " + shortCode.equals(shortCode2));
    }

    // 6. 测试Service - 获取长链接
    @Test
    public void testGetLongUrl() {
        // 先创建一个
        String longUrl = "https://www.stackoverflow.com";
        String shortCode = linkService.switchUrl(longUrl);

        // 再获取
        String foundUrl = linkService.getLongUrl(shortCode);
        System.out.println("短码 " + shortCode + " 对应: " + foundUrl);
    }
}