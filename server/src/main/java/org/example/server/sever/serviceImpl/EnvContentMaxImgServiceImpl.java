package org.example.server.sever.serviceImpl;

import org.example.common.utils.PageUtil;
import org.example.pojo.envcontent.envContentMaxImgData;
import org.example.server.sever.EnvContentMaxImgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.server.mapper.envContentMaxImgDataMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvContentMaxImgServiceImpl extends ServiceImpl<envContentMaxImgDataMapper, envContentMaxImgData> implements EnvContentMaxImgService {
    private  PageUtil page;
    public void page(PageUtil pageUtil) {
        this.page=pageUtil;
    }
    @Override
    public List<envContentMaxImgData> page(Integer pageNo, Integer pageSize) {

        //分页查询
        List<envContentMaxImgData> records = page.queryPage(
                pageNo,
                pageSize,
                (page) -> this.page(page, null) // 无查询条件，直接分页查询全表
        );
        return records;

    }

}
