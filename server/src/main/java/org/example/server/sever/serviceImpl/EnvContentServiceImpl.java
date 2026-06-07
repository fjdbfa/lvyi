package org.example.server.sever.serviceImpl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.pojo.dto.PageDto;
import org.example.pojo.envcontent.PageVo;
import org.example.pojo.envcontent.envContentCarouselData;
import org.example.server.mapper.envContentCarouselDataMapper;
import org.example.server.sever.EnvContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class EnvContentServiceImpl extends ServiceImpl<envContentCarouselDataMapper, envContentCarouselData> implements EnvContentService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    /**
     * 获取轮播图数据
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return PageDto 包含轮播图数据的DTO对象
     */
    public PageVo getCarouselData(Integer pageNo , Integer pageSize) {
        // 注释掉的代码：从数据库直接查询所有轮播图数据
        //List<envContentCarouselData> carouselData = baseMapper.selectList(null);
        // 创建轮播图数据传输对象
        PageVo carouselDto = new PageVo();

        // 从缓存中获取数据
        String carouselDataStr = stringRedisTemplate.opsForValue().get("carouselData" + pageNo);
        if (carouselDataStr != null) {
            // 1. 先将JSON字符串转为DTO（此时data可能是List<JSONObject>类型）
            PageVo bean = JSONUtil.toBean(carouselDataStr, PageVo.class);
            carouselDto.setTotal(bean.getTotal());

            // 2. 直接从原始JSON字符串中解析data数组（关键修复）
            JSONArray dataArray = JSONUtil.parseObj(carouselDataStr).getJSONArray("data");
            if (dataArray != null) {
                // 转换为指定泛型的List
                List<envContentCarouselData> dataList = JSONUtil.toList(dataArray, envContentCarouselData.class);
                carouselDto.setData(dataList);
            }
            return carouselDto;
        }


        Page<envContentCarouselData> objectPage = Page.of(pageNo, pageSize);
        // 按id升序排序
        objectPage.addOrder(OrderItem.desc("id"));


        //分页查询
        Page<envContentCarouselData> page = page(objectPage);

        //总条数
        long total = page.getTotal();
        System.out.println(total);
        //总页数
        long totalPages = page.getPages();
        System.out.println(totalPages);
        // 分页数据
        List<envContentCarouselData> records = page.getRecords();
        records.forEach(System.out::println);
        carouselDto.setTotal((int) total);
        carouselDto.setData(records);
        //缓存数据
        stringRedisTemplate.opsForValue().set("carouselData"+pageNo, JSONUtil.toJsonStr(carouselDto),10, TimeUnit.MINUTES);

        return carouselDto;
    }


}
