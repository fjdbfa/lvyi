package org.example.server.sever.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.utils.PageUtil;
import org.example.pojo.envcontent.envContentMinImgData;
import org.example.server.mapper.envContentMinImgDataMapper;
import org.example.server.sever.EnvContentMinImgService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EnvContentMinImgServiceImpl extends ServiceImpl<envContentMinImgDataMapper, envContentMinImgData> implements EnvContentMinImgService {
    private PageUtil page;
    public void page(PageUtil pageUtil) {
        this.page=pageUtil;
    }
    @Override
    public List<envContentMinImgData> page(Integer pageNo, Integer pageSize) {
        //分页查询
        List<envContentMinImgData> records = page.queryPage(
                pageNo,
                pageSize,
                (page) -> this.page(page, null) // 无查询条件，直接分页查询全表

        );
        return records;

    }
}
