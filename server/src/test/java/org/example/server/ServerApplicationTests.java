package org.example.server;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.utils.BaiduMapUtils;
import org.example.pojo.entity.Emp;
import org.example.server.sever.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ServerApplicationTests {
@Autowired
private RedisTemplate redisTemplate;
@Autowired
private UserService userServiceImpl;
    @Test
    void contextLoads() {
        System.out.println(redisTemplate);
        HashOperations hashOperations = redisTemplate.opsForHash();


    }
    @Test
    void xx(){
        redisTemplate.opsForValue().set("key2","你好",1, TimeUnit.MINUTES);
        String value = (String) redisTemplate.opsForValue().get("key2");
        System.out.println(value);

    }
    @Test
    @RequestMapping("/xx2")
    void xx2(){
        int pageNo = 1, pageSize = 2;
        Page<Emp> objectPage = Page.of(pageNo, pageSize);
        // 按id升序排序
        objectPage.addOrder(OrderItem.desc("id"));


        //分页查询

        Page<Emp> page = userServiceImpl.page(objectPage);

        //总条数
        long total = page.getTotal();
        System.out.println(total);
        //总页数
        long totalPages = page.getPages();
        System.out.println(totalPages);
        // 分页数据
        List<Emp> records = page.getRecords();
        records.forEach(System.out::println);



    }
    @Autowired
    private BaiduMapUtils baiduMapUtils;
    @Test
    void xxxxxxx() {
        double[] lngLat = baiduMapUtils.getLngLat("湖南/株洲/荷塘区hao", null);
        String address = baiduMapUtils.getAddressByLngLat(lngLat[1], lngLat[0]);
        System.out.println(address);
    }

}
