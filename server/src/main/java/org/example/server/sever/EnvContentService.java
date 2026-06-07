package org.example.server.sever;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.dto.PageDto;
import org.example.pojo.envcontent.PageVo;
import org.example.pojo.envcontent.envContentCarouselData;

public interface EnvContentService extends IService<envContentCarouselData> {
    public PageVo getCarouselData(Integer pageNo , Integer pageSize);

    //List<envContentCarouselData> getSpotNewData(Integer currentPage, Integer pageSize);
}
