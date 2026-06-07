package org.example.server.sever.serviceImpl;

import org.example.common.result.Result;
import org.example.pojo.dto.PageDto;
import org.example.pojo.envcontent.PageVo;
import org.example.pojo.envcontent.envContentMaxImgData;
import org.example.pojo.envcontent.envContentMinImgData;
import org.example.pojo.envcontent.envContentSpotNewData;
import org.example.server.sever.EnvContentMaxImgService;
import org.example.server.sever.EnvContentMinImgService;
import org.example.server.sever.EnvContentSpotNewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnvContentSpotNewServiceImpl implements EnvContentSpotNewService {
    @Autowired
    private EnvContentMaxImgService envContentMaxImgService;
    @Autowired
    private EnvContentMinImgService envContentMinImgService;
    public Result getSpotNewData(List<envContentMaxImgData> page, List<envContentMinImgData> page1) {
//        List<envContentMaxImgData> page = EnvContentMaxImgService.page(currentPage, pageSize );
//
//        List<envContentMinImgData> page1 = EnvContentMinImgService.page(currentPage, pageSize*2);

        // 检查page列表是否为空或不足3个元素
        if (page == null || page.isEmpty()) {
            return Result.error("没有景点数据");
        }

        List<envContentSpotNewData> list = new ArrayList<>();
        int maxItems = Math.min(3, page.size());

        for (int i = 0; i < maxItems; i++) {
            envContentSpotNewData envContentSpotNewData = new envContentSpotNewData();
            envContentSpotNewData.setEnvContentMaxImgData(page.get(i));

            // 检查page1列表是否有足够的元素
            if (page1 != null && page1.size() >= (i * 2 + 2)) {
                envContentSpotNewData.setEnvContentMinImgData(page1.subList(i * 2, i * 2 + 2));
            }

            list.add(envContentSpotNewData);
        }

        PageVo<envContentSpotNewData> carouselDto = new PageVo<>();
        carouselDto.setData(list);
        return Result.success(carouselDto);











    }


}
